package com.example.flixsterplus

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

/**
 * One movie or TV show from TMDB.
 * Parcelable so it can be passed to DetailActivity inside an Intent.
 */
@Parcelize
data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val voteCount: Int,
    val date: String,
    val isTvShow: Boolean
) : Parcelable {

    val posterUrl: String?
        get() = posterPath?.let { "$IMAGE_BASE_URL/w342$it" }

    val backdropUrl: String?
        get() = backdropPath?.let { "$IMAGE_BASE_URL/w780$it" } ?: posterUrl

    val year: String
        get() = date.take(4)

    companion object {
        private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"

        /** Parses the "results" array returned by TMDB list endpoints. */
        fun fromJsonArray(results: JSONArray, isTvShow: Boolean): List<MediaItem> =
            (0 until results.length()).map { fromJson(results.getJSONObject(it), isTvShow) }

        private fun fromJson(json: JSONObject, isTvShow: Boolean) = MediaItem(
            id = json.getInt("id"),
            // Movies use "title"/"release_date"; TV shows use "name"/"first_air_date".
            title = json.optString(if (isTvShow) "name" else "title"),
            overview = json.optString("overview"),
            posterPath = json.optString("poster_path").cleanPath(),
            backdropPath = json.optString("backdrop_path").cleanPath(),
            rating = json.optDouble("vote_average", 0.0),
            voteCount = json.optInt("vote_count", 0),
            date = json.optString(if (isTvShow) "first_air_date" else "release_date"),
            isTvShow = isTvShow
        )

        private fun String.cleanPath(): String? = takeIf { it.isNotBlank() && it != "null" }
    }
}
