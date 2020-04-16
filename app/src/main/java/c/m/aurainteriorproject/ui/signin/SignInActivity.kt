package c.m.aurainteriorproject.ui.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorproject.R
import c.m.aurainteriorproject.ui.main.MainActivity
import c.m.aurainteriorproject.util.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // sign in button
        btn_sign_in.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.PhoneBuilder()
                                .setDefaultCountryIso(getString(R.string.id_default_country))
                                .build()
                        )
                    ).build(),
                Constants.REQUEST_SIGN_IN_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.REQUEST_SIGN_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // open main activity and finish this activity
                startActivity<MainActivity>()
                finish()
            } else {
                if (response == null) {
                    toast(getString(R.string.sign_in_cancel_alert))
                }

                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    toast(getString(R.string.check_internet_connection_alert))
                }

                Log.e(
                    "Sign In Error",
                    "Sign In error: ${response?.error?.message} || ${response?.error}"
                )
            }
        }
    }
}
