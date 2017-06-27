package ru.javacourse.ajax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georgy Gobozov on 17.03.2015.
 */
public class CityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String country = request.getParameter("country");

        if (country != null) {
            System.out.println("country = " + country);
            List<MyDatabase.City> cities = MyDatabase.getCitiesByCountry(country);
            StringBuilder sb = new StringBuilder();
            for (MyDatabase.City city : cities) {
                sb.append(city.getName()).append("|");
            }

            response.setContentType("text/plain");
            Writer writer = response.getWriter();
            writer.write(sb.toString());
            writer.flush();

        } else {

            List<String> countries = new ArrayList<String>();
            countries.add("Russia");
            countries.add("USA");
            countries.add("Spain");

            request.setAttribute("countries", countries);
            getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
        }

    }
}
