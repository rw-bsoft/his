package chis.source.ws;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocumentCreateServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		System.out.println("调用doGet方法");
		System.out.println("servlet test success....");
		GetWorkListService gs = new GetWorkListService();
		gs.execute(request.getParameter("request"));
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		System.out.println("调用doPost方法");
		doGet(request, response);

	}

}
