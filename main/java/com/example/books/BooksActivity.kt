package com.example.books

import android.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BooksActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var genre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre = intent.getStringExtra("GENRE") ?: ""

        // Создаем основной контейнер
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Создаем TabLayout
        tabLayout = TabLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Создаем ViewPager2
        viewPager = ViewPager2(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Добавляем views в контейнер
        rootLayout.addView(tabLayout)
        rootLayout.addView(viewPager)

        setContentView(rootLayout)

        // Настраиваем адаптер для ViewPager
        viewPager.adapter = BooksPagerAdapter(this, genre)

        // Связываем TabLayout и ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Прочитано"
                1 -> "В планах"
                else -> "Любимое"
            }
        }.attach()
    }
}

class BooksPagerAdapter(activity: FragmentActivity, private val genre: String) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return BooksFragment.newInstance(position, genre)
    }
}

class BooksFragment : Fragment() {
    private lateinit var booksList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listView: ListView
    private lateinit var genre: String

    companion object {
        fun newInstance(position: Int, genre: String): BooksFragment {
            return BooksFragment().apply {
                arguments = Bundle().apply {
                    putInt("position", position)
                    putString("genre", genre)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        genre = arguments?.getString("genre") ?: ""
        // Создаем корневой контейнер
        val rootLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Создаем контейнер для ввода
        val inputLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
        }

        // EditText для ввода названия книги
        val editText = EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            hint = "Введите название книги"
        }

        // Кнопка добавления
        val addButton = Button(requireContext()).apply {
            text = "Добавить"
            setOnClickListener {
                val bookTitle = editText.text.toString()
                if (bookTitle.isNotEmpty()) {
                    addBook(bookTitle)
                    editText.text.clear()
                }
            }
        }

        // ListView для отображения книг
        listView = ListView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Добавляем views в контейнеры
        inputLayout.addView(editText)
        inputLayout.addView(addButton)
        rootLayout.addView(inputLayout)
        rootLayout.addView(listView)

        // Инициализируем список и адаптер
        val position = arguments?.getInt("position", 0) ?: 0
        loadBooks(position)

        return rootLayout
    }

    private fun loadBooks(position: Int) {
        val prefs = requireActivity().getSharedPreferences("Books", Context.MODE_PRIVATE)
        val key = "books_${genre}_${position}"  // Добавляем жанр в ключ
        val booksSet = prefs.getStringSet(key, setOf()) ?: setOf()
        booksList = booksSet.toMutableList()

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            booksList
        )
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            removeBook(position)
            true
        }
    }


    private fun addBook(title: String) {
        booksList.add(title)
        adapter.notifyDataSetChanged()
        saveBooks()
    }

    private fun removeBook(position: Int) {
        booksList.removeAt(position)
        adapter.notifyDataSetChanged()
        saveBooks()
    }

    private fun saveBooks() {
        val position = arguments?.getInt("position", 0) ?: 0
        val prefs = requireActivity().getSharedPreferences("Books", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("books_${genre}_${position}", booksList.toSet()).apply()  // Добавляем жанр в ключ
    }

}