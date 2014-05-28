package next.wildgoose.backcontroller;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import next.wildgoose.dao.ReporterDAO;
import next.wildgoose.dto.Reporter;
import next.wildgoose.dto.SearchResult;
import next.wildgoose.utility.Constants;
import next.wildgoose.utility.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchController implements BackController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class.getName());
	
	@Override
	public Object execute(HttpServletRequest request) {
		int howMany = Constants.NUM_OF_CARDS;
		String searchQuery = request.getParameter("q");
		boolean autocomplete = false;
		
		// searchQuery 존재여부 검사
		if (searchQuery == null) {
			SearchResult searchResult = new SearchResult(request.getParameterMap());
			searchResult.setStatus(200);
			searchResult.setMessage("welcome to search page! This path is not provided as API.");
			return searchResult;
		}
		
		// 자동완성 여부 검사
		if (request.getParameter("autocomplete") != null) {
			autocomplete = true;
			LOGGER.debug("searchQuery: " + searchQuery + ", autocompete: " + request.getParameter("autocomplete"));
			howMany = Integer.parseInt(request.getParameter("how_many"));
		}
		
		// searchQuery 에러 검사
		searchQuery.replaceAll("%", "");
		String trimmedQuery = searchQuery.trim();
		if ("".equals(trimmedQuery)) {
			SearchResult searchResult = new SearchResult(request.getParameterMap());
			searchResult.setMessage("You can not search with whitespace");
			return searchResult;
		}
		
		// 결과 반환
		if (autocomplete) {
			return getAutoCompleteResult(request, searchQuery, howMany);
		}
		return getSearchResult(request, searchQuery, howMany);
	}
	
	private SearchResult getAutoCompleteResult(HttpServletRequest request, String searchQuery, int howMany) {
		SearchResult searchResult = new SearchResult(request.getParameterMap());
		ServletContext context = request.getServletContext();
		ReporterDAO reporterDao = (ReporterDAO) context.getAttribute("ReporterDAO");
		
		List<Reporter> reporters = reporterDao.getSimilarNames(searchQuery, howMany);
		
		searchResult.setStatus(200);
		searchResult.setMessage("getting similar names result success");
		searchResult.setReporters(reporters);
		searchResult.setSearchQuery(searchQuery);
		
		return searchResult;
		
	}

	private SearchResult getSearchResult(HttpServletRequest request, String searchQuery, int count) {
		SearchResult searchResult = new SearchResult(request.getParameterMap());
		ServletContext context = request.getServletContext();
		ReporterDAO reporterDao = (ReporterDAO) context.getAttribute("ReporterDAO");
//		boolean hasMore = false;
		List<Reporter> reporters = null;
		
		reporters = getReporters(reporterDao, searchQuery, 0, count);
		searchResult.setStatus(200);
		searchResult.setMessage("getting search result success");
		searchResult.setReporters(reporters);
//		searchResult.setHasMore(hasMore);
//		searchResult.setTotalNum(Constants.NUM_OF_CARDS);
		searchResult.setSearchQuery(searchQuery);
		
		return searchResult;
	}

	private List<Reporter> getReporters(ReporterDAO reporterDao, String searchQuery, int start, int end) {
		List<Reporter> reporters = null;
		String type = null;
		
		// searchQuery의 검색 type설정
		type = Utility.isURL(searchQuery) ? "url" : "name";
		reporters = reporterDao.findReportersByType(type, searchQuery, start, end);

		return reporters;
	}
}
