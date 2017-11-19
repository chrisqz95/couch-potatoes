package com.example.potato.couchpotatoes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

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
    private static DBHelper dbHelper = new DBHelper();
    private static Calendar calendar;
    private static String tempEmail = "";
    private static String tempPass = "";
    private static String tempPassConfirm = "";
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

        calendar = Calendar.getInstance();
        tempDoBYear = calendar.get(Calendar.YEAR);
        tempDoBMonth = calendar.get(Calendar.MONTH);
        tempDoBDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Overrides the back button from ending the activity to returning to the previous fragment.
     * If on first fragment, ends activity.
     */
    public void onBackPressed(){
        if (mViewPager.getCurrentItem() <= 0)
            this.finish();
        else
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
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

            final EditText emailText = rootView.findViewById(R.id.fragment_sign_up_email_text);
            final EditText passwordText = rootView.findViewById(R.id.fragment_sign_up_password_text);
            final EditText passwordConfirmText = rootView.findViewById(R.id.fragment_sign_up_password_confirm_text);

            Button nextButton = rootView.findViewById(R.id.fragment_sign_up_email_password_next_button);

            nextButton.setOnClickListener(new View.OnClickListener() {
                /**
                 * Verifies email with StringValidator
                 * Displays error if email is invalid - continues to next fragment if valid
                 */
                public void onClick(View v) {
                    tempEmail = emailText.getText().toString();
                    tempPass = passwordText.getText().toString();
                    tempPassConfirm = passwordConfirmText.getText().toString();

                    if (tempEmail.isEmpty() || !StringValidator.isValidEmail(tempEmail)) {
                        emailText.setError(getString(R.string.sign_up_invalid_email));
                        emailText.requestFocus();
                        return;
                    }

                    if (!StringValidator.isValidPassword(tempPass)) {
                        passwordText.setError(getString(R.string.sign_up_invalid_password));
                        passwordText.requestFocus();
                        return;
                    }

                    if (!StringValidator.checkPasswords(tempPass, tempPassConfirm)) {
                        passwordConfirmText.setError(getString(R.string.sign_up_non_matching_passwords));
                        passwordConfirmText.requestFocus();
                        return;
                    }

                    dbHelper.getAuth().createUserWithEmailAndPassword(tempEmail, tempPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            passwordText.setError(getString(R.string.sign_up_invalid_password));
                                            passwordText.requestFocus();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            emailText.setError(getString(R.string.sign_up_invalid_email));
                                            emailText.requestFocus();
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            emailText.setError(getString(R.string.sign_up_email_collision));
                                            emailText.requestFocus();
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

            final EditText firstNameText = rootView.findViewById(R.id.fragment_sign_up_info_form_first_name_text);
            final EditText middleNameText = rootView.findViewById(R.id.fragment_sign_up_info_form_middle_name_text);
            final EditText lastNameText = rootView.findViewById(R.id.fragment_sign_up_info_form_last_name_text);
            final Button datePickerButton = rootView.findViewById(R.id.fragment_sign_up_info_form_date_picker_button);
            final CheckBox genderPreferenceCheckBoxMale = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_male_checkbox);
            final CheckBox genderPreferenceCheckBoxFemale = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_female_checkbox);
            final CheckBox genderPreferenceCheckBoxOther = rootView.findViewById(R.id.fragment_sign_up_info_form_gender_preference_other_checkbox);
            Button signUpButton = rootView.findViewById(R.id.fragment_sign_up_info_form_sign_up_button);

            datePickerButton.setOnClickListener(new View.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onClick(View v){
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_DARK,new DatePickerDialog.OnDateSetListener() {
                        /**
                         * Uses DatePickerDialog to have user enter DoB upon click, displays current date as default option and
                         * stores user entered date in currentUser.dob string variable.
                         */
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            tempDoBYear = y;
                            tempDoBMonth = m + 1;
                            tempDoBDay = d;
                            String datePickerButtonString = getString(R.string.sign_up_dob) + "  :  "
                                    + tempDoBYear + "/" + tempDoBMonth + "/" + tempDoBDay;
                            datePickerButton.setText(datePickerButtonString);
                        }
                    }, calendar.get(Calendar.YEAR) - minAge, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });
            signUpButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    /**
                     * Boolean flag to not create the user account if any fields are invalid.
                     */
                    boolean errorFlag = false;
                    /**
                     * Check first name for valid alpha and non empty
                     */
                    if (firstNameText.getText().toString().isEmpty()){
                        errorFlag = true;
                        firstNameText.setError(getString(R.string.sign_up_missing_name));
                    } else if (!StringValidator.isAlpha(firstNameText.getText().toString())){
                        errorFlag = true;
                        firstNameText.setError(getString(R.string.sign_up_invalid_name));
                    }

                    /**
                     * Check middle name for valid alpha
                     */
                    if (!StringValidator.isAlpha(middleNameText.getText().toString()) && !middleNameText.getText().toString().isEmpty()){
                        errorFlag = true;
                        middleNameText.setError(getString(R.string.sign_up_invalid_name));
                    }

                    /**
                     * Check last name for valid alpha and non empty
                     */
                    if (lastNameText.getText().toString().isEmpty()){
                        errorFlag = true;
                        lastNameText.setError(getString(R.string.sign_up_missing_name));
                    } else if (!StringValidator.isAlpha(lastNameText.getText().toString())){
                        errorFlag = true;
                        lastNameText.setError(getString(R.string.sign_up_invalid_name));
                    }

                    /**
                     * Check if user entered DoB
                     */
                    if (!checkOver18(tempDoBYear, tempDoBMonth, tempDoBDay)){
                        errorFlag = true;
                        datePickerButton.setTextColor(getResources().getColor(R.color.colorSignUpError));
                    } else {
                        datePickerButton.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                    }

                    /**
                     * Check if user selected at least one gender
                     */
                    if (!genderPreferenceCheckBoxMale.isChecked() && !genderPreferenceCheckBoxFemale.isChecked() && !genderPreferenceCheckBoxOther.isChecked()){
                        errorFlag = true;
                        genderPreferenceCheckBoxMale.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                        genderPreferenceCheckBoxFemale.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                        genderPreferenceCheckBoxOther.setTextColor((getResources().getColor(R.color.colorSignUpError)));
                    } else {
                        genderPreferenceCheckBoxMale.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                        genderPreferenceCheckBoxFemale.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                        genderPreferenceCheckBoxOther.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                    }

                    if (!errorFlag){
                        currentUser.setFirstName(firstNameText.getText().toString());
                        currentUser.setMiddleName(middleNameText.getText().toString());
                        currentUser.setLastName(lastNameText.getText().toString());

                        // TODO: Logic to connect to FireBase via DBHelper

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
