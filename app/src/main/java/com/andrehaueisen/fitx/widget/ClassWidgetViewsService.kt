package com.andrehaueisen.fitx.widget

import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.andrehaueisen.fitx.Constants
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.Utils
import com.andrehaueisen.fitx.client.firebase.FirebaseProfileImageCatcher
import com.andrehaueisen.fitx.models.ClientFitClass
import com.andrehaueisen.fitx.models.PersonalFitClass
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import java.util.concurrent.Semaphore


/**
 * Created by andre on 3/11/2017.
 */
class ClassWidgetViewsService : RemoteViewsService(), FirebaseProfileImageCatcher.FirebaseProfileCallback {

    lateinit var mDatabaseReference: DatabaseReference
    var mPersonalClasses = ArrayList<PersonalFitClass>()
    var mClientClasses = ArrayList<ClientFitClass>()
    val mSemaphore = Semaphore(0)

    private fun configurePersonalDatabaseListener(personalEncodedEmail: String?) {
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalEncodedEmail)
        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                mPersonalClasses = ArrayList()
                dataSnapshot?.children?.forEach { mPersonalClasses.add(it.getValue(PersonalFitClass::class.java)) }

                val profileImageCatcher = FirebaseProfileImageCatcher(this@ClassWidgetViewsService)
                for (i in 0..(mPersonalClasses.size - 1)) {
                    val personalClass = mPersonalClasses[i]
                    profileImageCatcher.getPersonalProfilePicture(personalClass.clientKey, i)
                }

                mSemaphore.release()
            }

            override fun onCancelled(p0: DatabaseError?) {
                mSemaphore.release()
            }
        })
        mSemaphore.acquire()
    }

    private fun configureClientDatabaseListener(clientEncodedEmail: String?) {
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(Constants.FIREBASE_LOCATION_CLIENT_CLASSES).child(clientEncodedEmail)
        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                mClientClasses = ArrayList()
                dataSnapshot?.children?.forEach { mClientClasses.add(it.getValue(ClientFitClass::class.java)) }

                val profileImageCatcher = FirebaseProfileImageCatcher(this@ClassWidgetViewsService)
                for (i in 0..(mClientClasses.size - 1)) {
                    val clientClass = mClientClasses[i]
                    profileImageCatcher.getClientProfilePicture(clientClass.personalKey, i)
                }
                mSemaphore.release()
            }

            override fun onCancelled(p0: DatabaseError?) {
                mSemaphore.release()
            }
        })
        mSemaphore.acquire()
    }


    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {

        return object : RemoteViewsFactory {

            override fun onCreate() {}

            override fun getLoadingView(): RemoteViews {
                return null!!
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun onDataSetChanged() {
                fetchClassesOnFirebase(intent)
            }

            override fun hasStableIds(): Boolean {
                return false
            }

            override fun getViewAt(position: Int): RemoteViews {

                val remoteViews = RemoteViews(packageName, R.layout.widget_item_class)
                val context = applicationContext

                val personImage : Bitmap?
                val classDate : String
                val classStartEnd : String
                val name : String
                val location : String
                val locationAddress : String

                if(mPersonalClasses.isNotEmpty()) {
                    val personalClass = mPersonalClasses[position]

                    personImage = personalClass.classProfileImage

                    val classStartTime = Utils.getClockFromTimeCode(context, personalClass.startTimeCode)
                    val classEndTime = Utils.getClockFromTimeCode(context, personalClass.startTimeCode + personalClass.durationCode)

                    classDate = Utils.getWrittenDateFromDateCode(context ,personalClass.dateCode)
                    classStartEnd = context.getString(R.string.class_start_end, classStartTime, classEndTime)
                    name = personalClass.clientName
                    location = personalClass.placeName
                    locationAddress = personalClass.placeAddress

                }else{
                    val clientClass = mClientClasses[position]

                    personImage = clientClass.classProfileImage

                    val classStartTime = Utils.getClockFromTimeCode(context, clientClass.startTimeCode)
                    val classEndTime = Utils.getClockFromTimeCode(context, clientClass.startTimeCode + clientClass.durationCode)

                    classDate = Utils.getWrittenDateFromDateCode(context ,clientClass.dateCode)
                    classStartEnd = context.getString(R.string.class_start_end, classStartTime, classEndTime)
                    name = clientClass.personalName
                    location = clientClass.placeName
                    locationAddress = clientClass.placeAddress

                }

                remoteViews.setImageViewBitmap(R.id.person_image_view, personImage)
                remoteViews.setTextViewText(R.id.class_date_text_view, classDate)
                remoteViews.setTextViewText(R.id.class_time_text_view, classStartEnd)
                remoteViews.setTextViewText(R.id.person_name_text_view, name)
                remoteViews.setTextViewText(R.id.class_location_name_text_view, location)
                remoteViews.setTextViewText(R.id.class_location_address_text_view, locationAddress)

                return remoteViews
            }

            override fun getCount(): Int {
                if (mPersonalClasses.isNotEmpty()) {
                    return mPersonalClasses.size
                } else {
                    return mClientClasses.size
                }
            }

            override fun getViewTypeCount(): Int {
                return 1
            }

            override fun onDestroy() {
                if(mPersonalClasses.isNotEmpty()){
                    mPersonalClasses.clear()

                }else{
                    mClientClasses.clear()
                }
            }
        }

    }

    override fun onProfileImageReady(personProfileImage: ByteArray?, positionOnArray: Int) {


        if (mPersonalClasses.isNotEmpty()) {
            val personalClass = mPersonalClasses[positionOnArray]
            personalClass.classProfileImage = Glide.with(this@ClassWidgetViewsService)
                    .load(personProfileImage)
                    .asBitmap()
                    .error(R.drawable.head_placeholder)
                    .into(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL).get()

        } else {
            val clientClass = mClientClasses[positionOnArray]
            clientClass.classProfileImage = Glide.with(this@ClassWidgetViewsService)
                    .load(personProfileImage)
                    .asBitmap()
                    .error(R.drawable.head_placeholder)
                    .into(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL).get()
        }

    }

    override fun onProfileImageReady(personProfileImage: ByteArray?) {

    }

    override fun onProfileImageReady(personProfileImage: ByteArray?, personalKey: String?) {

    }

    override fun onFrontBodyImageReady(personFrontImage: ByteArray?) {

    }

    override fun onPersonalPicsReady(classKey: String?, personPhotos: java.util.ArrayList<ByteArray>?) {

    }

    private fun fetchClassesOnFirebase(intent: Intent?) {
        var isPersonal = false

        if (intent?.extras != null) {
            isPersonal = intent.extras.getBoolean(Constants.IS_PERSONAL_EXTRA)
        }

        if (isPersonal) {
            val personalEncodedEmail = intent?.extras?.getString(Constants.PERSONAL_ENCODED_EMAIL_EXTRA_KEY)
            configurePersonalDatabaseListener(personalEncodedEmail)

        } else {
            val clientEncodedEmail = intent?.extras?.getString(Constants.CLIENT_ENCODED_EMAIL_EXTRA_KEY)
            configureClientDatabaseListener(clientEncodedEmail)
        }
    }

}