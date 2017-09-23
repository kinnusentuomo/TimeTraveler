package tuomomees.screentimecalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Luokan on luonut tuomo päivämäärällä 22.9.2017.
 */

public class MostUsedAppsFragment extends Fragment implements AdapterView.OnItemSelectedListener, AppInfoGetterThread.ThreadReport {

    View view;
    ListView applicationListView;
    Spinner spinner;
    ArrayList<String> listItems=new ArrayList<>();
    ArrayAdapter<String> adapter;
    AppInfoGetterThread appInfoGetterThread;
    ArrayList<Model> models = new ArrayList<Model>();
    MyAdapter mAdapter;
    String spinnerSelection;
    AppStatsManager appStatsManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mostusedapps, container, false);

        appStatsManager = new AppStatsManager(this.getContext());

        initializeWidgets();
        initializeSpinner();
        startThread();

        return view;
    }

    public void initializeWidgets()
    {

        /*
        applicationListView = (ListView) view.findViewById(R.id.applicationList);

        adapter = new ArrayAdapter<>(this.getActivity(),
        //adapter = new ArrayAdapter<String>(MostUsedAppsFragment.this,
                //R.layout.custom_listview,
                android.R.layout.simple_list_item_1,
                listItems);
        applicationListView.setAdapter(adapter);
        */

        // 1. pass context and data to the custom adapter
        mAdapter = new MyAdapter(this.getActivity(), generateData());

        // if extending Activity 2. Get ListView from activity_main.xml
        ListView listView = (ListView) view.findViewById(R.id.applicationList);

        // 3. setListAdapter
        listView.setAdapter(mAdapter);




    }

    public void initializeSpinner()
    {
        //Alustetaan Spinner -pudotavalikko
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerTimeSelection);
        spinner.setOnItemSelectedListener(this);

        //Luodaan adapteri spinnerille
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                //R.array.queries_array, android.R.layout.simple_spinner_item);
                R.array.queries_array, android.R.layout.simple_spinner_item);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_layout);



        spinner.setAdapter(adapter);
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
        //appInfoGetterThread.initializeAdapter(adapter);
        appInfoGetterThread.initializeAdapter(mAdapter);
        appInfoGetterThread.initializeList(listItems);
        appInfoGetterThread.initializeModelList(models);
        appInfoGetterThread.start();
    }


    public void addTextToListView(String str)
    {
        //counter++;
        //listItems.add(counter + ": " + str + "\n");

        if(str != null)
        {
            listItems.add(str);
            adapter.notifyDataSetChanged();
        }
        else
        {
            //listItems.add("Failed to add data.");
            Log.d("Error ", "cannot set list text");
            adapter.notifyDataSetChanged();
        }

        Log.d("Adding to list", str);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerSelection = parent.getItemAtPosition(position).toString();
        Log.d("MostUsedaps spinner", spinnerSelection);

        startThread();
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public String getSpinnerItem() {

        if(spinnerSelection != null)
        {
            return spinnerSelection;
        }
        else
        {
            return null;
        }
    }
}
