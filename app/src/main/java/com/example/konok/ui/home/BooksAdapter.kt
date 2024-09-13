package com.example.konok.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.konok.R

class BooksAdapter(
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    private var books = listOf<Book>()

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int = books.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.book_title)
        private val authorsTextView: TextView = itemView.findViewById(R.id.book_authors)
        private val dateTextView: TextView = itemView.findViewById(R.id.book_published_date)

        fun bind(book: Book) {
            titleTextView.text = book.title
            authorsTextView.text = book.authors.joinToString(", ")
            dateTextView.text = book.publishedDate

            itemView.setOnClickListener {
                onBookClick(book)
            }
        }
    }
}
