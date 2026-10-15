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

/** Horizontal row of popular TV shows (the second API call). */
class ShowAdapter(
    private val onClick: (MediaItem, ImageView) -> Unit
) : RecyclerView.Adapter<ShowAdapter.ViewHolder>() {

    private val items = mutableListOf<MediaItem>()

    fun submit(newItems: List<MediaItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.showPoster)
        val title: TextView = view.findViewById(R.id.showTitle)
        val rating: TextView = view.findViewById(R.id.showRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_show, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.title.text = item.title
        holder.rating.text = context.getString(
            R.string.star_rating, String.format(Locale.getDefault(), "%.1f", item.rating)
        )

        ViewCompat.setTransitionName(holder.poster, "poster_tv_${item.id}")

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
