package com.example.goout.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goout.R;
import com.example.goout.ui.FriendRequestProfile;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestsAdapter extends  RecyclerView.Adapter<FriendRequestsAdapter.friendRequestViewHolder>{
    private static final String TAG = "FriendRequestsAdapter";
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mUids = new ArrayList<>();
    private Context mContext;

    public FriendRequestsAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mUids) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mUids = mUids;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class friendRequestViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        public friendRequestViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_friendrequests);
            imageName = itemView.findViewById(R.id.image_text_friendrequests);
            parentLayout = itemView.findViewById(R.id.parent_layout_friendRequests);

        }
    }

    public FriendRequestsAdapter(String[] myDataset){
        //mDataset = myDataset;
    }

    @NonNull
    @Override
    public FriendRequestsAdapter.friendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friendrequestitem,parent,false);
        friendRequestViewHolder holder = new friendRequestViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull FriendRequestsAdapter.friendRequestViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d(TAG,"onBindViewHolder: called.");

        //mImages is the resource from which the images come from
        Glide.with(mContext).asBitmap().load(mImages.get(position)).into(holder.image);
        holder.imageName.setText(mImageNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: cliked on :" + mImageNames.get(position));
                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, FriendRequestProfile.class);
                intent.putExtra("image_url",mImages.get(position));
                intent.putExtra("image_name",mImageNames.get(position));
                intent.putExtra("user_id",mUids.get(position));

                //in adapter class. So we have to reference the context
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mImageNames.size();
    }
}