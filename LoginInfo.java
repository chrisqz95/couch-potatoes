import java.util.*;
import java.io.*;
import javax.mail.*;

private class LoginInfo {
	String email = LoginActivity.getEmail();
	String password = LoginActivity.getPassword();

	/*public static boolean isValidEmailAddress(String email) {
   	boolean result = true;

   	try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
   	} catch (AddressException ex) {
      result = false;
   	}
   	return result;*/
	}
}