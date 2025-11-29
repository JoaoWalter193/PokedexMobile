package com.example.agendapokemon

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.agendapokemon.Interface.ApiInterface
import com.example.agendapokemon.Model.PokemonInserir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CadastrarPokemonActivity : AppCompatActivity() {


    private lateinit var textNome: TextView
    private lateinit var textTipo: TextView
    private lateinit var textHabilidade1: TextView
    private lateinit var textHabilidade2: TextView
    private lateinit var textHabilidade3: TextView

    private lateinit var api: ApiInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastrar_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textNome = findViewById(R.id.editTextNomePokemon)
        textTipo = findViewById(R.id.editTextTipoPokemon)
        textHabilidade1 = findViewById(R.id.editTextHabilidade1)
        textHabilidade2 = findViewById(R.id.editTextHabilidade2)
        textHabilidade3 = findViewById(R.id.editTextHabilidade3)


        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiInterface::class.java)


    }


    fun cadastrarPokemon(view: View){

        val nomePokemon = textNome.text.toString()
        val tipoPokemon = textTipo.text.toString()
        val hab1 = textHabilidade1.text.toString()
        val hab2 = textHabilidade2.text.toString()
        val hab3 = textHabilidade3.text.toString()


        // validar se está preenchido
        if (nomePokemon.isEmpty() || tipoPokemon.isEmpty()){
            Toast.makeText(this, "Preencha Nome e Tipo", Toast.LENGTH_SHORT).show()
            return
        }

        if (hab1.isEmpty() && hab2.isEmpty() && hab3.isEmpty() ){
            Toast.makeText(this, "Preencha pelo menos uma habilidade", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val habilidades = listOf(hab1, hab2, hab3).filter { it.isNotEmpty() }
            val cadastroPokemon = PokemonInserir(nomePokemon,tipoPokemon,habilidades, UsuarioLogado.nomeUsuario)
                val response = withContext(Dispatchers.IO){
                    api.cadastrarPokemon(cadastroPokemon)
                }
                Toast.makeText(this@CadastrarPokemonActivity, "Pokemon: ${nomePokemon} cadastrado com sucesso!!",
                    Toast.LENGTH_SHORT).show()

                textNome.setText("")
                textTipo.setText("")
                textHabilidade1.setText("")
                textHabilidade2.setText("")
                textHabilidade3.setText("")
                // achei que fica mais "clean" dar o finish
                finish()

            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> Toast.makeText(
                        this@CadastrarPokemonActivity,
                        "Pokémon já existe ou dados inválidos",
                        Toast.LENGTH_SHORT
                    ).show()

                    500 -> Toast.makeText(
                        this@CadastrarPokemonActivity,
                        "Erro no servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }
}