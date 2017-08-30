package tuomomees.screentimecalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class MainActivity extends FragmentActivity{

    //näytön koon muuttujat
    int height, width;

    MyPagerAdapter adapterViewPager;
    SwipeRefreshLayout swipeLayout;

    Top5AppsFragment top5AppsFragment;
    LastTimeUsedFragment lastTimeUsedFragment;
    WeeklyBarDiagramFragment weeklyBarDiagramFragment;


    Thread appStatsQueryThread;
    Thread appStatsWeeklyQueryThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Teema täytyy kutsua ennen super.onCreate() -metodia
        //setTheme(R.style.AppTheme);

        //SystemClock.sleep(5000);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        top5AppsFragment = new Top5AppsFragment();
        lastTimeUsedFragment = new LastTimeUsedFragment();
        weeklyBarDiagramFragment = new WeeklyBarDiagramFragment();

        //Alustaa liukupäivityksen käyttöön
        initializeSwipeRefresh();

        //Alustaa viewpagerin käyttöön
        initializeViewPager();

        //Tarkistaa mm. näytön koon
        checkDisplayStats();

        //Alustaa Threadin ja threadhandlerin
        initializeThreads();

        //Tekee notification barista läpinäkyvän
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public void initializeThreads()
    {
        Context context = this.getApplicationContext();
        appStatsQueryThread = new AppStatsQueryThread(context);
        appStatsWeeklyQueryThread = new AppStatsWeeklyQueryThread(context);
        appStatsWeeklyQueryThread.run();
        appStatsQueryThread.run();
        //odotellaan, että thread on valmis
        try {
            appStatsQueryThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Metodi, joka alustaa käyttöön SwipeRefreshin
    protected void initializeSwipeRefresh()
    {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d("SwipeRefresh", "Päivitys aloitettu");

                        refreshFragment(top5AppsFragment);
                        refreshFragment(lastTimeUsedFragment);

                        //Ajetaan Threadin runnable uudelleen
                        appStatsQueryThread.run();

                        //Odotellaan, että THREAD on valmis
                        try {
                            appStatsQueryThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d("Threadin odottaminen", "ei onnistunut");
                        }

                        top5AppsFragment.getStats();
                        top5AppsFragment.setIconDrawable();
                        top5AppsFragment.setTextViewTexts();

                        swipeLayout.setRefreshing(false);
                        String toastRefreshningReady = getResources().getString(R.string.refreshing_ready);
                        Toast.makeText(MainActivity.this, toastRefreshningReady , Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    protected void initializeViewPager()
    {
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 3;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    //return Top5AppsFragment.newInstance(position);
                    return top5AppsFragment;
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    //return LastTimeUsedFragment.newInstance(1, "Page");
                    return lastTimeUsedFragment;
                case 2: // Fragment # 1 - This will show SecondFragment
                    //return top5AppsFragment;
                    //return null;
                    return weeklyBarDiagramFragment;
                default:
                    return null;
            }
        }

        //Metodi, jossa voi muuttaa sivun yläpalkin esittelytekstin (position palauttaa  sivunumeron 0-2)
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getResources().getString(R.string.top5appspage_title);
                case 1:
                    return getResources().getString(R.string.lastusedpage_title);
                case 2: 
                    return "Weekly Diagram";
                default:
                    return "Page" + position;
            }
        }
    }

    //TODO: muistinhallinta
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        finish();
        appStatsQueryThread.interrupt();
    }

    private void checkDisplayStats()
    {
        //Haetaan näytön koko muuttujiin
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Näytön koon tsekkaaminen Logissa
        Log.d("Näytön koko on", height + "x" + width);
    }

    @Override
    protected void onResume()
    {
        Log.d("mActivity", "onResume()");
        //refreshFragment(top5AppsFragment);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    //Metodi, jolla voi uudelleenkäynnistää fragmentin, jolloin se luodaan uudelleen
    protected void refreshFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }

    //Metodi, jolla voi sulkea fragmentin
    protected void detachFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
    }
}
