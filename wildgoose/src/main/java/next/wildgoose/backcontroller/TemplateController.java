package next.wildgoose.backcontroller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import next.wildgoose.dto.result.TemplateResult;
import next.wildgoose.framework.BackController;
import next.wildgoose.framework.Result;
import next.wildgoose.framework.support.ResourceLoader;
import next.wildgoose.framework.utility.Uri;

import org.springframework.stereotype.Component;

@Component("templates")
public class TemplateController implements BackController {
	
	@Override
	public Result execute(HttpServletRequest request) {
		ServletContext context = request.getServletContext();
		String root = context.getRealPath("/");
		Uri uri = new Uri(request);
		String templateFileName = uri.get(1);
		String path = root +"/html_templates/"+ templateFileName;
		Result result = readTemplate(path);
		
		return result;
	}
	
	private TemplateResult readTemplate(String path) {
		TemplateResult result = new TemplateResult();
		StringBuilder htmlDocumentSB = ResourceLoader.load(path);
		if (htmlDocumentSB != null) {
			result.setStatus(200);
			result.setMessage("OK");
			result.setTemplate(htmlDocumentSB.toString());
		}
		
		return result;
	}
}
