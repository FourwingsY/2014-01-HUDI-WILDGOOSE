package next.wildgoose.backcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import next.wildgoose.dao.SignDAO;
import next.wildgoose.framework.BackController;
import next.wildgoose.framework.Result;
import next.wildgoose.framework.SimpleResult;
import next.wildgoose.framework.security.SHA256;
import next.wildgoose.framework.utility.Uri;
import next.wildgoose.framework.utility.Utility;
import next.wildgoose.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("accounts")
@RequestMapping({"/api/v1/accounts", "/accounts"})
public class AccountController implements BackController {

	@Autowired private SignDAO signDao;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AccountController.class.getName());

	@Override
	public Result execute(HttpServletRequest request, HttpServletResponse response) {
		Result result = null;
		Uri uri = new Uri(request);
		String method = request.getMethod();
		LOGGER.debug("uri: " + uri.get(0) + ",  " + uri.get(1));

		if (uri.check(1, null)) {
			if ("POST".equals(method)) {
				// 체크하고 유효한 경우 가입
				if (request.getParameter("check") == null) {
					result = join(request);
				} else {
					result = withdraw(request);
				}
			} else if ("GET".equals(method)) {
				String email = request.getParameter("email");
				result = usedEmail(request, email);
			} else if ("PUT".equals(method)) {
				result = changePassword(request);
			}
		}

		LOGGER.debug("result: " + Utility.toJsonString(result));
		return result;
	}

	private Result changePassword(HttpServletRequest request) {
		Result result = new SimpleResult();

		// PUT method doesn't parse request parameter
		Map<String, String> parameterMap = getParameterMap(request);

		String email = parameterMap.get("email");
		String oldPassword = parameterMap.get("old_pw");
		String newPassword = parameterMap.get("new_pw");

		HttpSession session = request.getSession();
		String randNum = (String) session.getAttribute("randNum");
		String accountPw = signDao.findAccount(email);

		// 비밀번호 확인
		if (SHA256.testSHA256(accountPw + randNum).equals(oldPassword)) {
			boolean changed = signDao.changePassword(email, newPassword);
			result = new SimpleResult(changed);
		} else {
			result.setMessage(Constants.MSG_WRONG_PW);
		}
		return result;
	}

	private Map<String, String> getParameterMap(HttpServletRequest request) {
		Map<String, String> parameterMap = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream()));
			String data = br.readLine();
			// 한글 처리
			data = URLDecoder.decode(data, Constants.CHAR_ENCODING);
			String[] parameter = data.split("&");
			for (int i = 0; i < parameter.length; i++) {
				String[] parameterTemp = parameter[i].split("=");
				String name = parameterTemp[0];
				String value = parameterTemp[1];
				parameterMap.put(name, value);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return parameterMap;
	}

	private Result withdraw(HttpServletRequest request) {
		Result simpleResult = new SimpleResult();
		String email = request.getParameter("email");
		String hashedPassword = request.getParameter("password");

		HttpSession session = request.getSession();
		String randNum = (String) session.getAttribute("randNum");

		String accountPw = signDao.findAccount(email);

		// H(db_password+random)
		if (SHA256.testSHA256(accountPw + randNum).equals(hashedPassword)) {
			simpleResult = leave(request);
		} else {
			// 비밀번호 틀려서 탈퇴 못함!
			simpleResult.setMessage(Constants.MSG_WRONG_PW);
		}
		return simpleResult;
	}

	private Result leave(HttpServletRequest request) {
		// 확인하기 추가
		String email = request.getParameter("email");
		Result accountResult = new SimpleResult();

		// 기본 세팅 fail
		accountResult.setMessage(Constants.MSG_WRONG_PW);

		if (signDao.withdrawAccount(email)) {
			// 탈퇴 성공
			accountResult.setStatus(200);
			accountResult.setMessage("OK");

			HttpSession session = request.getSession();
			session.removeAttribute("userId");
		}

		return accountResult;
	}

	private Result usedEmail(HttpServletRequest request, String email) {
		Result accountResult = new SimpleResult();

		if (isJoinable(signDao, email)) {
			accountResult.setStatus(200);
			accountResult.setMessage("fetching account info succeed");
		} else {
			accountResult.setStatus(500);
			accountResult.setMessage("fetching account info failed");
		}
		accountResult.setData("email", email);

		return accountResult;
	}

	private Result join(HttpServletRequest request) {
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		LOGGER.debug("email: " + email + ",  passw: " + password);
		Result accountResult = new SimpleResult();

		// 기본 세팅 fail
		accountResult.setMessage("adding user account failed");

		if (isJoinable(signDao, email) && isHashedPassword(password)
				&& signDao.joinAccount(email, password)) {
			// 가입 성공
			accountResult.setStatus(200);
			accountResult.setMessage("adding user account succeed");

			HttpSession session = request.getSession();
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
