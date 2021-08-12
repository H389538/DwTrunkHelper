package org.dw.hive.analyser.entity;

public class Response {
	private boolean status;
	private String message;
	public Response(boolean status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Response [status=" + status + ", message=" + message + "]";
	}
	
}
