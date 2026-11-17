package com.github.cesarb98.artenarua.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.github.cesarb98.artenarua.data.Obra
import com.github.cesarb98.artenarua.navigation.Rotas
import com.github.cesarb98.artenarua.repository.ObraRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeObraRepository(iniciais: List<Obra> = emptyList()) : ObraRepository {
    val obras = MutableStateFlow(iniciais)

    override fun observarTodas(): Flow<List<Obra>> = obras
    override fun observarPorId(id: Long): Flow<Obra?> = obras.map { l -> l.find { it.id == id } }

    override suspend fun cadastrar(obra: Obra): Long {
        val id = (obras.value.maxOfOrNull { it.id } ?: 0L) + 1
        obras.update { it + obra.copy(id = id) }
        return id
    }

    override suspend fun alternarFavorita(id: Long, favorita: Boolean) =
        obras.update { l -> l.map { if (it.id == id) it.copy(favorita = favorita) else it } }

    override suspend fun salvarComentario(id: Long, comentario: String?) =
        obras.update { l -> l.map { if (it.id == id) it.copy(comentario = comentario) else it } }
}

private fun obra(id: Long, favorita: Boolean = false, comentario: String? = null) =
    Obra(id = id, titulo = "Obra $id", artista = "Artista", historia = "História",
        favorita = favorita, comentario = comentario)

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    // ---------- Listagem ----------

    @Test
    fun `lista - filtro favoritas mostra so favoritas e reage ao desfavoritar`() = runTest {
        val repo = FakeObraRepository(listOf(obra(1), obra(2, favorita = true)))
        val vm = ObraListViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        assertEquals(2, vm.uiState.value.obras.size)
        assertFalse(vm.uiState.value.carregando)

        vm.selecionarFiltro(FiltroObras.FAVORITAS)
        assertEquals(listOf(2L), vm.uiState.value.obras.map { it.id })

        vm.alternarFavorita(vm.uiState.value.obras.first())
        assertTrue(vm.uiState.value.obras.isEmpty())
        assertEquals(FiltroObras.FAVORITAS, vm.uiState.value.filtro)
    }

    // ---------- Detalhe ----------

    private fun detalhe(repo: ObraRepository, id: Long = 1) =
        ObraDetailViewModel(repo, SavedStateHandle(mapOf(Rotas.ARG_OBRA_ID to id)))

    @Test
    fun `detalhe - id inexistente termina o carregamento sem obra`() = runTest {
        val vm = detalhe(FakeObraRepository(), id = 99)
        assertNull(vm.uiState.value.obra)
        assertFalse(vm.uiState.value.carregando)
    }

    @Test
    fun `detalhe - salvar comentario faz trim e emite mensagem`() = runTest {
        val repo = FakeObraRepository(listOf(obra(1)))
        val vm = detalhe(repo)

        vm.iniciarEdicaoComentario()
        vm.alterarRascunho("  Linda!  ")
        vm.salvarComentario()

        assertEquals("Linda!", repo.obras.value.first().comentario)
        assertEquals("Linda!", vm.uiState.value.obra?.comentario)
        assertFalse(vm.uiState.value.editandoComentario)
        assertEquals(ObraDetailViewModel.MSG_COMENTARIO_SALVO, vm.uiState.value.mensagem)

        vm.mensagemExibida()
        assertNull(vm.uiState.value.mensagem)
    }

    @Test
    fun `detalhe - comentario em branco ou acima de 500 nao salva`() = runTest {
        val repo = FakeObraRepository(listOf(obra(1)))
        val vm = detalhe(repo)
        vm.iniciarEdicaoComentario()

        vm.alterarRascunho("   ")
        assertFalse(vm.uiState.value.podeSalvarComentario)
        vm.salvarComentario()
        assertNull(repo.obras.value.first().comentario)

        vm.alterarRascunho("a".repeat(501))
        assertNotNull(vm.uiState.value.erroComentario)
        vm.salvarComentario()
        assertNull(repo.obras.value.first().comentario)

        vm.alterarRascunho("a".repeat(500))
        assertNull(vm.uiState.value.erroComentario)
        assertTrue(vm.uiState.value.podeSalvarComentario)
    }

    @Test
    fun `detalhe - editar carrega o comentario atual e remover apaga`() = runTest {
        val repo = FakeObraRepository(listOf(obra(1, comentario = "antigo")))
        val vm = detalhe(repo)

        vm.iniciarEdicaoComentario()
        assertEquals("antigo", vm.uiState.value.rascunhoComentario)
        vm.cancelarEdicao()
        assertEquals("antigo", repo.obras.value.first().comentario)

        vm.removerComentario()
        assertNull(repo.obras.value.first().comentario)
        assertEquals(ObraDetailViewModel.MSG_COMENTARIO_REMOVIDO, vm.uiState.value.mensagem)
    }

    @Test
    fun `detalhe - alternar favorita reflete no repositorio`() = runTest {
        val repo = FakeObraRepository(listOf(obra(1)))
        val vm = detalhe(repo)
        vm.alternarFavorita()
        assertTrue(repo.obras.value.first().favorita)
        assertTrue(vm.uiState.value.obra!!.favorita)
    }

    // ---------- Cadastro ----------

    @Test
    fun `cadastro - obrigatorios vazios ou so com espacos mostram erro e nao gravam`() = runTest {
        val repo = FakeObraRepository()
        val vm = ObraFormViewModel(repo)
        vm.alterarTitulo("   ")
        vm.salvar()

        val estado = vm.uiState.value
        assertEquals(ObraFormViewModel.ERRO_TITULO, estado.erroTitulo)
        assertEquals(ObraFormViewModel.ERRO_ARTISTA, estado.erroArtista)
        assertEquals(ObraFormViewModel.ERRO_HISTORIA, estado.erroHistoria)
        assertFalse(estado.cadastrada)
        assertTrue(repo.obras.value.isEmpty())

        // O erro de um campo some assim que ele é editado; os outros continuam.
        vm.alterarTitulo("Mural")
        assertNull(vm.uiState.value.erroTitulo)
        assertNotNull(vm.uiState.value.erroArtista)
    }

    @Test
    fun `cadastro - valido grava com trim, opcionais vazios nulos e marca cadastrada`() = runTest {
        val repo = FakeObraRepository()
        val vm = ObraFormViewModel(repo)
        vm.alterarTitulo("  Mural  ")
        vm.alterarArtista(" Fulana ")
        vm.alterarHistoria(" Uma história ")
        vm.alterarLocal("  Centro ")
        vm.alterarPeriodo("   ")
        vm.salvar()

        assertTrue(vm.uiState.value.cadastrada)
        val salva = repo.obras.value.single()
        assertEquals("Mural", salva.titulo)
        assertEquals("Fulana", salva.artista)
        assertEquals("Uma história", salva.historia)
        assertEquals("Centro", salva.local)
        assertNull(salva.periodo)
        assertNull(salva.tecnica)
        assertNull(salva.imagemNome)
        assertNull(salva.comentario)
        assertFalse(salva.favorita)
        assertTrue(salva.criadaPeloUsuario)

        // Um segundo toque em salvar não duplica o registro.
        vm.salvar()
        assertEquals(1, repo.obras.value.size)
    }

    @Test
    fun `cadastro - campos respeitam o tamanho maximo`() = runTest {
        val vm = ObraFormViewModel(FakeObraRepository())
        vm.alterarTitulo("a".repeat(200))
        vm.alterarArtista("a".repeat(200))
        vm.alterarHistoria("a".repeat(2000))
        assertEquals(80, vm.uiState.value.titulo.length)
        assertEquals(60, vm.uiState.value.artista.length)
        assertEquals(1000, vm.uiState.value.historia.length)
    }
}
