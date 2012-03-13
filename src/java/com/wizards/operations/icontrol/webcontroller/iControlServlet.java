/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.webcontroller;

import com.wizards.operations.icontrol.data.Pool;
import com.wizards.operations.icontrol.session.PoolFacade;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author hildebj
 */
public class iControlServlet extends HttpServlet {

  /**
   * Processes requests for both HTTP
   * <code>GET</code> and
   * <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  
  private Map<String, List<Pool>> poolMap;
  
  @EJB
    private PoolFacade poolFacade;
    
  @Override
    public void init() throws ServletException {
      // store pool list in servlet context
      getServletContext().setAttribute("pools", poolFacade.findAll());
      poolMap = new HashMap<String, List<Pool>>();
    }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP
   * <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    HttpSession session = request.getSession();
    String id = session.getId();
    
    // insure that the pools are authorized in this session
    poolMap.put(id, poolFacade.findAll());
    
    //processRequest(request, response);
  }

  /**
   * Handles the HTTP
   * <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
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
