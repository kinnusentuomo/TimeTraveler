package tuomomees.screentimecalculator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    private AppStatsManager appStatsManager;
    private Context sentContext;

    //Olioiden lista, johon talletetaan appInfo -oliot
    private List<AppInfo> appInfoObjectList;


    //TESTISTISTI
    private Activity sentActivity;
    private ArrayAdapter sentAdapter;
    private ArrayList<String> listItems;
    private ArrayList<Model> sentModels;

    String sentSpinnerSelection;



    void initializeAdapter(ArrayAdapter a)
    {
        sentAdapter = a;
    }

    void initializeList(ArrayList list)
    {
        listItems = list;
    }

    AppInfoGetterThread(ThreadReport newObserver)
    {
        this.observer = newObserver;
    }


    public void run() {

        try {
            while (running) {
                //Log.d("Käynnistetään Thread ", "AppInfoGetter");
                //Do Stuff

                setStartValues();
                initializeVariables();
                sentSpinnerSelection = observer.getSpinnerItem();
                //updateUiThread();
                //getSpinnerItemFromFragment();
                getStats();
                checkSavedStats();
                checkMostUsedApp();
                orderArrayFromHighestToLowest();

                //printResults();
                updateUiThread();

                //observer.generateData(Drawable icon, String appName, int appRanking);



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

    void initializeModelList(ArrayList list)
    {
        sentModels = list;
    }

    private void setStartValues()
    {
        if(appInfoObjectList != null)
        {
            appInfoObjectList.clear();
        }
        else if(sentModels != null)
        {
            sentModels.clear();
        }
        else
        {
            Log.d("appInfoObjectList", "empty");
        }
    }

    private void initializeVariables()
    {
        //aStatsManager = new AppStatsManager(sentContext);
        timeConverter = new Converter();
        appInfoObjectList = new ArrayList<>();

    }

    private void printResults()
    {
        for(int i = 0; i < appInfoObjectList.size(); i++)
        {
            observer.addTextToListView(appInfoObjectList.get(i).getAppPackageName() +  " " +
                appInfoObjectList.get(i).getAppTotalUsageTime());
        }
    }

    void initializeActivity(Activity a)
    {
        sentActivity = a;
    }

    //Metodi, jossa haetaan appien infot
    private void getStats() {

        final UsageStatsManager lUsageStatsManager = (UsageStatsManager) sentContext.getSystemService(Context.USAGE_STATS_SERVICE);

        Map<String, UsageStats> mapOfAppsUsage = null;

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long begin = 0;
        long end = 0;

        //DAILY
        if(sentSpinnerSelection.equals(sentContext.getResources().getString(R.string.daily_text)))
        {
            begin = cal.getTimeInMillis();
            end = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(3);
        }

        //WEEKLY
        if(sentSpinnerSelection.equals(sentContext.getResources().getString(R.string.weekly_text)))
        {

        }

        //MONTHLY
        if(sentSpinnerSelection.equals(sentContext.getResources().getString(R.string.monthly_text)))
        {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            begin = cal.getTimeInMillis();
            end = System.currentTimeMillis();
        }

        //YEARLY
        if(sentSpinnerSelection.equals(sentContext.getResources().getString(R.string.yearly_text)))
        {

        }

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

    private void checkMostUsedApp()
    {
        long max = 0;

        for (int counter = 1; counter < appInfoObjectList.size(); counter++)
        {
            if(appInfoObjectList.get(counter).getAppTotalUsageTime() > max)
            {
                max = appInfoObjectList.get(counter).getAppTotalUsageTime();
            }
        }

        Log.d("Max value is", String.valueOf(max));
    }

    //Metodi, jolla voi järjestää array:n haluamansa arvon mukaan
    private void orderArrayFromHighestToLowest()
    {
        Collections.sort(appInfoObjectList, new Comparator<AppInfo>() {
            @Override public int compare(AppInfo p1, AppInfo p2) {
                return (int) (p1.getAppTotalUsageTime() - p2.getAppTotalUsageTime()); // Ascending
            }
        });

        //Käännetään vielä toisinpäin, jotta eniten käytetty ylimpänä
        Collections.reverse(appInfoObjectList);

        for (int i = 0; i < appInfoObjectList.size(); i++)
        {
            String printable;
            printable = appInfoObjectList.get(i).getAppName() + " " + appInfoObjectList.get(i).getAppPackageName() +
                    " " + appInfoObjectList.get(i).getAppLastTimeUsedMillis() + " " + appInfoObjectList.get(i).getAppTotalUsageTime();
            Log.d("Ordered", printable);
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
        void addTextToListView(String str);
    }

    public void initializeAppStatsManager(AppStatsManager a)
    {
        appStatsManager = a;
    }

    //Metodi, jossa voidaan tehdä asioita UI threadissa
    private void updateUiThread()
    {
        sentActivity.runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            public void run(){

                boolean runThis = true;
                //appStatsManager = new AppStatsManager(sentContext);

                while(runThis)
                {
                    for(int i = 0; i < appInfoObjectList.size(); i++)
                    {
                        Drawable icon;
                        icon = appStatsManager.getIconDrawable(appInfoObjectList.get(i).getAppPackageName());


                        long millis = appInfoObjectList.get(i).getAppTotalUsageTime();

                        String convertedTime = String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                        sentModels.add(new Model(icon , appStatsManager.getAppLabel(appInfoObjectList.get(i).getAppPackageName(), sentContext), convertedTime));
                    }

                    sentAdapter.notifyDataSetChanged();
                    Log.d("Thread valmis", "tuhotaan");
                    runThis = false;
                }
            }
        });
    }
}
