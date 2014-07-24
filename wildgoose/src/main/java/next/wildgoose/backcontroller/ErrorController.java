package next.wildgoose.backcontroller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.framework.BackController;
import next.wildgoose.framework.Result;
import next.wildgoose.utility.Constants;

import org.springframework.stereotype.Component;

@Component("error")
public class ErrorController implements BackController {

	@Override
	public Result execute(HttpServletRequest request) {
		
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