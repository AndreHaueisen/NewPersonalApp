package com.andrehaueisen.fitx.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.andrehaueisen.fitx.Constants
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.Utils
import com.andrehaueisen.fitx.models.ClientFitClass
import com.andrehaueisen.fitx.models.PersonalFitClass
import com.google.firebase.database.*
import java.util.concurrent.Semaphore


/**
 * Created by andre on 3/11/2017.
 */
class ClassWidgetViewsService : RemoteViewsService() {

    lateinit var mDatabaseReference: DatabaseReference
    var mPersonalClasses = ArrayList<PersonalFitClass>()
    var mClientClasses = ArrayList<ClientFitClass>()
    val mSemaphore = Semaphore(0)

    private fun configurePersonalDatabaseListener(personalEncodedEmail: String?) {
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).child(personalEncodedEmail)
        mDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (mPersonalClasses.isNotEmpty())
                    mPersonalClasses.clear()

                val unsortedList = ArrayList<PersonalFitClass>()
                dataSnapshot?.children?.forEach { unsortedList.add(it.getValue(PersonalFitClass::class.java)) }
                unsortedList.sortedWith(compareBy { it.isConfirmed }).forEach { mPersonalClasses.add(it) }

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
                if (mClientClasses.isNotEmpty())
                    mClientClasses.clear()

                val unsortedList = ArrayList<ClientFitClass>()
                dataSnapshot?.children?.forEach { unsortedList.add(it.getValue(ClientFitClass::class.java)) }
                unsortedList.sortedWith(compareBy { it.isConfirmed }).forEach { mClientClasses.add(it) }

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

            override fun onCreate() {
            }

            override fun getLoadingView(): RemoteViews {
                return RemoteViews(packageName, R.layout.widget_item_class)
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun onDataSetChanged() {
                fetchClassesOnFirebase()
            }

            override fun hasStableIds(): Boolean {
                return true
            }

            override fun getViewAt(position: Int): RemoteViews {

                val remoteViews = RemoteViews(packageName, R.layout.widget_item_class)
                val context = applicationContext

                val classDate: String
                val classStartEnd: String
                val name: String
                val location: String
                val locationAddress: String
                val isConfirmed: Boolean

                if (mPersonalClasses.isNotEmpty()) {
                    val personalClass = mPersonalClasses[position]

                    val classStartTime = Utils.getClockFromTimeCode(context, personalClass.startTimeCode)
                    val classEndTime = Utils.getClockFromTimeCode(context, personalClass.startTimeCode + personalClass.durationCode)

                    classDate = Utils.getWrittenDateFromDateCode(context, personalClass.dateCode)
                    classStartEnd = context.getString(R.string.class_start_end, classStartTime, classEndTime)
                    name = personalClass.clientName
                    location = personalClass.placeName
                    locationAddress = personalClass.placeAddress
                    isConfirmed = personalClass.isConfirmed

                } else {
                    val clientClass = mClientClasses[position]

                    val classStartTime = Utils.getClockFromTimeCode(context, clientClass.startTimeCode)
                    val classEndTime = Utils.getClockFromTimeCode(context, clientClass.startTimeCode + clientClass.durationCode)

                    classDate = Utils.getWrittenDateFromDateCode(context, clientClass.dateCode)
                    classStartEnd = context.getString(R.string.class_start_end, classStartTime, classEndTime)
                    name = clientClass.personalName
                    location = clientClass.placeName
                    locationAddress = clientClass.placeAddress
                    isConfirmed = clientClass.isConfirmed

                }

                remoteViews.setTextViewText(R.id.class_date_text_view, classDate)
                remoteViews.setTextViewText(R.id.class_time_text_view, classStartEnd)
                remoteViews.setTextViewText(R.id.person_name_text_view, name)
                remoteViews.setTextViewText(R.id.class_location_name_text_view, location)
                remoteViews.setTextViewText(R.id.class_location_address_text_view, locationAddress)
                if (isConfirmed) {
                    remoteViews.setTextViewText(R.id.is_confirmed_text_view, context.getString(R.string.confirmed))
                } else {
                    remoteViews.setTextViewText(R.id.is_confirmed_text_view, context.getString(R.string.not_confirmed))
                }

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
                if (mPersonalClasses.isNotEmpty()) {
                    mPersonalClasses.clear()

                } else if (mClientClasses.isNotEmpty()) {
                    mClientClasses.clear()
                }
            }
        }

    }

    private fun fetchClassesOnFirebase() {

        val personalUniqueKey = applicationContext.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null)
        val clientUniqueKey = applicationContext.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null)

        if (personalUniqueKey != null) {
            configurePersonalDatabaseListener(personalUniqueKey)

        } else if (clientUniqueKey != null) {
            configureClientDatabaseListener(clientUniqueKey)

        } else {
            mPersonalClasses.clear()
            mClientClasses.clear()
        }
    }
}
