package next.wildgoose.backcontroller;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import next.wildgoose.dao.SignDAO;
import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.framework.security.RandomNumber;
import next.wildgoose.framework.security.SHA256;
import next.wildgoose.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("session")
public class SessionController {
	
	@Autowired private SignDAO signDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class.getName());

	@RequestMapping(value="/api/v1/session", method=RequestMethod.POST)
	public String login(HttpSession session,
			@RequestParam("email") String email,
			@RequestParam("password") String hashedPassword,
			Map<String, Object> model) {
		
		String randNum = RandomNumber.get(session);
		SimpleResult simpleResult = new SimpleResult();
		
		String accountPw = signDao.findAccount(email);
		if (accountPw == null) {
			simpleResult.setMessage(Constants.MSG_WRONG_ID);
			model.put("result", simpleResult);
			return "session";
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
		
		model.put("result", simpleResult);
		return "session";
	}
	
	@RequestMapping(value="/api/v1/session", method=RequestMethod.GET)
	public String joinedEmail(@RequestParam("email") String email,
			Map<String, Object> model) {
		Result accountResult = new SimpleResult();
		
		if(isJoinable(email)){
			accountResult.setStatus(500);
			accountResult.setMessage(Constants.MSG_EXIST_ID);
		} else {
			accountResult.setStatus(200);
			accountResult.setMessage("OK");
		}
		accountResult.setData("email", email);

		model.put("result", accountResult);
		return "session";
	}
	
	@RequestMapping(value="/api/v1/session", method=RequestMethod.DELETE)
	public String logout(HttpSession session,
			Map<String, Object> model) {
		
		session.removeAttribute("userId");
		
		SimpleResult simpleResult = new SimpleResult();
		simpleResult.setStatus(200);
	    simpleResult.setMessage("OK");
		
	    model.put("result", simpleResult);
		return "session";
	}
	
	@RequestMapping(value="/api/v1/session/rand", method=RequestMethod.GET)
	public String getRandomNumber(HttpSession session, Map<String, Object> model) {
		Result accountResult = new SimpleResult();
		String randNum = RandomNumber.set(session);
		accountResult.setData("rand", randNum);
		LOGGER.debug("issue a randNum: " + randNum);
		
		accountResult.setStatus(200);
		accountResult.setMessage("OK");
		
		model.put("result", accountResult);
		return "session";
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
	
}
