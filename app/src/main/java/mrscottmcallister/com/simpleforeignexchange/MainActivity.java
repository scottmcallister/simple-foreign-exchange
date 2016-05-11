package mrscottmcallister.com.simpleforeignexchange;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String url = "http://api.fixer.io/latest?base=";
    private String base = "usd";
    private TextView text;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get JSON data from API
        queue = Volley.newRequestQueue(this);
        text = (TextView) findViewById(R.id.hello_text);
        JsonObjectRequest getExchangeRates = new JsonObjectRequest(
                Request.Method.GET,
                url + base,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        text.setText("Response: " + response.toString());
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("error...");
                    }
                });
        queue.add(getExchangeRates);

        // Initialize DB with country info
        DbHandler myDbHandler = new DbHandler(this, null, null, 1);
        try {

            myDbHandler.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHandler.openDataBase();

        }catch(SQLException sqle){

            throw sqle;

        }

    }

}
