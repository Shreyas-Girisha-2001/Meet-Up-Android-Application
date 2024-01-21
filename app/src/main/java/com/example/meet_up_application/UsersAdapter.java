package com.example.meet_up_application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meet_up_application.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends  RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    Context context;
    ArrayList<usergetset> users;
    public UsersAdapter(Context context, ArrayList<usergetset> users ){
        this.context=context;
        this.users=users;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        usergetset user = users.get(position);

        String senderID = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderID+user.getUid();

        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                    Long time = snapshot.child("lastMsgTime").getValue(Long.class);
                    SimpleDateFormat dateFormat= new SimpleDateFormat("hh:mm a");
                    holder.binding.lastmsg.setText(lastMsg);
                    holder.binding.timestap.setText(dateFormat.format(new Date(time)));
                }else{
                    holder.binding.lastmsg.setText("Tap To Chat");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            holder.binding.msguser.setText(user.getUsername());
        Glide.with(context).load(user.getProfile()).into(holder.binding.imageView2);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ChatActivity.class);
                intent.putExtra("name",user.getUsername());
                intent.putExtra("int1",user.getIntrest1());
                intent.putExtra("int2",user.getIntrest2());
                intent.putExtra("int3",user.getIntrest3());
                intent.putExtra("rec_image",user.getProfile());
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        RowConversationBinding binding;
        TextView status;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=RowConversationBinding.bind(itemView);
        }
    }
}
