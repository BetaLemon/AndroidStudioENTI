package org.neocities.betalemon.socialwall.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.board_message.view.*
import org.neocities.betalemon.socialwall.R
import org.neocities.betalemon.socialwall.models.MessageModel

class MessageAdapter(var messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun getItemCount(): Int {
        return messageList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MessageAdapter.MessageViewHolder, position: Int) {
        viewHolder.messageSenderContainer.text = messageList[position].username
        viewHolder.messageTextContainer.text = messageList[position].text
        viewHolder.messageSentTime.text = messageList[position].createdAt.toString()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var messageSenderContainer: TextView = itemView.messageSenderContainer
        var messageTextContainer: TextView = itemView.messageTextContainer
        var messageSentTime: TextView = itemView.messageSentTime
    }
}