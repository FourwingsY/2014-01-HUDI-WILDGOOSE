package next.wildgoose.backcontroller;

import java.util.List;

import next.wildgoose.dao.ReporterDAO;
import next.wildgoose.dto.Reporter;
import next.wildgoose.framework.Result;
import next.wildgoose.framework.SimpleResult;
import next.wildgoose.framework.utility.Utility;
import next.wildgoose.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("search")
public class SearchController{
	
	@Autowired private ReporterDAO reporterDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class.getName());
	
	@RequestMapping({"/", "/api/v1/search", "/search"})
	@ModelAttribute("result")
	public Result execute(@RequestParam(value="q", required=false) String searchQuery, 
						  @RequestParam(value="autocomplete", required=false) boolean autoComplete,
						  @RequestParam(value="how_many", required=false) Integer howMany,
						  @RequestParam(value="start_item", required=false) Integer startItem) {
		LOGGER.debug(searchQuery);
		
		int requestCardAmout = howMany != null ? howMany : Constants.NUM_OF_CARDS;
		int startCardNum = startItem != null ? startItem : -1;
//		int startPage = (request.getParameter("start_page") != null)? Integer.parseInt(request.getParameter("start_page")) : -1;
		
		Result searchResult = checkQuery(searchQuery);
		// 결과 반환
		// 에러 혹은 root인 경우 반환
		if (searchResult != null) {
			return searchResult;
		}
		if (autoComplete) {
			// 자동완성 반환
			LOGGER.debug("searchQuery: " + searchQuery + ", autocompete: " + autoComplete);
			searchResult = getAutoCompleteResult(searchQuery, requestCardAmout);
		} else if (startCardNum != -1) {
			// 결과를 특정 부분부터 반환
			searchResult = getSearchResult(searchQuery, startCardNum, requestCardAmout);
		} else {
			// 결과를 처음부터 반환
			searchResult = getSearchResult(searchQuery, requestCardAmout);
		}
		searchResult.setData("pageName", "home");
		return searchResult;
	}
	
	
	private Result getAutoCompleteResult(String searchQuery, int howMany) {
		Result searchResult = new SimpleResult();
		
		List<Reporter> reporters = reporterDao.getSimilarNames(searchQuery, howMany);
		
		searchResult.setStatus(200);
		searchResult.setMessage("OK");
		searchResult.setData("reporters", reporters);
		searchResult.setData("searchQuery", searchQuery);
		
		return searchResult;
		
	}
	
	private Result getSearchResult (String searchQuery, int start, int howMany) {
		Result searchResult = new SimpleResult();
		List<Reporter> reporters = null;
		
		if (start == 0) {
			int numOfResult = getNumOfResult(searchQuery);
			searchResult.setData("totalNum", numOfResult);
		}
		
		reporters = getReporters(searchQuery, start, howMany);
		searchResult.setStatus(200);
		searchResult.setMessage("OK");
		searchResult.setData("reporters", reporters);
		searchResult.setData("searchQuery",searchQuery);
		
		return searchResult;
	}

	private Result getSearchResult(String searchQuery, int howMany) {
		return getSearchResult(searchQuery, 0, howMany);
	}
	
	
	private int getNumOfResult(String searchQuery) {
		String type = null;
		
		// searchQuery의 검색 type설정
		type = Utility.isURL(searchQuery) ? "url" : "name";

		return reporterDao.findNumberOfReportersByType(type, searchQuery);
	}
	

	private List<Reporter> getReporters(String searchQuery, int start, int howMany) {
		String type = null;
		
		// searchQuery의 검색 type설정
		type = Utility.isURL(searchQuery) ? "url" : "name";

		return reporterDao.findReportersByType(type, searchQuery, start, howMany);
	}
	
	private Result checkQuery(String searchQuery) {
		Result searchResult = null;
		
		if (searchQuery == null) {
			searchResult = new SimpleResult();
			searchResult.setStatus(200);
			searchResult.setMessage("OK");
			return searchResult;
		}
				
		// searchQuery 에러 검사
		searchQuery.replaceAll("%", "");
		String trimmedQuery = searchQuery.trim();
		if ("".equals(trimmedQuery)) {
			searchResult = new SimpleResult();
			searchResult.setMessage(Constants.MSG_WRONG_QUERY);
			return searchResult;
		}
		return null;
	}
}
