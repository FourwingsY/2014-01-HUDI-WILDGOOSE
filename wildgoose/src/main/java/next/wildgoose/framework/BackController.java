package next.wildgoose.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import next.wildgoose.dto.result.Result;

public interface BackController {
	public Result execute(HttpServletRequest request, HttpServletResponse response);
}
