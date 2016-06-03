package mrscottmcallister.com.simpleforeignexchange;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;

public class PickCurrency extends AppCompatActivity {

    private DbHandler myDbHandler;
    private EditText searchBar;
    private ListView searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_currency);
        initDb();
        Log.v("DB", myDbHandler.dbToString());
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
