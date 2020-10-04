package com.wallpaper.wallpadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);*/


    //defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if the objects getcurrentuser method is not null
        //means user is already logged in


        if (firebaseAuth.getCurrentUser() != null) {
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), uploadactivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.mailinp);
        editTextPassword = (EditText) findViewById(R.id.passinp);
        buttonSignIn = (Button) findViewById(R.id.signin);
        /*textViewSignup = (TextView) findViewById(R.id.textViewSignUp);*/

        progressDialog = new ProgressDialog(this);

        //attaching click listener
        /*buttonSignIn.setOnClickListener((View.OnClickListener) this);*/
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        /* textViewSignup.setOnClickListener(this);*/







       /* String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            makeText(this, "Please enter email", LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            makeText(this, "Please enter password", LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), uploadactivity.class));
                        }
                    }
                });*/

    }

    //method for user login
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            makeText(this, "Please enter email", LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            makeText(this, "Please enter password", LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if (task.isSuccessful()) {
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), uploadactivity.class));
                        }
                    }
                });

    }

    /*@Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }

        if (view == textViewSignup) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }*/


}

