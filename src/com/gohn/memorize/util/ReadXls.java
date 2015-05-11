package com.gohn.memorize.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

public class ReadXls {
	
	public static ArrayList<WordSet> Read(String filePath) {

		ArrayList<WordSet> words = new ArrayList<WordSet>();

		File inputWorkbook = new File(filePath);
		// File inputWorkbook = new File(file.getAbsolutePath());
		Workbook w = null;

		try {
			w = Workbook.getWorkbook(inputWorkbook);
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the first sheet
		Sheet sheet = w.getSheet(0);
		// Loop over first 10 column and lines

		for (int r = 0; r < sheet.getRows(); r++) {

			WordSet word = new WordSet();

			for (int c = 0; c < sheet.getColumns(); c++) {

				Cell cell = sheet.getCell(c, r);

				if (cell.getType() == CellType.LABEL) {

					String content = cell.getContents();

					switch (c) {
					case 0:
						if (!WordType.isType(content))
							word.Type = WordType.ETC;
						word.Type = content;
						break;
					case 1:
						word.Word = content;
						break;
					case 2:
						word.Meaning = content;
						break;
					}
				}
			}
			words.add(word);
		}
		return words;
	}
}
