package cn.fengp.basic.module.nmt.service.studentachievement.excel;

import cn.fengp.basic.module.nmt.service.studentachievement.dto.ColumnDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.RowDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.TemplateDataDTO;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import java.util.*;

public class ExcelTemplateRenderer {

    public static void render(XSSFWorkbook workbook, TemplateDataDTO templateData) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCellStyle headerStyle = createHeaderStyle(workbook);
        XSSFCellStyle tipStyle = createRedStyle(workbook);

        List<ColumnDataDTO> columns = templateData.getColumns();
        List<RowDataDTO> rows = templateData.getRows();

        // 1. 初始化并获取 Row 0-5
        XSSFRow[] headerRows = new XSSFRow[6];
        for (int i = 0; i < 6; i++) {
            headerRows[i] = sheet.getRow(i) == null ? sheet.createRow(i) : sheet.getRow(i);
        }

        // 2. 填充左侧固定表头文字
        headerRows[0].createCell(0).setCellValue("考核方式");
        headerRows[1].createCell(0).setCellValue("考核内容");
        headerRows[2].createCell(0).setCellValue("课程目标");
        headerRows[3].createCell(0).setCellValue("总分值");
        headerRows[4].createCell(0).setCellValue("number");
        headerRows[4].createCell(1).setCellValue("name");
        headerRows[5].createCell(0).setCellValue("学号");
        headerRows[5].createCell(1).setCellValue("姓名");

        // 3. 动态填充考核计划列数据
        int startCol = 2;
        for (int i = 0; i < columns.size(); i++) {
            int colIdx = startCol + i;
            ColumnDataDTO col = columns.get(i);

            // 填充内容
            headerRows[0].createCell(colIdx).setCellValue(col.getModeName());
            headerRows[1].createCell(colIdx).setCellValue(col.getContent());
            headerRows[2].createCell(colIdx).setCellValue(col.getObjectiveName());
            headerRows[3].createCell(colIdx).setCellValue(col.getScore());
            headerRows[4].createCell(colIdx).setCellValue(col.getPlanId().toString());

            // 统一为前 4 行动态列应用样式
            for (int j = 0; j < 4; j++) {
                headerRows[j].getCell(colIdx).setCellStyle(headerStyle);
            }
        }

        // 4. 应用左侧标题列样式及合并
        for (int i = 0; i < 4; i++) {
            headerRows[i].getCell(0).setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
        }
        headerRows[5].getCell(0).setCellStyle(headerStyle);
        headerRows[5].getCell(1).setCellStyle(headerStyle);

        // 隐藏行设置
        headerRows[4].setZeroHeight(true);

        // 5. 动态“考核方式”合并逻辑
        if (columns.size() > 1) {
            int currentGroupStart = startCol;
            for (int i = 1; i < columns.size(); i++) {
                if (!columns.get(i).getModeName().equals(columns.get(i - 1).getModeName())) {
                    if (i - 1 + startCol > currentGroupStart) {
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, currentGroupStart, i - 1 + startCol));
                    }
                    currentGroupStart = i + startCol;
                }
            }
            // 最后一组处理
            if (startCol + columns.size() - 1 > currentGroupStart) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, currentGroupStart, startCol + columns.size() - 1));
            }
        }

        // 6. 说明文字填充与合并 (2-10列或更多)
        int tipMergeEnd = Math.max(10, startCol + columns.size() - 1);
        XSSFCell cell52 = headerRows[5].createCell(2);
        cell52.setCellValue("--说明：直接根据学生学号、姓名输入对应的的成绩，不要改动表头的任何数据--");
        cell52.setCellStyle(tipStyle);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 2, tipMergeEnd));

        // 7. 填充行数据
        for (int i = 0; i < rows.size(); i++) {
            XSSFRow row = sheet.createRow(i + 6);
            RowDataDTO rowData = rows.get(i);
            row.createCell(0).setCellValue(rowData.getStudentNumber());
            row.createCell(1).setCellValue(rowData.getStudentName());

            for (int j = 0; j < columns.size(); j++) {
                String val = rowData.getScores().get(columns.get(j).getPlanId());
                row.createCell(j + startCol).setCellValue(val == null ? "" : val);
            }
        }
    }

    private static XSSFCellStyle createHeaderStyle(XSSFWorkbook work) {
        XSSFCellStyle style = work.createCellStyle();
        style.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        XSSFFont font = work.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static XSSFCellStyle createRedStyle(XSSFWorkbook work) {
        XSSFCellStyle style = work.createCellStyle();
        XSSFFont font = work.createFont();
        font.setColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }
}