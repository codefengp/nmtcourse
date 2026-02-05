package cn.fengp.basic.module.nmt.service.studentachievement.importer;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.ImportExcelData;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImportExcelParser {

    /**
     * 解析导入 Excel
     *
     * @param file        上传文件
     * @param hideRowNum  隐藏行号（计划 id 行）
     * @param firstRowNum 第一行数据行号
     */
    public ImportExcelData parse(MultipartFile file,
                                 int hideRowNum,
                                 int firstRowNum) throws IOException {

        Workbook workbook = buildWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // 1. 解析表头
        String[][] columnKeys = parseColumnKeys(sheet, hideRowNum, firstRowNum);

        // 2. 解析数据行
        List<JSONObject> rowDataList = parseRowData(sheet, columnKeys, hideRowNum, firstRowNum);

        return new ImportExcelData(columnKeys, rowDataList);
    }

    private Workbook buildWorkbook(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (name == null || !name.endsWith(".xlsx")) {
            throw ServiceExceptionUtil.invalidParamException("导入文件格式必须为 xlsx");
        }
        return new XSSFWorkbook(file.getInputStream());
    }

    private String[][] parseColumnKeys(Sheet sheet, int hideRowNum, int firstRowNum) {
        Row hideRow = sheet.getRow(hideRowNum);
        String[][] column = new String[firstRowNum][hideRow.getLastCellNum()];

        column[hideRowNum][0] = "number";
        column[hideRowNum][1] = "name";

        for (Row row : sheet) {
            if (row.getRowNum() >= firstRowNum) break;
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
                column[cell.getRowIndex()][cell.getColumnIndex()] = cell.getStringCellValue();
            }
        }
        return column;
    }

    private List<JSONObject> parseRowData(Sheet sheet,
                                          String[][] column,
                                          int hideRowNum,
                                          int firstRowNum) {
        List<JSONObject> list = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() < firstRowNum) continue;

            JSONObject data = new JSONObject();
            data.put("row_num", row.getRowNum());

            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                cell = cell == null ? row.createCell(i) : cell;
                cell.setCellType(CellType.STRING);
                data.put(column[hideRowNum][i], cell.getStringCellValue());
            }
            list.add(data);
        }
        return list;
    }
}
