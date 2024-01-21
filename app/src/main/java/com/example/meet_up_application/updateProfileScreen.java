package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;


public class updateProfileScreen extends AppCompatActivity {

    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("users");
    Button password;
    Button b1;
    Uri img;
    FirebaseAuth mAuth;
    TextView intrest;
    ChipGroup sports;
    CircleImageView imageView;
    ProgressDialog dialog;
    String username,url,email,int1,int2,int3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_update_profile_screen);
        sports=findViewById(R.id.usports);
        password = findViewById(R.id.resetpass);
        imageView = findViewById(R.id.uimageView);
        intrest=findViewById(R.id.sportsintrests);
         username = getIntent().getStringExtra("username");
         email = getIntent().getStringExtra("email");
         int1 = getIntent().getStringExtra("int1");
         int2 = getIntent().getStringExtra("int2");
         int3 = getIntent().getStringExtra("int3");
         url = getIntent().getStringExtra("imgurl");
         intrest.setText("Your select sports intrests are : "+int1+","+int2+" and "+int3);
        Picasso.get().load(url).into(imageView);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Editing your account");
        dialog.setCancelable(false);
        mAuth=FirebaseAuth.getInstance();
        StorageReference storageref = FirebaseStorage.getInstance().getReference().child("Profiles").child(username);
        b1=findViewById(R.id.updatesubmit);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map <String,Object> map = new HashMap<>();
                List<Integer> a = sports.getCheckedChipIds();
                List<String> s = new ArrayList<String>();
                if(a.size()!=3){
                    Toast.makeText(updateProfileScreen.this, "Only 3 Sports to be selected", Toast.LENGTH_SHORT).show();
                }else {
                    for (int i = 0; i < sports.getChildCount(); i++) {
                        Chip chip = (Chip) sports.getChildAt(i);
                        if (chip.isChecked()) {
                            s.add(chip.getText().toString());
                        }
                    }
                }
                if(img!=null && !a.isEmpty()){
                    dialog.show();
                    storageref.putFile(img).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finaluri = uri.toString();
                                    map.put("profile", finaluri);
                                    if (a.size() != 3) {
                                        dialog.dismiss();
                                        Toast.makeText(updateProfileScreen.this, "Only 3 Sports to be Selected", Toast.LENGTH_SHORT).show();
                                    } else {
                                        map.put("intrest1", s.get(0));
                                        map.put("intrest2", s.get(1));
                                        map.put("intrest3", s.get(2));
                                        dr.child(mAuth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    dialog.dismiss();
                                                    Toast.makeText(updateProfileScreen.this, "data updated", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(updateProfileScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }else if(img==null && !a.isEmpty()){
                    dialog.show();
                    storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finaluri = uri.toString();
                            map.put("profile", finaluri);
                            if (a.size() != 3) {
                                dialog.dismiss();
                                Toast.makeText(updateProfileScreen.this, "Only 3 Sports to be Selected", Toast.LENGTH_SHORT).show();
                            } else {
                                map.put("intrest1", s.get(0));
                                map.put("intrest2", s.get(1));
                                map.put("intrest3", s.get(2));
                                dr.child(mAuth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(updateProfileScreen.this, "data updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(updateProfileScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }else if(img!=null && a.isEmpty()){
                    dialog.show();
                    storageref.putFile(img).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finaluri = uri.toString();
                                    map.put("profile",finaluri);
                                    dr.child(mAuth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                Toast.makeText(updateProfileScreen.this, "data updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                dialog.dismiss();
                                                Toast.makeText(updateProfileScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else if(img==null && a.isEmpty()){
                    dialog.show();
                    storageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri){
                            String finaluri = uri.toString();
                            map.put("profile", finaluri);
                            dr.child(mAuth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(updateProfileScreen.this, "data updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(updateProfileScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(updateProfileScreen.this, "reset password link is sent to your email inbox. Please Check", Toast.LENGTH_LONG).show();
                            }else{
                                dialog.dismiss();
                                Toast.makeText(updateProfileScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
            }
        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.updateprofile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.logout:
                        Intent i= new Intent(updateProfileScreen.this,LogoutScreen.class);
                        i.putExtra("uid",mAuth.getUid());
                        i.putExtra("username",username);
                        i.putExtra("imgurl",url);
                        startActivity(i);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chats:
                        Intent in= new Intent(updateProfileScreen.this,MainActivity.class);
                        in.putExtra("uid",mAuth.getUid());
                        in.putExtra("username",username);
                        startActivity(in);
                        finish();
                        overridePendingTransition(0,0);
                    case R.id.updateprofile:
                        return true;
                }
                return false;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10)
        {
            if(data!=null){
                img=data.getData();
                imageView.setImageURI(img);
            }
        }
    }
}