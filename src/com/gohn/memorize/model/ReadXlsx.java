package com.gohn.memorize.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import android.util.Log;

public class ReadXlsx {
	static final int BUF_SIZE = 8 * 1024;

	public static ArrayList<WordSet> Read(String filePath) {
		Log.d("gohn", "@@@@@ Read "+filePath);

		try {
			File simpleXlsxInternalStoragePath = new File(filePath);
			InputStream fileStream = new FileInputStream(simpleXlsxInternalStoragePath);
			return readExcel(fileStream, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<WordSet> readExcel(final InputStream fileStream, final String mime) {

		Log.d("gohn", "@@@@@ START "+fileStream.toString());

		ArrayList<WordSet> words = new ArrayList<WordSet>();

		try {
			Workbook workbook = null;
			if (mime.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
				Log.d("gohn", "@@@@@ xlsx ");
				workbook = new XSSFWorkbook(fileStream);
			} else if (mime.equals("application/vnd.ms-excel")) {
				Log.d("gohn", "@@@@@ xls");
				workbook = new HSSFWorkbook(fileStream);
			} else {
				return null;
			}
//			Sheet sheet = workbzmsook.getSheetAt(0);
//			int rowsCount = sheet.getPhysicalNumberOfRows();
//			FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
//			for (int r = 0; r < rowsCount; r++) {
//				Row row = sheet.getRow(r);
//				int cellsCount = row.getPhysicalNumberOfCells();
//				for (int c = 0; c < cellsCount; c++) {
//					String value = null;
//					try {
//						Cell cell = row.getCell(c);
//						CellValue cellValue = formulaEvaluator.evaluate(cell);
//						
//						Log.d("gohn", "@@@@@ toString " + cellValue.toString());
//						
//						switch (cellValue.getCellType()) {
//						case Cell.CELL_TYPE_BOOLEAN:
//							break;
//						case Cell.CELL_TYPE_NUMERIC:
//							double numericValue = cellValue.getNumberValue();
//							if (HSSFDateUtil.isCellDateFormatted(cell)) {
//								double date = cellValue.getNumberValue();
//								SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
//							} else {
//							}
//							break;
//						case Cell.CELL_TYPE_STRING:
//							Log.d("gohn", "@@@@@ " + cellValue.getStringValue());
//							break;
//						default:
//						}
//					} catch (NullPointerException nE) {
//					}
//				}
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return words;
	}
}
