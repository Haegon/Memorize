package com.gohn.memorize.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;
import com.opencsv.CSVReader;

public class ReadCSV {

	public static ArrayList<WordSet> Read(final String filePath) {

		File inputWorkbook = new File(filePath);

		ArrayList<WordSet> words = new ArrayList<WordSet>();

		try {
			CSVReader reader = new CSVReader(new FileReader(inputWorkbook));

			try {

				for (;;) {
					String[] sa = reader.readNext();
					
					if (sa == null)
						break;

					Log.d("gohn", sa[0] + "@" + sa[1] + "@" + sa[2]);
					WordSet word = new WordSet();

					word.Type = sa[0];
					word.Word = sa[1];
					word.Meaning = sa[2];

					if (WordType.isType(word.Type))
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
