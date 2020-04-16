package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.PFApplication;
import dbadapter.Project;

/**
 * Contains GUI for browsing by a Supporter.
 * 
 * @author kt
 *
 */
@WebServlet("/browse_projects")
public class SGUIBrowse extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String title = req.getParameter("title");
    String endDate = req.getParameter("endd");
    String status = req.getParameter("status");

    // if parameters are empty, user is requesting the browse-form
    if (endDate == null && title == null && status == null) {
      req.getRequestDispatcher("/browse_projects.ftl").forward(req, resp);
      return;
    }

    // otherwise user submitted the form via GET.
    try {
      PFApplication pfa = new PFApplication();
      List<Project> projects = pfa.getAllProjects(title, endDate, status);
      req.setAttribute("projects", projects);
      req.getRequestDispatcher("/show_projects.ftl").forward(req, resp);
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }

  }

  // Doesn't require doPost, because search parameters are best handled via GET
  // requests.

}
