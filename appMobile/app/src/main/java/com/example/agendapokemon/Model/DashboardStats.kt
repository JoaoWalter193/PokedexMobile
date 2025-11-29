package com.example.agendapokemon.Model

data class DashboardStats(
    val totalPokemons: Int,
    val topTipos: List<TipoCount>,
    val topHabilidades: List<HabilidadeCount>
)

data class TipoCount(
    val tipo: String,
    val quantidade: Int
)

data class HabilidadeCount(
    val habilidade: String,
    val quantidade: Int
)
