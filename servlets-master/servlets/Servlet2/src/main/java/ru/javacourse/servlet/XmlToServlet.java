package ru.javacourse.servlet;

import org.codehaus.jackson.map.ObjectMapper;
import ru.javacourse.servlet.model.Person;
import ru.javacourse.servlet.util.PersonStorage;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;

@WebServlet(urlPatterns = "/xmlToJson")
@MultipartConfig
public class XmlToServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Create path components to save the file
        final String path = request.getParameter("destination");
        final Part filePart = request.getPart("file");
        final String fullFileName = getFileName(filePart);
        String fileName = fullFileName.substring(0, fullFileName.indexOf('.'));
        final String extension = fullFileName.substring(fullFileName.indexOf('.')+1);

        System.out.println("fullFileName:" + fullFileName);
        System.out.println("fileName:" + fileName);
        System.out.println("extension:" + extension);
        OutputStream out = null;
        InputStream filecontent = null;
        InputStream is = null;
        OutputStream responseOut = null;
      //  final PrintWriter writer = response.getWriter();

        try {
            String newFileName  = "";
            File newFile = null;
            filecontent = filePart.getInputStream();
            if (extension.equalsIgnoreCase("xml"))  {
                newFileName = path + File.separator
                        + fileName + ".txt";
                newFile = new File(newFileName);
                out = new FileOutputStream(newFile);
                PersonStorage ps = readFromXML(filecontent);
                writeToJson(ps, out);
            }

            if (newFile != null) {
                System.out.println("New File : " +newFile.getAbsolutePath());
                responseOut = response.getOutputStream();
                is = new FileInputStream(newFile);
                int read = 0;
                final byte[] bytes = new byte[1024];

                while ((read = is.read(bytes)) != -1) {
                    System.out.println("Read is");
                    responseOut.write(bytes, 0, read);
                }
                System.out.println("getMimeType " + getMimeType(newFile.getName()));
                response.setContentType(getMimeType(newFile.getName()));
                response.setHeader("Content-Disposition",
                        "attachment;filename=\"" + newFile.getName() + "\"");
            }
          //  writer.println("New file " + newFileName + " created at " + path);

        } catch (FileNotFoundException fne) {
        /*    writer.println("You either did not specify a file to upload or are "
                    + "trying to upload a file to a protected or nonexistent "
                    + "location.");
            writer.println("<br/> ERROR: " + fne.getMessage());*/
            fne.printStackTrace();


        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
         /*   if (writer != null) {
                writer.close();
            }*/
            if (responseOut != null){
                responseOut.close();
            }
        }


    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");

        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                System.out.println("Filename: " + content);
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public static void writeToXml(PersonStorage personStorage) {
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

    public static PersonStorage readFromXML(InputStream is)  {
        PersonStorage personStorage = null;
        try {
            JAXBContext context = JAXBContext.newInstance(PersonStorage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            //   File file = new File("c:/temp/test.xml");
            personStorage =(PersonStorage) unmarshaller.unmarshal(is);

            List<Person> persons = personStorage.getPersons();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return personStorage;
    }

    public static void writeToJson(PersonStorage personStorage, OutputStream out) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String result = mapper.writeValueAsString(personStorage);
            out.write(result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMimeType(String filename) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(filename);
        return mimeType;
    }
}
