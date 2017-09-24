package tuomomees.screentimecalculator;

/**
 * Created by tuomo on 24.9.2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Luokan on luonut tuomo päivämäärällä 22.9.2017.
 */

public class LastUsedAppsFragment extends Fragment implements AppInfoGetterThread.ThreadReport{

    View view;
    ArrayList<String> listItems=new ArrayList<>();
    ArrayAdapter<String> adapter;
    AppInfoGetterThread appInfoGetterThread;
    ArrayList<Model> models = new ArrayList<Model>();
    MyAdapter mAdapter;
    AppStatsManager appStatsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lastusedapps, container, false);

        appStatsManager = new AppStatsManager(this.getContext());
        initializeWidgets();
        startThread();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    public void initializeWidgets()
    {
        // 1. pass context and data to the custom adapter
        mAdapter = new MyAdapter(this.getActivity(), generateData());

        // if extending Activity 2. Get ListView from activity_main.xml
        ListView listView = (ListView) view.findViewById(R.id.applicationLastUsedList);

        // 3. setListAdapter
        listView.setAdapter(mAdapter);
    }

    public ArrayList<Model> generateData(){

        //models.add(new Model(R.drawable.app_icon_png_48,"Example item 1","1"));
        return models;
    }

    public void startThread()
    {
        appInfoGetterThread = new AppInfoGetterThread(this);
        appInfoGetterThread.initializeActivity(this.getActivity());
        appInfoGetterThread.initializeContext(this.getContext());
        appInfoGetterThread.initializeAppStatsManager(appStatsManager);
        appInfoGetterThread.initializeAdapter(mAdapter);
        appInfoGetterThread.initializeList(listItems);
        appInfoGetterThread.initializeModelList(models);
        appInfoGetterThread.setOrderParameter("lastTimeUsed");
        appInfoGetterThread.start();

        Log.d("ASD", String.valueOf(this.getActivity()));
    }

    @Override
    public String getSpinnerItem() {
        return null;
    }
}

