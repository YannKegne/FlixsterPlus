package com.example.flixsterplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

/** Vertical list of popular movies. */
class MovieAdapter(
    private val onClick: (MediaItem, ImageView) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private val items = mutableListOf<MediaItem>()

    fun submit(newItems: List<MediaItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.moviePoster)
        val title: TextView = view.findViewById(R.id.movieTitle)
        val rating: TextView = view.findViewById(R.id.movieRating)
        val overview: TextView = view.findViewById(R.id.movieOverview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.title.text = item.title
        holder.overview.text = item.overview
        holder.rating.text = context.getString(
            R.string.rating_format,
            String.format(Locale.getDefault(), "%.1f", item.rating),
            item.year
        )

        // Each poster needs a unique transition name for the shared element animation.
        ViewCompat.setTransitionName(holder.poster, "poster_movie_${item.id}")

        Glide.with(context)
            .load(item.posterUrl)
            .placeholder(R.drawable.poster_placeholder)
            .error(R.drawable.poster_error)
            .transform(CenterCrop(), RoundedCorners(context.dp(12)))
            .into(holder.poster)

        holder.itemView.setOnClickListener { onClick(item, holder.poster) }
    }

    override fun getItemCount() = items.size
}
