package com.andrehaueisen.fitx.shared.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.models.AttributedPhoto;
import com.andrehaueisen.fitx.models.Gym;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by andre on 10/2/2016.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private Context mContext;
    private ArrayList<Gym> mWorkingPlaces;

    public PlacesAdapter(Context context, ArrayList<Gym> workingPlaces) {
        mContext = context;
        mWorkingPlaces = workingPlaces;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;

        view = inflater.inflate(R.layout.item_work_place, parent, false);


        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {

        holder.onBindPlaceItem(position);
    }

    @Override
    public int getItemCount() {

        if (mWorkingPlaces != null) {
            return mWorkingPlaces.size();
        } else {
            return 0;
        }

    }

    class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPlaceImageView;
        private TextView mAttributionTextView;
        private TextView mPlaceNameTextView;
        private TextView mPlaceAddressTextView;
        private ImageView mRemovePlaceImageButton;

        private PlaceViewHolder(View itemView) {
            super(itemView);


            mPlaceImageView = (ImageView) itemView.findViewById(R.id.work_place_image_view);
            mAttributionTextView = (TextView) itemView.findViewById(R.id.attributions_text_view);
            mPlaceNameTextView = (TextView) itemView.findViewById(R.id.work_place_name);
            mPlaceAddressTextView = (TextView) itemView.findViewById(R.id.work_place_address);
            mRemovePlaceImageButton = (ImageView) itemView.findViewById(R.id.remove_place_button);

            mRemovePlaceImageButton.setOnClickListener(this);
        }

        private void onBindPlaceItem(int position) {

            Gym gym = mWorkingPlaces.get(position);
            String placeName = gym.getName();
            String placeAddress = gym.getAddress();


            bindAttributedPhotoToViews(gym);

            mPlaceNameTextView.setText(placeName);
            mPlaceAddressTextView.setText(placeAddress);

        }

        private void bindAttributedPhotoToViews(Gym gym) {

            AttributedPhoto attributedPhoto = gym.getAttributedPhoto();

            if (attributedPhoto != null) {
                if (attributedPhoto.getBitmap() != null) {
                    mPlaceImageView.setImageBitmap(attributedPhoto.getBitmap());

                } else {
                    Glide.with(mContext).load(R.drawable.neutral_background).centerCrop().into(mPlaceImageView);
                }

                if (attributedPhoto.getAttribution() != null) {
                    String attribution = Html.fromHtml(attributedPhoto.getAttribution().toString()).toString();
                    mAttributionTextView.setVisibility(View.VISIBLE);
                    mAttributionTextView.setText(mContext.getString(R.string.taken_by_attribution, attribution));

                } else {
                    mAttributionTextView.setVisibility(View.GONE);
                }
            } else {
                Glide.with(mContext).load(R.drawable.neutral_background).centerCrop().into(mPlaceImageView);
                mAttributionTextView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mWorkingPlaces.remove(position);
            notifyItemRemoved(position);
        }
    }
}
