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
	public static int startRow = 24;
	private String rcdPath;
	public static List<List<String>> rcdFinalData;

	public ReadDtcTable() {
	}

	public String getRcdPath() {
		return rcdPath;
	}

	public void setRcdPath(String rcdPath) {
		this.rcdPath = rcdPath;
	}

	public void readTable() throws InvalidDtcCodeException {
		List<List<String>> data_dtc = new ArrayList<>();
		try {
			FileInputStream file = new FileInputStream(rcdPath);
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

	public String toTwoBitBinary(String code) throws InvalidDtcCodeException {
		char digitChar = code.charAt(1);
		if (digitChar < '0' || digitChar > '3') {
			System.out.println(digitChar);
			throw new InvalidDtcCodeException("Character must be between '0' and '3'");
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

	public String binaryToHex(String binary) throws InvalidDtcCodeException {
		if (binary.length() != 4 || !binary.matches("[01]+")) {
			throw new InvalidDtcCodeException("Input must be a 4-digit binary string");
		}
		int decimal = Integer.parseInt(binary, 2);
		return Integer.toHexString(decimal).toUpperCase();
	}

	public Map<String, List<String>> organizeDtcCaras(List<List<String>> rcdFinalData, Map<String, List<String>> OLD_DIAG_DTC) {
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
				allDtcsToAddInDiag.put("$"+dtc, new ArrayList<>(rcdCaracterizations));
			} else {
				List<String> nonDevCara = new ArrayList<>();
				for (String cara : rcdCaracterizations) {
					if (!devCaracterizations.contains(cara)) {
						nonDevCara.add(cara);
					}
				}
				if (!nonDevCara.isEmpty()) {
					allDtcsToAddInDiag.put(dtc, nonDevCara);
				}
			}
		}
		return allDtcsToAddInDiag;
	}
	
	public List<String> organize_labels(){
		List<String> dtc_labels = new ArrayList<String>();
		ReadDtcTable.rcdFinalData.parallelStream().forEach(innerList -> {
			String item ="";
		    for (String value : innerList) {
		    	item = item + value + "|";
		    }
		    dtc_labels.add(item);
		});
		return dtc_labels;
	}
	
	public List<List<String>> onlyDtcToDisplay(List<List<String>> rcdFinalData, Map<String, List<String>> allDtcsToAddInDiag){
		List<List<String>> onlyDTCsToAdd = new ArrayList<>();
		for (Map.Entry<String, List<String>> keys : allDtcsToAddInDiag.entrySet()) {
			String key = keys.getKey();
			for(int j=0; j<rcdFinalData.size(); j++) {
				if(key.replace("$","").equals(rcdFinalData.get(j).get(0))) {
					if(allDtcsToAddInDiag.get(key).contains(rcdFinalData.get(j).get(2))) {
						onlyDTCsToAdd.add(rcdFinalData.get(j));
					}
				}
			}
		}
		return onlyDTCsToAdd;
	}
}
