package ru.javacourse.webservice;

/**
 * Created by Georgy Gobozov on 10.02.2015.
 */
public class Main {

    public static void main(String[] args) {

        SayHelloServiceService service = new SayHelloServiceService();
        SayHelloService port = service.getSayHelloServicePort();

        String response  = port.sayHello("Georgy!");
        System.out.println("response = " + response);




    }

}
