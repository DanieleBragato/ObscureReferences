package it.infocamere.sipert.obscureReferences.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TnsNamesOra {
	private List<Parameter> netServiceName;
	private final String DESCRIPTION_LIST = "DESCRIPTION_LIST";
	private final String DESCRIPTION = "DESCRIPTION";
	private final String ADDRESS_LIST = "ADDRESS_LIST";
	private final String ADDRESS = "ADDRESS";
	private final String PROTOCOL = "PROTOCOL";
	String value = null;
	boolean trovato = false;	

	public TnsNamesOra(List<Parameter> netServiceNames) {
		this.netServiceName = netServiceNames;
	}

	public List<Parameter> getServiceNames(String servName) {
		
		List<Parameter> serviceNamesParameter = new ArrayList<Parameter>();
		
		if (netServiceName != null) {
			for (Parameter param : netServiceName) {
				if (param.getName().equals(servName)) {
					serviceNamesParameter.add(param);
				}
			}
		}
		
		return serviceNamesParameter;
	}

	public String getValueFromNameParameter(String nameParameter, List<Parameter> parameter) {
		value = null;
		trovato = false;
		return searchByNameParameter(nameParameter, parameter);
	}
	
	public String searchByNameParameter(String nameParameter, List<Parameter> parameters) {
		
		if (!trovato) {
			for (Parameter param : parameters) {
				if (param.getName().equals(nameParameter)) {
					trovato = true;
					value = param.getValue();
					break;
				} else {
					if (!trovato && param.getParameters() != null && param.getParameters().size() > 0) {
						searchByNameParameter(nameParameter , param.getParameters());
					}
				}
			}			
		}

		return value;
	}

	public List<Parameter> geParameters() {
		return netServiceName;
	}	
	
	
}