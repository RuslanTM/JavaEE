package ru.javacourse.servlet;

import org.codehaus.jackson.map.ObjectMapper;
import ru.javacourse.servlet.model.Address;
import ru.javacourse.servlet.model.Person;
import ru.javacourse.servlet.model.Phone;
import ru.javacourse.servlet.util.PersonStorage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class JAXBParser {
    static PersonStorage personStorage = new PersonStorage();
    static {

        List<Person> persons= new ArrayList<Person>();
        Person p1 = new Person("Vova", "Ivanov", 20);
        Address a1 = new Address("SPb", "Nevsky 10", 123456);
        Phone p11 = new Phone("cell", "23413452345");
        Phone p12 = new Phone("work", "1111111111");

        p1.setAddress(a1);
        p1.addPhone(p11);
        p1.addPhone(p12);


        Person p2 = new Person("Igor", "Petrov", 27);
        Address a2 = new Address("Moscow", "Lenina 20", 23423412);
        Phone p21 = new Phone("cell", "234234123");
        Phone p22 = new Phone("work", "11333331");

        p2.setAddress(a2);
        p2.addPhone(p21);
        p2.addPhone(p22);

        persons.add(p1);
        persons.add(p2);

        personStorage.setPersons(persons);
    }

    public static void writeToXml() {
       // Person person = PersonStorage.getPersons().get(0);

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(PersonStorage.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
           File file =  new File("c:/temp/test.xml");

            jaxbMarshaller.marshal(personStorage, file);


        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static PersonStorage readFromXML()  {
        PersonStorage personStorage = null;
        try {
            JAXBContext context = JAXBContext.newInstance(PersonStorage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            File file = new File("c:/temp/test.xml");
            personStorage =(PersonStorage) unmarshaller.unmarshal(file);

            List<Person> persons = personStorage.getPersons();
            for(Person person:persons) {
                System.out.println(person.toString());
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return personStorage;
    }

    public static void writeToJson(PersonStorage personStorage) {
        ObjectMapper mapper = new ObjectMapper();
        try(OutputStream out = new FileOutputStream( new File("c:/temp/test.txt"))) {
            String result = mapper.writeValueAsString(personStorage);
            out.write(result.getBytes());
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        writeToXml();
        PersonStorage ps = readFromXML();
        writeToJson(ps);
    }

}
