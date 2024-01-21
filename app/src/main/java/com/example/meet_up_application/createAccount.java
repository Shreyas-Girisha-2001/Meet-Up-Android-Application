package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meet_up_application.databinding.ActivityCreateAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class createAccount extends AppCompatActivity {
    ActivityCreateAccountBinding binding;
    EditText createusername1, createemail1, createpassword1;
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference("users");
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ChipGroup sports;
    Button b1;
    Uri img;
    CheckBox cb;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        storage=FirebaseStorage.getInstance();
        sports = findViewById(R.id.sports);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Creating your account");
        dialog.setCancelable(false);
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });
        createemail1 = findViewById(R.id.createemail);
        b1 = findViewById(R.id.createsubmit);
        createusername1 = findViewById(R.id.createusername);
        createpassword1 = findViewById(R.id.createpassword);
        mAuth=FirebaseAuth.getInstance();
        cb = findViewById(R.id.showpasssetip);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    createpassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    createpassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] imgurl = new String[1];
                String email = createemail1.getText().toString();
                String username = createusername1.getText().toString();
                String password = createpassword1.getText().toString();
                ArrayList<String> selected = new ArrayList<String>();
                List<Integer> id = sports.getCheckedChipIds();
                if (TextUtils.isEmpty(email)) {
                    createemail1.setError("Email cannot be empty");
                    createemail1.requestFocus();
                } else if (TextUtils.isEmpty(username)) {
                    createusername1.setError("Username cannot be empty");
                    createusername1.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    createpassword1.setError("Password cannot be empty");
                    createpassword1.requestFocus();
                }else if (password.length()<=7) {
                    createpassword1.setError("Password must have more then 7 character !");
                    createpassword1.requestFocus();
                } else if (validateEmail(createemail1)) {
                    createemail1.setError("Invalid Email ");
                    createemail1.requestFocus();
                } else if (validateUsername(createusername1)) {
                    createusername1.setError("Invalid Username ");
                    createusername1.requestFocus();
                } else if (id.isEmpty()) {
                    Toast.makeText(createAccount.this, "select at least one sport", Toast.LENGTH_SHORT).show();
                } else if (id.size() != 3) {
                    Toast.makeText(createAccount.this, "3 sports must be selected", Toast.LENGTH_SHORT).show();
                }
                else
                {
                       for (int i = 0; i < sports.getChildCount(); i++) {
                           Chip chip = (Chip) sports.getChildAt(i);
                           if (chip.isChecked()) {
                               selected.add(chip.getText().toString());
                           }
                       }
                    String intrest1 = selected.get(0);
                    String intrest2 = selected.get(1);
                    String intrest3 = selected.get(2);

                    DatabaseReference present = dr.child(username);
                    dialog.show();
                    present.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                createusername1.setError("username already taken , try new username ! ");
                                createusername1.requestFocus();
                            }else{
                                if(img!=null){
                                    StorageReference reference = storage.getReference().child("Profiles").child(username);
                                    reference.putFile(img).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imgurl[0] = uri.toString();
                                                        createaccount(email,password,username,imgurl[0],intrest1,intrest2,intrest3);
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(createAccount.this, "Something Went Wrong . retry Again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    imgurl[0]="https://firebasestorage.googleapis.com/v0/b/meet-up-d1f60.appspot.com/o/avatar.png?alt=media&token=f833299f-85c6-4fcf-8ad3-7360753f691a";
                                    createaccount(email,password,username,imgurl[0],intrest1,intrest2,intrest3);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            });
    }

    private void createaccount(String email,String password, String username,String url,String intrest1,String intrest2,String intrest3){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String uid=mAuth.getUid();
                    usergetset u = new usergetset(email.toLowerCase(Locale.ROOT),username,uid,intrest1,intrest2,intrest3,url);
                    u.setEmail(email.toLowerCase(Locale.ROOT));
                    u.setUsername(username);
                    u.setUid(uid);
                    u.setIntrest1(intrest1);
                    u.setIntrest2(intrest2);
                    u.setIntrest3(intrest3);
                    u.setProfile(url);
                    dr.child(mAuth.getUid()).setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            Intent i=new Intent(createAccount.this,MainActivity.class);
                            i.putExtra("username",username);
                            startActivity(i);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(createAccount.this, "Account not created", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    dialog.dismiss();
                    createemail1.setError("Email already existing try different and valid email");
                    createemail1.requestFocus();
                }
            }
        });
    }

    private boolean validateEmail(EditText em)
    {
        String emailIn=em.getText().toString();
        String regex="[a-zA-Z0-9_\\-\\.]+[@][a-z]+[\\.][a-z]{2,3}";
        Pattern email = Pattern.compile(regex);
        Matcher m=email.matcher(emailIn);
        if(!emailIn.isEmpty() && m.find()){
            return false;
        }
        else
        {
            return true;
        }

    } private boolean validateUsername(EditText em) {
        String emailIn = em.getText().toString();
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
        Pattern email = Pattern.compile(regex);
        Matcher m = email.matcher(emailIn);
        if (!emailIn.isEmpty() && m.find()) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                binding.imageView.setImageURI(data.getData());
                img=data.getData();
            }
        }
    }
}