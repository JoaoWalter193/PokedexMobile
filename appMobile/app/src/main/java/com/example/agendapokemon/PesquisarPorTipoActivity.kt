package com.example.agendapokemon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class PesquisarPorTipoActivity : AppCompatActivity() {

    private lateinit var api: ApiInterface
    private lateinit var adapter: PokemonAdapter

    private lateinit var editTipo: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var btnPesquisar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pesquisar_por_tipo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTipo = findViewById(R.id.editPesquisarTipo)
        btnPesquisar = findViewById(R.id.btnPesquisarTipo)
        recycler = findViewById(R.id.recyclerPesquisaTipo)

        recycler.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiInterface::class.java)

        adapter = PokemonAdapter(listOf()) { item ->
            // 🔥 Clique abre detalhes normalmente
            val i = Intent(this, DetalhesPokemonActivity::class.java)
            i.putExtra("id", item.id)
            startActivity(i)
        }

        recycler.adapter = adapter

        btnPesquisar.setOnClickListener {
            pesquisar()
        }
    }

    private fun pesquisar() {
        val tipo = editTipo.text.toString().trim()

        if (tipo.isEmpty()) {
            Toast.makeText(this, "Digite um tipo!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val lista = api.pesquisarTipo(tipo)

                if (lista.isEmpty()) {
                    Toast.makeText(this@PesquisarPorTipoActivity, "Nenhum Pokémon encontrado", Toast.LENGTH_SHORT).show()
                }

                val listaItem = lista.map { pokemon ->
                    PokemonItem(
                        id = pokemon.id,
                        nome = pokemon.nome
                    )
                }

                adapter.update(listaItem)

            } catch (e: Exception) {
                Toast.makeText(this@PesquisarPorTipoActivity, "Erro ao pesquisar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}