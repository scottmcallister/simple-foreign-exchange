package mrscottmcallister.com.simpleforeignexchange;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class PickCurrency extends AppCompatActivity {

    private DbHandler myDbHandler;
    private EditText searchBar;
    private ArrayList<String> searchResults;
    private ArrayAdapter<String> searchResultsAdapter;
    private ListView searchResultsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_currency);
        initDb();
        searchResultsView = (ListView) findViewById(R.id.listView);
        searchResults = myDbHandler.dbToArray();
        searchResultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, searchResults);
        searchResultsView.setAdapter(searchResultsAdapter);
    }

    public void initDb() {
        myDbHandler = new DbHandler(this, null, null, 1);
        try {
            myDbHandler.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHandler.openDataBase();
        } catch (SQLException sqle) {

            throw sqle;

        }
    }


}
