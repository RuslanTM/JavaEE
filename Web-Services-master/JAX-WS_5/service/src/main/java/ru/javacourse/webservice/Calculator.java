package ru.javacourse.webservice;


import javax.ejb.Local;
import javax.jws.WebService;

/**
 * Author: Georgy Gobozov
 * Date: 07.07.13
 */
@WebService
@Local
public interface Calculator {

    int sun(int a, int b);

    int sub(int a, int b);

    int div(int a, int b);

    int mult(int a, int b);

}
