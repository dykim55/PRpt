package com.cyberone.report;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		String str = "9\nA\n";
		
		System.out.println(str.length());
		System.out.println(str.lastIndexOf("\n"));

		System.out.println("|"+str.charAt(str.length()-1)+"|");
		
		if (str.charAt(str.length()-1) == '\n') {
			System.out.println(str.substring(0, str.length()-1));
		}
		*/
		String str = "AAAAAAAAAAAAA[BBBBBBBBB]";
		str = str.replaceAll("\\[[^\\]]*\\]", "");
		
		System.out.println("|"+str+"|");
		
		
		int nCtg = 7;
		
		System.out.println(nCtg);
		
		System.out.println(nCtg++%2);
		
		System.out.println(nCtg);
		
	}

}

