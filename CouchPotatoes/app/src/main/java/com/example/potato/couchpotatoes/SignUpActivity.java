package com.example.potato.couchpotatoes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link NonSwipeableViewPager} that will host the section contents.
     * Standard navigation via swiping screens is disabled.
     */
    private static NonSwipeableViewPager mViewPager;
    /**
     * CurrentUser - temp object to hold information collected from sign up forms
     * DBHelper - Connect to FireBase upon successful sign up
     * tempPass - Holds temp password for account creation
     * TODO: Resolve storage of temp variables.
     */
    private static int minAge = 18;

    private static CurrentUser currentUser = new CurrentUser();
    private static DBHelper dbHelper;
    private static Calendar calendar;
    private static String tempEmail;
    private static String tempPass;
    private static String tempPassConfirm;
    private static String tempFirstName;
    private static String tempMiddleName;
    private static String tempLastName;
    private static int tempDoBYear, tempDoBMonth, tempDoBDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Intialise variables
        dbHelper = new DBHelper();
        calendar = Calendar.getInstance();
        tempDoBYear = calendar.get(Calendar.YEAR);
        tempDoBMonth = calendar.get(Calendar.MONTH);
        tempDoBDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Add back button in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Function for ActionBar back button.
     */
    public boolean onOptionsItemSelected(MenuItem item){
        cancelSignUp();
        return true;
    }

    /**
     * Overrides the back button from ending the activity.
     */
    public void onBackPressed(){
        cancelSignUp();
    }

    /**
     * Function to handle going back to LoginActivity.
     * If on email and password page, simply go back to LoginActivity.
     * If on info form, ask to confirm aborting account creation and go to LoginActivity if so.
     */
    public void cancelSignUp(){
        // If on email password fragment, start LoginActivity and end SignUpActivity
        if (mViewPager.getCurrentItem() <= 0) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            this.finish();
        } else {
        // Else, show user confirmation dialog and delete user from FireBase if confirmed
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.sign_up_abort_title))
                    .setTitle(getString(R.string.sign_up_abort_message))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dbHelper.getAuth().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this,
                                                getString(R.string.sign_up_abort_toast_message),
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        finish();
                                    }
                                }
                            });
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    /**
     * Fragment class for reading in email and password from user
     */
    public static class SignUpEmailPasswordFragment extends Fragment {

        public SignUpEmailPasswordFragment() {
        }

        /**
         * Returns a new instance of this fragment.
         */
        public static SignUpEmailPasswordFragment newInstance() {
            SignUpEmailPasswordFragment fragment = new SignUpEmailPasswordFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_sign_up_email_password, container, false);

            final EditText mEmailText = rootView.findViewById(R.id.fragment_sign_up_email_text);
            final EditText mPasswordText = rootView.findViewById(R.id.fragment_sign_up_password_text);
            final EditText mPasswordConfirmText = rootView.findViewById(R.id.fragment_sign_up_password_confirm_text);

            Button mEmailPasswordNextButton = rootView.findViewById(R.id.fragment_sign_up_email_password_next_button);

            mEmailPasswordNextButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Verifies email with StringValidator
                 * Displays error if email or password is invalid
                 * Creates user and continues to next fragment if valid
                 */
                public void onClick(View v) {
                    // Read current fields
                    tempEmail = mEmailText.getText().toString();
                    tempPass = mPasswordText.getText().toString();
                    tempPassConfirm = mPasswordConfirmText.getText().toString();

                    // Check if email is empty or invalid
                    if (tempEmail.isEmpty() || !StringValidator.isValidEmail(tempEmail)) {
                        mEmailText.setError(getString(R.string.sign_up_invalid_email));
                        mEmailText.requestFocus();
                        return;
                    } else
                        mEmailText.setError(null);

                    // Check if password is invalid
                    if (!StringValidator.isValidPassword(tempPass)) {
                        mPasswordText.setError(getString(R.string.sign_up_invalid_password));
                        mPasswordText.requestFocus();
                        return;
                    } else
                        mPasswordText.setError(null);

                    // Check if passwords match
                    if (!StringValidator.checkPasswords(tempPass, tempPassConfirm)) {
                        mPasswordConfirmText.setError(getString(R.string.sign_up_non_matching_passwords));
                        mPasswordConfirmText.requestFocus();
                        return;
                    } else
                        mPasswordConfirmText.setError(null);

                    // Use DBHelper to create the user and handle exceptions, continues to next fragment if successful
                    dbHelper.getAuth().createUserWithEmailAndPassword(tempEmail, tempPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            mPasswordText.setError(getString(R.string.sign_up_invalid_password));
                                            mPasswordText.requestFocus();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            mEmailText.setError(getString(R.string.sign_up_invalid_email));
                                            mEmailText.requestFocus();
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            mEmailText.setError(getString(R.string.sign_up_email_collision));
                                            mEmailText.requestFocus();
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                                    }

                                }
                            });
                }
            });
            return rootView;
        }
    }
    /**
     * Fragment class for reading in basic information from user
     */
    public static class SignUpInfoFormFragment extends Fragment {

        public SignUpInfoFormFragment() {
        }

        /**
         * Returns a new instance of this fragment.
         */
        public static SignUpInfoFormFragment newInstance() {
            SignUpInfoFormFragment fragment = new SignUpInfoFormFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_sign_up_info_form, container, false);

            final EditText mFirstNameText = rootView.findViewById(R.id.fragment_sign_up_info_form_first_name_text);
            final EditText mMiddleNameText = rootView.findViewById(R.id.fragment_sign_up_info_form_middle_name_text);
            final EditText mLastNameText = rootView.findViewById(R.id.fragment_sign_up_info_form_last_name_text);
            final TextView mDateText = rootView.findViewById(R.id.fragment_sign_up_info_form_date_text);
            final Button mDatePickerButton = rootView.findViewById(R.id.fragment_sign_up_info_form_date_picker_button);
            final Spinner mGenderSpinner = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_spinner);
            final CheckBox mGenderPreferenceCheckBoxMale = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_male_checkbox);
            final CheckBox mGenderPreferenceCheckBoxFemale = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_female_checkbox);
            final CheckBox mGenderPreferenceCheckBoxNonbinary = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_nonbinary_checkbox);
            final CheckBox mGenderPreferenceCheckBoxOther = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_other_checkbox);
            Button mSignUpButton = rootView.findViewById(R.id.fragment_sign_up_info_form_sign_up_button);

            mDatePickerButton.setOnClickListener(new View.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(View v){
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_DARK,new DatePickerDialog.OnDateSetListener() {
                        /**
                         * Uses DatePickerDialog to have user enter DoB upon click, displays current date
                         * 18 years ago as default option and stores user entered date in temp variables.
                         */
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            tempDoBYear = y;
                            tempDoBMonth = m + 1;
                            tempDoBDay = d;
                            String mDatePickerButtonString = getString(R.string.sign_up_dob) + "  :  "
                                    + tempDoBYear + "/" + tempDoBMonth + "/" + tempDoBDay;
                            mDatePickerButton.setText(mDatePickerButtonString);
                        }
                    }, calendar.get(Calendar.YEAR) - minAge, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });
            mSignUpButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    // Boolean flag to not create the user account if any fields are invalid.
                    boolean errorFlag = false;

                    // Read current fields
                    tempFirstName = mFirstNameText.getText().toString();
                    tempMiddleName = mMiddleNameText.getText().toString();
                    tempLastName = mLastNameText.getText().toString();

                    // Check first name for valid alpha and non empty
                    if (tempFirstName.isEmpty()){
                        errorFlag = true;
                        mFirstNameText.setError(getString(R.string.sign_up_missing_name));
                    } else if (!StringValidator.isAlpha(tempFirstName)){
                        errorFlag = true;
                        mFirstNameText.setError(getString(R.string.sign_up_invalid_name));
                    } else
                        mFirstNameText.setError(null);


                    // Check middle name for valid alpha
                    if (!StringValidator.isAlpha(tempMiddleName) && !tempMiddleName.isEmpty()){
                        errorFlag = true;
                        mMiddleNameText.setError(getString(R.string.sign_up_invalid_name));
                    } else
                        mMiddleNameText.setError(null);

                    // Check last name for valid alpha and non empty
                    if (tempLastName.isEmpty()){
                        errorFlag = true;
                        mLastNameText.setError(getString(R.string.sign_up_missing_name));
                    } else if (!StringValidator.isAlpha(tempLastName)){
                        errorFlag = true;
                        mLastNameText.setError(getString(R.string.sign_up_invalid_name));
                    } else
                        mLastNameText.setError(null);

                     // Check if user entered DoB
                    if (!checkOver18(tempDoBYear, tempDoBMonth, tempDoBDay)){
                        errorFlag = true;
                        mDateText.setTextColor(getResources().getColor(R.color.colorSignUpError));
                    } else {
                        mDateText.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                    }

                    // Check if user selected at least one gender
                    if (!mGenderPreferenceCheckBoxMale.isChecked() && !mGenderPreferenceCheckBoxFemale.isChecked() &&
                            !mGenderPreferenceCheckBoxOther.isChecked() && !mGenderPreferenceCheckBoxNonbinary.isChecked()){
                        errorFlag = true;
                        mGenderPreferenceCheckBoxMale.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                        mGenderPreferenceCheckBoxFemale.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                        mGenderPreferenceCheckBoxOther.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                        mGenderPreferenceCheckBoxNonbinary.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                    } else {
                        mGenderPreferenceCheckBoxMale.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                        mGenderPreferenceCheckBoxFemale.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                        mGenderPreferenceCheckBoxOther.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                        mGenderPreferenceCheckBoxNonbinary.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                    }

                    if (!errorFlag){
                        // Enter information into FireBase
                        dbHelper.fetchCurrentUser();
                        DatabaseReference ref = dbHelper.getDb().getReference().child(getString(R.string.sign_up_firebase_user)).child(dbHelper.getUser().getUid());

                        // Account
                        ref.child(getString(R.string.sign_up_firebase_email)).setValue(tempEmail);
                        ref.child(getString(R.string.sign_up_firebase_suspended)).setValue(R.string.sign_up_firebase_false);
                        ref.child(getString(R.string.sign_up_firebase_locked)).setValue(R.string.sign_up_firebase_false);

                        // Name
                        ref.child(getString(R.string.sign_up_firebase_firstname)).setValue(tempFirstName);
                        ref.child(getString(R.string.sign_up_firebase_middlename)).setValue(tempMiddleName);
                        ref.child(getString(R.string.sign_up_firebase_lastname)).setValue(tempLastName);

                        // Personal Info
                        ref.child(getString(R.string.sign_up_firebase_bio)).setValue(getString(R.string.sign_up_firebase_empty));
                        String tempDoB = "" + tempDoBYear + "-" + (tempDoBMonth < 10 ? "0" + tempDoBMonth : tempDoBMonth)
                                + "-" + (tempDoBDay < 10 ? "0" + tempDoBDay : tempDoBDay);
                        ref.child(getString(R.string.sign_up_firebase_dob)).setValue(tempDoB);
                        ref.child(getString(R.string.sign_up_firebase_gender)).setValue(mGenderSpinner.getSelectedItem().toString());

                        // Location
                        // TODO: Resolve undefined variables
                        ref.child(getString(R.string.sign_up_firebase_city)).setValue(getString(R.string.sign_up_firebase_empty));
                        ref.child(getString(R.string.sign_up_firebase_country)).setValue(getString(R.string.sign_up_firebase_empty));
                        ref.child(getString(R.string.sign_up_firebase_latitude)).setValue(getString(R.string.sign_up_firebase_empty));
                        ref.child(getString(R.string.sign_up_firebase_longitude)).setValue(getString(R.string.sign_up_firebase_empty));
                        ref.child(getString(R.string.sign_up_firebase_state)).setValue(getString(R.string.sign_up_firebase_empty));
                        
                        // Start LoginActivity and end SignUpActivity
                        Toast.makeText(getActivity(), getString(R.string.sign_up_success_toast_message), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            });
            return rootView;
        }

        private boolean checkOver18(int year, int month, int day){
            if (year > calendar.get(Calendar.YEAR) - minAge)
                return false;
            else if (year == calendar.get(Calendar.YEAR) - minAge && month > calendar.get(Calendar.MONTH))
                return false;
            else if (year == calendar.get(Calendar.YEAR) - minAge && month == calendar.get(Calendar.MONTH)
                    && day > calendar.get(Calendar.DAY_OF_MONTH))
                return false;
            return true;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a new instance of the corresponding fragment position.
            switch(position){
                case 0:
                    return SignUpEmailPasswordFragment.newInstance();
                case 1:
                    return SignUpInfoFormFragment.newInstance();
            }
            return SignUpEmailPasswordFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Total of 2 pages
            return 2;
        }
    }
}
