package next.wildgoose.backcontroller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import next.wildgoose.dao.ArticleDAO;
import next.wildgoose.dao.DummyData;
import next.wildgoose.dao.NumberOfArticlesDAO;
import next.wildgoose.dao.ReporterDAO;
import next.wildgoose.dto.Article;
import next.wildgoose.dto.NumberOfArticles;
import next.wildgoose.dto.Reporter;
import next.wildgoose.dto.StatPoints;
import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.utility.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("reporters")
public class ReporterController {

	@Autowired private ReporterDAO reporterDao;
	@Autowired private NumberOfArticlesDAO numberOfArticlesDao;
	@Autowired private DummyData dummy;
	@Autowired private ArticleDAO articleDao;
	

	@RequestMapping({"/api/v1/reporters/"})
	public String getRandomReporters(HttpSession session, 
			@RequestParam(value="max", required=false, defaultValue="20") Integer reportersNum,
			Map<String, Object> model) {

		String userId = (String) session.getAttribute("userId");
		int howmany = Math.min(reportersNum, 20);
		
		Result result = new SimpleResult();
		if (userId == null) {
			result.setStatus(401);
			result.setMessage(Constants.MSG_AUTH_NEED);
		} else {
			result.setStatus(200);
			result.setMessage("OK");
			List<Reporter> totalReporters = reporterDao.getRandomReporters(userId, howmany);
			result.setData("reporters", totalReporters);
		}
		
		model.put("result", result);
		return "reporters";
	}


	@RequestMapping("/api/v1/reporters/{reporterId}/statistics")
	public String getGraphData(@PathVariable(value = "reporterId") Integer reporterId,
			@RequestParam(value="data") String yaxis,
			@RequestParam(value="by", required=false) String by,
			Map<String, Object> model) {
		
		Result result = new SimpleResult();
		
		List<NumberOfArticles> numberOfArticlesList = null;
		
		if(Constants.RESOURCE_NOA.equals(yaxis)){
			if("day".equals(by)){
				result.setStatus(200);
				result.setMessage("OK");
				numberOfArticlesList = numberOfArticlesDao.findNumberOfArticlesByDay(reporterId);
				result.setData("numberOfArticlesList", numberOfArticlesList);
			} else if ("section".equals(by)){
				result.setStatus(200);
				result.setMessage("OK");
				numberOfArticlesList = numberOfArticlesDao.findNumberOfArticlesBySection(reporterId);
				result.setData("numberOfArticlesList", numberOfArticlesList);
			}
		} else if ("stat_points".equals(yaxis)){
			StatPoints statPoints = dummy.getStatPoints(reporterId);
			result.setStatus(200);
			result.setMessage("OK");
			result.setData("statPoints", statPoints);
		}
		
		model.put("result", result);
		return "reporters";
	}

	@RequestMapping({"/api/v1/reporters/{reporterId}", "/reporters/{reporterId}"})
	public String getReporterPage(@PathVariable(value = "reporterId") Integer reporterId,
			Map<String, Object> model) {
		Result result = new SimpleResult();
		Reporter reporter = null;
		List<Article> articles = null;

		// DB에서 id로 검색하여 reporterCardData 가져오기
		reporter = reporterDao.findReporterById(reporterId);
		articles = articleDao.findArticlesById(reporterId);

		result.setData("reporter", reporter);
		result.setData("articles", articles);
		result.setStatus(200);
		result.setMessage("OK");
		
		model.put("result", result);
		return "reporters";
	}

}
