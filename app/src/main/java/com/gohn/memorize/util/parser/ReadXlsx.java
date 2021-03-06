package com.gohn.memorize.util.parser;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ReadXlsx {
	public static ArrayList<WordSet> Read(String filePath) {
		try {
			File simpleXlsxInternalStoragePath = new File(filePath);
			InputStream fileStream = new FileInputStream(simpleXlsxInternalStoragePath);
			return readExcel(fileStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<WordSet> readExcel(final InputStream fileStream) {

		ArrayList<WordSet> words = new ArrayList<WordSet>();

		try {
			Workbook wb = new Workbook(fileStream);
			Worksheet worksheet = wb.getWorksheets().get(0);

			Cells cells = worksheet.getCells();

			for (int r = 0; r < cells.getMaxRow(); r++) {

				WordSet word = new WordSet();

				word.setType(cells.get(r, 0).getStringValue());
				word.setWord(cells.get(r, 1).getStringValue());
				word.setMeaning(cells.get(r, 2).getStringValue());

				if (!WordType.isType(word.getType()))
					word.setType(WordType.ETC);
				words.add(word);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return words;
	}
}
