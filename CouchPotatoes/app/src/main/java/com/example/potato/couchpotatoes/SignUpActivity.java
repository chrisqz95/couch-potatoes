package com.cse110.potato.couchpotatoes;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

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
    private static CurrentUser currentUser = new CurrentUser();
    private static DBHelper dbHelper = new DBHelper();
    private static String tempPass;

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
     * Fragment class for reading in email from user
     */
    public static class SignUpEmailFragment extends Fragment {

        public SignUpEmailFragment() {
        }

        /**
         * Returns a new instance of this fragment.
         */
        public static SignUpEmailFragment newInstance() {
            SignUpEmailFragment fragment = new SignUpEmailFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_sign_up_email, container, false);

            final EditText emailText = rootView.findViewById(R.id.fragment_sign_up_email_text);
            Button nextButton = rootView.findViewById(R.id.fragment_sign_up_email_next_button);

            nextButton.setOnClickListener(new View.OnClickListener(){
                /**
                 * Verifies email with StringValidator
                 * Displays error if email is invalid - continues to next fragment if valid
                 */
                public void onClick(View v){
                    if (StringValidator.isValidEmail(emailText.getText().toString())){
                        currentUser.setEmail(emailText.getText().toString());
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    } else {
                        emailText.setError(getString(R.string.error_invalid_email));
                    }
                }
            });
            return rootView;
        }
    }
    /**
     * Fragment class for reading in password from user
     */
    public static class SignUpPasswordFragment extends Fragment {

        public SignUpPasswordFragment() {
        }

        /**
         * Returns a new instance of this fragment.
         */
        public static SignUpPasswordFragment newInstance() {
            SignUpPasswordFragment fragment = new SignUpPasswordFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_sign_up_password, container, false);

            final EditText passwordText = rootView.findViewById(R.id.fragment_sign_up_password_text);
            final EditText passwordConfirmText = rootView.findViewById(R.id.fragment_sign_up_password_confirm_text);
            Button nextButton = rootView.findViewById(R.id.fragment_sign_up_password_next_button);

            nextButton.setOnClickListener(new View.OnClickListener(){
                /**
                 * Verifies password with StringValidator
                 * Displays error if password is invalid or does not match
                 * continues to next fragment if valid
                 */
                public void onClick(View v){
                    if (StringValidator.isValidPassword(passwordText.getText().toString())) {
                        if (StringValidator.checkPasswords(passwordText.getText().toString(), passwordConfirmText.getText().toString())) {
                            tempPass = passwordText.getText().toString();
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                        } else {
                            passwordConfirmText.setError(getString(R.string.sign_up_non_matching_passwords));
                        }
                    } else {
                        passwordText.setError(getString(R.string.sign_up_invalid_password));
                    }
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        /**
                         * Uses DatePickerDialog to have user enter DoB upon click, displays current date as default option and
                         * stores user entered date in currentUser.dob string variable.
                         */
                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            currentUser.setDob("" + y + "/" + (m + 1) + "/" + d);
                            datePickerButton.setText(getString(R.string.sign_up_dob) + "  :  " + currentUser.getDob());
                        }
                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
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
                    if (currentUser.getDob() == null){
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
                    return SignUpEmailFragment.newInstance();
                case 1:
                    return SignUpPasswordFragment.newInstance();
                case 2:
                    return SignUpInfoFormFragment.newInstance();
            }
            return SignUpEmailFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Total of 3 pages
            return 3;
        }
    }
}
