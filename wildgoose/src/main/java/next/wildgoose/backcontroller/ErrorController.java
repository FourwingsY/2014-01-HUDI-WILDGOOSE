package next.wildgoose.backcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.utility.Constants;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("error")
public class ErrorController {

	@RequestMapping({"/error"})
	public Result execute(HttpServletRequest request, HttpServletResponse response) {
		
		Map<Integer, String> errorCodeMap = new HashMap<Integer, String>();
		errorCodeMap.put(404, Constants.ERROR_404);
		errorCodeMap.put(401, Constants.ERROR_401);
		errorCodeMap.put(500, Constants.ERROR_500);
		
		Result result = new SimpleResult();
		result.setStatus(404);
		result.setMessage(errorCodeMap.get(404));
		return result;
	}

}