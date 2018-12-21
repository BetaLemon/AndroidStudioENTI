package org.neocities.betalemon.socialwall.fragments


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_news.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.adapters.NewsAdapter
import org.neocities.betalemon.socialwall.models.NewsModel
import java.util.*

class NewsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshData()

        newsRefreshSwipe.setOnRefreshListener {
            refreshData()
        }

    }

    private fun refreshData(){

        newsRefreshSwipe.isRefreshing = true

        val db = FirebaseFirestore.getInstance()

        db.collection(COLLECTION_NEWS).get().addOnCompleteListener{ task->
            if(!isAdded) return@addOnCompleteListener

            if(task.isSuccessful){
                val list = ArrayList<NewsModel>()
                task.result?.forEach{documentSnapshot->
                    val news = documentSnapshot.toObject(NewsModel::class.java)
                    list.add(news)
                    Log.i("BoardFragment", news.toString())
                }
                // Set Layout Manager
                newsRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                // Create Adapter
                val adapter = NewsAdapter(list)
                // Assign Adapter
                newsRecyclerView.adapter = adapter
            }
            else{
                Log.e("NewsFragment", "Error getting news: " + task.exception?.message)
                Snackbar.make(newsRecyclerView.rootView, R.string.news_refresh_error, Snackbar.LENGTH_SHORT).show()
            }

            newsRefreshSwipe.isRefreshing = false
        }
    }
}
