package next.wildgoose.backcontroller;

import java.util.List;
import java.util.Map;

import next.wildgoose.dao.ReporterDAO;
import next.wildgoose.dto.Reporter;
import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.framework.utility.Utility;
import next.wildgoose.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("search")
public class SearchController{
	
	@Autowired private ReporterDAO reporterDao;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class.getName());
	
	@RequestMapping({"/", "/api/v1/search", "/search"})
	public String searchReporters(@RequestParam(value="q", required=false) String searchQuery, 
						  @RequestParam(value="autocomplete", required=false, defaultValue="false") boolean autoComplete,
						  @RequestParam(value="how_many", required=false) Integer howMany,
						  @RequestParam(value="start_item", required=false, defaultValue="-1") Integer startItem,
						  Map<String, Object> model) {
		LOGGER.debug(searchQuery);
		
		int requestCardAmout = howMany != null ? howMany : Constants.NUM_OF_CARDS;
//		int startPage = (request.getParameter("start_page") != null)? Integer.parseInt(request.getParameter("start_page")) : -1;
		
		Result searchResult = checkQuery(searchQuery);
		// 결과 반환
		// 에러 혹은 root인 경우 반환
		if (searchResult != null) {
			System.out.println(searchResult.getStatus());
			model.put("result", searchResult);
			return "search";
		}
		if (autoComplete) {
			// 자동완성 반환
			LOGGER.debug("searchQuery: " + searchQuery + ", autocompete: " + autoComplete);
			searchResult = getAutoCompleteResult(searchQuery, requestCardAmout);
		} else if (startItem != -1) {
			// 결과를 특정 부분부터 반환
			searchResult = getSearchResult(searchQuery, startItem, requestCardAmout);
		} else {
			// 결과를 처음부터 반환
			searchResult = getSearchResult(searchQuery, requestCardAmout);
		}
		searchResult.setData("pageName", "home");
		model.put("result", searchResult);
		return "search";
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
