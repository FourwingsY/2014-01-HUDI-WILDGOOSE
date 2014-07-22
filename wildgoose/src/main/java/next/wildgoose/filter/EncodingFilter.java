package next.wildgoose.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EncodingFilter implements Filter {
	private String encodingType = null;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.encodingType = filterConfig.getServletContext().getInitParameter("encoding");
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		// post
		if (request.getCharacterEncoding() == null) {
	        request.setCharacterEncoding(encodingType);
	    }
		// get
		EncodingRequestWrapper wrappedRequest = new EncodingRequestWrapper(request, encodingType);
		
		chain.doFilter(wrappedRequest, response);
	}
	
	
	public void destroy() {
		this.encodingType = null;
	}
}
