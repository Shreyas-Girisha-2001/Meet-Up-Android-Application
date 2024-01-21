package com.example.meet_up_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login_SignUp extends AppCompatActivity {
    TextView register,forgetpass;
    EditText loginpass,loginemail,loginusername;
    Button loginbutton;
    CheckBox cb;
    FirebaseAuth mAuth;
    ProgressDialog dialog;

    DatabaseReference da = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_sign_up);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        register=findViewById(R.id.register);
        loginpass=findViewById(R.id.loginpass);
        loginemail=findViewById(R.id.loginemail);
        forgetpass=findViewById(R.id.forgotpass);
        loginusername=findViewById(R.id.loginusername);
        mAuth=FirebaseAuth.getInstance();
        loginbutton=findViewById(R.id.loginbutton);
        cb = findViewById(R.id.showpass);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    loginpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    loginpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = loginusername.getText().toString();
                String email = loginemail.getText().toString();
                String pass = loginpass.getText().toString();
                DatabaseReference see = da.child("users");
                if (TextUtils.isEmpty(email)) {
                    loginemail.setError("Email cannot be empty");
                    loginemail.requestFocus();
                } else if (TextUtils.isEmpty(username)) {
                    loginusername.setError("Username cannot be empty");
                    loginusername.requestFocus();
                } else if (TextUtils.isEmpty(pass)) {
                    loginpass.setError("Password cannot be empty");
                    loginpass.requestFocus();
                } else if (validateEmail(loginemail)) {
                    loginemail.setError("Invalid Email ");
                    loginemail.requestFocus();
                } else if (validateUsername(loginusername)) {
                    loginusername.setError("Invalid Username ");
                    loginusername.requestFocus();
                } else {
                    dialog.show();
                    see.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    String mail = snapshot1.child("email").getValue().toString();
                                    if (email.equals(mail)) {
                                        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    if (snapshot1.child("uid").getValue().toString().equals(mAuth.getUid())) {
                                                        if (snapshot1.child("email").getValue().toString().equals(mail)) {
                                                            if (snapshot1.child("username").getValue().toString().equals(username)) {
                                                                Toast.makeText(Login_SignUp.this, "Logged In", Toast.LENGTH_SHORT).show();
                                                                Intent i = new Intent(Login_SignUp.this, MainActivity.class);
                                                                i.putExtra("username", username);
                                                                startActivity(i);
                                                                finish();
                                                            } else {
                                                                dialog.dismiss();
                                                                loginusername.setError("Check Username");
                                                                loginusername.requestFocus();
                                                            }
                                                        } else {
                                                            dialog.dismiss();

                                                            loginemail.setError("Check Email");
                                                            loginemail.requestFocus();
                                                        }
                                                    } else {
                                                        dialog.dismiss();

                                                        loginusername.setError("User does not exist");
                                                        loginusername.requestFocus();
                                                    }
                                                } else {
                                                    dialog.dismiss();
                                                    loginpass.setError("Incorrect Password");
                                                    loginpass.requestFocus();
                                                }
                                            }
                                        });
                                    }else{
                                        dialog.dismiss();
                                        loginemail.setError("Check Email");
                                        loginemail.requestFocus();
                                    }


                                }
                            } else {
                                dialog.dismiss();
                                loginusername.setError("User does not exist");
                                loginusername.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_SignUp.this,createAccount.class));
                finish();
            }
        });

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1=loginemail.getText().toString();
                dialog.show();
                if (!email1.isEmpty()) {
                    mAuth.sendPasswordResetEmail(email1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(Login_SignUp.this, "reset password link is sent to your email inbox. Please Check", Toast.LENGTH_LONG).show();
                            }else{
                                dialog.dismiss();

                            }
                        }
                    });
                }else{
                    dialog.dismiss();
                    loginemail.setError("Email cannot be empty");
                    loginemail.requestFocus();
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
}