package se.frand.app.todo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by victorfrandsen on 10/21/15.
 */
public class Util {
    public static final String MDY_DATE_FORMAT =  "MM/dd/y";
    public static final String SQL_DATE_FORMAT = "yyyyMMdd";

    public static String getDate(Date date, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return formatter.format(calendar.getTime());
    }
}
