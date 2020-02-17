package it.infocamere.sipert.obscureReferences.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class TnsOraParser {

	private static final String COMMENT = "#";
	private Stack<Parameter> stack = new Stack<Parameter>();
	private List<Parameter> netServiceNames = new ArrayList<Parameter>();
	private boolean bracketOpen;
	private String fileName;
	FileInputStream is;

	public TnsOraParser(String fileName) {
		
		this.fileName = fileName;
		
		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public TnsNamesOra parse() {
		
		Scanner scan = new Scanner(is);
		StringBuilder buffer = new StringBuilder();
		boolean quote = false;
		while (scan.hasNextLine()) {
			String aLine = scan.nextLine();
			aLine = aLine.trim();
			if (aLine.startsWith(COMMENT)) {
				continue;
			}

			for (int i = 0; i < aLine.length(); i++) {
				char ch = aLine.charAt(i);
				if (ch == '"') {
					quote = !quote;
				} else if (quote) {
					buffer.append(ch);
					continue;
				} else if (ch == ' ' || ch == '\n' || ch == '\r') {
					continue;
				}
				if (ch == '=') {
					gotNewParam(buffer);
				} else if (ch == '(') {
					bracketOpen = true;
				} else if (ch == ')') {
					bracketOpen = false;
					Parameter cp = stack.pop();
					if (cp != null) {
						if (buffer.length() > 0) {
							cp.setValue(buffer.toString());
							buffer.delete(0, buffer.length());
						}
						if (!stack.isEmpty()) {
							stack.peek().addParm(cp);
						}
					}
				} else {
					buffer.append(ch);
				}
			}
		}
		scan.close();
		return new TnsNamesOra(netServiceNames);
	}

	private void gotNewParam(StringBuilder buffer) {
		Parameter param = new Parameter(buffer.toString());
		if (!bracketOpen && stack.size() == netServiceNames.size()) {
			netServiceNames.add(param);
		}
		buffer.delete(0, buffer.length());
		stack.push(param);
	}

//	public static void main(String args[]) throws Exception {
//		
//		FileInputStream testFile = new FileInputStream("D:\\SIPERT\\APPO\\tnsnames.ora");
//		TnsOraParser tnsOraParser = new TnsOraParser();
//		TnsNamesOra tnsOra = tnsOraParser.parse(testFile);
//		System.out.println(tnsOra.getServiceNames());
//	}
}