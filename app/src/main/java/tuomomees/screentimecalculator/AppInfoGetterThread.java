package tuomomees.screentimecalculator;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Luokan on luonut tuomo päivämäärällä 21.9.2017.
 */


//Thread, jossa on tarkoitus hakea appien käytöttiedot ja tallettaa ne AppInfo -luokan avulla olioihin
class AppInfoGetterThread extends Thread implements Runnable{

    //Observer, jota käytetään UI -metodien käyttämiseen fragmentista
    private ThreadReport observer = null;
    private boolean running = true;

    private Converter timeConverter;
    private AppStatsManager aStatsManager;
    private Context sentContext;

    //Olioiden lista, johon talletetaan appInfo -oliot
    private List<AppInfo> appInfoObjectList;


    //TESTISTISTI
    private Activity sentActivity;

    /*
    AppInfoGetterThread(ThreadReport newObserver)
    {
        this.observer = newObserver;
    }
*/

    public void run() {

        try {
            while (running) {
                //Log.d("Käynnistetään Thread ", "AppInfoGetter");
                //Do Stuff

                initializeVariables();
                //updateUiThread();
                //getSpinnerItemFromFragment();
                getStats();
                checkSavedStats();

                //When ready set running = false
                //Log.d("Sammutetaan thread ", "AppInfoGetter");
                running = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initializeContext(Context c)
    {
        sentContext = c;
    }

    private void initializeVariables()
    {
        //aStatsManager = new AppStatsManager(sentContext);
        timeConverter = new Converter();
        appInfoObjectList = new ArrayList<>();
    }

    void initializeActivity(Activity a)
    {
        sentActivity = a;
    }

    //Metodi, jossa haetaan appien infot
    private void getStats() {

        final UsageStatsManager lUsageStatsManager = (UsageStatsManager) sentContext.getSystemService(Context.USAGE_STATS_SERVICE);

        Map<String, UsageStats> mapOfAppsUsage = null;


        //TODO: tähän hakuajat spinnerin perusteella
        long end = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(3);
        //timezone "UTC" toimii ainoastaan jos käytössä on GMT + 0
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+03:00"));
        TimeZone tz = cal.getTimeZone();
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long begin = cal.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mapOfAppsUsage = lUsageStatsManager.queryAndAggregateUsageStats(begin, end);
        }

        List<UsageStats> listOfAppsUsage = new ArrayList<>();
        listOfAppsUsage.addAll(mapOfAppsUsage.values());

        //Looppi, joka käy läpi käyttäjän kaikki appsit
        for (UsageStats lUsageStats : listOfAppsUsage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                //Hakee applikaation käyttöajan
                long totalTimeInForeground = lUsageStats.getTotalTimeInForeground();

                //Hakee koska appia on viimeksi käytetty ja tallettaa sen long tyyppiseen muuttujaan
                long lastTimeUsed = lUsageStats.getLastTimeUsed();

                //Luodaan applikaation infot sisältävä uusi olio joka "kierroksella"
                AppInfo newApp = new AppInfo();
                newApp.setAppLastTimeUsedMillis(lastTimeUsed);
                //newApp.setAppName(aStatsManager.getAppLabel(lUsageStats.getPackageName(), sentContext)); //TODO: tähän tarvitaan konteksti
                newApp.setAppPackageName(lUsageStats.getPackageName());
                newApp.setAppTotalUsageTime(totalTimeInForeground);
                //newApp.setAppIcon(aStatsManager.getIconDrawable(lUsageStats.getPackageName()));

                //Lisätään juuri luotu olio olioiden listaan
                appInfoObjectList.add(newApp);
            }
        }
    }

    //Metodi tietojen tarkistamista varten Logista
    private void checkSavedStats()
    {
        for(int i = 0; i < appInfoObjectList.size(); i++)
        {
            String printable;
            printable = appInfoObjectList.get(i).getAppName() + " " + appInfoObjectList.get(i).getAppPackageName() +
                    " " + appInfoObjectList.get(i).getAppLastTimeUsedMillis() + " " + appInfoObjectList.get(i).getAppTotalUsageTime();

            Log.d("AppInfo ", printable);
        }
    }

    private void getSpinnerItemFromFragment()
    {
        String spinnerSelection = observer.getSpinnerItem();
    }

    //Thread ForceClose
    void stopThread()
    {
        running = false;
    }

    interface ThreadReport
    {
        //void setTextViewTexts();
        String getSpinnerItem();
    }

    //Metodi, jossa voidaan tehdä asioita UI threadissa
    private void updateUiThread()
    {
        sentActivity.runOnUiThread(new Runnable() {
            public void run(){
                initializeVariables();
            }
        });
    }
}
