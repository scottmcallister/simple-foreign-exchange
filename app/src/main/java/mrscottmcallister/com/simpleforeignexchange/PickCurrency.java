package mrscottmcallister.com.simpleforeignexchange;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PickCurrency extends Activity {

    private DbHandler myDbHandler;
    private EditText searchBar;
    private ArrayList<Country> searchResults;
    private CountryAdapter searchResultsAdapter;
    private ListView searchResultsView;
    private String currencyToSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_currency);
        currencyToSet = getIntent().getExtras().getString("currencyToSet");
        Toast.makeText(getApplicationContext(),currencyToSet, Toast.LENGTH_LONG).show();
        initDb();
        searchResultsView = (ListView) findViewById(R.id.listView);
        searchResults = myDbHandler.searchDb("");
        searchResultsAdapter = new CountryAdapter(this, searchResults);
        searchResultsView.setAdapter(searchResultsAdapter);
        searchResultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(PickCurrency.this, MainActivity.class);
                String code = searchResults.get(position).get_code();
                String symbol = searchResults.get(position).get_symbol();
                Bundle bundle = new Bundle();
                bundle.putString(currencyToSet, code);
                bundle.putString("symbol", symbol);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
