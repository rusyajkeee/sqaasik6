package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

public class ExcelReader {

    public static Object[][] getData(String path) throws Exception {

        FileInputStream fis = new FileInputStream(path);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        int rows = sheet.getPhysicalNumberOfRows() - 1;
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();

        Object[][] data = new Object[rows][cols];

        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);

            for (int j = 0; j < cols; j++) {

                Cell cell = row.getCell(j);

                if (cell == null) {
                    data[i - 1][j] = "";
                } else {
                    cell.setCellType(CellType.STRING);
                    data[i - 1][j] = cell.getStringCellValue().trim();
                }
            }
        }

        wb.close();
        fis.close();
        return data;
    }
}
