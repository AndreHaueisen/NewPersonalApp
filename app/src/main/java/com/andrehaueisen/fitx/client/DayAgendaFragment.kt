package com.andrehaueisen.fitx.client

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrehaueisen.fitx.Constants
import com.andrehaueisen.fitx.CustomTextView
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.Utils
import com.andrehaueisen.fitx.client.adapters.PersonalWorkingTimesAdapter
import com.andrehaueisen.fitx.models.Gym
import com.andrehaueisen.fitx.models.PersonalTrainer
import org.joda.time.LocalDate
import java.util.*

/**
 * Created by andre on 12/14/2016.
 */
internal class DayAgendaFragment : Fragment(){

    private var mStartTimeClassesWithDuration : ArrayList<Int>? = null
    private var mEndTimeClassesWithDuration : ArrayList<Int>? = null

    companion object newInstance{

        fun newInstance(bundle: Bundle):DayAgendaFragment{
            val fragment = DayAgendaFragment()
            fragment.arguments = bundle

            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater?.inflate(R.layout.fragment_day_agenda, container, false)

        if(!arguments.isEmpty) {
            val startTimeClasses = arguments.getIntegerArrayList(Constants.CLASS_START_TIMES_BUNDLE_KEY)
            val endTimeClasses = arguments.getIntegerArrayList(Constants.CLASS_END_TIMES_BUNDLE_KEY)
            val duration = arguments.getInt(Constants.CLASS_DURATION_BUNDLE_KEY)
            val date = arguments.getSerializable(Constants.CLASS_DATE_BUNDLE_KEY) as LocalDate
            val personalTrainer : PersonalTrainer = arguments.getParcelable(Constants.CLASS_PERSONAL_BUNDLE_KEY)
            val workingPlaces : ArrayList<Gym> = arguments.getParcelableArrayList(Constants.PERSONAl_WORKING_LOCATIONS_BUNDLE_KEY)
            val specialties : ArrayList<String> = arguments.getStringArrayList(Constants.PERSONAL_SPECIALTIES_BUNDLE_KEY)
            val personalProfilePic : ByteArray = arguments.getByteArray(Constants.PERSONAL_PROFILE_PIC_BUNDLE_KEY)

            when{
                duration == 0 -> Utils.generateInfoToast(context, getString(R.string.configure_class_duration)).show()

                startTimeClasses != null && endTimeClasses != null && view != null ->{

                    createClassesSchedules(duration, startTimeClasses, endTimeClasses)

                    val availableClassesRecyclerView = view.findViewById(R.id.classes_recycler_view) as RecyclerView
                    availableClassesRecyclerView.layoutManager = GridLayoutManager(context, 2)
                    availableClassesRecyclerView.setHasFixedSize(true)
                    availableClassesRecyclerView.adapter = PersonalWorkingTimesAdapter(context, activity.supportFragmentManager, personalTrainer,
                            personalProfilePic, workingPlaces, specialties, date, duration, mStartTimeClassesWithDuration!!, mEndTimeClassesWithDuration!!)

                    val dateTextView = view.findViewById(R.id.date_text_view) as (CustomTextView)
                    dateTextView.text = date.toString("MM/dd/yyy")
                }
            }

        } else {
            Utils.generateInfoToast(context, getString(R.string.no_class_available)).show()
        }

        return view
    }

    private fun createClassesSchedules(duration: Int, startTimeClasses: ArrayList<Int>, endTimeClasses: ArrayList<Int>){

        mStartTimeClassesWithDuration = ArrayList<Int>()
        mEndTimeClassesWithDuration = ArrayList<Int>()

        for(i in startTimeClasses.indices) {
            var initialPeriod = startTimeClasses[i]
            val finalPeriod = endTimeClasses[i]

            while(initialPeriod + duration <= finalPeriod){

                mStartTimeClassesWithDuration?.add(initialPeriod)
                mEndTimeClassesWithDuration?.add(initialPeriod + duration)

                initialPeriod += duration
            }
        }
    }
}