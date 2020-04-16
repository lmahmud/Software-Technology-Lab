package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import application.PFApplication;
import datatypes.ProjectAndRewards;
import dbadapter.Project;

/**
 * Contains GUI to view a project by a supporter.
 * 
 * @author kt
 *
 */
@WebServlet("/view_project")
public class SGUIViewProject extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String idStr = req.getParameter("id");

    try {
      int id = (idStr == null) ? 0 : Integer.parseInt(idStr);
      PFApplication pfa = new PFApplication();
      Optional<ProjectAndRewards> pjRw = pfa.getProjectAndRewards(id);

      if (!pjRw.isPresent()) {
        resp.sendError(404);
        return;
      }

      req.setAttribute("pjrw", pjRw.get());
      req.getRequestDispatcher("/view_project.ftl").forward(req, resp);
    } catch (SQLException | NumberFormatException e) {
      System.err.println(e.getMessage());
    }
  }

}
