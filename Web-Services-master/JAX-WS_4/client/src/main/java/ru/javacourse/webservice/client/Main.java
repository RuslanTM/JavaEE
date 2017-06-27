package ru.javacourse.webservice.client;

import ru.javacourse.Calculator;
import ru.javacourse.Calculator_Service;

/**
 * Created by Georgy Gobozov on 10.02.2015.
 */
public class Main {

    public static void main(String[] args) {

        Calculator_Service service = new Calculator_Service();
        Calculator calculator = service.getCalculatorServicePort();

        int result = calculator.mult(3, 7);
        System.out.println("result = " + result);


    }

}
