package next.wildgoose.backcontroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import next.wildgoose.dao.SignDAO;
import next.wildgoose.framework.BackController;
import next.wildgoose.framework.Result;
import next.wildgoose.framework.SimpleResult;
import next.wildgoose.utility.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AuthController implements BackController {

	@Autowired private SignDAO signDao;
	@Override
	public abstract Result execute(HttpServletRequest request);
	
	public SimpleResult authenticate(HttpServletRequest request, String userId) {
		SimpleResult sResult = null;
		if (!isValidUserId(request, userId)) {
			sResult = new SimpleResult();
			sResult.setStatus(404);
			sResult.setMessage(Constants.MSG_WRONG_ID);
			return sResult;
		}
		
		HttpSession session = request.getSession();
		String visitor = (String) session.getAttribute("userId");
		
		if (visitor == null) {
			sResult = new SimpleResult();
			sResult.setStatus(401);
			sResult.setMessage(Constants.MSG_AUTH_NEED);
			// 로그인 하도록 유도하기
		}
		return sResult;
	}
	private boolean isValidUserId(HttpServletRequest request, String userId) {
		if (signDao.findEmail(userId)) {
			return true;
		}
		return false;
	}

}
