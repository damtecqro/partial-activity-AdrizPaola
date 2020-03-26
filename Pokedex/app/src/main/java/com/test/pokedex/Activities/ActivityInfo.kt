package com.test.pokedex.Activities

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.R
import java.util.*

class ActivityInfo : AppCompatActivity() {

    private var context: Context = this
    private var auxID: String = "0"
    private lateinit var data: JsonObject
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var imagePokemon: ImageView
    private lateinit var namePokemon: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        manageIntent()
        initializeComponents()
        initializeData()
    }

    private fun initializeComponents() {
        imagePokemon = findViewById(R.id.pokemon_detail_image)
        namePokemon = findViewById(R.id.pokemon_detail_name)
    }

    private fun manageIntent() {
        if (intent != null)
            auxID = intent.getStringExtra("auxID")
    }

    private fun addTextView(innerText: String, parentID: Int) {
        val linearLayout: LinearLayout = findViewById(parentID)

        var type = TextView(this)

        type.gravity = Gravity.CENTER
        type.textSize = 18f
        type.setTextColor(Color.WHITE)
        type.text = innerText

        linearLayout.addView(type)
    }

    private fun initializeData() {


        Ion.with(context)
            .load("https://pokeapi.co/api/v2/pokemon/" + auxID + "/")
            .asJsonObject()
            .done { e, result ->
                if (e == null) {

                    data = result
                    if (!data.get("sprites").isJsonNull) {

                        if (data.get("sprites").asJsonObject.get("front_default") != null) {
                            Glide
                                .with(context)
                                .load(data.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .error(R.drawable.pokemon_logo_min)
                                .into(imagePokemon)

                        } else {
                            imagePokemon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pokemon_logo_min))
                        }
                    } else {

                        imagePokemon.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.pokemon_logo_min
                            )
                        )

                    }

                    if (data.get("name") != null) {
                        val nombre: String = "#$auxID: " + (this.data.get("name").toString().toUpperCase(Locale.ROOT))


                        namePokemon.text = nombre
                    }

                    if (!data.get("types").isJsonNull) {

                        val tipos = data.get("types").asJsonArray
                        for (i in 0.until(tipos.size())) {

                            val item = tipos.get(i).asJsonObject.get("type").asJsonObject
                            addTextView(item.get("name").toString().toUpperCase(Locale.ROOT), R.id.TypesLayout)

                        }

                    }

                    if (!data.get("stats").isJsonNull) {
                        val estadisticas = data.get("stats").asJsonArray
                        for (i in 0.until(estadisticas.size())) {
                            val estadisticaBase = estadisticas.get(i).asJsonObject.get("base_stat")
                            val estadisticaNombre = estadisticas.get(i).asJsonObject.get("stat").asJsonObject.get("name")

                            val result = estadisticaNombre.toString().toUpperCase(Locale.ROOT) + ": " + estadisticaBase.toString()
                            addTextView(result, R.id.StatsLayout)

                        }
                    }

                    if (!data.get("moves").isJsonNull) {

                        val movimientos = data.get("moves").asJsonArray



                        for (i in 0.until(movimientos.size())) {
                            val move_name = movimientos.get(i).asJsonObject.get("move").asJsonObject.get("name")

                            val result =
                                move_name.toString().toUpperCase(Locale.ROOT)
                            addTextView(result, R.id.MovesLayout)
                        }
                    }


                }
                initializeList()

            }


    }

    private fun initializeList() {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

    }

}
