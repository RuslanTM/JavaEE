1) Open www.webservicex.com/airport.asmx?wsdl
2) Run wsimport
3) Create client
4) Get the data!


Generate wsdl with IDEA

-right click on package
-web services
-generate Java code from WSDL
-select METRO
- OK


Metro impl


        Airport airport = new Airport();
        AirportSoap airportSoap = airport.getAirportSoap();
        String info = airportSoap.getAirportInformationByAirportCode("BKK");
        System.out.println("info = " + info);
