package org.neocities.betalemon.socialwall.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_news.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.adapters.NewsAdapter
import org.neocities.betalemon.socialwall.models.NewsList
import org.neocities.betalemon.socialwall.models.NewsModel
import java.util.*

class NewsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsArray = ArrayList<NewsModel>()

        val newsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NEWS)
        newsCollection.get()
                .addOnSuccessListener {dbNews->
                    for(news in dbNews){
                        newsArray.add(NewsModel(author = news[NEWS_AUTHOR].toString(),
                                title = news[NEWS_TITLE].toString(),
                                imageUrl = news[NEWS_IMAGE].toString(),
                                description = news[NEWS_DESCRIPTION].toString()))
                    }
                }

        val newsList = NewsList(newsArray)

        newsList.news?.let {
            // Set Layout Manager
            newsRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            // Create Adapter
            var adapter = NewsAdapter(it)
            // Assign Adapter
            newsRecyclerView.adapter = adapter

        }
    }
}
