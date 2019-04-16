package com.bw.kaoshi.checkscore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.Attributes.Name;

public class MyPropertyRead {
	static {
		
		Properties prop = new Properties();
		
		File file = new File("./score.properties");

		try {
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = prop.getProperty("name");
		System.out.println("name = " + name);

		
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
	
		System.out.println("goo");

	}

}
