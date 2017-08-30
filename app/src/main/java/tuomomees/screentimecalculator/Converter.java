package tuomomees.screentimecalculator;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


class Converter {

    long convertMillisToMinutes(long millis)
    {
        return (millis / 1000) / 60;
    }

    String convertMillisToHoursMinutesSeconds(long millis)
    {
        //Log.d("Muunnetaan", String.valueOf(millis));

        //@SuppressLint("DefaultLocale") String usageTime = String.format("%02d hour, %02d min, %02d sec",
        //@SuppressLint("DefaultLocale") String usageTime = String.format("%02d hour, %02d min, %02d sec",

        if(millis >= TimeUnit.HOURS.toMillis(1))
        {
            @SuppressLint("DefaultLocale") String usageTime = String.format("%d hour, %d min, %d sec",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            return usageTime;
        }

        if(millis >= TimeUnit.MINUTES.toMillis(1))
        {
            @SuppressLint("DefaultLocale") String usageTime = String.format("%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            return usageTime;
        }

        if(millis >= TimeUnit.SECONDS.toMillis(1))
        {
            @SuppressLint("DefaultLocale") String usageTime = String.format("%d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis));

            return usageTime;
        }

        else
        {
            Log.d("Error code", "1");
            return "Error code: 1";
        }

        //Log.d("Muunnettu", usageTime);
    }

    @SuppressLint("DefaultLocale")
    protected String convertMillisToMinutesSeconds(long millis)
    {
        return String.format("%02d min, %02d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    String convertMillisToDate(long millis)
    {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(millis);

        //Tämä vain jos UTC
        calendar.add(Calendar.HOUR, 3);

        String date = "" + calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
        String time = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

        @SuppressLint("DefaultLocale") String formattedTime = String.format("%02d:%02d:%02d %d.%d.%04d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR));

        return formattedTime;
    }
}
