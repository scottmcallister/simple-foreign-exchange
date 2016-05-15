package mrscottmcallister.com.simpleforeignexchange;

/**
 * Created by scott on 16-05-15.
 */
public class Calculator {
    private double solution;
    private static double x;
    private static double y;
    private char operator;

    public double getSolution() {
        return solution;
    }

    public void setSolution(double solution) {
        this.solution = solution;
    }

    public static double getX() {
        return x;
    }

    public static void setX(double x) {
        Calculator.x = x;
    }

    public static double getY() {
        return y;
    }

    public static void setY(double y) {
        Calculator.y = y;
    }

    public char getOperator() {
        return operator;
    }

    public void setOperator(char operator) {
        this.operator = operator;
    }

    public Calculator(){
        solution = 0.0;
        x = 0.0;
        y = 0.0;
        operator = '+';
    }

    public double calculate(){
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
        }
        return solution;
    }

}
