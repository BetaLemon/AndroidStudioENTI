package org.neocities.betalemon.socialwall.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.models.UserModel

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Send message button:
        submitButton.setOnClickListener{thisView ->
            // 1. Get form data
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            signUp_progressBar.visibility = View.VISIBLE

            // 2. Validate form data
            if(!username.isEmpty() && email.isNotEmpty() && password.isNotEmpty()) { // Per comprovar si el text és un mail, utilitzariem un regex.
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("SignUpActivity", "createUserWithEmail:success")
                            val user = FirebaseAuth.getInstance().currentUser
                            val userModel = UserModel(user?.uid, username, user?.email)
                            val db = FirebaseFirestore.getInstance()
                            db.collection(COLLECTION_USERS)
                                    .document(user!!.uid)
                                    .set(userModel)
                                    .addOnSuccessListener {
                                        // Sign Up Completed!
                                        val editor = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit()
                                        editor.putString(PREF_USERID, user.uid)
                                        editor.putString(PREF_USERNAME, username)
                                        editor.apply()
                                        signUp_progressBar.visibility = View.GONE
                                        Snackbar.make(thisView, R.string.register_successful, Snackbar.LENGTH_SHORT).show()
                                        finish()

                                    }.addOnFailureListener {
                                        Log.e("SignUpActivity", it.message)
                                        signUp_progressBar.visibility = View.GONE
                                    }

                        } else {
                            // If sign in fails, display a message to the user.
                            signUp_progressBar.visibility = View.GONE
                            Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                            Snackbar.make(thisView, R.string.register_error, Snackbar.LENGTH_SHORT).show()
                        }


                    }
            }
            else{
                signUp_progressBar.visibility = View.GONE
                Snackbar.make(thisView, R.string.register_error, Snackbar.LENGTH_SHORT).show()
            }
        }

        goToLogin.setOnClickListener{
            // Start Login Activity:
            val goToLoginIntent = Intent(this, LogInActivity::class.java)
            //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
            startActivity(goToLoginIntent)
            return@setOnClickListener
        }
    }
}
