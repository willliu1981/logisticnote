package idv.kw.tool;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {


    public static String formatGMTtoLocal(java.util.Date GMTDateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String format = formatter.format(GMTDateTime);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parse = formatter.parse(format);
            formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));



            return formatter.format(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatLocalToGMT(java.util.Date localDateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String format = formatter.format(localDateTime);

        return format;
    }


    public static Timestamp formatToTimestampGMTtoLocal(java.util.Date GMTDateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String format = formatter.format(GMTDateTime);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parse = formatter.parse(format);
            formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));


            String format1 = formatter.format(parse);
            Date parse1 = formatter.parse(format1);
            Timestamp timestamp = Timestamp.valueOf(format1);
            return  timestamp;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


}
