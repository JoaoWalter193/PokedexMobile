package com.example.agendapokemon.Model

data class PokemonInserir(
    val nome: String,
    val tipo: String,
    val habilidades: List<String>,
    val usuario_login: String
)
