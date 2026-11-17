package com.github.cesarb98.artenarua.data

/** Obras iniciais, inseridas uma única vez na criação do banco. */
object ObraSeed {
    val obras: List<Obra> = listOf(
        Obra(
            titulo = "Beco do Batman",
            artista = "Vários artistas",
            historia = "Galeria a céu aberto no coração da Vila Madalena. O nome vem de um " +
                "grafite do Batman que, segundo a história contada no bairro, apareceu nos " +
                "muros nos anos 1980. O desenho original não existe mais, e os muros são " +
                "repintados com frequência, então cada visita mostra algo novo.",
            periodo = "Desde os anos 1980",
            local = "Vila Madalena (Zona Oeste), entre as ruas Gonçalo Afonso e Medeiros de Albuquerque",
            tecnica = "Grafite",
            imagemNome = "obra_beco_do_batman"
        ),
        Obra(
            titulo = "Ayrton Senna — A Lenda do Brasil",
            artista = "Eduardo Kobra",
            historia = "Homenagem ao piloto tricampeão de Fórmula 1 na esquina da Av. Paulista " +
                "com a Rua da Consolação, pintada por Eduardo Kobra com apoio da Audi do " +
                "Brasil, em parceria com o Instituto Ayrton Senna. Foi revitalizada no fim " +
                "de 2020.",
            periodo = "2015 (revitalizado em 2020)",
            local = "Esquina da Av. Paulista com a Rua da Consolação (Centro)",
            tecnica = "Mural",
            imagemNome = "obra_senna_lenda_brasil"
        ),
        Obra(
            titulo = "Muro das Memórias",
            artista = "Eduardo Kobra",
            historia = "Um dos maiores murais de Eduardo Kobra, com cerca de 1.000 m², pintado " +
                "na Av. 23 de Maio em 2009 para o aniversário da cidade, dentro do projeto " +
                "Muros da Memória, que resgata a história de São Paulo.",
            periodo = "2009",
            local = "Av. 23 de Maio, perto do Viaduto Tutóia (Zona Sul/Centro)",
            tecnica = "Mural",
            imagemNome = "obra_muro_das_memorias"
        ),
        Obra(
            titulo = "Histórias em Nossas Memórias",
            artista = "Coletivos Arte e Cultura na Kebrada, BocaSujaCrew e Sou Favela",
            historia = "Mural de 2021 criado por artistas de coletivos da própria região, a " +
                "partir de uma pesquisa sobre a história de São Miguel Paulista, um dos " +
                "bairros mais antigos da cidade, para que quem passa pelo local conheça " +
                "essa história.",
            periodo = "2021",
            local = "Rua Corinthians, Vila Nova União, São Miguel Paulista (Zona Leste)",
            tecnica = "Mural / grafite",
            imagemNome = "obra_historias_nossas_memorias"
        ),
        Obra(
            titulo = "Nós Somos São Paulo",
            artista = "Eduardo Kobra",
            historia = "Mural de Eduardo Kobra no Autódromo de Interlagos, com cerca de " +
                "2.000 m² (225 m por 8,17 m), que reúne Ayrton Senna, trabalhadores de " +
                "diferentes áreas e marcos históricos e arquitetônicos da cidade.",
            periodo = "2026",
            local = "Autódromo de Interlagos (Zona Sul)",
            tecnica = "Mural",
            imagemNome = "obra_nos_somos_sao_paulo"
        )
    )
}
