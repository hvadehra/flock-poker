package co.flock.web.servlets;

import com.google.common.base.Joiner;
import com.google.inject.Singleton;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class PingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // do health check
            successResponse(resp);
        } catch (Throwable t) {
            failureResponse(resp, t);
        }
    }

    private void successResponse(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html");
        ServletOutputStream out = resp.getOutputStream();
        out.println("Success!");
        out.flush();
    }

    private void failureResponse(HttpServletResponse resp, Throwable t) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.setContentType("text/html");
        ServletOutputStream out = resp.getOutputStream();
        out.println("Failure: " + t.getMessage());
        out.println(Joiner.on("\n").join(t.getStackTrace()));
        out.flush();
    }
}
