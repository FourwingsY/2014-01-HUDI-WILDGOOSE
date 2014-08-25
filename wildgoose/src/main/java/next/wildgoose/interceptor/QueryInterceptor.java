package next.wildgoose.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import next.wildgoose.dao.SearchKeywordDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class QueryInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired private SearchKeywordDAO sdao;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		String keyword = request.getParameter("q");
		if (keyword != null && "".equals(keyword)) {
			sdao.addKeywordRecord(keyword);
		}

		return true;
	}
}
