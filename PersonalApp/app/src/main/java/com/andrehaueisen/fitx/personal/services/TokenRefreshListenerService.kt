package com.andrehaueisen.fitx.personal.services

import android.util.Log
import com.andrehaueisen.fitx.Constants
import com.andrehaueisen.fitx.Utils
import com.example.andre.fitx.backend.registration.Registration
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by andre on 1/25/2017.
 */
class TokenRefreshListenerService : FirebaseInstanceIdService(){


    override fun onTokenRefresh() {
        super.onTokenRefresh()

        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("TokenRefListenerService", "Refreshes token: $refreshedToken")

        sendRegistrationToServer(refreshedToken)
        saveSentToken(refreshedToken)
    }

    fun sendRegistrationToServer(token: String?){

        val builder = Registration.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory(), null)
                .setRootUrl("https://personalapp-ad97d.appspot.com/_ah/api/")

        val regService = builder.build()
        regService.register(token).execute()

    }

    fun saveSentToken(refreshedToken: String?){
        //This will be sent to server when the user logs in
        Utils.getSharedPreferences(this).edit().putString(Constants.SHARED_PREF_SERVER_TOKEN, refreshedToken).apply()
    }
}