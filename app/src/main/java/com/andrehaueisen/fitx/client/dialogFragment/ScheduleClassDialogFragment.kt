package com.andrehaueisen.fitx.client.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.andrehaueisen.fitx.*
import com.andrehaueisen.fitx.client.ScheduleConfirmation
import com.andrehaueisen.fitx.models.Gym
import com.andrehaueisen.fitx.models.PersonalFitClass
import com.andrehaueisen.fitx.models.PersonalTrainer
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.joda.time.LocalDate
import java.util.*


/**
 * Created by andre on 1/7/2017.
 */
class ScheduleClassDialogFragment(personalTrainer: PersonalTrainer, personalProfilePic: ByteArray, workingPlaces : ArrayList<Gym>,
                                  specialties : ArrayList<String>, date: LocalDate, startTimeCode: Int, duration: Int) : DialogFragment() {

    private val mPersonalTrainer = personalTrainer
    private val mWorkingPlaces = workingPlaces
    private val mSpecialties = specialties
    private val mDate = date
    private val mStartTimeCode = startTimeCode
    private val mDuration = duration
    private val mPersonalProfilePic = personalProfilePic

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater

        val view = layoutInflater.inflate(R.layout.dialog_fragment_confirm_class_schedule, null)
        val profileImageView = view.findViewById(R.id.person_image_view) as CircleImageView
        val workingPlacesSpinner = view.findViewById(R.id.working_places_spinner) as Spinner
        val specialtiesSpinner = view.findViewById(R.id.specialties_spinner) as Spinner
        val classDateTextView = view.findViewById(R.id.class_date_text_view) as CustomTextView
        val classTimeTextView = view.findViewById(R.id.class_time_text_view) as CustomTextView
        val personalNameTextView = view.findViewById(R.id.personal_name_text_view) as CustomTextView

        val confirmButton = view.findViewById(R.id.confirm_button) as CustomButton
        val cancelButton = view.findViewById(R.id.cancel_button) as CustomButton

        bindDataToViews(profileImageView, workingPlacesSpinner, specialtiesSpinner, classDateTextView, classTimeTextView, personalNameTextView)
        builder.setView(view)

        confirmButton.setOnClickListener {
            val dateCode = mDate.toString("MMddyyyy", Locale.US)
            val workingPlaceName = workingPlacesSpinner.selectedItem as String
            val workingPlace = mWorkingPlaces.find { it -> it.name == workingPlaceName }
            val mainObjective = specialtiesSpinner.selectedItem as String
            val sharedPref = Utils.getSharedPreferences(activity)
            val clientKey = sharedPref.getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null)
            val clientName = sharedPref.getString(Constants.SHARED_PREF_CLIENT_NAME, null)
            var fitClass : PersonalFitClass? = null

            if (workingPlace != null) {
                fitClass = PersonalFitClass(dateCode, workingPlace.longitude, workingPlace.latitude, workingPlace.name, mStartTimeCode, mDuration,
                        clientKey, clientName, mainObjective, workingPlace.address, false)
            }

            val scheduleConfirmation = ScheduleConfirmation(activity, mPersonalTrainer, fitClass)
            scheduleConfirmation.makeAppointment()

            activity.finish()
            dismiss()

        }
        cancelButton.setOnClickListener { dismiss() }

        return builder.create()
    }

    private fun bindDataToViews(profileImageView: ImageView, workingPlacesSpinner: Spinner, specialtiesSpinner : Spinner,
                                classDateTextView: CustomTextView, classTimeTextView: CustomTextView, personalNameTextView: CustomTextView) {

        val placeNames = mWorkingPlaces.map { it.name }
        val placesAdapter = ArrayAdapter<String>(context, R.layout.item_simple_name, placeNames)
        placesAdapter.setDropDownViewResource(R.layout.item_simple_name)
        workingPlacesSpinner.adapter = placesAdapter

        val specialtiesAdapter = ArrayAdapter<String>(context, R.layout.item_simple_name, mSpecialties)
        specialtiesAdapter.setDropDownViewResource(R.layout.item_simple_name)
        specialtiesSpinner.adapter = specialtiesAdapter

        val startTime = Utils.getClockFromTimeCode(context, mStartTimeCode)
        val endTime = Utils.getClockFromTimeCode(context, mStartTimeCode + mDuration)

        classDateTextView.text = mDate.toString("MM/dd/yyyy")
        classTimeTextView.text = getString(R.string.class_start_end, startTime, endTime)
        personalNameTextView.text = mPersonalTrainer.name

        Glide.with(this).load(mPersonalProfilePic).asBitmap().into(profileImageView)
    }
}