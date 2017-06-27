package ru.javacourse.webservice.client;


import ru.javacourse.Calculator;
import ru.javacourse.Calculator_Service;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceRef;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: Georgy Gobozov
 * Date: 07.07.13
 */
//http://localhost:8080/servlet/first
@WebServlet(name="ClientServlet", urlPatterns={"/ClientServlet"})
public class WebServiceServlet extends HttpServlet {
		
	@WebServiceRef
    public Calculator_Service service;
    
    @Resource
    protected WebServiceContext context;
    
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<h2>Servlet ClientServlet at " + request.getContextPath () + "</h2>");



            Calculator port = service.getCalculatorServicePort();

                int i = Integer.parseInt(request.getParameter("value1"));
                int j = Integer.parseInt(request.getParameter("value2"));

                int result = port.sun(i, j);

                out.println("<br/>");
                out.println("Result:");
                out.println("" + i + " + " + j + " = " + result);

                ((Closeable)port).close();
//
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
    * Returns a short description of the servlet.
    */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
		
}
