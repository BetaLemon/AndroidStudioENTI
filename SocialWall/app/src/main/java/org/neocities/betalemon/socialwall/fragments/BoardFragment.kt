package org.neocities.betalemon.socialwall.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_board.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.activities.SignUpActivity
import org.neocities.betalemon.socialwall.models.MessageModel
import java.util.*

class BoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendButton.setOnClickListener{
            val messageText = inputText.text.toString()
            if(messageText.isEmpty()) return@setOnClickListener

            // If userkey == null -> SignUp
            if(FirebaseAuth.getInstance().currentUser == null) {
                val goToSignUpIntent = Intent(activity, SignUpActivity::class.java)
                //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
                startActivity(goToSignUpIntent)
                return@setOnClickListener
            }

            // Send message to database

            val userPreferences = context?.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            val userId = userPreferences?.getString(PREF_USERID, "")
            val username = userPreferences?.getString(PREF_USERNAME, "")

            val message = MessageModel(text = messageText, createdAt = Date())
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_MESSAGES)
                .add(message)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(activity, R.string.add_message_success, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{e->
                    Toast.makeText(activity, getString(R.string.add_message_error), Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun refreshData(){
        val db = FirebaseFirestore.getInstance()

        db.collection(COLLECTION_MESSAGES).get().addOnCompleteListener{ task->
            if(!isAdded) return@addOnCompleteListener

            if(task.isSuccessful){
                var list = ArrayList<MessageModel>()
                task.result?.forEach{documentSnapshot->
                    var message = documentSnapshot.toObject(MessageModel::class.java)
                    list.add(message)
                    Log.i("BoardFragment", message.toString())
                }
            }
            else{
                Log.e("BoardFragment", "Error getting messages: " + task.exception?.message)
                Toast.makeText(context, "Error, sorry", Toast.LENGTH_SHORT).show()
            }
        }
    }

}