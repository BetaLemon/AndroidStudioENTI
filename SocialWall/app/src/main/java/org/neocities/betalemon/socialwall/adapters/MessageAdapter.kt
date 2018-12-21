package org.neocities.betalemon.socialwall.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.board_message.view.*
import org.neocities.betalemon.socialwall.COLLECTION_USERS
import org.neocities.betalemon.socialwall.R
import org.neocities.betalemon.socialwall.USER_AVATAR
import org.neocities.betalemon.socialwall.USER_ID
import org.neocities.betalemon.socialwall.models.MessageModel

class MessageAdapter(private var messageList: ArrayList<MessageModel>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
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

        FirebaseFirestore.getInstance().collection(COLLECTION_USERS).whereEqualTo(USER_ID, messageList[position].userId)
                .get().addOnSuccessListener {
                    if(!it.documents.isEmpty()){
                        val user = it.documents[0]
                        val avatar = user[USER_AVATAR].toString()
                        avatar.let{avatarURL->
                            Glide.with(viewHolder.itemView).load(avatarURL).apply(
                                    RequestOptions()
                                            .transforms(CircleCrop())
                                            .placeholder(R.drawable.ic_profile)
                            ).into(viewHolder.messageAvatar)
                        }
                    }
                }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var messageSenderContainer: TextView = itemView.messageSenderContainer
        var messageTextContainer: TextView = itemView.messageTextContainer
        var messageSentTime: TextView = itemView.messageSentTime
        var messageAvatar: ImageView = itemView.messageAvatar
    }
}