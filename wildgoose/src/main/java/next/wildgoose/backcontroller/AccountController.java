package next.wildgoose.backcontroller;

import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import next.wildgoose.dao.SignDAO;
import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.framework.security.SHA256;
import next.wildgoose.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("accounts")
public class AccountController {

	@Autowired private SignDAO signDao;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccountController.class.getName());


	@RequestMapping(value={"/api/v1/accounts", "/accounts"}, method=RequestMethod.GET)
	private String checkEmail(@RequestParam(value="email", required=false) String email,
			Map<String, Object> model) {
		Result accountResult = new SimpleResult();

		if (isJoinable(signDao, email)) {
			accountResult.setStatus(200);
			accountResult.setMessage("fetching account info succeed");
		} else {
			accountResult.setStatus(500);
			accountResult.setMessage("fetching account info failed");
		}
		accountResult.setData("email", email);

		model.put("result", accountResult);
		return "account";
	}
	
	@RequestMapping(value={"/api/v1/accounts", "/accounts"}, method=RequestMethod.POST)
	public String joinOrSignOut(HttpSession session,
			@RequestParam(value="check", required=false) String check,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="password", required=false) String password,
			Map<String, Object> model) {
		
		Result result = null;
		if (check == null) {
			result = join(session, email, password);
		} else {
			result = withdraw(session, email, password);
		}
		
		model.put("result", result);
		return "account";
	}

	@RequestMapping(value={"/api/v1/accounts", "/accounts"}, method=RequestMethod.PUT)
	private String changePassword(HttpSession session,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="old_pw", required=false) String oldPassword,
			@RequestParam(value="new_pw", required=false) String newPassword,
			Map<String, Object> model) {
		
		Result result = new SimpleResult();

		String randNum = (String) session.getAttribute("randNum");
		String accountPw = signDao.findAccount(email);

		// 비밀번호 확인
		if (SHA256.testSHA256(accountPw + randNum).equals(oldPassword)) {
			boolean changed = signDao.changePassword(email, newPassword);
			result = new SimpleResult(changed);
		} else {
			result.setMessage(Constants.MSG_WRONG_PW);
		}
		
		model.put("result", result);
		return "account";
	}


	private Result withdraw(HttpSession session, String email, String hashedPassword) {
		
		Result simpleResult = new SimpleResult();
		String randNum = (String) session.getAttribute("randNum");
		String accountPw = signDao.findAccount(email);

		// H(db_password+random)
		if (SHA256.testSHA256(accountPw + randNum).equals(hashedPassword)) {
			simpleResult = leave(session, email);
		} else {
			// 비밀번호 틀려서 탈퇴 못함!
			simpleResult.setMessage(Constants.MSG_WRONG_PW);
		}
		return simpleResult;
	}

	private Result leave(HttpSession session, String email) {
		// 확인하기 추가
		Result accountResult = new SimpleResult();

		// 기본 세팅 fail
		accountResult.setMessage(Constants.MSG_WRONG_PW);

		if (signDao.withdrawAccount(email)) {
			// 탈퇴 성공
			accountResult.setStatus(200);
			accountResult.setMessage("OK");

			session.removeAttribute("userId");
		}

		return accountResult;
	}
	
	

	private Result join(HttpSession session, String email, String password) {

		LOGGER.debug("email: " + email + ",  passw: " + password);
		Result accountResult = new SimpleResult();

		// 기본 세팅 fail
		accountResult.setMessage("adding user account failed");

		if (isJoinable(signDao, email) && isHashedPassword(password)
				&& signDao.joinAccount(email, password)) {
			// 가입 성공
			accountResult.setStatus(200);
			accountResult.setMessage("adding user account succeed");

			session.setAttribute("userId", email);
		}
		return accountResult;
	}

	private boolean isJoinable(SignDAO signDao, String email) {
		if (isValidEmail(email)) {
			return !signDao.findEmail(email);
		}
		return false;
	}

	private boolean isValidEmail(String email) {
		String regex = "^[\\w\\.-_\\+]+@[\\w-]+(\\.\\w{2,4})+$";

		return isFilled(email) && Pattern.matches(regex, email);
	}

	private boolean isHashedPassword(String password) {
		if (password.length() == 64) {
			return true;
		}
		return false;
	}

	private boolean isFilled(String data) {
		if (data != null && data.length() > 0) {
			return true;
		}
		return false;
	}
}
