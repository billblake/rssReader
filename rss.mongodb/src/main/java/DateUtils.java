import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class DateUtils {

    public static String formatDate(Date pubDate) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
        Calendar pubDateCalender = Calendar.getInstance();
        pubDateCalender.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
        pubDateCalender.setTime(pubDate);

        if (nowCalendar.get(DATE) == pubDateCalender.get(DATE) &&
            nowCalendar.get(MONTH) == pubDateCalender.get(MONTH) &&
            nowCalendar.get(YEAR) == pubDateCalender.get(YEAR)) {

            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
            timeFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
            return timeFormatter.format(pubDate);
        } else {
            SimpleDateFormat time = new SimpleDateFormat("MMM dd");
            return time.format(pubDate);
        }
    }
}
