package com.example.konok.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konok.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        searchInput = root.findViewById(R.id.search_input)
        searchButton = root.findViewById(R.id.search_button)
        recyclerView = root.findViewById(R.id.recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        booksAdapter = BooksAdapter { book -> showBookOptionsDialog(book) }
        recyclerView.adapter = booksAdapter

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
            }
        }

        sharedPreferences = requireContext().getSharedPreferences("BookLists", Context.MODE_PRIVATE)

        return root
    }

    private fun searchBooks(query: String) {
        val client = OkHttpClient()
        val url = "https://www.googleapis.com/books/v1/volumes?q=$query"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val jsonObject = JSONObject(json)
                    val itemsArray = jsonObject.getJSONArray("items")

                    val books = mutableListOf<Book>()

                    for (i in 0 until itemsArray.length()) {
                        val item = itemsArray.getJSONObject(i)
                        val volumeInfo = item.getJSONObject("volumeInfo")

                        val title = volumeInfo.getString("title")
                        val authors = volumeInfo.optJSONArray("authors")
                        val authorList = mutableListOf<String>()

                        if (authors != null) {
                            for (j in 0 until authors.length()) {
                                authorList.add(authors.getString(j))
                            }
                        }

                        val publishedDate = volumeInfo.optString("publishedDate", "N/A")
                        val id = item.getString("id")
                        val etag = item.getString("etag")

                        books.add(Book(id, etag, title, authorList, publishedDate))
                    }

                    activity?.runOnUiThread {
                        booksAdapter.updateBooks(books)
                    }
                }
            }
        })
    }

    private fun showBookOptionsDialog(book: Book) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.book_options_modal, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Escolha uma opção")

        val addToReadListButton = dialogView.findViewById<Button>(R.id.add_to_read_list)
        val addToOwnedListButton = dialogView.findViewById<Button>(R.id.add_to_owned_list)

        addToReadListButton.setOnClickListener {
            addBookToList("read_list", book)
            Toast.makeText(requireContext(), "Livro adicionado à lista de lidos", Toast.LENGTH_SHORT).show()
        }

        addToOwnedListButton.setOnClickListener {
            addBookToList("owned_list", book)
            Toast.makeText(requireContext(), "Livro adicionado à lista de possuídos", Toast.LENGTH_SHORT).show()
        }

        builder.create().show()
    }

    private fun addBookToList(listName: String, book: Book) {
        val editor = sharedPreferences.edit()

        val bookList = sharedPreferences.getStringSet(listName, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val bookJson = JSONObject().apply {
            put("id", book.id)
            put("etag", book.etag)
            put("title", book.title)
            put("authors", JSONArray(book.authors))
            put("publishedDate", book.publishedDate)
        }.toString()

        bookList.add(bookJson)

        editor.putStringSet(listName, bookList)
        editor.apply()
    }

}

data class Book(
    val id: String,
    val etag: String,
    val title: String,
    val authors: List<String>,
    val publishedDate: String
)
