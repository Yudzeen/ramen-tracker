package ebj.yujinkun.ramentracker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class DateUtils {

    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_DATE_ONLY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DATE_PRETTY = "MMMM d, yyyy";
    public static final String DATE_FORMAT_DATE_AND_TIME_PRETTY = "MMMM d, yyyy  h:mm a";

    public static String formatDate(int year, int month, int day) {
        return formatDate(year, month, day, DATE_FORMAT_DEFAULT);
    }

    public static String formatDate(int year, int month, int day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return formatDate(calendar.getTimeInMillis(), format);
    }

    public static String formatDate(long dateInMillis) {
        return formatDate(dateInMillis, DATE_FORMAT_DEFAULT);
    }

    public static String formatDate(long dateInMillis, String dateFormat) {
        Date date = new Date(dateInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        return sdf.format(date);
    }

    public static String formatDate(String date, String currentFormat, String newFormat) {
        return formatDate(DateUtils.getDate(date, currentFormat), newFormat);
    }

    public static long getDate(String date, String dateFormat) {
        try {
            return Objects.requireNonNull(new SimpleDateFormat(dateFormat, Locale.US).parse(date)).getTime();
        } catch (ParseException e) {
            Timber.e(e, "Unable to parse: %s", date);
        }
        return -1;
    }

    public static long getDate(String date) {
        return getDate(date, DATE_FORMAT_DEFAULT);
    }

    public static String getCurrentDate() {
        return formatDate(getCurrentTimeInMillis());
    }

    public static long getCurrentTimeInMillis() {
        return new Date().getTime();
    }

}
