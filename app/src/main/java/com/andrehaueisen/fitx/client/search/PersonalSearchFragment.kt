package com.andrehaueisen.fitx.client.search


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.andrehaueisen.fitx.*
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.client.DayAgendaFragment
import com.andrehaueisen.fitx.client.firebase.FirebaseProfileImageCatcher
import com.andrehaueisen.fitx.models.Gym
import com.andrehaueisen.fitx.models.PersonalTrainer
import com.andrehaueisen.fitx.shared.dialogFragments.ClassDurationDialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import jp.co.recruit_mp.android.lightcalendarview.LightCalendarView
import jp.co.recruit_mp.android.lightcalendarview.MonthView
import net.danlew.android.joda.JodaTimeAndroid
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import java.io.ByteArrayOutputStream
import java.util.*


class PersonalSearchFragment : Fragment(), FirebaseProfileImageCatcher.FirebaseProfileCallback, ClassDurationDialogFragment.DurationCallBack {

    private val mDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mStartTimesHashMap = HashMap<String, ArrayList<Int>>()
    private val mEndTimesHashMap = HashMap<String, ArrayList<Int>>()
    private val mRestrictionsStartTimesHashMap = HashMap<String, ArrayList<Int>>()
    private val mRestrictionsEndTimesHashMap = HashMap<String, ArrayList<Int>>()
    private val mDateKeys = configureDateKeys()
    private var mClassDurationButton: CustomButton? = null

    private var mUnitedStartTimeCodesForSpecificDay: ArrayList<Int>? = null
    private var mUnitedEndTimeCodesForSpecificDay: ArrayList<Int>? = null
    private var mSelectedDateCode: String = ""
    private var mDuration: Int = 0

    private lateinit var mPersonalTrainer: PersonalTrainer
    private lateinit var mWorkingLocations: ArrayList<Gym>
    private lateinit var mSpecialties: ArrayList<String>
    private var mPersonalProfilePic: ByteArray? = null

    companion object {

        fun newInstance(bundle: Bundle): PersonalSearchFragment {
            val fragment = PersonalSearchFragment()

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        JodaTimeAndroid.init(context)
        setDefaultProfileImage()
    }

    private fun setDefaultProfileImage() {
        val defaultImage = resources.getDrawable(R.drawable.head_placeholder, null)
        val bitmap = (defaultImage as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        mPersonalProfilePic = stream.toByteArray()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater?.inflate(R.layout.fragment_search_specific_personal, container, false)

        if (!arguments.isEmpty) {
            val query = arguments.getString(Constants.SEARCH_BUNDLE_KEY)

            if (query != null && view != null) {
                actOnQuery(query, view)
            }
        }


        mClassDurationButton = view?.findViewById(R.id.class_duration_button) as (CustomButton)
        configureClassDurationButton()

        val monthNameTextView = view.findViewById(R.id.month_name_text_view) as CustomTextView
        val calendarView = view.findViewById(R.id.personal_calendar_view) as (LightCalendarView)
        configureCalendar(calendarView, monthNameTextView)

        return view
    }

    fun configureClassDurationButton() {
        mClassDurationButton?.setOnClickListener {
            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
            val fragment = activity.supportFragmentManager.findFragmentByTag("dialog")

            if (fragment != null) {
                fragmentTransaction.remove(fragment)
            }
            fragmentTransaction.addToBackStack(null)

            val durationFragment = ClassDurationDialogFragment.newInstance()
            durationFragment.setTargetFragment(this, 0)
            durationFragment.show(fragmentTransaction, "dialog")
        }
    }

    fun configureCalendar(calendarView: LightCalendarView, monthNameTextView: CustomTextView) {

        val startDate = LocalDate()

        monthNameTextView.text = startDate.monthOfYear().asText
        calendarView.monthFrom = startDate.toDate()
        calendarView.monthTo = startDate.plusMonths(2).toDate()
        calendarView.monthCurrent = startDate.toDate()
        calendarView.setOnStateUpdatedListener(object : LightCalendarView.OnStateUpdatedListener {

            override fun onDateSelected(date: Date) {

                if (isSelectedDatePresentOrPast(date)) {

                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setIcon(context.getDrawable(R.drawable.ic_log_out_24dp))
                            .setTitle(getString(R.string.date_unavailable))
                            .setMessage(R.string.correct_date_instruction)
                            .show()

                    val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
                    val dayAgendaFragment = activity.supportFragmentManager.findFragmentByTag("dayAgendaFragment")
                    if (dayAgendaFragment != null) {
                        fragmentTransaction.hide(dayAgendaFragment).commit()
                    }

                } else {

                    val selectedDate = LocalDate(date, DateTimeZone.forTimeZone(TimeZone.getDefault()))
                    val selectedDateCode = selectedDate.toString("MMddyyyy", Locale.US)

                    if (!mSelectedDateCode.equals(selectedDateCode)) {

                        val weekDay = selectedDate.dayOfWeek().getAsText(Locale.US).capitalize()

                        uniteAgendaWithRestrictions(mStartTimesHashMap[weekDay], mEndTimesHashMap[weekDay], mRestrictionsStartTimesHashMap[selectedDateCode],
                                mRestrictionsEndTimesHashMap[selectedDateCode])

                        val bundle = Bundle()
                        if (mUnitedStartTimeCodesForSpecificDay != null && mUnitedEndTimeCodesForSpecificDay != null) {
                            bundle.putIntegerArrayList(Constants.CLASS_START_TIMES_BUNDLE_KEY, mUnitedStartTimeCodesForSpecificDay)
                            bundle.putIntegerArrayList(Constants.CLASS_END_TIMES_BUNDLE_KEY, mUnitedEndTimeCodesForSpecificDay)
                            bundle.putInt(Constants.CLASS_DURATION_BUNDLE_KEY, mDuration)
                            bundle.putSerializable(Constants.CLASS_DATE_BUNDLE_KEY, selectedDate)
                            bundle.putParcelable(Constants.CLASS_PERSONAL_BUNDLE_KEY, mPersonalTrainer)
                            bundle.putParcelableArrayList(Constants.PERSONAl_WORKING_LOCATIONS_BUNDLE_KEY, mWorkingLocations)
                            bundle.putStringArrayList(Constants.PERSONAL_SPECIALTIES_BUNDLE_KEY, mSpecialties)
                            bundle.putByteArray(Constants.PERSONAL_PROFILE_PIC_BUNDLE_KEY, mPersonalProfilePic)
                        }

                        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
                        var dayAgendaFragment = activity.supportFragmentManager.findFragmentByTag("dayAgendaFragment")

                        if (dayAgendaFragment != null) {
                            fragmentTransaction.remove(dayAgendaFragment)
                        }

                        dayAgendaFragment = DayAgendaFragment.newInstance(bundle)
                        fragmentTransaction.add(R.id.fragment_day_agenda_container_view, dayAgendaFragment, "dayAgendaFragment")
                        fragmentTransaction.commit()

                        mSelectedDateCode = selectedDateCode
                    }
                }
            }

            override fun onMonthSelected(date: Date, view: MonthView) {

                val initialMonthDate = LocalDate(date).dayOfMonth().withMinimumValue()
                monthNameTextView.text = initialMonthDate.monthOfYear().asText
            }
        })
    }

    private fun isSelectedDatePresentOrPast(date: Date): Boolean {
        val selectedDate = LocalDate(date)

        return selectedDate.isBefore(LocalDate().plusDays(1))
    }

    fun actOnQuery(query: String, view: View) {

        if (query.isNotBlank()) {

            mDatabaseReference.child(Constants.FIREBASE_LOCATION_PERSONAL_TRAINER).addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot?) {

                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        val personal = filterResults(query, dataSnapshot)
                        val noResultTextView = view.findViewById(R.id.no_results_text_view) as TextView
                        val personalView = view.findViewById(R.id.personal_view)

                        if (personal != null) {
                            getPersonalWorkingLocations(personal)
                            getPersonalSpecialties(personal)
                            getPersonalSchedule(personal)
                            configurePersonalView(view, personal)
                            mPersonalTrainer = personal
                            noResultTextView.visibility = View::GONE.get()
                            personalView.visibility = View::VISIBLE.get()

                        } else {
                            noResultTextView.visibility = View::VISIBLE.get()
                            personalView.visibility = View::GONE.get()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError?) {}
            })
        }
    }

    private fun getPersonalWorkingLocations(personal: PersonalTrainer) {
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_GYMS).child(Utils.encodeEmail(personal.email))
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Utils.generateWarningToast(context, "Fetch working places failed").show()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            val genericTypeIndicator = object : GenericTypeIndicator<ArrayList<Gym>>() {}
                            mWorkingLocations = dataSnapshot.getValue(genericTypeIndicator)
                        }
                    }
                })
    }

    private fun getPersonalSpecialties(personal: PersonalTrainer) {
        mDatabaseReference.child(Constants.FIREBASE_LOCATION_SPECIALTIES).child(Utils.encodeEmail(personal.email)).child("specialties")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Utils.generateWarningToast(context, "Fetch specialties failed").show()
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            val genericTypeIndicator = object : GenericTypeIndicator<ArrayList<String>>() {}
                            mSpecialties = dataSnapshot.getValue(genericTypeIndicator)
                            mSpecialties[0]
                        }
                    }
                })
    }

    private fun getPersonalSchedule(personal: PersonalTrainer) {

        val personalKey = Utils.encodeEmail(personal.email)

        mDatabaseReference.child(Constants.FIREBASE_LOCATION_AGENDA).child(personalKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot != null && dataSnapshot.exists()) {

                    var agendaTimeCodesEnd: ArrayList<Int>?
                    var agendaTimeCodesStart: ArrayList<Int>

                    val genericTypeIndicator = object : GenericTypeIndicator<ArrayList<Int>>() {}

                    for (snapshot in dataSnapshot.children) {
                        val weekDay = snapshot.key

                        agendaTimeCodesEnd = snapshot.child(Constants.AGENDA_CODES_END_LIST).getValue(genericTypeIndicator)
                        if (agendaTimeCodesEnd != null) {
                            mEndTimesHashMap.put(weekDay, agendaTimeCodesEnd)
                        }

                        agendaTimeCodesStart = snapshot.child(Constants.AGENDA_CODES_START_LIST).getValue(genericTypeIndicator)
                        if (agendaTimeCodesStart != null) {
                            mStartTimesHashMap.put(weekDay, agendaTimeCodesStart)
                        }

                    }
                    getPersonalRestrictions(personalKey)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })
    }

    private fun getPersonalRestrictions(personalKey: String) {

        mDatabaseReference.child(Constants.FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {

                if (snapshot != null && snapshot.exists()) {

                    val genericTimeValue = object : GenericTypeIndicator<ArrayList<Int>>() {}

                    for (date in mDateKeys) {
                        val snapshotFromPersonal = snapshot.child(date).child(personalKey)
                        if (snapshotFromPersonal.exists()) {
                            mRestrictionsStartTimesHashMap.put(date, snapshotFromPersonal.child(Constants.AGENDA_CODES_START_LIST).getValue(genericTimeValue))
                            mRestrictionsEndTimesHashMap.put(date, snapshotFromPersonal.child(Constants.AGENDA_CODES_END_LIST).getValue(genericTimeValue))
                        }
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        })

    }

    private fun uniteAgendaWithRestrictions(agendaTimeCodesStart: ArrayList<Int>?, agendaTimeCodesEnd: ArrayList<Int>?,
                                            restrictionStartTimeCodes: ArrayList<Int>?, restrictionEndTimeCodes: ArrayList<Int>?) {

        var restrictionStart: Int
        var restrictionEnd: Int

        mUnitedStartTimeCodesForSpecificDay = ArrayList<Int>()
        mUnitedEndTimeCodesForSpecificDay = ArrayList<Int>()

        if (agendaTimeCodesStart != null && agendaTimeCodesEnd != null) {
            mUnitedStartTimeCodesForSpecificDay?.addAll(agendaTimeCodesStart)
            mUnitedEndTimeCodesForSpecificDay?.addAll(agendaTimeCodesEnd)

            if (restrictionStartTimeCodes != null && restrictionEndTimeCodes != null) {

                for (i in restrictionStartTimeCodes.indices) {
                    restrictionStart = restrictionStartTimeCodes[i]
                    restrictionEnd = restrictionEndTimeCodes[i]

                    mUnitedStartTimeCodesForSpecificDay?.add(restrictionEnd)
                    mUnitedEndTimeCodesForSpecificDay?.add(restrictionStart)
                }

                sortAgendaInAscendingOrder()
            }
        } else {
            mUnitedStartTimeCodesForSpecificDay = null
            mUnitedEndTimeCodesForSpecificDay = null
        }

    }

    private fun sortAgendaInAscendingOrder() {
        Collections.sort(mUnitedStartTimeCodesForSpecificDay)
        Collections.sort(mUnitedEndTimeCodesForSpecificDay)
    }

    //TODO make multiple results possible
    private fun filterResults(query: String, dataSnapshot: DataSnapshot): PersonalTrainer? {

        val personals = dataSnapshot.children.map { it.getValue(PersonalTrainer::class.java) }

        when {

            query.matches(Regex("[0-9]+")) -> personals.filter { it.cref.contains(query) }.forEach { return it }

            Patterns.EMAIL_ADDRESS.matcher(query).matches() -> personals.filter { it.email.contains(query) }.forEach { return it }

            else -> return null//personals.filter { it.name.toUpperCase().contains(query.toUpperCase()) }.forEach { return it }
        }

        return null
    }

    private fun configurePersonalView(view: View, personal: PersonalTrainer) {

        val imageCatcher = FirebaseProfileImageCatcher(this)
        imageCatcher.getPersonalProfilePicture(activity, Utils.encodeEmail(personal.email))

        val nameTextView = view.findViewById(R.id.personal_name_text_view) as TextView
        val gradeTextView = view.findViewById(R.id.grade_text_view) as TextView

        nameTextView.text = personal.name
        gradeTextView.text = Utils.formatGrade(personal.grade)

    }

    private fun configureDateKeys(): List<String> {

        val startDate = LocalDate()

        val days = Days.daysBetween(startDate.plusDays(1), startDate.plusMonths(2)).days

        return days.downTo(0).map { startDate.withFieldAdded(DurationFieldType.days(), it).toString("MMddyyyy") }
    }

    override fun setDuration(duration: Int) {
        val textDuration = Utils.getClassDurationText(context, duration)
        mClassDurationButton?.text = getString(R.string.class_duration_is, textDuration)

        mDuration = duration
    }

    override fun onProfileImageReady(personProfileImage: ByteArray?) {
        val profileImageView = view?.findViewById(R.id.profile_image_view) as ImageView
        Glide.with(this).load(personProfileImage).asBitmap().into(profileImageView)

        if (personProfileImage != null) {
            mPersonalProfilePic = personProfileImage
        }

    }
}
