package servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.PFApplication;

/**
 * Contains GUI for Project Starter funding request creation.
 * @author kt
 *
 */
@WebServlet("/create_project")
public class PSGUIFund extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/create_project.ftl").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String title = req.getParameter("title");
    String endDate = req.getParameter("endd");
    String description = req.getParameter("description");
    String fundingLimit = req.getParameter("flimit");
    String psemail = req.getParameter("psemail");
    String psname = req.getParameter("psname");
    String rewards = req.getParameter("rewards");
    String payinfo = req.getParameter("payinfo");

    try {
      PFApplication pfa = new PFApplication();
      int id = pfa.createFR(title, endDate, description, fundingLimit, psemail, psname, payinfo,
          rewards);
      // if a project exists
      if (id == -2) {
        req.setAttribute("msg",
            "A project with similar title exists. Try again with a different title");
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
      req.setAttribute("msg", "A confirmation link has been sent to '" + psemail
          + "'. Please confirm within 24 hours to complete the project creation.");
      req.getRequestDispatcher("/feedback.ftl").forward(req, resp);

    } catch (SQLException e) {
      System.err.println(e.getMessage());
      req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
    }

  }

}
