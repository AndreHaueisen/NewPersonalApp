package com.andrehaueisen.fitx.personal.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.pojo.Review;

import java.util.ArrayList;

/**
 * Created by andre on 10/31/2016.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private Context mContext;
    private ArrayList<Review> mReviews;

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.mReviewTextView.setText(review.getText());
        holder.mGradeTextView.setText(Utils.formatGrade(review.getGrade()));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {

        private TextView mGradeTextView;
        private TextView mReviewTextView;

        public ReviewHolder(View itemView) {
            super(itemView);

            mGradeTextView = (TextView) itemView.findViewById(R.id.grade_text_view);
            mReviewTextView = (TextView) itemView.findViewById(R.id.review_text_view);
        }


    }
}
