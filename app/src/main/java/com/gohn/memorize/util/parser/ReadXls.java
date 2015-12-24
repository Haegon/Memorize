package com.gohn.memorize.util.parser;

import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadXls {
	
	public static ArrayList<WordSet> Read(String groupName, String filePath) {

		ArrayList<WordSet> words = new ArrayList<WordSet>();

		File inputWorkbook = new File(filePath);
		// File inputWorkbook = new File(file.getAbsolutePath());
		Workbook w = null;

		try {
			w = Workbook.getWorkbook(inputWorkbook);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get the first sheet
		Sheet sheet = w.getSheet(0);
		// Loop over first 10 column and lines

		for (int r = 0; r < sheet.getRows(); r++) {

			WordSet word = new WordSet(groupName);

			for (int c = 0; c < sheet.getColumns(); c++) {

				Cell cell = sheet.getCell(c, r);

				if (cell.getType() == CellType.LABEL) {

					String content = cell.getContents();

					switch (c) {
					case 0:
						if (!WordType.isType(content))
							word.setType(WordType.ETC);
						else
                            word.setType(content);
						break;
					case 1:
						word.setWord(content);
						break;
					case 2:
						word.setMeaning(content);
						break;
					}
				}
			}
			words.add(word);
		}
		return words;
	}
}
