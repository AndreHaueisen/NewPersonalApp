package com.andrehaueisen.fitx.client.adapters

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.Utils
import com.andrehaueisen.fitx.client.dialogFragment.ScheduleClassDialogFragment
import com.andrehaueisen.fitx.models.Gym
import com.andrehaueisen.fitx.models.PersonalTrainer
import org.joda.time.LocalDate
import java.util.*

/**
 * Created by andre on 1/7/2017.
 */
class PersonalWorkingTimesAdapter(context: Context, fragmentManager: FragmentManager,
                                  personalTrainer: PersonalTrainer, personalProfilePic: ByteArray, workingPlaces: ArrayList<Gym>,
                                  specialties : ArrayList<String>,
                                  date: LocalDate, duration: Int, startTimes: ArrayList<Int>, endTimes: ArrayList<Int>)
    : RecyclerView.Adapter<PersonalWorkingTimesAdapter.TimeViewHolder>()
{

    private val mContext: Context = context
    private val mStartTimes: ArrayList<Int> = startTimes
    private val mEndTimes: ArrayList<Int> = endTimes
    private val mFragmentManager = fragmentManager
    private val mPersonalTrainer = personalTrainer
    private val mWorkingPlaces = workingPlaces
    private val mSpecialties = specialties
    private val mDate = date
    private val mDuration = duration
    private val mPersonalProfilePic = personalProfilePic

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalWorkingTimesAdapter.TimeViewHolder {

        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_simple_time, parent, false)

        return TimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonalWorkingTimesAdapter.TimeViewHolder, position: Int) {

        holder.mTimeTextView.text = mContext.getString(R.string.class_duration, Utils.getClockFromTimeCode(mContext, mStartTimes[position]),
                Utils.getClockFromTimeCode(mContext, mEndTimes[position]))

    }

    override fun getItemCount(): Int {

        return mStartTimes.size

    }

    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mTimeTextView: TextView = itemView.findViewById(R.id.time_text_view) as TextView

        init {
            mTimeTextView.setOnClickListener {
                view -> val dialog = ScheduleClassDialogFragment(mPersonalTrainer, mPersonalProfilePic, mWorkingPlaces, mSpecialties, mDate,
                    mStartTimes[layoutPosition], mDuration)
                dialog.show(mFragmentManager, "ScheduleClassDialogFragment")}
        }
    }

}