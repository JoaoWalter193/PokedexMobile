package com.example.agendapokemon

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.agendapokemon.Interface.ApiInterface
import com.example.agendapokemon.Model.EditarPokemon
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetalhesPokemonActivity : AppCompatActivity() {

    private lateinit var textNome: TextView
    private lateinit var editTipo: EditText
    private lateinit var editHab1: EditText
    private lateinit var editHab2: EditText
    private lateinit var editHab3: EditText

    private lateinit var api: ApiInterface


    private var pokemonId = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhes_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pokemonId = intent.getIntExtra("id", 0)


        textNome = findViewById(R.id.textViewPokemonNome)
        editTipo = findViewById(R.id.editTextTextTipoPokemon)
        editHab1 = findViewById(R.id.editTextTextHab1)
        editHab2 = findViewById(R.id.editTextTextHab2)
        editHab3 = findViewById(R.id.editTextTextHab3)


        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiInterface::class.java)

        carregarPokemon()

    }

    fun carregarPokemon() {
        lifecycleScope.launch {
            try {
                val p = api.getPokemon(pokemonId)

                textNome.text = p.nome
                editTipo.setText(p.tipo)

                // Garante que não quebra caso tenha menos habilidades
                editHab1.setText(p.habilidades.getOrNull(0) ?: "")
                editHab2.setText(p.habilidades.getOrNull(1) ?: "")
                editHab3.setText(p.habilidades.getOrNull(2) ?: "")

            } catch (e: Exception) {
                Toast.makeText(this@DetalhesPokemonActivity, "Erro ao carregar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun salvar(view: View) {
        lifecycleScope.launch {
            try {

                val habilidades = listOf(
                    editHab1.text.toString(),
                    editHab2.text.toString(),
                    editHab3.text.toString()
                ).filter { it.isNotBlank() } // Remove campos vazios

                val body = EditarPokemon(
                    tipo = editTipo.text.toString(),
                    habilidades = listOf(
                        editHab1.text.toString(),
                        editHab2.text.toString(),
                        editHab3.text.toString()
                    ).filter { it.isNotBlank() }
                )

                api.editarPokemon(pokemonId, body)

                Toast.makeText(this@DetalhesPokemonActivity, "${textNome.text.toString()} atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@DetalhesPokemonActivity, "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun excluir(view: View) {
        lifecycleScope.launch {
            try {
                api.excluirPokemon(pokemonId)
                Toast.makeText(this@DetalhesPokemonActivity, "${textNome.text.toString()} excluído!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@DetalhesPokemonActivity, "Erro ao excluir", Toast.LENGTH_SHORT).show()
            }
        }
    }


}