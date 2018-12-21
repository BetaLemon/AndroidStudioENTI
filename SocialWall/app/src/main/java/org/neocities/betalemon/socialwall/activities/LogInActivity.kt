package org.neocities.betalemon.socialwall.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.fragments.ProfileFragment
import org.neocities.betalemon.socialwall.models.UserModel

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        loginButton.setOnClickListener {
            // Get entered text from text input fields:
            val mail = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            // Hide keyboard:
            val mgr = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            mgr?.hideSoftInputFromWindow(loginPassword.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // SignIn in database:
            FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(it, R.string.login_successful, Snackbar.LENGTH_SHORT).show()
                    val user = FirebaseAuth.getInstance().currentUser
                    val userModel = UserModel()
                    userModel.userId = user?.uid

                    // Get Username from database:
                    FirebaseFirestore.getInstance().collection(COLLECTION_USERS).whereEqualTo(USER_ID, userModel.userId).get()
                            .addOnSuccessListener { querySnapshot ->
                                val u = querySnapshot.documents[0]
                                userModel.username = u[USER_NAME].toString()

                                val editor = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit()
                                editor.putString(PREF_USERID, user?.uid)
                                editor.putString(PREF_USERNAME, userModel.username)
                                editor.apply()

                                finish()
                            }

                } else {
                    Snackbar.make(it, R.string.login_wrong_credentials, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        goToSignUp.setOnClickListener {
            // Go to Sign Up activity:
            val goToSignUpIntent = Intent(this, SignUpActivity::class.java)
            //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
            startActivity(goToSignUpIntent)
            return@setOnClickListener
        }

    }
}
