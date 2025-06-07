package com.automation.dtc.inputsdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadDtcTable {
	private int startRow;
	private String rcdPath;
	static List<List<String>> rcdFinalData;

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
		List<List<String>> data_dtc = new ArrayList<>();
		try {
			FileInputStream file = new FileInputStream(
					"C://Users//mtouhafi//Desktop//DIAG-COM-22-053_O_FCPS (1).xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet("LECTURE_DEFAUTS");

			int maxEmptyRows = 2;
			int emptyCount = 0;

			for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					emptyCount++;
					if (emptyCount >= maxEmptyRows)
						break;
					else
						continue;
				}

				boolean allCellsEmpty = true;
				for (int col : new int[] { 2, 3, 4, 6 }) {
					Cell cell = row.getCell(col);
					if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
						allCellsEmpty = false;
						break;
					}
				}

				if (allCellsEmpty) {
					emptyCount++;
					if (emptyCount >= maxEmptyRows)
						break;
					continue;
				} else {
					emptyCount = 0;
				}
				List<String> cols_vals = new ArrayList<String>();
				for (int col : new int[] { 2, 3, 4, 6 }) {
					Cell cell = row.getCell(col);
					String value = (cell != null) ? cell.toString().trim().replace(".0", "") : "";
					if (col == 2) {
						value = value.toUpperCase();
						value = value.replace("LEFT(", "");
						value = value.replace(")", "");
						value = value.split(",")[0];
						String secondDigit = toTwoBitBinary(value);
						String convertedDigit = binaryToHex(concatDigits(value.charAt(0), secondDigit));
						value = convertedDigit + value.substring(2);
					}
					cols_vals.add(value);
				}
				data_dtc.add(cols_vals);
			}
			workbook.close();
			file.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		ReadDtcTable.rcdFinalData = data_dtc;
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
			return "00" + second;
		case 'C':
			return "01" + second;
		case 'B':
			return "10" + second;
		case 'U':
			return "11" + second;
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

	public Map<String, List<String>> organize(List<List<String>> rcdFinalData, Map<String, List<String>> OLD_DIAG_DTC) {
		List<String> DTC = new ArrayList<String>();
		for (List<String> list : rcdFinalData) {
			if (!DTC.contains(list.get(0))) {
				DTC.add(list.get(0));
			}
		}

		Map<String, List<String>> organizedRcdDtc = new HashMap<>();
		for (int i = 0; i < DTC.size(); i++) {
			List<String> caracterizations = new ArrayList<String>();
			for (int j = 0; j < rcdFinalData.size(); j++) {
				if (DTC.get(i).equals(rcdFinalData.get(j).get(0))) {
					if (!caracterizations.contains(rcdFinalData.get(j).get(2))) {
						caracterizations.add(rcdFinalData.get(j).get(2));
					}
				}
			}
			organizedRcdDtc.put(DTC.get(i), caracterizations);
		}

		Map<String, List<String>> allDtcsToAddInDiag = new HashMap<>();
		for (Map.Entry<String, List<String>> rcdEntry : organizedRcdDtc.entrySet()) {
			String dtc = rcdEntry.getKey();
			List<String> rcdCaracterizations = rcdEntry.getValue();

			List<String> devCaracterizations = OLD_DIAG_DTC.get(dtc);

			if (devCaracterizations == null) {
				allDtcsToAddInDiag.put(dtc, new ArrayList<>(rcdCaracterizations));
				for (String cara : rcdCaracterizations) {
					System.out.println(dtc + "_" + cara);
				}
			} else {
				List<String> nonDevCara = new ArrayList<>();
				for (String cara : rcdCaracterizations) {
					if (!devCaracterizations.contains(cara)) {
						System.out.println(dtc + "_" + cara);
						nonDevCara.add(cara);
					}
				}
				if (!nonDevCara.isEmpty()) {
					allDtcsToAddInDiag.put(dtc, nonDevCara);
				}
			}
		}
		System.out.println(allDtcsToAddInDiag);
		return allDtcsToAddInDiag;
	}
}
