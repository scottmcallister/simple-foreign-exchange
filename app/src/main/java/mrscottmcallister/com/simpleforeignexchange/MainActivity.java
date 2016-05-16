package mrscottmcallister.com.simpleforeignexchange;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String url = "https://currency-exchange.p.mashape.com/exchange";
    private String from = "USD";
    private String to = "CAD";
    private String apiKey = "PLum3weAI4mshDYIvKhIyYvSuNSHp1EzY7fjsneJdFaHScp6zQ";
    private SurfaceView left;
    private SurfaceView right;
    private String selected = "left";
    private RequestQueue queue;
    private DbHandler myDbHandler;
    private Calculator calculator;
    private TextView leftTotal;
    private TextView rightTotal;
    private double baseTotal;
    private double altTotal;
    private double rate;
    private NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        left = (SurfaceView) findViewById(R.id.left);
        right = (SurfaceView) findViewById(R.id.right);
        altTotal = 0.5;
        baseTotal = 1.0;
        leftTotal = (TextView) findViewById(R.id.left_total);
        rightTotal = (TextView) findViewById(R.id.right_total);
        leftTotal.setText(formatter.format(baseTotal));
        rightTotal.setText(formatter.format(altTotal));
        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                left.setBackgroundColor(0xFF0000FF);
                right.setBackgroundColor(0xFF5cc9ff);
                selected = "left";
                leftTotal.setText(formatter.format(baseTotal));
                rightTotal.setText(formatter.format(altTotal));
            }
        });
        right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                right.setBackgroundColor(0xFF0000FF);
                left.setBackgroundColor(0xFF5cc9ff);
                selected = "right";
                rightTotal.setText(formatter.format(baseTotal));
                leftTotal.setText(formatter.format(altTotal));
            }
        });

        // get JSON data from API
        queue = Volley.newRequestQueue(this);
        fetchData();

        // Initialize DB with country info
        initDb();

    }

    public void fetchData(){
        StringRequest getExchangeRates = new StringRequest(
                Request.Method.GET,
                url + "?from=" + from + "&val=1.0&to=" + to,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        //textView.setText("Response: " + response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //textView.setText("Error: " + error.getMessage());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Mashape-Key", apiKey);
                headers.put("Accept", "text/plain");
                return headers;
            }
        };
        queue.add(getExchangeRates);
    }

    public void initDb(){
        myDbHandler = new DbHandler(this, null, null, 1);
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
