package next.wildgoose.service;

import javax.servlet.http.HttpServletRequest;

import next.wildgoose.utility.Constants;
import next.wildgoose.utility.Uri;

public class Error implements Action {
	public ActionResult execute(HttpServletRequest request) {
		ActionResult ar = new ActionResult();
		Uri uri = new Uri(request);
		ar.setForwardingOption(false, Constants.PAGE_ERROR_SEARCH_REPORTER);
		
		if (uri.check(0, Constants.RESOURCE_ERROR)) {
			request.setAttribute(Constants.KEYWORD_MSG, Constants.MSG_ERROR);
		} else {
			request.setAttribute(Constants.KEYWORD_MSG, Constants.MSG_WENT_WRONG);
		}
		return ar;
	}

}