package com.example.flixsterplus

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/** Small helper for calling TMDB endpoints. */
object TmdbClient {

    private const val BASE_URL = "https://api.themoviedb.org/3"
    private val client = OkHttpClient()

    /** GET /movie/popular */
    suspend fun popularMovies(): List<MediaItem> = fetch("movie/popular", isTvShow = false)

    /** GET /tv/popular */
    suspend fun popularTvShows(): List<MediaItem> = fetch("tv/popular", isTvShow = true)

    private suspend fun fetch(path: String, isTvShow: Boolean): List<MediaItem> =
        withContext(Dispatchers.IO) {
            val url = "$BASE_URL/$path?api_key=${BuildConfig.TMDB_API_KEY}&language=en-US&page=1"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code}: $body")
                MediaItem.fromJsonArray(JSONObject(body).getJSONArray("results"), isTvShow)
            }
        }
}
