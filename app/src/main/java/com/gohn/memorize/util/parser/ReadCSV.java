package com.gohn.memorize.util.parser;

import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSV {

	public static ArrayList<WordSet> Read(final String filePath) {

		File inputWorkbook = new File(filePath);

		ArrayList<WordSet> words = new ArrayList<WordSet>();

		try {
			CSVReader reader = new CSVReader(new FileReader(inputWorkbook));

			try {

				for (;;) {
					String[] row= reader.readNext();

					if (row == null)
						break;

					WordSet word = new WordSet("",row[0],row[1],row[2]);

					if (!WordType.isType(word.getType()))
						word.setType(WordType.ETC);
					words.add(word);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return words;
	}
}
