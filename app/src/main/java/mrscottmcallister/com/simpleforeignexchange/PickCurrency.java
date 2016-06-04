package mrscottmcallister.com.simpleforeignexchange;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
        searchResults = myDbHandler.searchDb("");
        searchResultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, searchResults);
        searchResultsView.setAdapter(searchResultsAdapter);
        searchBar = (EditText) findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });
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

    public void search(String searchString){
        searchResults.clear();
        searchResults.addAll(myDbHandler.searchDb(searchString));
        searchResultsAdapter.notifyDataSetChanged();
    }


}
