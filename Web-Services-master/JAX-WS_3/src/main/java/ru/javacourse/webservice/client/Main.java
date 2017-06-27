package ru.javacourse.webservice.client;

/**
 * Created by Georgy Gobozov on 11.02.2015.
 */
public class Main {

    public static void main(String[] args) {

        Airport airport = new Airport();
        AirportSoap airportSoap = airport.getAirportSoap();
        String info = airportSoap.getAirportInformationByAirportCode("JFK");


        System.out.println("info = " + info);



    }

}
