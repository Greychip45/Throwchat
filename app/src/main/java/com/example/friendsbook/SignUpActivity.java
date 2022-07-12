package com.example.friendsbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {
    EditText inputSignInEmail,inputSignInPassword,inputConfirmPassword;
    Button btnRegister,btnSkip;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    TextInputLayout emailLayout,pwdLayout,cpwdlayout;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView  textView5;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        





        emailLayout = findViewById(R.id.email_layout);
        pwdLayout = findViewById(R.id.pwd_layout);
        cpwdlayout = findViewById(R.id.cpwd_layout);


        inputSignInEmail=findViewById(R.id.inputSignUpEmail);
        inputSignInPassword=findViewById(R.id.inputSignUpPassword);
        inputConfirmPassword=findViewById(R.id.inputConfirmPassword);

        inputSignInEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                emailLayout.setErrorEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                emailLayout.setErrorEnabled(false);
            }
        });

        inputSignInPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                cpwdlayout.setErrorEnabled(false);
                pwdLayout.setErrorEnabled(false);

                inputConfirmPassword.setText("");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        btnRegister=findViewById(R.id.btnRegister);
        progressDialog= new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        textView5 = findViewById(R.id.textView5);


        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performAuth();


            }
        });



    }



    private void performAuth() {
        String email = inputSignInEmail.getText().toString();
        String password = inputSignInPassword.getText().toString();

        String confirmPassword = inputConfirmPassword.getText().toString();




        if(!email.matches(emailPattern)) {
            emailLayout.setError("Invalid Email");

        } else if(password.isEmpty() || password.length()<6 ) {
            pwdLayout.setError("Password should be six characters");


        } else if(confirmPassword.length()<6) {
            pwdLayout.setError("Password should be six characters");
        } else if(!confirmPassword.equals(password)) {
            pwdLayout.setError("Password do not match");
            cpwdlayout.setError("Password do not match");


        } else {

            progressDialog.setMessage("Registering...");

            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        progressDialog.dismiss();



                        sendUserToNextActivity();
                        Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Error : "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    private void sendUserToNextActivity() {

        String inputEmail = inputSignInEmail.getText().toString();

        Intent intent = new Intent(SignUpActivity.this, InfoActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


        intent.putExtra("inputEmail",inputEmail);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

}
