package cn.fengp.basic.module.nmt.service.studentachievement.excel;

import cn.fengp.basic.module.nmt.service.studentachievement.dto.HeaderDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.ImportRowDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.RowDataDTO;
import cn.fengp.basic.module.nmt.service.studentachievement.dto.TemplateDataDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelTemplateHelper {

    /**
     * 【第一阶段入口】渲染完整模板数据
     *  1.绘制表头
     *  2.填充业务行数据
     * @param workbook
     * @param templateData
     * @param headerCursor 表头总行数
     */
    public static void renderTemplate(XSSFWorkbook workbook, TemplateDataDTO templateData,int headerCursor) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        //设置表头样式
        XSSFCellStyle headerStyle = createHeaderStyle(workbook);
        //设置说明文本样式
        XSSFCellStyle tipStyle = createRedStyle(workbook);
        //表头数据
        List<HeaderDataDTO> headers = templateData.getHeaders();
        //业务行数据
        List<RowDataDTO> rows = templateData.getRows();
        // 1. 绘制表头
        int lastColIdx = drawHeader(sheet, headers, headerCursor, headerStyle, tipStyle);
        // 2. 填充业务行数据 (模板下载不需要批注，errorMap 传 null)
        for (int i = 0; i < rows.size(); i++) {
            // 数据行从第 headerCursor 行开始
            XSSFRow row = sheet.createRow(i + headerCursor);
            fillRowData(row, rows.get(i), 2, headers, null);
        }
    }

    /**
     * 【第三阶段入口】错误数据导出渲染（带分割线、批注）
     *      1. 画表头
     *      2. 填充校验失败的数据 (带批注)
     *      3. 绘制分割行
     *      4. 填充校验成功的数据 (不带批注)
     * @param headerCursor 表头总行数
     * @param failData 校验失败的数据
     * @param successData 校验成功的数据
     */
    public static void renderFail(XSSFWorkbook workbook,
                                  List<HeaderDataDTO> headers,
                                  int headerCursor,
                                  List<ImportRowDTO> failData,
                                  List<ImportRowDTO> successData) {
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFCellStyle headerStyle = createHeaderStyle(workbook);
        XSSFCellStyle redStyle = createRedStyle(workbook);
        // 1. 画表头
        int lastColIdx = drawHeader(sheet, headers, headerCursor,headerStyle, redStyle);
        // 2. 填充校验失败的数据 (带批注)
        if (failData != null) {
            for (ImportRowDTO row : failData) {
                fillRowData(sheet.createRow(headerCursor++), row, 2, headers, row.getErrorMap());
            }
        }
        // 3. 绘制分割行
        XSSFRow splitRow = sheet.createRow(headerCursor++);
        XSSFCell splitCell = splitRow.createCell(0);
        splitCell.setCellStyle(redStyle);
        splitCell.setCellValue("------------------------错误数据分割行，下方是验证通过的数据行----------------------");
        sheet.addMergedRegion(new CellRangeAddress(splitRow.getRowNum(), splitRow.getRowNum(), 0, lastColIdx));
        // 4. 填充校验成功的数据 (不带批注)
        if (successData != null) {
            for (ImportRowDTO row : successData) {
                fillRowData(sheet.createRow(headerCursor++), row, 2, headers, null);
            }
        }
    }

    /**
     * 【核心绘制方法】绘制 0-5 行表头并处理合并逻辑（无省略）
     * @return 动态列的最后一列索引
     */
    public static int drawHeader(XSSFSheet sheet, List<HeaderDataDTO> headers, int headerCursor, XSSFCellStyle headerStyle, XSSFCellStyle tipStyle) {
        XSSFRow[] headerRows = new XSSFRow[headerCursor];
        for (int i = 0; i < headerCursor; i++) {
            headerRows[i] = sheet.getRow(i) == null ? sheet.createRow(i) : sheet.getRow(i);
        }
        // 1. 填充固定文本
        headerRows[0].createCell(0).setCellValue("考核方式");
        headerRows[1].createCell(0).setCellValue("考核内容");
        headerRows[2].createCell(0).setCellValue("课程目标");
        headerRows[3].createCell(0).setCellValue("总分值");
        headerRows[4].createCell(0).setCellValue("number");
        headerRows[4].createCell(1).setCellValue("name");
        headerRows[5].createCell(0).setCellValue("学号");
        headerRows[5].createCell(1).setCellValue("姓名");
        // 2. 动态填充考核列数据
        int startCol = 2;
        for (int i = 0; i < headers.size(); i++) {
            int colIdx = startCol + i;
            HeaderDataDTO col = headers.get(i);
            headerRows[0].createCell(colIdx).setCellValue(col.getModeName());
            headerRows[1].createCell(colIdx).setCellValue(col.getContent());
            headerRows[2].createCell(colIdx).setCellValue(col.getObjectiveName());
            headerRows[3].createCell(colIdx).setCellValue(col.getScore());
            headerRows[4].createCell(colIdx).setCellValue(col.getPlanId().toString());
            // 应用表头样式 (前4行)
            for (int j = 0; j < 4; j++) {
                headerRows[j].getCell(colIdx).setCellStyle(headerStyle);
            }
        }
        // 3. 应用固定列样式及合并
        for (int i = 0; i < 4; i++) {
            headerRows[i].getCell(0).setCellStyle(headerStyle);
            //表头合并前两列
            sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 1));
        }
        headerRows[4].setZeroHeight(true); // 隐藏 planId 行
        headerRows[5].getCell(0).setCellStyle(headerStyle);
        headerRows[5].getCell(1).setCellStyle(headerStyle);
        // 4. 【核心合并逻辑】处理考核方式跨列合并
        if (headers.size() > 1) {
            int currentGroupStart = startCol;
            for (int i = 1; i < headers.size(); i++) {
                // 如果当前模式名称与前一个不同，说明上一组结束了
                if (!headers.get(i).getModeName().equals(headers.get(i - 1).getModeName())) {
                    int endColIdx = i - 1 + startCol;
                    if (endColIdx > currentGroupStart) {
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, currentGroupStart, endColIdx));
                    }
                    currentGroupStart = i + startCol; // 开启新组
                }
            }
            // 处理最后一组合并
            // 例如：【作业, 作业, 期中, 期中】,最后一组是【期中, 期中】,后面没有“新成员”触发if判断进行合并
            int finalEndColIdx = startCol + headers.size() - 1;
            if (finalEndColIdx > currentGroupStart) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, currentGroupStart, finalEndColIdx));
            }
        }
        // 5. 说明文字填充与合并
        int tipMergeEnd = Math.max(10, startCol + headers.size() - 1);
        XSSFCell cell52 = headerRows[5].createCell(2);
        cell52.setCellValue("--说明：直接根据学生学号、姓名输入对应的成绩，不要改动表头的任何数据--");
        cell52.setCellStyle(tipStyle);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 2, tipMergeEnd));

        return startCol + headers.size() - 1;
    }

    /**
     * 【核心填充逻辑】单行数据填充及错误批注回写
     */
    public static void fillRowData(XSSFRow row, RowDataDTO rowData, int startCol, List<HeaderDataDTO> headers, Map<String, String> errorMap) {
        XSSFSheet sheet = (XSSFSheet) row.getSheet();
        // 填充学号列
        XSSFCell numCell = row.createCell(0);
        numCell.setCellValue(rowData.getStudentNumber());
        if (errorMap != null && errorMap.containsKey("number")) {
            addComment(sheet, numCell, errorMap.get("number"));
        }
        // 填充姓名列
        row.createCell(1).setCellValue(rowData.getStudentName());
        // 循环填充成绩动态列
        for (int j = 0; j < headers.size(); j++) {
            Long planId = headers.get(j).getPlanId();
            String scoreVal = rowData.getScores().get(planId);
            XSSFCell scoreCell = row.createCell(startCol + j);
            scoreCell.setCellValue(scoreVal == null ? "" : scoreVal);
            // 检查该计划是否有错，若有则添加批注
            if (errorMap != null && errorMap.containsKey(planId.toString())) {
                addComment(sheet, scoreCell, errorMap.get(planId.toString()));
            }
        }
    }

    public static XSSFCellStyle createHeaderStyle(XSSFWorkbook work) {
        XSSFCellStyle style = work.createCellStyle();
        style.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        XSSFFont font = work.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static XSSFCellStyle createRedStyle(XSSFWorkbook work) {
        XSSFCellStyle style = work.createCellStyle();
        XSSFFont font = work.createFont();
        font.setColor(org.apache.poi.ss.usermodel.IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }

    /**
     * 统一获取单元格内容（静态辅助方法）
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) return "";
        // 强制转为 STRING 类型读取，避免读取数字时的科学计数法或格式问题
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    /**
     * 判断行是否为空
     */
    public static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        Cell cell = row.getCell(0); // 默认以第一列（学号）为准
        return cell == null || !StringUtils.hasText(getCellValue(cell));
    }

    /**
     * 判断字符串是否是整数或者小数
     */
    public static boolean isNumeric(String str) {
        if(!StringUtils.hasText(str)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 添加 Excel 批注
     */
    public static void addComment(XSSFSheet sheet, XSSFCell cell, String msg) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,
                (short) cell.getColumnIndex(), cell.getRowIndex(),
                (short) (cell.getColumnIndex() + 2), cell.getRowIndex() + 3);
        XSSFComment comment = drawing.createCellComment(anchor);
        comment.setString(new XSSFRichTextString(msg));
        cell.setCellComment(comment);
    }

}