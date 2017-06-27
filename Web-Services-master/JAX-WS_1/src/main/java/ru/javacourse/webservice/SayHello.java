package ru.javacourse.webservice;

import javax.jws.WebService;

/**
 * Created by Georgy Gobozov on 10.02.2015.
 */
@WebService
public interface SayHello {

    public String sayHello(String name);

}
