package next.wildgoose.backcontroller;

import java.util.List;

import javax.servlet.http.HttpSession;

import next.wildgoose.dao.FavoriteDAO;
import next.wildgoose.dto.Reporter;
import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("users")
@RequestMapping({"/api/v1/users/{userId:.+}", "/users/{userId:.+}"})
public class UserController extends AuthController {
	
	@Autowired private FavoriteDAO favoriteDao;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class.getName());
	
	@RequestMapping(value="/favorites", method=RequestMethod.GET)
	private Result getFavorites(HttpSession session, @PathVariable("userId") String userId) {
		Result result = authenticate(session, userId);
		if (result != null) {
			return result;
		}
		
		List<Reporter> reporters = favoriteDao.findFavoriteReporters(userId);
		
		result = new SimpleResult();
		result.setStatus(200);
		result.setMessage("OK");
		LOGGER.debug(""+reporters.size());
		result.setData("reporters", reporters);
		return result;
	}
	
	@RequestMapping(value="/favorites/{reporterId}", method=RequestMethod.GET)
	private Result isFavorite(HttpSession session, 
			@PathVariable("userId") String userId,
			@PathVariable("reporterId") Integer reporterId) {
		Result result = authenticate(session, userId);
		if (result != null) {
			return result;
		}
		result = new SimpleResult(true);
		result.setData("bool", favoriteDao.isFavorite(userId, reporterId));
		return result;
	}
	
	@RequestMapping(value="/favorites", method=RequestMethod.POST)
	private Result addFavorites(HttpSession session, 
			@PathVariable("userId") String userId,
			@RequestParam("reporter_id") Integer reporterId) {
		Result result = authenticate(session, userId);
		if (result != null) {
			return result;
		}
		boolean success = false;
		
		if (favoriteDao.addFavorite(reporterId, userId)) {
			success = true;
		}
		return new SimpleResult(success);
	}
	
	@RequestMapping(value="/favorites", method=RequestMethod.DELETE)
	private Result removeFavorites(HttpSession session, 
			@PathVariable("userId") String userId,
			@RequestParam("reporter_id") Integer reporterId) {
		Result result = authenticate(session, userId);
		if (result != null) {
			return result;
		}
		boolean success = false;
		
		if (favoriteDao.removeFavorite(reporterId, userId)) {
			success = true;
		}
		return new SimpleResult(success);
	}
	
}
