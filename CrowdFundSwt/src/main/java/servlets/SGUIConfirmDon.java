package servlets;

import application.PFApplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Contains GUI for Project Starter funding request confirmation.
 * @author kt
 *
 */
@WebServlet("/confirmdon")
public class SGUIConfirmDon extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String hash = req.getParameter("h");
    if (hash == null || hash.isEmpty()) {
      resp.sendError(404);
      return;
    }

    try {
      PFApplication pfa = new PFApplication();
      pfa.confirmDonation(hash);
      req.setAttribute("mainmsg", "Success");
      req.setAttribute("msg", "Confirmation is successful");
      req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
    }
  }

}
