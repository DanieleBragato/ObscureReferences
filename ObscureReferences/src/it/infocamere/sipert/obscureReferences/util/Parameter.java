package it.infocamere.sipert.obscureReferences.util;

import java.util.ArrayList;
import java.util.List;

public class Parameter {
	private String name;
	private String value;
	private List<Parameter> parameters;

	public Parameter(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public void addParm(Parameter cp) {
		if (parameters == null) {
			parameters = new ArrayList<Parameter>();
		}
		parameters.add(cp);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + "=(");
		if (value != null) {
			sb.append(value);
		} else if (parameters != null) {
			for (Parameter param : parameters) {
				sb.append(param);
			}
		}
		sb.append(")");
		return sb.toString();
	}

}
