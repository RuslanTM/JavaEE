package ru.javacourse.webservice;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 * Created by Georgy Gobozov on 10.02.2015.
 */

@WebService(
        endpointInterface = "ru.javacourse.webservice.Calculator",
        serviceName = "calculator",
        targetNamespace = "http://javacourse.ru"
)
@Stateless
public class CalculatorService implements Calculator {

    @Override
    public int sun(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return  a - b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }

    @Override
    public int mult(int a, int b) {
        return  a * b;
    }
}
