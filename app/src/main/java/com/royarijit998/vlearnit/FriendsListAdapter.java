package com.royarijit998.vlearnit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsViewHolder> {

    private ArrayList<String> friendsArrayList;
    private DatabaseReference usersRef;

    public FriendsListAdapter(DatabaseReference usersRef, ArrayList<String> friendsArrayList) {
        this.friendsArrayList = friendsArrayList;
        this.usersRef = usersRef;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, null, false);
        FriendsListAdapter.FriendsViewHolder friendsViewHolder = new FriendsListAdapter.FriendsViewHolder(layoutView);
        return friendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position) {
        final String userId = friendsArrayList.get(position);

        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String fullname = "";
                    String status = "";
                    String profileImg = "";

                    if(dataSnapshot.child("fullname").getValue() != null){
                        fullname = dataSnapshot.child("fullname").getValue().toString();
                    }
                    if(dataSnapshot.child("status").getValue() != null){
                        status = dataSnapshot.child("status").getValue().toString();
                    }
                    if(dataSnapshot.child("profileImg").getValue() != null){
                        profileImg = dataSnapshot.child("profileImg").getValue().toString();
                    }

                    holder.allUsersFnameTextView.setText(fullname);
                    holder.allUsersStatusTextView.setText(status);
                    if(profileImg.isEmpty()){
                        holder.allUsersProfileImageView.setImageResource(R.drawable.profile);
                    }else{
                        Picasso.get().load(profileImg).placeholder(R.drawable.profile).into(holder.allUsersProfileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonProfileActivity.class);
                intent.putExtra("userId", userId);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsArrayList.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        private View allUsersView;
        private ImageView allUsersProfileImageView;
        private TextView allUsersFnameTextView, allUsersStatusTextView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            allUsersView = itemView.findViewById(R.id.allUsersView);

            allUsersProfileImageView = itemView.findViewById(R.id.allUsersProfileImageView);

            allUsersFnameTextView = itemView.findViewById(R.id.allUsersFnameTextView);
            allUsersStatusTextView = itemView.findViewById(R.id.allUsersStatusTextView);

        }
    }

}
