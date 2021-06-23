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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmployeeChangePassword extends AppCompatActivity {

    private EditText oldPassText, newPassText, conPassText;
    private Button changeButton;
    private ProgressBar progressBar;
    private FirebaseUser user;
    Convert convert = new Convert();
    String mobile, who, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_change_password);

        validate();

        mobile = getIntent().getStringExtra("mobile");

        who = getIntent().getStringExtra("who");
        from = getIntent().getStringExtra("from");

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String old = convert.ObjectToString(oldPassText.getText());
                String newPass = convert.ObjectToString(newPassText.getText());
                String confirm = convert.ObjectToString(conPassText.getText());

                if (old.equals("")){
                    progressBar.setVisibility(View.GONE);
                    oldPassText.setError("Please Enter Old Password");
                }
                else if (newPass.equals("")){
                    progressBar.setVisibility(View.GONE);
                    newPassText.setError("Please Enter New Password");

                }
                else if (confirm.equals("")){
                    progressBar.setVisibility(View.GONE);
                    conPassText.setError("Please Enter Confirmation Password");
                }
                else if (!confirm.equals(newPass)){
                    progressBar.setVisibility(View.GONE);
                    conPassText.setError("Password Does not Match");
                }
                else {
                    changePassword(old, newPass);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (who.equals("employee")){
            Intent intent = new Intent(new Intent(EmployeeChangePassword.this, EmployeeHome.class));
            intent.putExtra("mobile", mobile);
            intent.putExtra("from", from);
            intent.putExtra("who", who);
            startActivity(intent);
            finish();
        }
        else if(who.equals("admin")){
            Intent intent = new Intent(new Intent(EmployeeChangePassword.this, AdminHome.class));
            intent.putExtra("mobile", mobile);
            intent.putExtra("from", from);
            intent.putExtra("who", who);
            startActivity(intent);
            finish();
        }
    }

    private void changePassword(String old, final String newPass) {
        final String email = user.getEmail();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, old);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                                        oldPassText.setText("");
                                        newPassText.setText("");
                                        conPassText.setText("");
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Something Went Wrong, Please Try Later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void validate() {
        oldPassText = (EditText) findViewById(R.id.employee_change_pass_old_pass);
        newPassText = (EditText) findViewById(R.id.employee_change_pass_new_pass);
        conPassText = (EditText) findViewById(R.id.employee_change_pass_confirm);

        changeButton = (Button) findViewById(R.id.employee_change_pass_button);

        progressBar = (ProgressBar) findViewById(R.id.employee_change_pass_progress);
        progressBar.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}
