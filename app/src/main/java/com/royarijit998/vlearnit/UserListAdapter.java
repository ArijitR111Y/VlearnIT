package com.royarijit998.vlearnit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder>  {

    private ArrayList<Users> usersArrayList;
    private Context context;

    public UserListAdapter(Context context, ArrayList<Users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, null, false);
        UserViewHolder userViewHolder = new UserViewHolder(layoutView);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        holder.allUsersFnameTextView.setText(usersArrayList.get(position).getUserFullName());
        holder.allUsersStatusTextView.setText(usersArrayList.get(position).getUserStatus());
        Picasso.get().load(usersArrayList.get(position).getUserProfileImg()).placeholder(R.drawable.profile).into(holder.allUsersProfileImageView);
        
        holder.allUsersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PersonProfileActivity.class);
                intent.putExtra("userId", usersArrayList.get(position).getUserId());
                v.getContext().startActivity(intent);
                Toast.makeText(context.getApplicationContext(), "User: " + usersArrayList.get(position).getUserFullName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private View allUsersView;
        private ImageView allUsersProfileImageView;
        private TextView allUsersFnameTextView, allUsersStatusTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            allUsersView = itemView.findViewById(R.id.allUsersView);
            
            allUsersProfileImageView = itemView.findViewById(R.id.allUsersProfileImageView);

            allUsersFnameTextView = itemView.findViewById(R.id.allUsersFnameTextView);
            allUsersStatusTextView = itemView.findViewById(R.id.allUsersStatusTextView);

        }
    }
}
