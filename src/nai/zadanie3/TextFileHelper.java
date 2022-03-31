package nai.zadanie3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static nai.zadanie3.Main.VECTOR_SIZE;

public class TextFileHelper {

	//Use a private constructor to override the default public one
	private TextFileHelper(){}

	public static double[] calculateRatios(Path path) throws IOException { 
		double[] chars = new double[VECTOR_SIZE];
		int charCount = 0;
		try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)){

			int c;
			while ((c = in.read()) != -1) {
				if (Character.isLetter(c) && c <= 'z') {
					c = (Character.toLowerCase(c) - 'a');
					chars[c]++;
					charCount++;
				}
			}
		}

		if (charCount == 0) 
			return new double[0];

		for (int i = 0; i < chars.length; i++) {
			chars[i] /= charCount;
		}
		return chars;
	}
}
