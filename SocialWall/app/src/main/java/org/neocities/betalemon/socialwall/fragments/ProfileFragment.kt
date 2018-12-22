package org.neocities.betalemon.socialwall.fragments


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import org.neocities.betalemon.socialwall.*
import org.neocities.betalemon.socialwall.activities.SignUpActivity
import org.neocities.betalemon.socialwall.models.UserModel
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.features.ImagePicker
import android.net.Uri
import android.support.design.widget.Snackbar
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileData()

        logOutButton.setOnClickListener {
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

        profileAvatarEdit.setOnClickListener {
            uploadAvatar()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // We first check if we're actually logged in, and retrieve the userID:
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null && userId != "") {
            val images = ImagePicker.getImages(data)
            if (images != null && !images.isEmpty()) {
                //val usersStorageReference = FirebaseStorage.getInstance().getReference(DB_AVATARS)
                val file = File(images[0].path)
                val avatarStorageReference = FirebaseStorage.getInstance().getReference("images/users/${file.name}.jpg")
                val uri = Uri.fromFile(file)
                val uploadTask = avatarStorageReference.putFile(uri)
                uploadTask.addOnSuccessListener {
                    // All good!

                    // Get Download Url
                    val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation avatarStorageReference.downloadUrl
                    }).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Got URL!!
                            val downloadUrl = task.result

                            // Save to user profile
                            FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
                                // Actualizamos el documento del usuario
                                FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(uid).update(USER_AVATAR, downloadUrl.toString())
                            }

                            Log.i("ProfileFragment", "File upload success!")
                            Snackbar.make(this.view!!, "Successfully uploaded new avatar!", Snackbar.LENGTH_SHORT).show()

                            // Update Image in fragment:
                            Glide.with(this)
                                    .load(downloadUrl)
                                    .apply(
                                            RequestOptions()
                                                    .transforms(CircleCrop())
                                                    .placeholder(R.drawable.ic_profile)
                                    )
                                    .into(profileAvatar)

                        } else {
                            // Handle failures
                            Log.w("ProfileFragment", "Error getting download url :( " + task.exception?.message)
                            Toast.makeText(context, "Sorry, was unable to get image url from database.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                        .addOnFailureListener {
                            // Handle unsuccessful uploads
                            Log.w("ProfileFragment", "Error uploading file :(")
                            Snackbar.make(this.view!!, "Sorry, was unable to upload your new avatar.", Snackbar.LENGTH_LONG).show()
                        }

                //imageView.setImageBitmap(BitmapFactory.decodeFile(images[0].path))
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadProfileData() {
        val user = UserModel()

        val userPreferences = context?.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        val userId = userPreferences?.getString(PREF_USERID, "")?.toString()

        // If userkey == null -> SignUp
        if (userId == null || userId == "") {
            val goToSignUpIntent = Intent(activity, SignUpActivity::class.java)
            //goToSignUpIntent.putExtra() // podríem ficar info que es podría utilitzar més endavant
            startActivity(goToSignUpIntent)
            loadProfileData()
            return
        } else {
            // If user is logged in:
            FirebaseFirestore.getInstance().collection(COLLECTION_USERS).whereEqualTo(USER_ID, userId).get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            val u = it.documents[0]
                            user.avatarUrl = u[USER_AVATAR].toString()
                            user.email = u[USER_MAIL].toString()
                            user.username = u[USER_NAME].toString()
                            user.userId = userId

                            profileUsername.text = user.username
                            profileEmail.text = user.email
                            if (user.avatarUrl != "" && user.avatarUrl != null) {

                                Glide.with(this)
                                        .load(user.avatarUrl)
                                        .apply(
                                                RequestOptions()
                                                        .transforms(CircleCrop())
                                                        .placeholder(R.drawable.ic_profile)
                                        )
                                        .into(profileAvatar)

                                //profileAvatar.src = user.avatarUrl;

                            }
                            Log.i("ProfileFragment", user.username)
                        } else {
                            Toast.makeText(activity, R.string.profile_get_error, Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun uploadAvatar() {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                .single() // single mode
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                .theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                .enableLog(true) // disabling log
                .start() // start image picker activity with request code
    }

}
