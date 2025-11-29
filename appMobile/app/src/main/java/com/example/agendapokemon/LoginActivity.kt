package com.example.agendapokemon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.agendapokemon.Interface.ApiInterface
import com.example.agendapokemon.Model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var textNome: EditText
    private lateinit var textSenha: EditText

    private lateinit var btnLogin: Button

    private lateinit var api: ApiInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textNome = findViewById(R.id.editTextNome)
        textSenha = findViewById(R.id.editTextSenha)
        btnLogin = findViewById(R.id.button)


        val retrofit = Retrofit.Builder()
            .baseUrl(ApiUrl.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiInterface::class.java)
    }


    fun validarLogin (view: View){
        val nome = textNome.text.toString()
        val senha = textSenha.text.toString()

        if (nome.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Digite nome e senha", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try{
                val request = LoginRequest(nome,senha)
                val loginResponse = withContext(Dispatchers.IO){
                    api.login(request)
                }
                println(loginResponse.toString())
                UsuarioLogado.nomeUsuario = nome
                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    startActivity(intent)

            } catch (e: HttpException){

                when (e.code()) {
                    500 -> {Log.e("Login Activitty","Erro na requisição" + e.toString())
                    Toast.makeText(this@LoginActivity, "Erro Interno de Servidor", Toast.LENGTH_SHORT).show()}

                    401 -> {Log.e("Login Activitty","Erro na requisição" + e.toString())
                        Toast.makeText(this@LoginActivity, "Nome e senha incorretos", Toast.LENGTH_SHORT).show()
                    }

                }


            }
        }


    }


}