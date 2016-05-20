package mrscottmcallister.com.simpleforeignexchange;

import android.app.Activity;
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
    private String selected;
    private Double baseTotal;
    private Double rate;
    private Double flippedRate;
    private NumberFormat formatter = new DecimalFormat("#0.00");
    private NumberFormat baseFormat = new DecimalFormat("#0");
    private boolean decimal;

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
                updateTotals(baseTotal);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right.setBackgroundColor(0xFF00abff);
                left.setBackgroundColor(0xFF5cc9ff);
                selected = "right";
                updateTotals(baseTotal);
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
                        updateTotals(baseTotal);
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
        addBtn.setOnClickListener(new OperatorButtonListenter(new Character('+')));
        subBtn = (Button) findViewById(R.id.btnSubId);
        subBtn.setOnClickListener(new OperatorButtonListenter(new Character('-')));
        mulBtn = (Button) findViewById(R.id.btnMulId);
        mulBtn.setOnClickListener(new OperatorButtonListenter(new Character('x')));
        divBtn = (Button) findViewById(R.id.btnDivId);
        divBtn.setOnClickListener(new OperatorButtonListenter(new Character('/')));
        backBtn = (Button) findViewById(R.id.btnBackId);
        backBtn.setOnClickListener(new BackButtonListener());
        dotBtn = (Button) findViewById(R.id.btnDotId);

        // TO DO - implement decimal place logic
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

    public void updateTotals(Double givenTotal){
        baseTotal = givenTotal;
        NumberFormat currentFormat;
        if(decimal){
            currentFormat = formatter;
        }
        else{
            currentFormat = baseFormat;
        }
        if(baseTotal == 0.0){
            leftTotal.setText("");
            rightTotal.setText("");
            return;
        }
        if(selected == "left"){
            leftTotal.setText(currentFormat.format(baseTotal));
            rightTotal.setText(formatter.format(baseTotal * rate));
        }
        else{
            rightTotal.setText(currentFormat.format(baseTotal));
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
            if (calculator.getOperator() == null) {
                if(calculator.getSolution() != null){
                    calculator.setSolution(null);
                }
                if(calculator.getX() == null)
                    calculator.setX(0.0);
                Double total = (calculator.getX() * 10) + myNum;
                calculator.setX(total);
                updateTotals(total);
            }
            else{
                if(calculator.getY() == null)
                    calculator.setY(0.0);
                Double total = (calculator.getY() * 10) + myNum;
                calculator.setY(total);
                updateTotals(total);
            }
        }
    }

    public class OperatorButtonListenter implements View.OnClickListener {
        Character myOperator;
        public OperatorButtonListenter(Character operator){
            myOperator = operator;
        }

        @Override
        public void onClick(View v) {
            if (calculator.getOperator() == null) {
                calculator.setOperator(myOperator);
            }
        }
    }

    public class BackButtonListener implements View.OnClickListener {
        public BackButtonListener(){}

        @Override
        public void onClick(View v){
            if (calculator.getOperator() == null) {
                if(calculator.getSolution() != null){
                    calculator.setSolution(null);
                }
                if(calculator.getX() == null)
                    calculator.setX(0.0);
                Double total = calculator.getX() % 10;
                calculator.setX(total);
                updateTotals(total);
            }
            else{
                if(calculator.getY() == null)
                    calculator.setY(0.0);
                Double total = calculator.getY() % 10;
                calculator.setY(total);
                updateTotals(total);
            }
        }
    }

    public class EqualButtonListener implements View.OnClickListener {
        public EqualButtonListener(){ }

        @Override
        public void onClick(View v){
            calculator.calculate();
            updateTotals(calculator.getSolution());
        }
    }

    public class ClearButtonListener implements View.OnClickListener {
        public ClearButtonListener(){ }

        @Override
        public void onClick(View v){
            calculator.clear();
            updateTotals(0.0);
        }
    }

    public class DotButtonListener implements View.OnClickListener {
        public DotButtonListener(){ }

        @Override
        public void onClick(View v){
            if(!decimal){
                decimal = true;
            }
            else{
                decimal = false;
            }
        }
    }

}
