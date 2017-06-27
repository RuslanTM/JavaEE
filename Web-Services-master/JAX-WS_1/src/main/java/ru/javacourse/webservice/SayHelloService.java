package ru.javacourse.webservice;

import javax.jws.WebService;

/**
 * Author: Georgy Gobozov
 * Date: 15.07.13
 */
@WebService(endpointInterface = "ru.javacourse.webservice.SayHello")
public class SayHelloService implements SayHello {


    public String sayHello(String name){
        return "Hello, "  + name;
    }


}
