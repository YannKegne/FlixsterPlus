package com.example.flixsterplus

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.Locale

/** Shows the details of one movie or TV show, received through an Intent. */
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM = "extra_item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val item: MediaItem = readItem() ?: run { finish(); return }
        title = item.title

        val backdrop = findViewById<ImageView>(R.id.detailBackdrop)
        val poster = findViewById<ImageView>(R.id.detailPoster)
        findViewById<TextView>(R.id.detailTitle).text = item.title
        findViewById<TextView>(R.id.detailType).setText(
            if (item.isTvShow) R.string.type_tv else R.string.type_movie
        )
        findViewById<TextView>(R.id.detailRating).text = getString(
            R.string.rating_detail,
            String.format(Locale.getDefault(), "%.1f", item.rating),
            item.voteCount
        )
        findViewById<TextView>(R.id.detailDate).text = getString(
            if (item.isTvShow) R.string.first_aired else R.string.released,
            item.date.ifBlank { getString(R.string.unknown) }
        )
        findViewById<TextView>(R.id.detailOverview).text =
            item.overview.ifBlank { getString(R.string.no_overview) }

        // Same transition name as the clicked poster, so the image animates into place.
        ViewCompat.setTransitionName(
            poster, if (item.isTvShow) "poster_tv_${item.id}" else "poster_movie_${item.id}"
        )

        // Wait for the poster to load before running the shared element animation.
        supportPostponeEnterTransition()
        Glide.with(this)
            .load(item.posterUrl)
            .dontAnimate()
            .error(R.drawable.poster_error)
            .transform(CenterCrop(), RoundedCorners(dp(12)))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any, target: Target<Drawable>?,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }
            })
            .into(poster)

        Glide.with(this)
            .load(item.backdropUrl)
            .placeholder(R.drawable.poster_placeholder)
            .centerCrop()
            .into(backdrop)
    }

    @Suppress("DEPRECATION")
    private fun readItem(): MediaItem? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_ITEM, MediaItem::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_ITEM)
        }

    override fun onSupportNavigateUp(): Boolean {
        supportFinishAfterTransition()
        return true
    }
}
