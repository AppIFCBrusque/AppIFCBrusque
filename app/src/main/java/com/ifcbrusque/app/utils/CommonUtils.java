package com.ifcbrusque.app.utils;

import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA_EXTENSO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    private CommonUtils() {
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String formatDate(Date date) {
        Locale brazilianLocale = new Locale("pt", "BR");
        DateFormat formatter = new SimpleDateFormat(FORMATO_DATA_EXTENSO, brazilianLocale);
        return formatter.format(date);
    }
}
