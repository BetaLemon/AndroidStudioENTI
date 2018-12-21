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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.activities.SignUpActivity
import org.neocities.betalemon.socialwall.models.NewsModel
import org.neocities.betalemon.socialwall.models.UserModel

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val user = UserModel()

        val userPreferences = context?.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val userId = userPreferences?.getString(PREF_USERID, "")?.toString()

        // If userkey == null -> SignUp
        if(userId == null || userId == "") {
            val goToSignUpIntent = Intent(activity, SignUpActivity::class.java)
            //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
            startActivity(goToSignUpIntent)
            return@onViewCreated
        }else{
            // If user is logged in:
            FirebaseFirestore.getInstance().collection(COLLECTION_USERS).whereEqualTo(USER_ID, userId).get()
                    .addOnSuccessListener {
                        if(!it.isEmpty){
                            var u = it.documents[0]
                            user.avatarUrl = u[USER_AVATAR].toString()
                            user.email = u[USER_MAIL].toString()
                            user.username = u[USER_NAME].toString()
                            user.userId = userId;

                            profileUsername.text = user.username;
                            profileEmail.text = user.email;
                            if(user.avatarUrl != "" && user.avatarUrl != null) {

                                Glide.with(this)
                                        .load(user.avatarUrl)
                                        .apply(
                                                RequestOptions()
                                                        .transforms(CircleCrop())
                                                        .placeholder(R.drawable.ic_profile)
                                        )
                                        .into(profileAvatar);

                                //profileAvatar.src = user.avatarUrl;

                            }
                            Log.i("ProfileFragment", user.username)
                        }
                        else{
                            Toast.makeText( activity, R.string.profile_get_error, Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        logOutButton.setOnClickListener{
            val editor = context?.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)?.edit()
            editor?.remove(PREF_USERID)
            editor?.remove(PREF_USERNAME)
            editor?.apply()

            FirebaseAuth.getInstance().signOut()

            val goToSignUpIntent = Intent(activity, SignUpActivity::class.java)
            //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
            startActivity(goToSignUpIntent)
            return@setOnClickListener
        }

    }

}
