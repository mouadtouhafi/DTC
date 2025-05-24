package com.automation.dtc.inputsdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	static List<List<String>> rcdFinalData;

	public void readTable() {
		List<List<String>> data_dtc = new ArrayList<>();
		try {
			FileInputStream file = new FileInputStream(rcdPath);
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet("LECTURE_DEFAUTS");

			int maxEmptyRows = 3;
			int emptyCount = 0;
			
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
			        String value = (cell != null) ? cell.toString().trim().replace(".0", "") : "";
			        if(col==2) {
			        	value = value.toUpperCase();
			        	String secondDigit = toTwoBitBinary(value);
			        	String convertedDigit = binaryToHex(concatDigits(value.charAt(0), secondDigit));
			        	value = convertedDigit + value.substring(2);
			        }
			        cols_vals.add(value);
			    }
			    data_dtc.add(cols_vals);
			}
//			System.out.println(data_dtc);
			workbook.close();
			file.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		ReadDtcTable.rcdFinalData = data_dtc;
		System.out.println("DTC dans RCD : " + data_dtc);
	}
	
	public String toTwoBitBinary(String code) {
		char digitChar = code.charAt(1);
	    if (digitChar < '0' || digitChar > '3') {
	        throw new IllegalArgumentException("Character must be between '0' and '3'");
	    }

	    int number = digitChar - '0';
	    String binary = Integer.toBinaryString(number);
	    return String.format("%2s", binary).replace(' ', '0');
	}
	
	public String concatDigits(char first, String second) {
		switch (first) {
		case 'P':
			return "00"+second;
		case 'C':
			return "01"+second;
		case 'B':
			return "10"+second;
		case 'U':
			return "11"+second;
		default:
			throw new IllegalArgumentException("Unexpected value: " + first);
		}
	}
	
	public String binaryToHex(String binary) {
	    if (binary.length() != 4 || !binary.matches("[01]+")) {
	        throw new IllegalArgumentException("Input must be a 4-digit binary string");
	    }

	    int decimal = Integer.parseInt(binary, 2);
	    return Integer.toHexString(decimal).toUpperCase();
	}
	
	public Set<String> extractDTCsCode(){
		Set<String> allDtcCodes = new HashSet<>();
		for (List<String> row : ReadDtcTable.rcdFinalData) {
            allDtcCodes.add(row.get(0) + "_" + row.get(2));
        }
		return allDtcCodes;
	}
	
	public Set<String> removeIntegratedDTCs(Set<String> allDtcCodes, List<String> dtcCodesInXml) {
		Set<String> missingDtcCodes = new HashSet<>(allDtcCodes);
        missingDtcCodes.removeAll(dtcCodesInXml);
        System.out.println(missingDtcCodes);
        return missingDtcCodes;
	}
}
