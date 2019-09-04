package com.tc.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JarTester {

	public static void main(String[] args) {
		
		System.out.println(JarTester.class.getResource("./utilConfig.properties"));
		System.out.println(JarTester.class.getResourceAsStream("./utilConfig.properties"));
		
		System.out.println(JarTester.class.getResource("utilConfig.properties"));
		System.out.println(JarTester.class.getResourceAsStream("utilConfig.properties"));
		
		System.out.println(new File(JarTester.class.getResource("utilConfig.properties").getPath()));
		System.out.println(JarTester.class.getResourceAsStream("utilConfig.properties"));
		
		
		System.out.println(JarTester.class.getResource("deeper/deeper.properties"));
		System.out.println(JarTester.class.getResourceAsStream("deeper/deeper.properties"));
		
		System.out.println(JarTester.class.getResource("../higher.properties"));
		System.out.println(JarTester.class.getResourceAsStream("../higher.properties"));
		
		
		System.out.println(JarTester.class.getResource("/root.properties"));
		System.out.println(JarTester.class.getResourceAsStream("/root.properties"));
		
		
		try {System.out.println(new FileInputStream(JarTester.class.getResource("/root.properties").getPath()));} catch (FileNotFoundException e) {e.printStackTrace();}
		try {System.out.println(java.lang.String.class.getResourceAsStream(JarTester.class.getResource("/root.properties").getPath()));} catch (Exception e) {e.printStackTrace();}
		
		
		try {System.out.println(JarTester.class.getResource("/root.properties").openStream());} catch (IOException e) {e.printStackTrace();}
		
		
	}
}