package next.wildgoose.framework;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface View {
	public void show(Result resultData, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
