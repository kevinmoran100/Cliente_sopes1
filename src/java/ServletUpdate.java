/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@WebServlet(urlPatterns = {"/ServletUpdate"})
public class ServletUpdate extends HttpServlet {

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
        HttpSession session = request.getSession();
        String user = (String)session.getAttribute("user");
        String pass1 = request.getParameter("pass1");
        String pass2 = request.getParameter("pass2");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        if (StringValido(user) && StringValido(pass1) && StringValido(pass2) && StringValido(nombre) && StringValido(apellido)) {
            if (pass1.equals(pass2)) {
                if (esPasswordValida(pass1)) {
                    try (PrintWriter out = response.getWriter()) {
                        RPCClient_Cassandra rpc = null;
                        String resp = null;
                        try {
                            rpc = new RPCClient_Cassandra();
                            pass1=Sha256.Encriptar(pass1);
                            System.out.println(" [x] Requesting registro user: " + user + " pass: " + pass1);
                            String req = "update#%" + user + "," + pass1 + "," + nombre + "," + apellido + "," + getFecha();
                            System.out.println(req);
                            resp = rpc.call(req);
                            out.println(resp);
                            if (resp.equals("5")) {
                                response.sendRedirect("/cuenta");
                            } else {
                                session.setAttribute("error", "Error al guardar el usuario");
                                response.sendRedirect("/cuenta");
                            }
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
                } else {
                    session.setAttribute("error", "La contraseña no es segura, debe tener por lo menos 8 caracteres, una letra mayuscula, una minuscula, un numero y un simbolo");
                    response.sendRedirect("/cuenta");
                }
            } else {
                session.setAttribute("error", "La contraseña no concide");
                response.sendRedirect("/cuenta");
            }
        }
    }

    private boolean esPasswordValida(String password) {
        char clave;
        int contNumero = 0, contLetraMay = 0, contLetraMin = 0, contSimb = 0;
        for (int i = 0; i < password.length(); i++) {
            clave = password.charAt(i);
            String passValue = String.valueOf(clave);
            if (passValue.matches("[A-Z]")) {
                contLetraMay++;
            } else if (passValue.matches("[a-z]")) {
                contLetraMin++;
            } else if (passValue.matches("[0-9]")) {
                contNumero++;
            } else {
                contSimb++;
            }
        }
        return password.length() >= 8 && contNumero > 0 && contLetraMay > 0 && contLetraMin > 0 && contSimb > 0;
    }

    private String getFecha() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH + 1) + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
    }

    private boolean StringValido(String s) {
        return s != null && !(s.equals(""));
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
            Logger.getLogger(ServletUpdate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServletUpdate.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ServletUpdate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServletUpdate.class.getName()).log(Level.SEVERE, null, ex);
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
