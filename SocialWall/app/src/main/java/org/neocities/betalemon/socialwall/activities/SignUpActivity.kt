package org.neocities.betalemon.socialwall.activities

import android.content.Context
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

        submitButton.setOnClickListener{thisView ->
            // 1. Get form data
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            signIn_progressBar.visibility = View.VISIBLE
            // 2. Validate form data
            if(!username.isEmpty() && email.isNotEmpty() && password.isNotEmpty()) { // Per comprovar si el text és un mail, utilitzariem un regex.
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUpActivity", "createUserWithEmail:success")
                            val user = FirebaseAuth.getInstance().getCurrentUser()
                            val userModel = UserModel(user?.uid, username, user?.email)
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users")
                                    .document(user!!.uid)
                                    .set(userModel)
                                    .addOnSuccessListener {
                                        // Sign Up Completed!
                                        // TODO: Tell user everything was fine and finish!
                                        val editor = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit()
                                        editor.putString(PREF_USERID, user.uid)
                                        editor.putString(PREF_USERNAME, username)
                                        editor.apply()
                                        signIn_progressBar.visibility = View.GONE
                                        Snackbar.make(thisView, "Sign In Successful!", Snackbar.LENGTH_SHORT).show()
                                        finish()

                                    }.addOnFailureListener {
                                        // TODO: Handle failure
                                        Log.e("SignUpActivity", it.message)
                                        signIn_progressBar.visibility = View.GONE
                                    }

                        } else {
                            // If sign in fails, display a message to the user.
                            signIn_progressBar.visibility = View.GONE
                            Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                            Snackbar.make(thisView, "Authentication failed.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }


                    }
            }
            else{
                // TODO: Notify user
            }
        }

        // TODO: Go To Login
        goToLogin.setOnClickListener{
            // TODO: Start LoginActivity
            finish()
        }
    }
}
