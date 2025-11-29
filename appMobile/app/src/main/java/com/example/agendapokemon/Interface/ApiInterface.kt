package com.example.agendapokemon.Interface

import com.example.agendapokemon.Model.DashboardStats
import com.example.agendapokemon.Model.EditarPokemon
import com.example.agendapokemon.Model.LoginRequest
import com.example.agendapokemon.Model.Pokemon
import com.example.agendapokemon.Model.PokemonInserir
import com.example.agendapokemon.Model.PokemonItem
import com.example.agendapokemon.Model.RespostaApi
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiInterface {


    @POST("login")
    suspend fun login(@Body body: LoginRequest): RespostaApi

    @GET("dashboard/stats")
    suspend fun getStats(): DashboardStats

    @GET("pokemons")
    suspend fun listarTodos(): List<Pokemon>

    @POST("pokemons")
    suspend fun cadastrarPokemon(@Body pokemon: PokemonInserir): RespostaApi

    @GET("pokemons/{id}")
    suspend fun getPokemon(@Path("id") id: Int): Pokemon

    @PUT("pokemons/{id}")
    suspend fun editarPokemon(
        @Path("id") id: Int,
        @Body body: EditarPokemon
    ): RespostaApi

    @DELETE("pokemons/{id}")
    suspend fun excluirPokemon(@Path("id") id: Int): RespostaApi

    @GET("pokemons/tipo/{tipo}")
    suspend fun pesquisarTipo(@Path("tipo") tipo: String): List<PokemonItem>

    @GET("pokemons/habilidade/{habilidade}")
    suspend fun pesquisarHabilidade(@Path("habilidade") habilidade: String): List<PokemonItem>
}