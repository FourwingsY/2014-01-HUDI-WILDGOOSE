package next.wildgoose.backcontroller;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import next.wildgoose.dao.SignDAO;
import next.wildgoose.framework.BackController;
import next.wildgoose.framework.Result;
import next.wildgoose.framework.SimpleResult;
import next.wildgoose.framework.security.RandomNumber;
import next.wildgoose.framework.security.SHA256;
import next.wildgoose.framework.utility.Uri;
import next.wildgoose.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("session")
public class SessionController implements BackController {
	
	@Autowired private SignDAO signDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class.getName());

	@Override
	@RequestMapping({"/api/v1/session", "/session"})
	public Result execute(HttpServletRequest request, HttpServletResponse response) {
		Result result = null;
		Uri uri = new Uri(request);
		String method = request.getMethod();
		
		if (uri.check(1, null)) {
			if ("POST".equals(method)) {
				result = login(request);
			} else if ("DELETE".equals(method)) {
				result = logout(request);
			} else if ("GET".equals(method)) {
				String email = request.getParameter("email");
				result = joinedEmail(request, email);
			}
		} else if (uri.check(1, "rand")) {
			result = getRanomNumber(request);
		}
		
		return result;
	}
	private Result getRanomNumber(HttpServletRequest request) {
		Result accountResult = new SimpleResult();
		String randNum = RandomNumber.set(request.getSession());
		accountResult.setData("rand", randNum);
		LOGGER.debug("issue a randNum: " + randNum);
		
		accountResult.setStatus(200);
		accountResult.setMessage("OK");
		return accountResult; 
	}
	
	private Result joinedEmail(HttpServletRequest request, String email) {
		Result accountResult = new SimpleResult();
		
		if(isJoinable(email)){
			accountResult.setStatus(500);
			accountResult.setMessage(Constants.MSG_EXIST_ID);
		} else {
			accountResult.setStatus(200);
			accountResult.setMessage("OK");
		}
		accountResult.setData("email", email);

		return accountResult;
	}
	
	private boolean isJoinable(String email) {
		if (isValidEmail(email)) {
			return !signDao.findEmail(email);
		}
		return false;
	}
	
	private boolean isValidEmail(String email) {
		String regex = "^[\\w\\.-_\\+]+@[\\w-]+(\\.\\w{2,4})+$";

		return isFilled(email) && Pattern.matches(regex, email);
	}
	
	private boolean isFilled(String data) {
		
		if (data != null && data.length() > 0) {
			return true;
		}
		
		return false;
		
	}
	
	private SimpleResult login(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		String email = request.getParameter("email");
		String hashedPassword = request.getParameter("password");
		String randNum = RandomNumber.get(session);
		LOGGER.debug ("check randNum: " + randNum);

		SimpleResult simpleResult = new SimpleResult();
		LOGGER.debug("email: " + email + ", passw: " + hashedPassword);
		LOGGER.debug(randNum);
		
		String accountPw = signDao.findAccount(email);
		if (accountPw == null) {
			simpleResult.setMessage(Constants.MSG_WRONG_ID);
			return simpleResult;
		}
		// H(db_password+random)
		if(SHA256.testSHA256(accountPw + randNum).equals(hashedPassword)){
			
			simpleResult.setStatus(200);
			simpleResult.setMessage("OK");
			simpleResult.setData("userId", email);
			session.setAttribute("userId", email);
			session.setMaxInactiveInterval(Constants.SESSION_EXPIRING_TIME);

		} else {
			simpleResult.setMessage(Constants.MSG_WRONG_PW);
		}
		return simpleResult;
	}

	private SimpleResult logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("userId");
		
		SimpleResult simpleResult = new SimpleResult();
		simpleResult.setStatus(200);
	    simpleResult.setMessage("OK");
		return simpleResult;
	}
}
