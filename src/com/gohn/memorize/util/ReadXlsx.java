package com.gohn.memorize.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import jxl.CellType;
import jxl.Sheet;
import jxl.read.biff.CellValue;
import android.util.Log;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Row;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

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

				word.Type = cells.get(r, 0).getStringValue();
				word.Word = cells.get(r, 1).getStringValue();
				word.Meaning = cells.get(r, 2).getStringValue();

				if (WordType.isType(word.Type))
					words.add(word);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return words;
	}
}
