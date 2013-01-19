package com.github.JamesNorris.Util;

import java.util.ArrayList;
import java.util.List;

import com.github.JamesNorris.Enumerated.MessageDirection;

public class SpecificMessage {
	private final String message;
	private List<String> exceptions = new ArrayList<String>();
	private List<String> targets = new ArrayList<String>();
	private boolean exceptionBased = true;
	private MessageDirection direction;

	public SpecificMessage(MessageDirection direction, String message) {
		this.message = message;
		this.direction = direction;
	}

	public SpecificMessage(MessageDirection direction, String message, List<String> exceptions, List<String> targets) {
		this.message = message;
		this.exceptions = exceptions;
		this.targets = targets;
		this.direction = direction;
	}
	
	public MessageDirection getDirection() {
		return direction;
	}
	
	public void setDirection(MessageDirection direction) {
		this.direction = direction;
	}

	public void addException(String exception) {
		exceptions.add(exception);
	}

	public List<String> getExceptions() {
		return exceptions;
	}

	public void removeException(String exception) {
		if (exceptions.contains(exception))
			exceptions.remove(exception);
	}

	public String getMessage() {
		return message;
	}

	public boolean isExceptionBased() {
		return exceptionBased;
	}

	public void setExceptionBased(boolean tf) {
		this.exceptionBased = tf;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void addTarget(String target) {
		targets.add(target);
	}

	public void removeTarget(String target) {
		if (targets.contains(target))
			targets.remove(target);
	}
}
