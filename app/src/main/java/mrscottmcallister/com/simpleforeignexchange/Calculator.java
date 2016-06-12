package mrscottmcallister.com.simpleforeignexchange;

import android.util.Log;

/**
 * Created by scott on 16-05-15.
 */
public class Calculator {
    private Double solution;
    private static Double x;
    private static Double y;
    private Character operator;

    public Double getSolution() {
        return solution;
    }

    public void setSolution(Double solution) {
        this.solution = solution;
    }

    public static Double getX() {
        return x;
    }

    public static void setX(Double x) {
        Calculator.x = x;
    }

    public static Double getY() {
        return y;
    }

    public static void setY(Double y) {
        Calculator.y = y;
    }

    public Character getOperator() {
        return operator;
    }

    public void setOperator(Character operator) {
        Log.v("SetOperator", "x: "+x+"y: "+y+"operator: "+operator);
        if(operator != null && y != null && x != null){
            x = calculate();
        }
        if(solution != null){
            x = solution;
            solution = null;
        }
        if(x == null){
            x = 0.0;
        }
        this.operator = operator;
    }

    public Calculator(){
        solution = null;
        x = null;
        y = null;
        operator = null;
    }

    public Double calculate(){
        if(solution != null)
            return solution;
        if(operator == null){
            solution = x;
            if(solution == null){
                return 0.0;
            }
            return solution;
        }

        switch(operator){
            case '+':
                solution = x + y;
                break;
            case '-':
                solution = x - y;
                break;
            case '/':
                solution = x / y;
                break;
            case 'x':
                solution = x * y;
                break;
            default:
                if(x == null)
                    x = 0.0;
                solution = x;
                break;
        }
        x = null;
        y = null;
        operator = null;
        return solution;
    }

    public void clear(){
        setOperator(null);
        setSolution(null);
        setY(null);
        setX(null);
    }

}
