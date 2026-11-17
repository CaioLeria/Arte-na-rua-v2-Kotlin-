# Arte na Rua v2 — Plano de implementação

**Spec:** `2026-11-16-arte-na-rua-v2-design.md` (seção 10 = ordem sugerida)
**Pacote base:** `com.github.cesarb98.artenarua` · minSdk 24 · compileSdk 35 · Kotlin 2.1.0 · AGP 8.8.0 · KSP · Room 2.6.1 · Hilt 2.53.1 (versões do projeto base de aula)

## Tasks (1 commit cada)

1. **Projeto base, Hilt, tema e strings** — Gradle, Application/MainActivity, tema Material 3 com a paleta da seção 7, `strings.xml`.
2. **Dados: Obra, ObraDao, AppDatabase e seed** — entidade, DAO, banco v1, `ObraSeed` (5 obras), placeholder e `ImagemUtil`.
3. **Repository e módulos Hilt** — `ObraRepository(Impl)`, `DatabaseModule` (com callback de seed), `RepositoryModule`.
4. **Navegação, Scaffold e bottom bar** — `Rotas`, `ArteNaRuaNavHost`, 4 destinos com telas vazias.
5. **Listagem** — `ObraListViewModel`, `ObraCard`, `EstadoVazio`, filtro, favoritar, Snackbar.
6. **Detalhe** — `ObraDetailViewModel`, comentário (adicionar/editar/remover).
7. **Cadastro** — `ObraFormViewModel`, validação, retorno com resultado.
8. **Home** — tela estática de apresentação.
9. **Testes unitários das ViewModels** — validação, filtro, comentário.
10. **README e prints** — documentação e evidências.

Cada task termina com `./gradlew assembleDebug` verde antes do commit.
