package com.example.agendapokemon.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendapokemon.Model.Pokemon
import com.example.agendapokemon.Model.PokemonItem
import com.example.agendapokemon.R


class PokemonAdapter(
    private var lista: List<PokemonItem>,
    private val onClick: (PokemonItem) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomeText = view.findViewById<TextView>(R.id.textNomeItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nomeText.text = item.nome

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun update(nova: List<PokemonItem>) {
        lista = nova
        notifyDataSetChanged()
    }
}

