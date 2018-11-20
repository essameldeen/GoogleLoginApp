package com.example.essam.googleloginapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult

import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        private var permissionCode = 999
    }

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleApiClient: GoogleApiClient
    lateinit var alertDialog: android.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configrationGoogleClient()
        firebaseAuth = FirebaseAuth.getInstance()
        showDialog()


        btn_login.setOnClickListener {
            signIn()
        }


    }

    private fun showDialog() {
        alertDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait ...")
            .setCancelable(false)
            .build()
    }

    private fun signIn() {
        val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent, permissionCode)
    }


    private fun configrationGoogleClient() {
        var option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, option)
            .build()

        mGoogleApiClient.connect() // Do not forget connect :D

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Error :" + p0.errorMessage, Toast.LENGTH_LONG).show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == permissionCode) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                val token = account!!.idToken
                val credential = GoogleAuthProvider.getCredential(token, null)
                firebaseAuthWithGoogle(credential)
            } else {
                Toast.makeText(this, "UnSuccessful logIn ", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential?) {
        firebaseAuth.signInWithCredential(credential!!)
            .addOnFailureListener {
                Toast.makeText(this, "Error :" + it.message, Toast.LENGTH_LONG).show()

            }.addOnSuccessListener {
                val email = it.user.email
                email_txt.text = email.toString()
            }

    }

}
