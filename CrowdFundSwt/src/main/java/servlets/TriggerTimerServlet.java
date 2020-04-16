package servlets;

import application.PFATimer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/timer")
public class TriggerTimerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PFATimer timer = new PFATimer();
                timer.checkEndDate();
            }
        };

        new Thread(runnable).start();

        req.setAttribute("mainmsg", "Timer");
        req.setAttribute("msg", "Timer triggered");
        req.getRequestDispatcher("/feedback.ftl").forward(req, resp);
    }
}
