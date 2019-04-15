package com.nguyendinhdoan.storiesprogressviewwithkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var moviesDB: CollectionReference
    private var count = 0

    companion object {
        private const val STORIES_PROGRESS_VIEW_DURATION = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load_button.setOnClickListener {
            moviesDB = FirebaseFirestore.getInstance().collection("Movies")
            moviesDB.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val moviesList = ArrayList<Movies>()
                    for (documentMovies in task.result!!) {
                        val movies = documentMovies.toObject(Movies::class.java)
                        moviesList.add(movies)
                    }
                    setupStoriesProgressView(moviesList)
                }
            }
        }
    }

    private fun setupStoriesProgressView(moviesList: List<Movies>) {
        stories_progress_view.setStoriesCount(moviesList.size)
        stories_progress_view.setStoryDuration(STORIES_PROGRESS_VIEW_DURATION)

        // load first image
        Picasso.get().load(moviesList[0].image).into(avatar_image_view, object: Callback {
            override fun onError(e: Exception?) {
                Toast.makeText(this@MainActivity, e?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                stories_progress_view.startStories()
            }

        })

        stories_progress_view.setStoriesListener(object: StoriesProgressView.StoriesListener {

            override fun onComplete() {
                if (count < moviesList.size) {
                    count++
                    Picasso.get().load(moviesList[count].image).into(avatar_image_view)
                }
            }

            override fun onPrev() {
                if (count > 0) {
                    count--
                    Picasso.get().load(moviesList[count].image).into(avatar_image_view)
                }
            }

            override fun onNext() {
                count = 0
                Toast.makeText(this@MainActivity, "Load image done", Toast.LENGTH_SHORT).show()
            }

        })
    }


}
