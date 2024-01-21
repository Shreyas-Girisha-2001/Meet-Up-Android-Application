package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String rec_img,rec_uid,rec_name,send_uid;
    CircleImageView profileimg;
    TextView reciverName;
    FirebaseDatabase database,similarint;
    TextView similarinttxt;
    FirebaseAuth mAuth;
    public static String sImage;
    public static String rImage;

    CardView sendbtn;
    EditText textmessage;

    String senderRoom,reciverRoom;

    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    MessagesAdapter Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);

        similarinttxt=findViewById(R.id.similarint);
        List<String> listint = new ArrayList<String>();
        database=FirebaseDatabase.getInstance();
        similarint=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        rec_name=getIntent().getStringExtra("name");
        rec_img=getIntent().getStringExtra("rec_image");
        listint.add(getIntent().getStringExtra("int1"));
        listint.add(getIntent().getStringExtra("int2"));
        listint.add(getIntent().getStringExtra("int3"));
        rImage=rec_img;
        messagesArrayList=new ArrayList<>();
        rec_uid=getIntent().getStringExtra("uid");
        send_uid=mAuth.getUid();
        final String[] username = new String[1];
        messageAdapter=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        Adapter=new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageAdapter.setAdapter(Adapter);


        profileimg=findViewById(R.id.userprofile);
        reciverName=findViewById(R.id.chatusername);
        sendbtn=findViewById(R.id.sendbtn);
        textmessage=findViewById(R.id.textmessage);

        Picasso.get().load(rec_img).into(profileimg);
        reciverName.setText(rec_name);

        senderRoom=send_uid+rec_uid;
        reciverRoom=rec_uid+send_uid;
        similarint.getReference().child("users").child(send_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listint.contains(snapshot.child("intrest3").getValue().toString()) && listint.contains(snapshot.child("intrest1").getValue().toString()) && listint.contains(snapshot.child("intrest3").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest3").getValue().toString()+","+snapshot.child("intrest1").getValue().toString()+" and "+snapshot.child("intrest2").getValue().toString());
                }else if(listint.contains(snapshot.child("intrest1").getValue().toString()) && listint.contains(snapshot.child("intrest2").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest1").getValue().toString()+" and "+snapshot.child("intrest2").getValue().toString());
                }else if(listint.contains(snapshot.child("intrest2").getValue().toString()) && listint.contains(snapshot.child("intrest3").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest2").getValue().toString()+" and "+snapshot.child("intrest3").getValue().toString());
                }else if(listint.contains(snapshot.child("intrest3").getValue().toString()) && listint.contains(snapshot.child("intrest1").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest3").getValue().toString()+" and "+snapshot.child("intrest1").getValue().toString());
                }else if(listint.contains(snapshot.child("intrest1").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest1").getValue().toString());
                }else if(listint.contains(snapshot.child("intrest2").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest2").getValue().toString());
                }else if(listint.contains(snapshot.child("intrest3").getValue().toString())){
                    similarinttxt.setText("You Have Similar Intrest in "+snapshot.child("intrest3").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef = database.getReference().child("chats").child(senderRoom).child("messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                Adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                                String sendimg= snapshot.child("profile").getValue().toString();
                                username[0] = snapshot.child("username").getValue().toString();
                                sImage=sendimg;

                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=textmessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Please enter valid message", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmessage.setText("");
                Date date=new Date();

                Messages messages=new Messages(message,send_uid,date.getTime());

                database=FirebaseDatabase.getInstance();
                HashMap<String,Object> lastmsgobj=new HashMap<>();
                lastmsgobj.put("lastMsg",messages.getMessage());
                lastmsgobj.put("lastMsgTime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastmsgobj);
                database.getReference().child("chats").child(reciverRoom).updateChildren(lastmsgobj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(reciverRoom)
                                        .child("messages")
                                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });

                            }
                        });
            }
        });
    }

}