package com.andrehaueisen.fitx.shared.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;

import java.util.ArrayList;

/**
 * Created by andre on 9/11/2016.
 */

public class SpecialtiesAdapter extends RecyclerView.Adapter<SpecialtiesAdapter.SpecialtiesViewHolder> {

    private Context mContext;
    private String[] mSpecialties;
    private ArrayList<String> mSelectedSpecialties;

    public SpecialtiesAdapter(Context context, String[] specialties) {
        super();
        mContext = context;
        mSpecialties = specialties;
        mSelectedSpecialties = new ArrayList<>();
    }

    public SpecialtiesAdapter(Context context, String[] specialties, ArrayList<String> selectedSpecialties) {
        super();
        mContext = context;
        mSpecialties = specialties;
        mSelectedSpecialties = selectedSpecialties;
    }

    @Override
    public SpecialtiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_specialties, parent, false);

        return new SpecialtiesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SpecialtiesViewHolder holder, int position) {

        holder.bindSpecialtyItem(position);
    }

    public ArrayList<String> getSelectedSpecialties() {
        return mSelectedSpecialties;
    }

    @Override
    public int getItemCount() {
        return mSpecialties.length;
    }

    class SpecialtiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mSpecialtyTextView;


        public SpecialtiesViewHolder(View itemView) {
            super(itemView);

            mSpecialtyTextView = (TextView) itemView.findViewById(R.id.specialty_text_view);

        }

        private void bindSpecialtyItem(int position) {

            mSpecialtyTextView.setText(mSpecialties[position]);
            mSpecialtyTextView.setOnClickListener(this);

            setSpecialtySelected();
        }

        private void setSpecialtySelected() {

            String selectedSpecialty = mSpecialtyTextView.getText().toString();

            if(mSelectedSpecialties != null) {
                if (mSelectedSpecialties.contains(selectedSpecialty)) {
                    mSpecialtyTextView.setBackground(mContext.getDrawable(R.drawable.shape_rectangle_background));
                    //mSpecialtyTextView.setTextColor(mContext.getResources().getColor(R.color.lightText));
                }
            }

        }

        @Override
        public void onClick(View view) {

            String selectedSpecialty = mSpecialtyTextView.getText().toString();

            if (mSelectedSpecialties.contains(selectedSpecialty)) {
                mSpecialtyTextView.setBackground(null);
                //mSpecialtyTextView.setTextColor(mContext.getResources().getColor(R.color.black));
                mSelectedSpecialties.remove(mSelectedSpecialties.indexOf(selectedSpecialty));

            } else {
                mSpecialtyTextView.setBackground(mContext.getDrawable(R.drawable.shape_rectangle_background));
                //mSpecialtyTextView.setTextColor(mContext.getResources().getColor(R.color.lightText));
                mSelectedSpecialties.add(selectedSpecialty);
            }
        }
    }
}
