import java.util.*;
import java.io.*;
import javax.mail.*;

abstract private class User {

	String email;
	String first_name;
	String middle_name;
	String last_name;
	String birth_date;
	String gender;
	String city;
	String state;
	String country;
	String bio;
	float latitude;
	float longitude;
	boolean locked;
	boolean suspended;
	int age;
	int matched_user_id[];

	//int convertAge(String birth_date);
}