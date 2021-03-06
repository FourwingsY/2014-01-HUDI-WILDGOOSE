package next.wildgoose.dto.result;

import java.util.HashMap;
import java.util.Map;

public abstract class Result {
	
	protected int status;
	protected String message;
	protected Map<String, Object> data;
	
	protected Result() {
		this(false);
	}
	protected Result(boolean success) {
		if (success) {
			this.status = 200;
			this.message = "success";	
		} else {
			this.status = 500;
			this.message = "failure";
		}
		this.data = new HashMap<String, Object>();
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

	public void setData(String key, Object value) {
		this.data.put(key, value);
	}
	
	public Map<String, Object> getData() {
		return this.data;
	}
	
}
