package com.andrehaueisen.fitx.shared.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;

/**
 * Created by andre on 10/5/2016.
 */

public class MainObjectiveAdapter extends RecyclerView.Adapter<MainObjectiveAdapter.ObjectivesViewHolder> {

    private Context mContext;
    private String[] mObjectives;
    private String mMainObjective;
    private TextView mPreviousTextView;

    public MainObjectiveAdapter(Context context, String[] objectives, String mainObjective) {
        super();
        mContext = context;
        mObjectives = objectives;
        mMainObjective = mainObjective;
    }

    @Override
    public MainObjectiveAdapter.ObjectivesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_specialties, parent, false);

        return new MainObjectiveAdapter.ObjectivesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainObjectiveAdapter.ObjectivesViewHolder holder, int position) {

        holder.bindSpecialtyItem(position);
    }

    public String getMainObjective() {
        return mMainObjective;
    }

    @Override
    public int getItemCount() {
        return mObjectives.length;
    }


    class ObjectivesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mObjectiveTextView;

        public ObjectivesViewHolder(View itemView) {
            super(itemView);

            mObjectiveTextView = (TextView) itemView.findViewById(R.id.specialty_text_view);
        }

        private void bindSpecialtyItem(int position) {

            mObjectiveTextView.setText(mObjectives[position]);
            mObjectiveTextView.setOnClickListener(this);

            setSpecialtySelected();
        }

        private void setSpecialtySelected() {

            String selectedSpecialty = mObjectiveTextView.getText().toString();

            if (mMainObjective != null) {
                if (mMainObjective.equals(selectedSpecialty)) {
                    mObjectiveTextView.setBackground(mContext.getDrawable(R.drawable.shape_rectangle_background));
                    mPreviousTextView = mObjectiveTextView;
                }
            }

        }

        @Override
        public void onClick(View view) {

            String selectedSpecialty = mObjectiveTextView.getText().toString();

            mObjectiveTextView.setBackground(mContext.getDrawable(R.drawable.shape_rectangle_background));
            mMainObjective = selectedSpecialty;

            if (mPreviousTextView != null) {
                mPreviousTextView.setBackground(null);
            }

            mPreviousTextView = mObjectiveTextView;
        }
    }
}
