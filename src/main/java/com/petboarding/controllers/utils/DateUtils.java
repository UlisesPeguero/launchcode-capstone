package com.petboarding.controllers.utils;

import net.bytebuddy.TypeCache;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static SimpleDateFormat parseFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static SimpleDateFormat showFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

    public static String format(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
        return formatter.format(date);
    }

    public static Date parse(String strDate, String format) {
        SimpleDateFormat parser = new SimpleDateFormat(format, Locale.ENGLISH);
        Date date = null;
        try{
            date = parser.parse(strDate);
        } catch(ParseException exception) {
            //TODO handle the exception
        }
        return date;
    }

    public static Long getDaysDifference(Date fromDate, Date toDate) {
        LocalDate from = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(from, to);
    }
}
