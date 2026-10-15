package com.example.flixsterplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val movieAdapter = MovieAdapter(::openDetails)
    private val showAdapter = ShowAdapter(::openDetails)

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.mainRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)

        // The LayoutManager must be set BEFORE inflating the header into this RecyclerView.
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Header = "Popular TV Shows" title + horizontal RecyclerView + "Popular Movies" title.
        val header = layoutInflater.inflate(R.layout.main_header, recyclerView, false)
        header.findViewById<RecyclerView>(R.id.showsRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = showAdapter
        }

        recyclerView.adapter = ConcatAdapter(HeaderAdapter(header), movieAdapter)

        retryButton.setOnClickListener { loadData() }
        loadData()
    }

    /** Calls both TMDB endpoints at the same time and fills both RecyclerViews. */
    private fun loadData() {
        if (BuildConfig.TMDB_API_KEY.isBlank()) {
            showError(getString(R.string.error_missing_key))
            return
        }
        showLoading()
        lifecycleScope.launch {
            try {
                val movies = async { TmdbClient.popularMovies() }
                val shows = async { TmdbClient.popularTvShows() }
                movieAdapter.submit(movies.await())
                showAdapter.submit(shows.await())
                showContent()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load data", e)
                showError(getString(R.string.error_loading))
            }
        }
    }

    /** Opens DetailActivity with an Intent and a shared element transition on the poster. */
    private fun openDetails(item: MediaItem, poster: ImageView) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ITEM, item)
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, poster, ViewCompat.getTransitionName(poster) ?: ""
        )
        startActivity(intent, options.toBundle())
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorText.visibility = View.GONE
        retryButton.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        errorText.text = message
        errorText.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }

    /** Shows a single fixed view as the first row of the main list. */
    private class HeaderAdapter(private val header: View) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(header) {}.apply { setIsRecyclable(false) }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit

        override fun getItemCount() = 1
    }
}