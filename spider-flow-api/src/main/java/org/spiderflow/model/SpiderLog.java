package org.spiderflow.model;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

public class SpiderLog {

	private String level;

	private String message;

	private List<Object> variables;

	public SpiderLog(String level, String message, List<Object> variables) {
		if (variables != null && variables.size() > 0) {
			List<Object> nVariables = new ArrayList<>(variables.size());
			for (Object object : variables) {
				if (object instanceof Throwable) {
					nVariables.add(ExceptionUtils.getStackTrace((Throwable) object));
				} else {
					nVariables.add(object);
				}
			}
			this.variables = nVariables;
		}
		this.level = level;
		this.message = message;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Object> getVariables() {
		return variables;
	}

	public void setVariables(List<Object> variables) {
		this.variables = variables;
	}
}
