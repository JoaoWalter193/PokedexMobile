package com.example.agendapokemon

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendapokemon.Adapter.PokemonAdapter
import com.example.agendapokemon.Interface.ApiInterface
import com.example.agendapokemon.Model.PokemonItem
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListaPokemonsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var api: ApiInterface
    private lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pokemons)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recycler = findViewById(R.id.recyclerPokemons)
        recycler.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiInterface::class.java)

        adapter = PokemonAdapter(listOf()) { item ->
            val i = Intent(this, DetalhesPokemonActivity::class.java)
            i.putExtra("id", item.id)
            startActivity(i)
        }
        recycler.adapter = adapter


        carregarLista()


    }

    override fun onResume() {
        super.onResume()
        carregarLista()
    }


    private fun carregarLista() {
        lifecycleScope.launch {
            try {
                val lista = api.listarTodos()
                val listaItem = lista.map { PokemonItem(id = it.id, nome = it.nome) }
                adapter.update(listaItem)
            } catch(e: Exception) {
                Toast.makeText(this@ListaPokemonsActivity, "Erro ao carregar lista", Toast.LENGTH_SHORT).show()
            }
        }
    }
}