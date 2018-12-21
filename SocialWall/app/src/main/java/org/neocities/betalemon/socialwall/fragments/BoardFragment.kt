package org.neocities.betalemon.socialwall.fragments


import android.content.Context
import android.content.Intent
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
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_board.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.activities.SignUpActivity
import org.neocities.betalemon.socialwall.adapters.MessageAdapter
import org.neocities.betalemon.socialwall.models.MessageModel
import java.util.*
import kotlin.collections.ArrayList
import android.view.inputmethod.InputMethodManager


class BoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshData()

        boardRefreshSwipe.setOnRefreshListener{
            refreshData()
        }

        sendButton.setOnClickListener{v->
            val messageText = inputText.text.toString()
            if(messageText.isEmpty()){
                Snackbar.make(v, R.string.add_message_notext, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var userPreferences = context?.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            var userId = userPreferences?.getString(PREF_USERID, "")?.toString()

            // If userkey == null -> SignUp
            if(userId == null || userId == "") {
                val goToSignUpIntent = Intent(activity, SignUpActivity::class.java)
                //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
                startActivity(goToSignUpIntent)
                return@setOnClickListener
            }

            // Send message to database

            userPreferences = context?.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            userId = userPreferences?.getString(PREF_USERID, "")?.toString()
            val username = userPreferences?.getString(PREF_USERNAME, "")?.toString()

            val message = MessageModel(text = messageText, createdAt = Date(), username = username, userId = userId)
            val db = FirebaseFirestore.getInstance()
            db.collection(COLLECTION_MESSAGES)
                .add(message)
                .addOnSuccessListener {
                    // Clear the text input field:
                    inputText.text.clear()
                    // Hide the keyboard:
                    val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    mgr?.hideSoftInputFromWindow(inputText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    // Update message list:
                    refreshData()
                    // Notify the user:
                    Snackbar.make(v, R.string.add_message_success, Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener{e->
                    Log.e("BoardFragment", e.message)
                    Snackbar.make(v, R.string.add_message_error, Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun refreshData(){

        boardRefreshSwipe.isRefreshing = true

        val db = FirebaseFirestore.getInstance()

        db.collection(COLLECTION_MESSAGES).orderBy(MSG_DATE, Query.Direction.DESCENDING).get().addOnCompleteListener { task ->
            if (!isAdded) return@addOnCompleteListener

            if (task.isSuccessful) {
                val list = ArrayList<MessageModel>()
                task.result?.forEach { documentSnapshot ->
                    val message = documentSnapshot.toObject(MessageModel::class.java)
                    list.add(message)
                    Log.i("BoardFragment", message.toString())
                }
                // Set Layout Manager
                boardRecyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
                // Create Adapter
                val adapter = MessageAdapter(list)
                // Assign Adapter
                boardRecyclerView.adapter = adapter
            } else {
                Log.e("BoardFragment", "Error getting messages: " + task.exception?.message)
                Toast.makeText(context, "Error, sorry", Toast.LENGTH_SHORT).show()
            }

            boardRefreshSwipe.isRefreshing = false
        }
    }

}
