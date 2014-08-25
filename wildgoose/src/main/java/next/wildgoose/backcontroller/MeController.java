package next.wildgoose.backcontroller;

import java.util.List;
import java.util.Map;

import next.wildgoose.dao.ArticleDAO;
import next.wildgoose.dao.FavoriteDAO;
import next.wildgoose.dao.ReporterDAO;
import next.wildgoose.dto.Article;
import next.wildgoose.dto.Reporter;
import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.framework.utility.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("me")
public class MeController extends AuthController {
	
	@Autowired private ArticleDAO articleDao;
	@Autowired private FavoriteDAO favoriteDao;
	@Autowired private ReporterDAO reporterDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MeController.class.getName());
	
	@RequestMapping({"/api/v1/me/{userId:.+}", "/me/{userId:.+}"})
	public String getMe(@PathVariable(value = "userId") String userId,
			@RequestParam(value="start_item", required=false, defaultValue="0") Integer start,
			@RequestParam(value="how_many", required=false, defaultValue="10") Integer howMany,
			Map<String, Object> model) {
		
		Result meResult = new SimpleResult();
		List<Article> articles = articleDao.findArticlesByFavorite(userId, start, howMany);
		int totalNum = articleDao.findNumberOfArticlesByFavorite(userId);
		List<Reporter> reporters = favoriteDao.findFavoriteReporters(userId);
		List<Reporter> recommands = reporterDao.getRandomReporters(userId, 12);
		System.out.println(userId);
		System.out.println(start + " / " + howMany);
		System.out.println("articles: " + articles);
		
		meResult.setStatus(200);
		meResult.setMessage("success");
		meResult.setData("totalNum", totalNum);
		meResult.setData("articles", articles);
		meResult.setData("favorites", reporters);
		meResult.setData("recommands", recommands);
		meResult.setData("pageName", "me");
		
		model.put("result", meResult);
		return "me";
	}
}
