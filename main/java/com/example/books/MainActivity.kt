package com.example.books

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val genres = listOf("Фантастика", "Детективы", "Романы", "Ужасы", "Приключения", "Научпоп", "Фэнтези", "Историческая проза", "Триллеры", "Романтика", "Мистика", "Научная фантастика", "Сатирические романы", "Классическая литература", "Современная проза", "Автобиографии", "Биографии", "Поэзия", "Детская литература", "Юмористическая проза", "Социальная проза", "Криминальная проза", "Эссеистика", "Психологические романы", "Спортивные книги", "Антиутопия", "Утопия", "Путешествия", "Комиксы", "Графические новеллы", "Дорожные книги", "Сказки", "Легенды", "Мифы", "Нон-фикшн", "Философская проза", "ЛитРПГ", "Киберпанк", "Стимпанк", "Приключенческая проза", "Агата-детектив", "Фантастический детектив", "Приключенческий роман", "Технологический триллер", "Научные исследования", "Философия", "Драма")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Создаем RecyclerView
        val recyclerView = RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Создаем адаптер прямо здесь
        recyclerView.adapter = object : RecyclerView.Adapter<GenreViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
                val button = Button(parent.context).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        200 // высота кнопки
                    )
                }
                return GenreViewHolder(button)
            }

            override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
                holder.button.text = genres[position]
                holder.button.setOnClickListener {
                    showBooksActivity(genres[position])
                }
            }

            override fun getItemCount() = genres.size
        }

        setContentView(recyclerView)
    }

    class GenreViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    private fun showBooksActivity(genre: String) {
        val intent = Intent(this, BooksActivity::class.java)
        intent.putExtra("GENRE", genre)  // Передаем выбранный жанр
        startActivity(intent)
    }

}