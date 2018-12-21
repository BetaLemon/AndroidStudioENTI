package org.neocities.betalemon.socialwall.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.news_message.view.*
import org.neocities.betalemon.socialwall.R
import org.neocities.betalemon.socialwall.models.NewsModel
import com.bumptech.glide.Glide


class NewsAdapter(private var newsList: ArrayList<NewsModel>) : RecyclerView.Adapter<NewsAdapter.MessageViewHolder>() {
    override fun getItemCount(): Int {
        return newsList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: NewsAdapter.MessageViewHolder, position: Int) {
        viewHolder.newsTitle.text = newsList[position].title

        Glide.with(viewHolder.newsImage)
                .load(newsList[position].imageUrl)
                .into(viewHolder.newsImage)

        viewHolder.newsAuthor.text = newsList[position].author
        viewHolder.newsDescription.text = newsList[position].description
        viewHolder.newsSentTime.text = newsList[position].createdAt.toString()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var newsTitle: TextView = itemView.newsTitle
        var newsImage: ImageView = itemView.newsImage
        var newsAuthor: TextView = itemView.newsAuthor
        var newsDescription: TextView = itemView.newsDescription
        var newsSentTime: TextView = itemView.newsSentTime
    }
}