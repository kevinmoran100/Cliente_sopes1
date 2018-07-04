/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kevin
 */
@WebServlet(urlPatterns = {"/home"})
public class ServletGetMessages extends HttpServlet {

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
            RPCClient_Redis rpc = null;
            String resp = null;
            try {
                rpc = new RPCClient_Redis();
                System.out.println(" [x] Requesting mensajes");
                String req = "get$hola";
                System.out.println(req);
                resp = rpc.call(req);
                LinkedList<String> mensajes = new LinkedList<>();
                resp = resp.substring(1, resp.length() - 1);
                String elemento = "";
                char letra;
                boolean bandera = true;
                int estado = 0;
                for (int i = 0; i < resp.length(); i++) {
                    letra = resp.charAt(i);
                    switch (estado) {//estado inicial
                        case 0://estado inicial
                            if (letra == '\'') {
                                estado = 1;
                            }
                            else if (letra == '\"') {
                                estado = 2;
                            }
                            break;
                        case 1://si es un '
                            if (letra == '\'') {
                                mensajes.add(elemento);
                                elemento = "";
                                estado = 0;
                            } else {
                                elemento += letra;
                            }
                            break;
                        case 2://si es un "
                            if (letra == '\"') {
                                mensajes.add(elemento);
                                elemento = "";
                                estado = 0;
                            } else {
                                elemento += letra;
                            }
                            break;
                    }
                }
                LinkedList<String> m_final = new LinkedList<>();
                for (String m : mensajes) {
                    String[] ax = m.split("#%");
                    m_final.add("<tr><td>" + ax[1] + "</td><td>" + ax[0] + "</td><td>" + ax[2] + "</td></tr>");
                }
//                String[] aux = resp.split("\'\"");
//                for (int i = 1; i < aux.length; i++) {
//                    aux[i] = aux[i].substring(0, aux[i].length() - 1);
//                    String[] men = aux[i].split("#%");
//                    mensajes.add(men[0] + "," + men[1] + "," + men[2]);
//                }
                request.setAttribute("mensajes", m_final);
                RequestDispatcher rd = request.getRequestDispatcher("index_s.jsp");
                rd.forward(request, response);
//                if (resp.equals("1")) {
//                    response.sendRedirect("/");
//                } else {
//                    session.setAttribute("error", "Error al enviar mensaje");
//                    response.sendRedirect("/");
//                }
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
            Logger.getLogger(ServletRedis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServletSendMessage.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ServletRedis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServletSendMessage.class.getName()).log(Level.SEVERE, null, ex);
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

    private String getFecha() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH + 1) + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
    }
}
