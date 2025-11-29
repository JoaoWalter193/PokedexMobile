package com.example.agendapokemon.Model

data class Pokemon(
    val id: Int,
    val nome: String,
    val tipo: String,
    val habilidades: List<String>,
    val usuario_login: String
)
