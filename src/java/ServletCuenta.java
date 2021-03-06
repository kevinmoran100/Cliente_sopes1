/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kevin
 */
@WebServlet(urlPatterns = {"/cuenta"})
public class ServletCuenta extends HttpServlet {

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
            throws ServletException, IOException, TimeoutException, InterruptedException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            RPCClient_Cassandra rpc = null;
            String resp = null;
            try {
                rpc = new RPCClient_Cassandra();
                System.out.println(" [x] Requesting mensajes");
                HttpSession session = request.getSession();
                Object user = session.getAttribute("user");
                String req = "getdatos#%" + user;
                System.out.println(req);
                resp = rpc.call(req);
                String[] ax = resp.split(",");
                if (ax.length == 3) {
                    request.setAttribute("nombre", ax[0]);
                    request.setAttribute("apellido", ax[1]);
                    try {
                        request.setAttribute("pass", Sha256.Desencriptar(ax[2]));
                    } catch (Exception ex) {
                        Logger.getLogger(ServletCuenta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                RequestDispatcher rd = request.getRequestDispatcher("cuenta.jsp");
                rd.forward(request, response);
                System.out.println(" [.] Got '" + resp + "'");
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            } finally {
                if (rpc != null) {
                    try {
                        rpc.close();
                    } catch (IOException _ignore) {
                    }
                }
            }

        }
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
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (TimeoutException ex) {
            Logger.getLogger(ServletCuenta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServletCuenta.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (TimeoutException ex) {
            Logger.getLogger(ServletCuenta.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServletCuenta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
