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
import com.example.goout.ui.GalleryFriendActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class friendsActivityFriendsListAdapter extends RecyclerView.Adapter<friendsActivityFriendsListAdapter.friendsActivityFriendsListViewHolder> {
    private static final String TAG = "friendsActivityFriendsL";
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mUids = new ArrayList<>();
    private Context mContext;

    public friendsActivityFriendsListAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mImages, ArrayList<String> mUids)
    {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mUids = mUids;
    }

    //Setting up/linking the XML viewholder
    public class friendsActivityFriendsListViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;
        public friendsActivityFriendsListViewHolder(View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.friendsActivityItemImage);
            imageName = itemView.findViewById(R.id.friendsActivityItemText);
            parentLayout = itemView.findViewById(R.id.friendsActivityItemRelativeLayout);
        }

    }

    public friendsActivityFriendsListAdapter.friendsActivityFriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //creating the new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friendsactivityfriendslistitem,parent,false);
        friendsActivityFriendsListViewHolder holder = new friendsActivityFriendsListViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull friendsActivityFriendsListAdapter.friendsActivityFriendsListViewHolder holder, final int position) {
        // Get element from your dataset at this position
        // Replace the contents of the view with that element
        Log.d(TAG, "onBindViewHolder: Called");

        //mImages is the resource from which the images come from
        Glide.with(mContext).asBitmap().load(mImages.get(position)).into(holder.image);
        holder.imageName.setText(mImageNames.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: cliked on :" + mImageNames.get(position));
                Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, GalleryFriendActivity.class);
                intent.putExtra("image_url",mImages.get(position));
                intent.putExtra("image_name",mImageNames.get(position));
                intent.putExtra("user_id",mUids.get(position));

                //in adapter class. So we have to reference the context
                mContext.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }
}
