package com.example.agendapokemon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.agendapokemon.Interface.ApiInterface
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DashboardActivity : AppCompatActivity() {


    private lateinit var textCadastrado: TextView
    private lateinit var textTipo: TextView
    private lateinit var textHabilidade: TextView

    private lateinit var api: ApiInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)

        textCadastrado = findViewById(R.id.textViewCadastrado)
        textHabilidade = findViewById(R.id.textViewHabilidade)
        textTipo = findViewById(R.id.textViewTipo)


        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiInterface::class.java)

        carregarDashboard()

    }

    // menuzinho bala
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_item, menu)
        return true
    }

    // opções do menuzinho bala
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_cadastrar -> {
                startActivity(Intent(this, CadastrarPokemonActivity::class.java))
            }

            R.id.menu_listar -> {
                startActivity(Intent(this, ListaPokemonsActivity::class.java))
            }

            R.id.menu_pesquisar_tipo -> {
                startActivity(Intent(this, PesquisarPorTipoActivity::class.java))
            }

            R.id.menu_pesquisar_habilidade -> {
                startActivity(Intent(this, PesquisarPorHabilidadeActivity::class.java))
            }

            R.id.menu_sair -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        carregarDashboard()
    }

    private fun carregarDashboard() {

        lifecycleScope.launch {
            try {

                val response = api.getStats()

                textCadastrado.setText(response.totalPokemons.toString())

                textTipo.text = response.topTipos.joinToString("\n") {
                    "${it.tipo} (${it.quantidade})"
                }

                textHabilidade.text = response.topHabilidades.joinToString("\n") {
                    "${it.habilidade} (${it.quantidade})"
                }

            } catch (e: Exception){
                Log.e("DashboardActivity", "Erro na request dashboard")
            }
        }

    }

}