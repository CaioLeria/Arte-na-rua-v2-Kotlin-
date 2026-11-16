# Arte na Rua v2 — Especificação do app Android

**Disciplina:** Android Kotlin Developer — Global Solution 2026 (2º semestre), desafio CitizenTech
**Versão da spec:** 1.0 (2026-11-16)
**Prazo de entrega da GS:** 19/11/2026, 23h59 (horário de Brasília)

---

## 1. Visão geral

### 1.1 O que é

**Arte na Rua** é um aplicativo Android que funciona como um catálogo pessoal de arte urbana de São Paulo. O usuário conhece obras de rua da cidade, favorita as que mais gosta, registra seus próprios comentários sobre cada uma e cadastra novas obras que encontrar.

Esta é a **segunda versão**, mais simples, do app Arte na Rua: **sem chamadas externas** (sem API, sem mapa, sem audiodescrição). Todos os dados ficam no dispositivo, em Room.

### 1.2 Problema e público

- **Problema:** a arte de rua é uma forma de patrimônio cultural urbano, mas é efêmera (os muros são repintados) e pouco documentada. Muita gente passa por ela sem saber quem é o artista, o que a obra conta ou quando foi feita.
- **Público beneficiado:** moradores e visitantes de São Paulo interessados em cultura urbana, estudantes e turistas que querem conhecer e guardar suas obras preferidas.
- **Como o app ajuda:** reúne em um só lugar as informações de cada obra (artista, história, período, local) e permite ao cidadão manter seu próprio registro, ajudando a preservar a memória dessas obras.

### 1.3 ODS relacionado (a confirmar)

**ODS 11 — Cidades e Comunidades Sustentáveis**, em especial a meta 11.4 (proteger e salvaguardar o patrimônio cultural). A justificativa vai no README: o app documenta e divulga a arte urbana como patrimônio cultural da cidade e incentiva o cidadão a participar do registro dela.

---

## 2. Escopo

### 2.1 Dentro do escopo

- Tela **Home** que apresenta o projeto, com navegação inferior (bottom bar).
- Tela de **Listagem de obras** com cards, botão de favoritar no card, filtro "Todas / Favoritas" e botão de cadastrar obra na top bar.
- Tela de **Detalhe da obra** com dados do artista, da obra e de quando foi feita, e comentário pessoal (criar, editar, remover).
- Tela de **Cadastro de obra** com validação de campos obrigatórios.
- **5 obras mockadas** de São Paulo, carregadas no Room na primeira abertura do app (seed).
- Persistência local com **Room**: obras, favoritos e comentários sobrevivem ao fechamento do app.

### 2.2 Fora do escopo

- API, Retrofit, Gemini, Google Maps/Places, geolocalização, mapa.
- Audiodescrição / TTS e reconhecimento de obra por foto.
- Autenticação, backend, sincronização na nuvem.
- Excluir ou editar os dados de uma obra (a única edição é o comentário pessoal).
- Seleção de foto da galeria ou câmera para obras cadastradas.

### 2.3 Mapeamento com a rubrica da GS

| Critério da rubrica | Pontos | Onde a spec cobre |
|---|---|---|
| 1. Contextualização e tela inicial | 1,0 | Seções 1 e 6.1 (Home); ODS no README |
| 2. Navegação entre telas | 1,5 | Seção 5 (Navigation Compose, 4 destinos) |
| 3. Interface e experiência de uso | 1,5 | Seção 6 (Material 3, estados vazios, Snackbars) |
| 4. Dados, cadastro e interação | 2,0 | Seções 4 e 6.4 (cadastro com validação), favoritar, comentar |
| 5. Persistência com Room | 1,5 | Seção 4 (Room, seed, DAO) |
| 6. Organização e estado | 1,5 | Seção 3 (MVVM, ViewModel, Hilt, StateFlow) |
| 7. Documentação e demonstração | 1,0 | Seção 9 (checklist do README e evidências) |

---

## 3. Arquitetura (MVVM)

### 3.1 Camadas

```
┌──────────────────────────────────────────────────────────┐
│ View (ui/)                                               │
│ Composables sem regra de negócio.                        │
│ Observam UiState e enviam eventos para a ViewModel.      │
└───────────────┬──────────────────────────▲───────────────┘
                │ eventos (funções)        │ StateFlow<UiState>
┌───────────────▼──────────────────────────┴───────────────┐
│ ViewModel (viewmodel/)                                   │
│ Estado da tela, validação, regras de apresentação.       │
│ @HiltViewModel, viewModelScope.                          │
└───────────────┬──────────────────────────▲───────────────┘
                │ chamadas suspend         │ Flow<List<Obra>>
┌───────────────▼──────────────────────────┴───────────────┐
│ Model (repository/ + data/)                              │
│ ObraRepository → ObraDao → Room (SQLite)                 │
└──────────────────────────────────────────────────────────┘
```

**Regras de camada**

- A View nunca acessa DAO nem Repository; fala só com a ViewModel.
- A ViewModel não conhece nada de Android UI (sem `Context`, sem Composable).
- O Repository é a única porta de entrada para o DAO.
- O estado da tela é imutável (`data class`) e exposto como `StateFlow`; a View o coleta com `collectAsStateWithLifecycle()`.
- Eventos de uma vez só (por exemplo, "mostrar Snackbar") são enviados por `SharedFlow`/`Channel` ou por um campo de mensagem consumido pela View.

### 3.2 Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose |
| Injeção de dependência | Hilt (`hilt-android`, `hilt-navigation-compose`) |
| Persistência | Room (SQLite) |
| Assincronismo | Kotlin Coroutines + Flow |
| Processamento de anotações | KSP (Room e Hilt) |
| Imagens | `painterResource` (drawables locais, sem biblioteca de rede) |

As versões seguem o projeto base visto em aula. `minSdk` e `compileSdk` também devem seguir esse projeto.

### 3.3 Estrutura de pacotes

```
app/src/main/java/<pacote-base>/
├── ArteNaRuaApplication.kt        # @HiltAndroidApp
├── MainActivity.kt                # @AndroidEntryPoint, setContent
├── data/
│   ├── Obra.kt                    # @Entity (modelo de dados)
│   ├── ObraDao.kt                 # @Dao
│   ├── AppDatabase.kt             # @Database (versão 1)
│   └── ObraSeed.kt                # lista das 5 obras iniciais
├── repository/
│   ├── ObraRepository.kt          # interface
│   └── ObraRepositoryImpl.kt      # implementação com DAO
├── di/
│   ├── DatabaseModule.kt          # provê AppDatabase e ObraDao
│   └── RepositoryModule.kt        # @Binds ObraRepository
├── viewmodel/
│   ├── ObraListViewModel.kt
│   ├── ObraDetailViewModel.kt
│   └── ObraFormViewModel.kt
├── ui/
│   ├── theme/                     # Color, Type, Theme
│   ├── components/                # ObraCard, EstadoVazio, FiltroObras etc.
│   ├── home/HomeScreen.kt
│   ├── lista/ObraListScreen.kt
│   ├── detalhe/ObraDetailScreen.kt
│   └── cadastro/ObraFormScreen.kt
├── navigation/
│   ├── Rotas.kt                   # rotas e argumentos
│   └── ArteNaRuaNavHost.kt        # NavHost + Scaffold com bottom bar
└── util/
    └── ImagemUtil.kt              # nome do drawable → resource id, com placeholder
```

A Home não tem ViewModel: é uma tela estática, sem estado que mude.

### 3.4 Injeção de dependência (Hilt)

- `ArteNaRuaApplication` anotada com `@HiltAndroidApp`; `MainActivity` com `@AndroidEntryPoint`.
- `DatabaseModule` (`@InstallIn(SingletonComponent::class)`): provê `AppDatabase` como `@Singleton` (com o callback de seed) e `ObraDao`.
- `RepositoryModule`: `@Binds` de `ObraRepositoryImpl` para `ObraRepository`.
- ViewModels com `@HiltViewModel` e `@Inject constructor(repository, savedStateHandle)`; nas telas, `hiltViewModel()`.
- O callback de seed recebe um `Provider<ObraDao>` (ou `Lazy`) para evitar dependência circular com o banco que está sendo criado.

---

## 4. Dados e persistência

### 4.1 Entidade `Obra`

Tabela `obras`.

| Campo | Tipo Kotlin | Obrigatório | Descrição |
|---|---|---|---|
| `id` | `Long` (PK, autoGenerate) | sim (gerado) | Identificador |
| `titulo` | `String` | **sim** | Nome da obra |
| `artista` | `String` | **sim** | Autor (pessoa, dupla ou coletivo) |
| `historia` | `String` | **sim** | Texto sobre a obra e seu contexto |
| `periodo` | `String?` | não | Quando foi feita (ex.: "2015", "Desde os anos 1980") |
| `local` | `String?` | não | Bairro e/ou endereço |
| `tecnica` | `String?` | não | Ex.: grafite, mural, lambe-lambe (texto livre) |
| `imagemNome` | `String?` | não | Nome do drawable (ex.: `obra_beco_do_batman`); nulo usa o placeholder |
| `favorita` | `Boolean` (padrão `false`) | sim | Marcada como favorita |
| `comentario` | `String?` | não | Comentário pessoal do usuário (opcional) |
| `criadaPeloUsuario` | `Boolean` (padrão `false`) | sim | `false` para as obras do seed |

`periodo` é texto (e não número) porque a informação pode ser aproximada, como "Desde os anos 1980".

### 4.2 DAO (`ObraDao`)

| Método | Retorno | Uso |
|---|---|---|
| `observarTodas()` | `Flow<List<Obra>>` | Lista (ordenada por `id` crescente) |
| `observarPorId(id: Long)` | `Flow<Obra?>` | Detalhe |
| `inserir(obra: Obra)` | `Long` | Cadastro e seed |
| `atualizarFavorita(id: Long, favorita: Boolean)` | `Int` | Botão de favoritar |
| `atualizarComentario(id: Long, comentario: String?)` | `Int` | Salvar e remover comentário |

O filtro "Favoritas" é aplicado na ViewModel sobre o `Flow` de todas as obras (uma única fonte de dados).

### 4.3 Repository (`ObraRepository`)

```kotlin
interface ObraRepository {
    fun observarTodas(): Flow<List<Obra>>
    fun observarPorId(id: Long): Flow<Obra?>
    suspend fun cadastrar(obra: Obra): Long
    suspend fun alternarFavorita(id: Long, favorita: Boolean)
    suspend fun salvarComentario(id: Long, comentario: String?)
}
```

### 4.4 Banco e seed

- `AppDatabase`: `@Database(entities = [Obra::class], version = 1, exportSchema = true)`.
- **Seed:** `RoomDatabase.Callback.onCreate` insere as 5 obras de `ObraSeed` em uma coroutine (`Dispatchers.IO`), só na criação do banco (primeira abertura). Depois disso, os dados vivem exclusivamente no Room e os favoritos e comentários do usuário nunca são sobrescritos.
- Sem `fallbackToDestructiveMigration` desnecessário: a versão do schema é 1 durante a entrega.

### 4.5 Imagens

- Arquivos em `res/drawable-nodpi/`, nomeados conforme `imagemNome` (ex.: `obra_beco_do_batman.jpg`).
- `ImagemUtil` resolve o nome para o id do recurso com `Resources.getIdentifier(...)`; se o nome for nulo ou o recurso não existir (retorno 0), usa `R.drawable.obra_placeholder`.
- Assim o app compila e roda **antes** das fotos reais estarem no projeto: as fotos são adicionadas depois, apenas copiando os arquivos com os nomes combinados.
- Obras cadastradas pelo usuário usam o placeholder (seção 2.2).

### 4.6 Dados mockados (seed)

Sugestão de 5 obras, espalhadas por regiões diferentes de São Paulo. **Antes de entregar, conferir cada informação** (principalmente períodos e endereços) e reescrever os textos com as próprias palavras. Os textos abaixo são rascunhos curtos.

| # | Título | Artista | Período | Local | Técnica | `imagemNome` |
|---|---|---|---|---|---|---|
| 1 | Beco do Batman | Vários artistas | Desde os anos 1980 | Vila Madalena (Zona Oeste), entre as ruas Gonçalo Afonso e Medeiros de Albuquerque | Grafite | `obra_beco_do_batman` |
| 2 | Ayrton Senna — A Lenda do Brasil | Eduardo Kobra | 2015 (revitalizado em 2020) | Esquina da Av. Paulista com a Rua da Consolação (Centro) | Mural | `obra_senna_lenda_brasil` |
| 3 | Muro das Memórias | Eduardo Kobra | 2009 | Av. 23 de Maio, perto do Viaduto Tutóia (Zona Sul/Centro) | Mural | `obra_muro_das_memorias` |
| 4 | Histórias em Nossas Memórias | Coletivos Arte e Cultura na Kebrada, BocaSujaCrew e Sou Favela | 2021 | Rua Corinthians, Vila Nova União, São Miguel Paulista (Zona Leste) | Mural / grafite | `obra_historias_nossas_memorias` |
| 5 | Nós Somos São Paulo | Eduardo Kobra | 2026 | Autódromo de Interlagos (Zona Sul) | Mural | `obra_nos_somos_sao_paulo` |

**Rascunhos de `historia`**

1. *Beco do Batman:* Galeria a céu aberto no coração da Vila Madalena. O nome vem de um grafite do Batman que, segundo a história contada no bairro, apareceu nos muros nos anos 1980. O desenho original não existe mais, e os muros são repintados com frequência, então cada visita mostra algo novo.
2. *Ayrton Senna — A Lenda do Brasil:* Homenagem ao piloto tricampeão de Fórmula 1 na esquina da Av. Paulista com a Rua da Consolação, pintada por Eduardo Kobra com apoio da Audi do Brasil, em parceria com o Instituto Ayrton Senna. Foi revitalizada no fim de 2020.
3. *Muro das Memórias:* Um dos maiores murais de Eduardo Kobra, com cerca de 1.000 m², pintado na Av. 23 de Maio em 2009 para o aniversário da cidade, dentro do projeto Muros da Memória, que resgata a história de São Paulo. *(Verificar se a obra ainda existe e, se não, ajustar o texto.)*
4. *Histórias em Nossas Memórias:* Mural de 2021 criado por artistas de coletivos da própria região, a partir de uma pesquisa sobre a história de São Miguel Paulista, um dos bairros mais antigos da cidade, para que quem passa pelo local conheça essa história. *(Confirmar qual coletivo pintou este mural específico.)*
5. *Nós Somos São Paulo:* Mural de Eduardo Kobra no Autódromo de Interlagos, com cerca de 2.000 m² (225 m por 8,17 m), que reúne Ayrton Senna, trabalhadores de diferentes áreas e marcos históricos e arquitetônicos da cidade.

Observação: 3 das 5 obras são de Eduardo Kobra. Se preferir mais variedade de artistas, basta trocar uma ou duas por obras de outros nomes; a estrutura do seed não muda.

---

## 5. Navegação

### 5.1 Rotas (Navigation Compose)

| Rota | Tela | Argumentos | Bottom bar |
|---|---|---|---|
| `home` | Home (destino inicial) | — | visível |
| `obras` | Listagem de obras | — | visível |
| `detalhe/{obraId}` | Detalhe da obra | `obraId: Long` | oculta |
| `cadastro` | Cadastro de obra | — | oculta |

### 5.2 Fluxos

```
Home ──(botão "Ver obras" ou aba Obras)──► Listagem
Listagem ──(toque no card)──► Detalhe
Listagem ──(ícone "+" na top bar)──► Cadastro
Cadastro ──(salvar com sucesso)──► volta à Listagem (Snackbar "Obra cadastrada")
Detalhe / Cadastro ──(seta de voltar)──► volta à tela anterior
```

### 5.3 Bottom bar

- `NavigationBar` do Material 3 com dois itens: **Home** (ícone de casa) e **Obras** (ícone de galeria/paleta).
- Definida no `Scaffold` raiz em `ArteNaRuaNavHost` e exibida **somente** quando a rota atual é `home` ou `obras`.
- Troca de aba com `launchSingleTop = true`, `restoreState = true` e `popUpTo(startDestination) { saveState = true }`, para o back não empilhar abas.

### 5.4 Resultado do cadastro

O `ObraFormViewModel` sinaliza o sucesso; a tela de cadastro grava `obra_cadastrada = true` no `savedStateHandle` do `previousBackStackEntry` e chama `popBackStack()`. A listagem observa essa chave, mostra o Snackbar e limpa a chave.

---

## 6. Telas

### 6.1 Home

**Objetivo:** apresentar o projeto (nome, objetivo, público e o que dá para fazer) e levar à listagem. Não é onboarding: é uma tela fixa dentro da bottom bar.

**Layout (de cima para baixo, rolável com `LazyColumn` ou `Column` + `verticalScroll`)**

1. **Cabeçalho:** ícone/marca do app, nome **"Arte na Rua"** e frase de apoio (ex.: "Conheça, guarde e comente a arte urbana de São Paulo").
2. **Card "O que é":** texto curto explicando o projeto.
3. **Card "Para quem é":** público beneficiado.
4. **Seção "O que você pode fazer":** 3 itens com ícone: *Explorar obras*, *Favoritar as que mais gosta*, *Registrar seus comentários e novas obras*.
5. **Chip/selo ODS 11:** "ODS 11 — Cidades e Comunidades Sustentáveis".
6. **Botão primário "Ver obras":** navega para `obras`.

**Estados:** apenas conteúdo estático. Sem carregamento e sem vazio.

**Critérios de aceite**

- Mostra nome, objetivo e público do app sem depender de dados.
- "Ver obras" abre a listagem e a aba "Obras" fica selecionada.

### 6.2 Listagem de obras

**ViewModel:** `ObraListViewModel`

```kotlin
enum class FiltroObras { TODAS, FAVORITAS }

data class ObraListUiState(
    val obras: List<Obra> = emptyList(),   // já filtradas
    val filtro: FiltroObras = FiltroObras.TODAS,
    val carregando: Boolean = true
)
```

- Combina `repository.observarTodas()` com o filtro selecionado (`combine`) e expõe `StateFlow<ObraListUiState>` via `stateIn(viewModelScope, WhileSubscribed(5_000), ...)`.
- Funções: `selecionarFiltro(filtro)` e `alternarFavorita(obra)`.

**Layout**

- **TopAppBar:** título "Obras" e ação com o ícone `Add` (descrição de acessibilidade "Cadastrar obra") que navega para `cadastro`.
- **Filtro:** linha de `FilterChip` abaixo da top bar: **Todas** | **Favoritas**.
- **Lista:** `LazyColumn` de `ObraCard`.
- **Bottom bar:** visível (aba Obras selecionada).
- **Snackbar:** `SnackbarHost` no `Scaffold`.

**`ObraCard`**

| Elemento | Detalhe |
|---|---|
| Imagem | Foto da obra (ou placeholder) no topo ou à esquerda, com `ContentScale.Crop` |
| Título | Nome da obra (uma ou duas linhas) |
| Subtítulo | Artista · Bairro/local (quando houver) |
| Botão favoritar | `IconButton` com coração: preenchido quando favorita, contornado quando não; `contentDescription` "Favoritar"/"Remover dos favoritos" |
| Toque no card | Abre o detalhe (`detalhe/{id}`), onde fica a edição do comentário |

**Estados**

| Situação | O que aparece |
|---|---|
| Carregando | Indicador de progresso circular centralizado |
| Filtro "Todas" sem obras (improvável após o seed) | Estado vazio: "Nenhuma obra por aqui ainda. Toque em + para cadastrar." |
| Filtro "Favoritas" sem favoritas | Estado vazio: "Você ainda não favoritou nenhuma obra." |
| Favoritou/desfavoritou | Ícone muda imediatamente; no filtro "Favoritas", a obra sai da lista ao desfavoritar |
| Voltou do cadastro com sucesso | Snackbar "Obra cadastrada" |

**Critérios de aceite**

- Ao abrir o app pela primeira vez, aparecem as 5 obras do seed.
- Tocar no coração altera o estado e persiste após fechar e reabrir o app.
- O filtro "Favoritas" mostra apenas as favoritas.
- O "+" abre o cadastro; o toque no card abre o detalhe da obra correta. O card não tem botão de edição: a edição fica só no detalhe.

### 6.3 Detalhe da obra

**ViewModel:** `ObraDetailViewModel` (recebe `obraId` por `SavedStateHandle`)

```kotlin
data class ObraDetailUiState(
    val obra: Obra? = null,
    val carregando: Boolean = true,
    val editandoComentario: Boolean = false,
    val rascunhoComentario: String = "",
    val erroComentario: String? = null,
    val mensagem: String? = null            // para Snackbar (consumida pela View)
)
```

- Observa `repository.observarPorId(id)`.
- Funções: `alternarFavorita()`, `iniciarEdicaoComentario()`, `alterarRascunho(texto)`, `salvarComentario()`, `cancelarEdicao()`, `removerComentario()`, `mensagemExibida()`.

**Layout (rolável)**

- **TopAppBar:** seta de voltar, título curto da obra e ícone de favoritar (coração) como ação.
- **Imagem** grande da obra (ou placeholder).
- **Bloco "A obra":** título, técnica, **quando foi feita** (`periodo`), **local**.
- **Bloco "O artista":** nome do artista (`artista`).
- **Bloco "História":** texto de `historia`.
- **Bloco "Meu comentário":**
  - *Sem comentário:* texto de apoio "Você ainda não comentou esta obra" e botão **"Adicionar comentário"**.
  - *Com comentário:* o texto do comentário e botões **"Editar"** e **"Remover"**.
  - *Em edição:* `OutlinedTextField` multilinha com contador de caracteres (máx. 500), botões **"Salvar"** e **"Cancelar"**.

**Regras do comentário**

- O comentário é **opcional**: a obra pode existir para sempre sem ele.
- Um comentário por obra, criado, editado e removido nesta tela.
- Salvar só é permitido com texto não vazio (após `trim`); com o campo em branco o botão "Salvar" fica desabilitado. Para apagar, o usuário usa **"Remover"**.
- Limite de 500 caracteres; ao passar, `erroComentario` mostra a mensagem e impede salvar.

**Estados**

| Situação | O que aparece |
|---|---|
| Carregando | Indicador de progresso |
| Obra não encontrada (id inválido) | Mensagem "Obra não encontrada" e botão de voltar |
| Comentário salvo | Snackbar "Comentário salvo" |
| Comentário removido | Snackbar "Comentário removido" |
| Favoritou/desfavoritou | Ícone da top bar muda na hora |

**Critérios de aceite**

- Mostra artista, dados da obra, período e história da obra selecionada.
- É possível adicionar, editar e remover o comentário; o texto continua lá após fechar e reabrir o app.
- O favorito marcado aqui aparece refletido na listagem.

### 6.4 Cadastro de obra

**ViewModel:** `ObraFormViewModel`

```kotlin
data class ObraFormUiState(
    val titulo: String = "",
    val artista: String = "",
    val historia: String = "",
    val periodo: String = "",
    val local: String = "",
    val tecnica: String = "",
    val erroTitulo: String? = null,
    val erroArtista: String? = null,
    val erroHistoria: String? = null,
    val salvando: Boolean = false,
    val cadastrada: Boolean = false         // dispara a volta para a listagem
)
```

**Campos**

| Campo (rótulo) | Obrigatório | Regra de validação | Mensagem de erro |
|---|---|---|---|
| Nome da obra | **sim** | Não vazio após `trim`; máx. 80 caracteres | "Informe o nome da obra" |
| Autor | **sim** | Não vazio após `trim`; máx. 60 caracteres | "Informe o autor" |
| História | **sim** | Não vazia após `trim`; máx. 1.000 caracteres | "Conte a história da obra" |
| Quando foi feita | não | Texto livre; máx. 40 caracteres | — |
| Bairro / endereço | não | Texto livre; máx. 120 caracteres | — |
| Técnica | não | Texto livre; máx. 40 caracteres | — |

O comentário **não** faz parte do cadastro; é adicionado depois, no detalhe.

**Layout**

- **TopAppBar:** seta de voltar e título "Nova obra".
- Formulário rolável com `OutlinedTextField` (campos obrigatórios marcados com `*`, `isError` e `supportingText` com a mensagem), `imePadding()` para o teclado.
- Botão **"Salvar"** na parte inferior (ou na top bar). Sem bottom bar.

**Comportamento**

1. Ao tocar em "Salvar", a ViewModel valida os três campos obrigatórios. Se algum for inválido, preenche os erros e **não** grava.
2. Se tudo estiver válido, cria `Obra(favorita = false, comentario = null, criadaPeloUsuario = true, imagemNome = null, ...)` com os textos já com `trim`, chama `repository.cadastrar(...)` e marca `cadastrada = true`.
3. Os erros de um campo somem assim que o usuário o edita.
4. A tela volta à listagem com o resultado descrito na seção 5.4.

**Critérios de aceite**

- Não é possível cadastrar com nome, autor ou história vazios (ou só com espaços); cada campo mostra sua mensagem.
- Após cadastrar, a nova obra aparece na listagem, com o placeholder de imagem, e continua lá após reabrir o app.

---

## 7. Interface e identidade visual

- **Material 3** com tema próprio em `ui/theme` (`ColorScheme`, tipografia).
- **Paleta definida:**

| Hex | Nome | Papel no tema (Material 3) |
|---|---|---|
| `#E6EED6` | Beige | `background` |
| `#DDE2C6` | Beige | `surface` / `surfaceVariant` (cards, barras) |
| `#BBC5AA` | Ash Grey | `secondaryContainer` (chip de filtro selecionado, indicador da bottom bar), bordas e divisores |
| `#A72608` | Oxidized Iron | `primary` (botões, coração de favorita, destaques) |
| `#090C02` | Pitch Black | `onBackground` / `onSurface` (textos) |

  `onPrimary` usa `#E6EED6` (ou branco) sobre `#A72608`. Contrastes calculados: `#090C02` sobre `#E6EED6` ≈ 16:1, `#090C02` sobre `#BBC5AA` ≈ 11:1 e `#E6EED6` sobre `#A72608` ≈ 6:1, todos acima do mínimo de 4,5:1 para texto.
- **Tema escuro:** fora do escopo. O app usa apenas o tema claro, com `ColorScheme` fixo (sem cores dinâmicas), para manter a identidade da paleta.
- Todos os textos em `strings.xml` (português).
- Acessibilidade: `contentDescription` nos ícones, áreas de toque de pelo menos 48 dp e contraste legível.
- Componentes compartilhados em `ui/components`: `ObraCard`, `EstadoVazio` (ícone + mensagem), `FiltroObras` (chips), `ImagemObra` (usa `ImagemUtil`).

---

## 8. Requisitos não funcionais

- Funciona **offline** e sem permissões especiais.
- Projeto compila e roda no Android Studio e no emulador.
- A rotação de tela e a recriação da Activity não perdem o estado (ViewModel + `SavedStateHandle`; texto do formulário e rascunho de comentário em `StateFlow` dentro da ViewModel).
- Operações de banco fora da thread principal (Room com `suspend`/`Flow`).

---

## 9. Verificação e evidências para o README

### 9.1 Roteiro de teste manual

1. Instalar e abrir o app pela primeira vez → a listagem mostra as 5 obras.
2. Favoritar duas obras → filtrar por "Favoritas" → aparecem só as duas.
3. Abrir uma obra → adicionar comentário → salvar → editar → remover → adicionar de novo.
4. Cadastrar uma obra nova deixando um campo obrigatório vazio → ver o erro; preencher e salvar → aparece na lista.
5. **Fechar o app por completo e reabrir** → favoritos, comentário e a obra nova continuam lá.

### 9.2 Prints obrigatórios (rubrica, item 7)

- Home.
- Listagem (com favoritos marcados) e filtro "Favoritas".
- Detalhe com comentário.
- Cadastro com erro de validação e cadastro bem-sucedido.
- **Criação de um registro** (obra nova na lista).
- **Ação adicional** (favoritar e/ou comentar).
- **Permanência após reabrir o app** (print antes e depois).

### 9.3 Conteúdo do `README.md`

- Nome do app, problema, público beneficiado e proposta.
- ODS escolhido e a justificativa.
- Funcionalidades.
- Tecnologias e arquitetura (MVVM, com o diagrama da seção 3).
- Instruções para executar o app.
- Fluxo principal explicado e prints reais.
- **Nome completo e RM de cada integrante** (1 a 3 pessoas).

### 9.4 Entrega

- Projeto completo em `.zip`, sem pastas `build/` e sem arquivos locais (`local.properties`, `.idea` desnecessário).
- `README.md` dentro do projeto.
- Vídeo pitch conforme as orientações gerais da Global Solution.

---

## 10. Ordem sugerida de implementação

1. Projeto base, Hilt, tema e `strings.xml`.
2. `Obra`, `ObraDao`, `AppDatabase` e seed → confirmar as 5 obras no banco.
3. `ObraRepository`, módulos Hilt.
4. Navegação, `Scaffold` e bottom bar com telas vazias.
5. Listagem (`ObraListViewModel`, `ObraCard`, filtro, favoritar).
6. Detalhe (`ObraDetailViewModel`, comentário).
7. Cadastro (`ObraFormViewModel`, validação).
8. Home.
9. Fotos reais em `drawable-nodpi`, prints e README.

---

## 11. Decisões a confirmar

- ODS 11 como ODS do projeto.
- Nome do pacote base, `minSdk` e `compileSdk`.
- As 5 obras do seed (3 são de Eduardo Kobra) e a conferência dos dados.
- Obras cadastradas pelo usuário usam o placeholder de imagem.
- Salvar comentário em branco fica desabilitado (para apagar, usa-se "Remover").
- Técnica como texto livre.
