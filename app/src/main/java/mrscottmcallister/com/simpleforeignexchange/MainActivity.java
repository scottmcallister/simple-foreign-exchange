package mrscottmcallister.com.simpleforeignexchange;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends Activity {

    private String url = "https://currency-exchange.p.mashape.com/exchange";
    private String from = "USD";
    private String to = "CAD";
    private String apiKey = "PLum3weAI4mshDYIvKhIyYvSuNSHp1EzY7fjsneJdFaHScp6zQ";
    private SurfaceView left;
    private SurfaceView right;
    private RequestQueue queue;
    private DbHandler myDbHandler;
    private Calculator calculator;
    private TextView leftTotal;
    private TextView rightTotal;
    private String selected;
    private double baseTotal;
    private Double rate;
    private Double flippedRate;
    private NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        left = (SurfaceView) findViewById(R.id.left);
        right = (SurfaceView) findViewById(R.id.right);
        baseTotal = 1.0;
        selected = "left";

        // get JSON data from API
        queue = Volley.newRequestQueue(this);
        fetchData();
        leftTotal = (TextView) findViewById(R.id.left_total);
        rightTotal = (TextView) findViewById(R.id.right_total);
        leftTotal.setText(formatter.format(baseTotal));
        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                left.setBackgroundColor(0xFF00abff);
                right.setBackgroundColor(0xFF5cc9ff);
                leftTotal.setText(formatter.format(baseTotal));
                rightTotal.setText(formatter.format(baseTotal * rate));
            }
        });
        right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                right.setBackgroundColor(0xFF00abff);
                left.setBackgroundColor(0xFF5cc9ff);
                rightTotal.setText(formatter.format(baseTotal));
                leftTotal.setText(formatter.format(baseTotal * flippedRate));
            }
        });

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
                        rate = Double.parseDouble(response);
                        flippedRate = 1 / rate;
                        if(selected == "left"){
                            leftTotal.setText(formatter.format(baseTotal));
                            rightTotal.setText(formatter.format(baseTotal * rate));
                        }
                        else{
                            rightTotal.setText(formatter.format(baseTotal));
                            leftTotal.setText(formatter.format(baseTotal * flippedRate));
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not fetch conversion rate", Toast.LENGTH_SHORT ).show();
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
