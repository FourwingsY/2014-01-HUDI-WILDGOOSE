package next.wildgoose.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import next.wildgoose.utility.Wildgoose;

public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FrontController.class.getName());
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher reqDispatcher = null;
		String requestURI = request.getRequestURI();
					
		RestfulURI restful = new RestfulURI (requestURI);
		LOGGER.debug(restful.toString());

		Action action = null;
		ActionForward forward = null;
		
		try {
			if (restful.check(0, Wildgoose.RESOURCE_INDEX)) {
				action = new SearchReporterAction();
				forward = action.execute(request, response, restful);
			}
			else if (restful.check(0, Wildgoose.RESOURCE_REPORTERS)) {
				action = new ShowReporterAction();
				forward = action.execute(request, response, restful);
			}
			else if (restful.check(0, Wildgoose.RESOURCE_ERROR)) {
				action = new ErrorAction(Wildgoose.PAGE_ERROR_SEARCH_REPORTER, Wildgoose.MSG_ERROR);
				forward = action.execute(request, response, restful);
			}
			else {
				action = new ErrorAction(Wildgoose.PAGE_ERROR_SEARCH_REPORTER, Wildgoose.MSG_WENT_WRONG);
				forward = action.execute(request, response, restful);
			}
		}
		catch (Exception e) {
			LOGGER.debug(e.getMessage(), e);
			
			forward.setRedirect(true);
			forward.setPath(Wildgoose.RESOURCE_ERROR);
		}
		LOGGER.debug(forward.toString());
		
		// redirect
		if (forward.isRedirect()) {
			response.sendRedirect(forward.getPath());
			return;
		}
		
		// forward
		reqDispatcher = request.getRequestDispatcher(forward.getPath());
		reqDispatcher.forward(request, response);
		
	}

}