package com.automation.dtc.readtable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadDtcTable {
	private int startRow;
	private String rcdPath;

	public ReadDtcTable() {
		this.startRow = 26;
	}

	public String getRcdPath() {
		return rcdPath;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	
	public void setRcdPath(String rcdPath) {
		this.rcdPath = rcdPath;
	}
	
	public void readTable() {
		try {
			FileInputStream file = new FileInputStream(rcdPath);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet("LECTURE_DEFAUTS");

			
			int maxEmptyRows = 3;
			int emptyCount = 0;
			List<List<String>> data_dtc = new ArrayList<>();
			for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
			    Row row = sheet.getRow(i);
			    if (row == null) {
			        emptyCount++;
			        if (emptyCount >= maxEmptyRows) break;
			        else continue;
			    }

			    boolean allCellsEmpty = true;
			    for (int col : new int[] {2, 3, 4, 6}) {
			        Cell cell = row.getCell(col);
			        if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
			            allCellsEmpty = false;
			            break;
			        }
			    }

			    if (allCellsEmpty) {
			        emptyCount++;
			        if (emptyCount >= maxEmptyRows) break;
			        continue;
			    } else {
			        emptyCount = 0;
			    }
			    List<String> cols_vals = new ArrayList<String>();
			    for (int col : new int[] {2, 3, 4, 6}) {
			        Cell cell = row.getCell(col);
			        String value = (cell != null) ? cell.toString().trim() : "";
			        cols_vals.add(value);
			    }
			    data_dtc.add(cols_vals);
			}
			System.out.println(data_dtc);
			workbook.close();
			file.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
