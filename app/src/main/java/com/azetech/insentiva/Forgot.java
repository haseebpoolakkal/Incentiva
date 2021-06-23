package com.azetech.insentiva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot extends AppCompatActivity {

    private EditText emailText;
    private Button sentButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    Convert convert = new Convert();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        validate();

        sentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = convert.ObjectToString(emailText.getText()).trim();

                if (email.equals("")){
                    emailText.setError("Enter Valid Email");
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    sendLink(email);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Forgot.this, MainActivity.class));
        finish();
    }

    private void sendLink(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Forgot.this, "A Password Reset Mail has been sent to your Mail Address.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void validate() {
        emailText = (EditText) findViewById(R.id.forgot_email);
        sentButton = (Button) findViewById(R.id.forgot_button);
        progressBar = (ProgressBar) findViewById(R.id.forgot_progress);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
    }
}
