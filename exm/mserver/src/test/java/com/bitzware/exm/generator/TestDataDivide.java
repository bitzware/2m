package com.bitzware.exm.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class TestDataDivide {

	public static void main(String[] args) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("../visitordb/sql/dummy-master2.sql"), "UTF-8"));
		
		Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../visitordb/sql/dummy-master2b.sql"), "UTF-8"));
		
		boolean writing = false;
		String line;
		while ((line = input.readLine()) != null) {
			if (writing) {
				output.write(line);
				output.write('\n');
			}
			
			if (line.contains("-5662887")) {
				writing = true;
			}
		}
		
		output.close();
		input.close();
	}
	
}
