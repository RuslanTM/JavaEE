Tomcat:

1) создать класс веб-сервиса
2) Генерация стабов веб-сервиса и wsdl
wsgen -verbose -cp target/classes -s src\main\java -wsdl -r src\main\webapp\WEB-INF -d target\classes ru.javacourse.webservice.SayHelloService
3) Создать web.xml и sun-jaxws.xml
4) Запустить сервис
5)Генерация клиента
wsimport.exe http://localhost:8080/sayHello/SayHelloService?wsdl -s client/src -d target/classes


6) Запуск клиента

SayHelloServiceService service = new SayHelloServiceService();
SayHelloService port = service.getSayHelloServicePort();

String response  = port.sayHello("Georgy!");
System.out.println("response = " + response);




Glassfish/Jboss

1) создать класс веб-сервиса
5)Генерация клиента
wsimport.exe http://localhost:8080/sayHello/SayHelloService?wsdl -s client/src -d target/classes


6) Запуск клиента

SayHelloServiceService service = new SayHelloServiceService();
SayHelloService port = service.getSayHelloServicePort();

String response  = port.sayHello("Georgy!");
System.out.println("response = " + response);


