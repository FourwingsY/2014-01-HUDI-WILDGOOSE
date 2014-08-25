package next.wildgoose.backcontroller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import next.wildgoose.dto.result.Result;
import next.wildgoose.dto.result.SimpleResult;
import next.wildgoose.framework.support.ResourceLoader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("templates")
public class TemplateController {
	
	@RequestMapping("/api/v1/templates/{fileName}.{ext}")
	public Result execute(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("fileName") String templateFileName,
			@PathVariable("ext") String templateFileExt) {
		ServletContext context = request.getServletContext();
		String root = context.getRealPath("/");
		String path = root +"/html_templates/"+ templateFileName + "." + templateFileExt;
		Result result = readTemplate(path);
		
		return result;
	}
	
	private Result readTemplate(String path) {
		Result result = new SimpleResult();
		StringBuilder htmlDocumentSB = ResourceLoader.load(path);
		if (htmlDocumentSB != null) {
			result.setStatus(200);
			result.setMessage("OK");
			result.setData("template", htmlDocumentSB.toString());
		}
		
		return result;
	}
}
