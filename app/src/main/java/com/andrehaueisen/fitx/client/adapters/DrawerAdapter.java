package com.andrehaueisen.fitx.client.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrehaueisen.fitx.Constants;
import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.Utils;
import com.andrehaueisen.fitx.pojo.Client;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by andre on 11/2/2016.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerItemHolder> {

    private final int HEADER_TYPE = 0;
    private final int OPTIONS_TYPE = 1;
    private int[] mIconsAddress;

    private Context mContext;
    private String[] mDrawerItems;

    private Client mClient;

    public DrawerAdapter(Context context, String[] drawerItems, int[] iconsAddress) {
        super();

        mContext = context;
        mDrawerItems = drawerItems;
        mIconsAddress = iconsAddress;

    }

    @Override
    public DrawerAdapter.DrawerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);

        if(viewType == OPTIONS_TYPE) {
            View view = inflater.inflate(R.layout.item_drawer_list, parent, false);
            return new DrawerItemHolder(view, OPTIONS_TYPE);
        } else if (viewType == HEADER_TYPE){
            View view = inflater.inflate(R.layout.drawer_header_client, parent, false);
            return new DrawerItemHolder(view, HEADER_TYPE);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return HEADER_TYPE;
        else {
            return OPTIONS_TYPE;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(DrawerItemHolder holder, int position) {
        holder.bindDrawerItem(position);
    }

    @Override
    public int getItemCount() {
        return mDrawerItems.length + 1;
    }

    protected class DrawerItemHolder extends RecyclerView.ViewHolder{

        private CircleImageView mHeadPhotoImageView;
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private TextView mMainObjectiveTextView;

        private TextView mDrawerItemTitleTextView;
        private ImageView mDrawerIconImageView;

        private int mViewType;

        private DrawerItemHolder(View itemView, int viewType) {
            super(itemView);

            mViewType = viewType;

            if(mViewType == HEADER_TYPE){
                fetchClient();
            }

            mHeadPhotoImageView = (CircleImageView) itemView.findViewById(R.id.client_head_photo_circle_view);
            mNameTextView = (TextView) itemView.findViewById(R.id.client_name_text_view);
            mEmailTextView = (TextView) itemView.findViewById(R.id.client_email_text_view);
            mMainObjectiveTextView = (TextView) itemView.findViewById(R.id.client_main_objective_text_view);

            mDrawerItemTitleTextView = (TextView) itemView.findViewById(R.id.drawer_option_text_view);
            mDrawerIconImageView = (ImageView) itemView.findViewById(R.id.image_option_image_view);

        }

        private void fetchClient(){

            String clientUniqueKey = Utils.getSharedPreferences(mContext).getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(Constants.FIREBASE_LOCATION_CLIENT).child(clientUniqueKey).addValueEventListener(mValueEventListener);
        }

        ValueEventListener mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mClient = dataSnapshot.getValue(Client.class);
                    bindPersonalDataToViews();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        private void bindDrawerItem(int position){
            if(mViewType == OPTIONS_TYPE){

                mDrawerItemTitleTextView.setText(mDrawerItems[position-1]);
                mDrawerIconImageView.setImageResource(mIconsAddress[position-1]);

            } else {
                setProfileImage();
            }
        }

        private void setProfileImage(){
            String photoUriPath = Utils.getSharedPreferences(mContext).getString(Constants.SHARED_PREF_CLIENT_PHOTO_URI_PATH, null);

            if(photoUriPath == null){
                Glide.with(mContext).load(R.drawable.head_placeholder).into(mHeadPhotoImageView);
            }else {
                Uri photoUri = Uri.parse(photoUriPath);
                Glide.with(mContext).load(photoUri).into(mHeadPhotoImageView);
            }
        }

        private void bindPersonalDataToViews(){
            mNameTextView.setText(mClient.getName());
            mEmailTextView.setText(mClient.getEmail());
            mMainObjectiveTextView.setText(mClient.getMainObjective());
        }

    }
}
