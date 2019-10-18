package com.bits.cps.Helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldValidetor {
    public static boolean email(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean name(String name) {
        String nameRegex = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}";
        Pattern pat = Pattern.compile(nameRegex);
        if (name == null)
            return false;
        return pat.matcher(name).matches();
    }
    public static boolean password(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,20}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
    public static boolean confirmPassword(String password, String confirmPassword){
        if (password.equals(confirmPassword))
        {
            return true;
        }
        return false;
    }
}
