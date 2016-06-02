package mrscottmcallister.com.simpleforeignexchange;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
    private TextView rateText;
    private TextView flippedText;
    private Button leftCurrency;
    private Button rightCurrency;
    private String selected;
    private String baseString;
    private Double baseTotal;
    private Double rate;
    private Double flippedRate;
    private NumberFormat formatter = new DecimalFormat("#0.00");

    private Button[] numberButtons;
    private Button clearBtn;
    private Button equalBtn;
    private Button addBtn;
    private Button subBtn;
    private Button mulBtn;
    private Button divBtn;
    private Button backBtn;
    private Button dotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        left = (SurfaceView) findViewById(R.id.left);
        right = (SurfaceView) findViewById(R.id.right);
        baseTotal = 0.0;
        selected = "left";
        calculator = new Calculator();
        numberButtons = new Button[10];
        // set default rates
        rate = 1.0;
        flippedRate = 1.0;
        baseString = "";
        rateText = (TextView) findViewById(R.id.rateText);
        flippedText = (TextView) findViewById(R.id.flippedText);

        // get JSON data from API
        queue = Volley.newRequestQueue(this);
        fetchData();
        leftTotal = (TextView) findViewById(R.id.left_total);
        rightTotal = (TextView) findViewById(R.id.right_total);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setBackgroundColor(0xFF00abff);
                right.setBackgroundColor(0xFF5cc9ff);
                selected = "left";
                updateTotals(baseString);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right.setBackgroundColor(0xFF00abff);
                left.setBackgroundColor(0xFF5cc9ff);
                selected = "right";
                updateTotals(baseString);
            }
        });

        // Initialize DB with country info
        initDb();
        setUpButtons();
    }

    public void fetchData() {
        StringRequest getExchangeRates = new StringRequest(
                Request.Method.GET,
                url + "?from=" + from + "&val=1.0&to=" + to,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        rate = Double.parseDouble(response);
                        flippedRate = 1 / rate;
                        rateText.setText(formatter.format(rate));
                        flippedText.setText(formatter.format(flippedRate));
                        updateTotals(baseString);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not fetch conversion rate", Toast.LENGTH_SHORT).show();
                    }
                }) {
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

    public void pickRightCurrency(View view){
        Intent intent = new Intent(this, PickCurrency.class);
        startActivity(intent);
    }

    public void pickLeftCurrency(View view){
        Intent intent = new Intent(this, PickCurrency.class);
        startActivity(intent);
    }

    public void setUpButtons(){
        // can't reference a resource id dynamically in Android
        numberButtons[0] = (Button) findViewById(R.id.btnNum0Id);
        numberButtons[1] = (Button) findViewById(R.id.btnNum1Id);
        numberButtons[2] = (Button) findViewById(R.id.btnNum2Id);
        numberButtons[3] = (Button) findViewById(R.id.btnNum3Id);
        numberButtons[4] = (Button) findViewById(R.id.btnNum4Id);
        numberButtons[5] = (Button) findViewById(R.id.btnNum5Id);
        numberButtons[6] = (Button) findViewById(R.id.btnNum6Id);
        numberButtons[7] = (Button) findViewById(R.id.btnNum7Id);
        numberButtons[8] = (Button) findViewById(R.id.btnNum8Id);
        numberButtons[9] = (Button) findViewById(R.id.btnNum9Id);

        for(int i = 0; i < 10; i++){
            numberButtons[i].setOnClickListener(new NumberButtonListener(i));
        }

        clearBtn = (Button) findViewById(R.id.btnClearId);
        clearBtn.setOnClickListener(new ClearButtonListener());
        equalBtn = (Button) findViewById(R.id.btnEqualId);
        equalBtn.setOnClickListener(new EqualButtonListener());
        addBtn = (Button) findViewById(R.id.btnAddId);
        addBtn.setOnClickListener(new OperatorButtonListener(new Character('+')));
        subBtn = (Button) findViewById(R.id.btnSubId);
        subBtn.setOnClickListener(new OperatorButtonListener(new Character('-')));
        mulBtn = (Button) findViewById(R.id.btnMulId);
        mulBtn.setOnClickListener(new OperatorButtonListener(new Character('x')));
        divBtn = (Button) findViewById(R.id.btnDivId);
        divBtn.setOnClickListener(new OperatorButtonListener(new Character('/')));
        backBtn = (Button) findViewById(R.id.btnBackId);
        backBtn.setOnClickListener(new BackButtonListener());
        dotBtn = (Button) findViewById(R.id.btnDotId);
        dotBtn.setOnClickListener(new DotButtonListener());
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

    public void updateTotals(String givenTotal){
        if(givenTotal.equals("")){
            baseTotal = 0.0;
        } else{
            baseTotal = Double.parseDouble(givenTotal);
        }
        baseString = givenTotal;
        if(baseTotal == 0.0){
            leftTotal.setText("");
            rightTotal.setText("");
            return;
        }
        if(selected == "left"){
            leftTotal.setText(baseString);
            rightTotal.setText(formatter.format(baseTotal * rate));
        }
        else{
            rightTotal.setText(baseString);
            leftTotal.setText(formatter.format(baseTotal * flippedRate));
        }
    }

    public class NumberButtonListener implements View.OnClickListener {
        int myNum;

        public NumberButtonListener(int num) {
            myNum = num;
        }

        @Override
        public void onClick(View v) {
            String totalString;
            if (calculator.getOperator() == null) {
                if(calculator.getSolution() != null){
                    calculator.setSolution(null);
                }
                if(calculator.getX() == null) {
                    calculator.setX(0.0);
                    baseString = "";
                }
                totalString = baseString.toString() + myNum;
                Double total = Double.parseDouble(totalString);
                calculator.setX(total);
            }
            else{
                if(calculator.getY() == null){
                    calculator.setY(0.0);
                    baseString = "";
                }
                totalString = baseString.toString() + myNum;
                Double total = Double.parseDouble(totalString);
                calculator.setY(total);
            }
            updateTotals(totalString);
        }
    }

    public class OperatorButtonListener implements View.OnClickListener {
        Character myOperator;
        public OperatorButtonListener(Character operator){
            myOperator = operator;
        }

        @Override
        public void onClick(View v) {
            calculator.setOperator(myOperator);
        }
    }

    public class BackButtonListener implements View.OnClickListener {
        public BackButtonListener(){}

        @Override
        public void onClick(View v){
            String newString = (baseString.equals("")) ? "" : baseString.substring(0, baseString.length()-1);
            Double newTotal = (newString.equals("")) ? 0.0 : Double.parseDouble(newString);
            if (calculator.getOperator() == null) {
                if(calculator.getSolution() != null){
                    calculator.setSolution(null);
                }
                if(calculator.getX() == null)
                    calculator.setX(0.0);
                calculator.setX(newTotal);
            }
            else{
                if(calculator.getY() == null)
                    calculator.setY(0.0);
                calculator.setY(newTotal);
            }
            updateTotals(newString);
        }
    }

    public class EqualButtonListener implements View.OnClickListener {
        public EqualButtonListener(){ }

        @Override
        public void onClick(View v){
            calculator.calculate();
            updateTotals(calculator.getSolution().toString());
        }
    }

    public class ClearButtonListener implements View.OnClickListener {
        public ClearButtonListener(){ }

        @Override
        public void onClick(View v){
            calculator.clear();
            updateTotals("");
        }
    }

    public class DotButtonListener implements View.OnClickListener {
        public DotButtonListener(){ }

        @Override
        public void onClick(View v){
            baseString = baseString + ".";
            updateTotals(baseString);
        }
    }

}
