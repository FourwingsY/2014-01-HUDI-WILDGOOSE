package next.wildgoose.backcontroller;

import javax.servlet.http.HttpSession;

import next.wildgoose.dao.SignDAO;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.utility.Constants;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AuthController {

	@Autowired
	private SignDAO signDao;
	
	
	public SimpleResult authenticate(HttpSession session, String userId) {
		SimpleResult sResult = null;
		if (!isValidUserId(userId)) {
			sResult = new SimpleResult();
			sResult.setStatus(404);
			sResult.setMessage(Constants.MSG_WRONG_ID);
			return sResult;
		}
		
		String visitor = (String) session.getAttribute("userId");
		
		if (visitor == null) {
			sResult = new SimpleResult();
			sResult.setStatus(401);
			sResult.setMessage(Constants.MSG_AUTH_NEED);
			// 로그인 하도록 유도하기
		}
		return sResult;
	}
	private boolean isValidUserId(String userId) {
		if (signDao.findEmail(userId)) {
			return true;
		}
		return false;
	}

}
