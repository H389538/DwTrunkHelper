package org.dw.common.entity;

public class StackNode {
	private String method;
	private String command;
	
	public StackNode(StackNode node) {
		this(node.getMethod(),node.getCommand());
	}
	public StackNode(String command) {
		this(null, command);
	}
	public StackNode(String method, String command) {
		this.method = method;
		this.command = command;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
}
