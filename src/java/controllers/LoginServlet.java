package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.*;
import models.Account;

/**
 * @author Edrine Frances
 * @author Leonne Matthew Dayao
 * @author Rayna Gulifardo
 * 2CSC - CICS - University of Santo Tomas
 */
@WebServlet(name = "LoginServlet", urlPatterns =
{
    "/login"
})
public class LoginServlet extends HttpServlet
{
    
    private static Connection con;
    private static boolean isServerWorking;
    private static String driver, url, username, password;
    private static LoginRequester lr;
    
    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        // Load Driver
        try {
            driver = config.getInitParameter("driver");
            Class.forName(driver);
            System.out.println("Loaded Driver: " + driver);
            // Establish Connection
            url = config.getInitParameter("url"); // change to UserDB once ready
            username = config.getInitParameter("username");
            password = config.getInitParameter("password");
            this.con = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to: " + url);
            // LoginRequester object manages secure login requests.
            // - Retrieves a key from the server config of the deployment descriptor
            this.lr = new LoginRequester(con, config.getInitParameter("key"), config.getInitParameter("cipher"));
            isServerWorking = true;
        }
        catch (ClassNotFoundException | SQLException e) {
            isServerWorking = false;
            System.out.println("An exception occurred.");
            System.out.println("Time of exception: " + new java.util.Date());
            System.out.println("Exception:");
            e.printStackTrace();
            if (e instanceof ClassNotFoundException)
                System.out.println("Please check if derbyclient.jar (1.4) is installed.");
            else
                System.out.println("Please restart the database, then the server.");
        }
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        System.out.println(System.getProperty("com.sun.aas.instanceRoot"));
        if (isServerWorking) {
            String uname, pword;
            try {
                uname = request.getParameter("uname");
                pword = request.getParameter("pword");
            }
            catch (NullPointerException npe) {
                uname = "";
                pword = "";
            }
            
            try {
                Account acc = lr.loginRequest(uname, pword);
                HttpSession session = request.getSession();
                session.setAttribute("uname", uname);
                session.setAttribute("urole", acc.getUrole());
                //RequestDispatcher rs = request.getRequestDispatcher("/success");
                //rs.forward(request, response);
                response.sendRedirect("success");
            }
            catch (AuthenticationException ae)  {
                ae.printStackTrace();
                switch (ae.getMessage().charAt(0)) {
                    case '1':
                        
                        response.sendRedirect("error_1.jsp");
                        break;
                    case '2':
                        response.sendRedirect("error_2.jsp");
                        break;
                    case '3':
                        response.sendRedirect("error_3.jsp");
                        break;
                    // Error 4 reserved for 404s; already handled by web.xml
                    case '5':
                        response.sendRedirect("error_5.jsp");
                        break;
                    default:
                        response.sendRedirect("error_gen.jsp");
                        break;
                }
            }
            catch (NullValueException nve) {
                response.sendRedirect("noLoginCredentials.jsp");
                nve.printStackTrace();
            }
            catch (ServerAuthenticationException sae) {
                response.sendRedirect("error_con.jsp");
                sae.printStackTrace();
            }
        }
        else {
            response.sendRedirect("error_srv.jsp");
        }
        //  if login has data
        //      forward to other servlet
        //  else
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
