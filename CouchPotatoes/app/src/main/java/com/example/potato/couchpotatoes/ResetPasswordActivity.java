package com.example.potato.couchpotatoes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnBack;
    private ProgressBar progressBar;
    private ResetPasswordTask mResetTask = null;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBHelper();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.reset_email);
        progressBar = (ProgressBar) findViewById(R.id.reset_progressBar);
        btnBack = (Button) findViewById(R.id.btn_reset_back);
        Button btnReset = (Button) findViewById(R.id.btn_reset_password);

        btnBack.setOnClickListener(onClickListener);
        btnReset.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.btn_reset_back:
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                    break;

                case R.id.btn_reset_password:
                    attemptResetPassword();
                    break;
            }
        }
    };

    private void attemptResetPassword() {
        if (mResetTask != null) {
            return;
        }

        // Reset errors.
        inputEmail.setError(null);

        // Store values at the time of the reset attempt.
        String email = inputEmail.getText().toString().trim();
        boolean cancel = false;
        View focusView = null;

        // Check for valid email address.
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("This field is required");
            focusView = inputEmail;
            cancel = true;
        } else if (!StringValidator.isValidEmail(email)) {
            inputEmail.setError("This email address is invalid");
            focusView = inputEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressBar.setVisibility(View.VISIBLE);
            mResetTask = new ResetPasswordTask(email);
            mResetTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous reset password task used to reset the password
     * of the user.
     */
    public class ResetPasswordTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;

        ResetPasswordTask(String email) {
            mEmail = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Attempt to send the reset link with the passed email
            final boolean[] success = {false}; // TODO check if this is correct
            dbHelper.getAuth().sendPasswordResetEmail(mEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setContentView(R.layout.activity_reset_password_success);

                                TextView displayResetSuccess = (TextView) findViewById(R.id.lbl_reset_password_success);

                                btnBack = (Button) findViewById(R.id.btn_reset_back);
                                btnBack.setOnClickListener(onClickListener);

                                displayResetSuccess.setText(getString(R.string.reset_password_success_msg_1)
                                        + " " + mEmail + " "
                                        + getString(R.string.reset_password_success_msg_2));
//                                Toast.makeText(ResetPasswordActivity.this,
//                                        "We have sent you instructions to reset your password!",
//                                        Toast.LENGTH_SHORT).show();
                                success[0] = true;
                            } else {
                                Toast.makeText(ResetPasswordActivity.this,
                                        "Failed to send reset email!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            progressBar.setVisibility(View.GONE);
                        }
                    });

            return success[0];
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mResetTask = null;
        }

        @Override
        protected void onCancelled() {
            mResetTask = null;
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
