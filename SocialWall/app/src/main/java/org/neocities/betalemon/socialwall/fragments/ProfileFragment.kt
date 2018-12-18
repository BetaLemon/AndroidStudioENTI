package org.neocities.betalemon.socialwall.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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
        if(userId == null) {
            val goToSignUpIntent = Intent(activity, SignUpActivity::class.java)
            //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
            startActivity(goToSignUpIntent)
            return@onViewCreated
        }

        val dbUser = FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document("userId") as DocumentSnapshot

        user.userId = userId;
        user.avatarUrl = dbUser.get(USER_AVATAR).toString()
        user.email = dbUser.get(USER_MAIL).toString()
        user.username = dbUser.get(USER_NAME).toString()

        profileUsername.text = user.username;
        profileEmail.text = user.email;
        //profileAvatar.src = user.avatarUrl;
    }

}
