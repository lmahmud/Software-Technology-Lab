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
 * Contains GUI for Project Supporter donation request.
 *
 * @author kt
 */
@WebServlet("/donate_project")
public class SGUIDonate extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/donate_project.ftl").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String idStr = req.getParameter("id");
    if (idStr == null) {
      req.getRequestDispatcher("/404.html").forward(req, resp);
      return;
    }

    String semail = req.getParameter("semail");
    String sname = req.getParameter("sname");
    String amountStr = req.getParameter("amount");
    String payinfo = req.getParameter("spayinfo");

    try {
      int project_id = Integer.parseInt(idStr);
      if (project_id < 0) {
        req.getRequestDispatcher("/404.html").forward(req, resp);
        return;
      }
      double amount = Double.parseDouble(amountStr);
      PFApplication pfa = new PFApplication();
      int id = pfa.makeDonation(project_id, amount, semail, sname, payinfo);

      if (id == -2) {
        req.setAttribute("msg", "Confirming is no longer possible.");
        req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
        return;
      }

      // if invalid id
      if (id < 1) {
        req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
        return;
      }

      // otherwise
      req.setAttribute("mainmsg", "Success");
      req.setAttribute(
          "msg",
          "A confirmation link has been sent to '"
              + semail
              + "'. Please confirm within 24 hours to complete the donation.");
      req.getRequestDispatcher("/feedback.ftl").forward(req, resp);

    } catch (SQLException e) {
      System.err.println(e.getMessage());
      req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
    }
  }
}
