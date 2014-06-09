package next.wildgoose.framework;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import next.wildgoose.utility.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSPView implements View {
	private static final Logger LOGGER = LoggerFactory.getLogger(JSPView.class.getName());

	@Override
	public void show(HttpServletRequest request, HttpServletResponse response, Result resultData) throws ServletException, IOException{
		String jspName = (String) request.getAttribute("jspName");
		LOGGER.debug("jspFileName " + jspName);
		
		request.setAttribute("data", resultData);
		RequestDispatcher reqDispatcher = request.getRequestDispatcher("/" + jspName);
		reqDispatcher.forward(request, response);
	}



}
