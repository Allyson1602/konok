package com.example.konok.ui.myBooks

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konok.R
import com.example.konok.ui.home.Book
import com.example.konok.ui.home.BooksAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OwnedBooksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_books_list, container, false)

        recyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        booksAdapter = BooksAdapter {}
        recyclerView.adapter = booksAdapter

        sharedPreferences = requireContext().getSharedPreferences("BookLists", Context.MODE_PRIVATE)

        return root
    }

    override fun onResume() {
        super.onResume()
        loadBooks("owned_list")
    }

    fun loadBooks(listName: String) {
        val bookJsons = sharedPreferences.getStringSet(listName, emptySet()) ?: emptySet()

        val books = bookJsons.mapNotNull { json ->
            try {
                val jsonObject = JSONObject(json)

                val id = jsonObject.optString("id", "")
                val etag = jsonObject.optString("etag", "")
                val title = jsonObject.optString("title", "")

                val authorsJsonArray = jsonObject.optJSONArray("authors") ?: JSONArray()
                val authors = List(authorsJsonArray.length()) { index ->
                    authorsJsonArray.optString(index, "")
                }

                val publishedDate = jsonObject.optString("publishedDate", "")

                Book(id, etag, title, authors, publishedDate)
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }

        booksAdapter.updateBooks(books)
    }
}
