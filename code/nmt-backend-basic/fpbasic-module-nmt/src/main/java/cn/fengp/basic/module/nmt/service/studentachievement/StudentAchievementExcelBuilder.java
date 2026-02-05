package cn.fengp.basic.module.nmt.service.studentachievement;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * 学生成绩 Excel Builder
 * <p>
 * 统一处理两种场景：
 * 1. 模板下载（buildTemplateWorkbook）
 * 2. outFail 导出（buildOutFailWorkbook）
 * <p>
 * 所有 Excel 操作集中在该 Builder 类中，ServiceImpl 只负责传入数据
 */
public class StudentAchievementExcelBuilder {

    private final List<JSONObject> column;             // 表头列信息
    private final List<JSONObject> stuAchievements;   // 学生成绩列表（模板下载用）
    private final JSONArray failData;                 // 错误数据（outFail用）
    private final JSONArray successData;              // 正确数据（outFail用）

    /**
     * 万能构造函数
     *
     * @param column          列信息
     * @param stuAchievements 学生成绩列表（模板下载可用）
     * @param failData        错误数据（outFail可用）
     * @param successData     正确数据（outFail可用）
     */
    public StudentAchievementExcelBuilder(List<JSONObject> column,
                                          List<JSONObject> stuAchievements,
                                          JSONArray failData,
                                          JSONArray successData) {
        this.column = column;
        this.stuAchievements = stuAchievements != null ? stuAchievements : Collections.emptyList();
        this.failData = failData != null ? failData : new JSONArray();
        this.successData = successData != null ? successData : new JSONArray();
    }

    /**
     * -------------------------
     * 第一阶段：模板下载
     * -------------------------
     */
    public XSSFWorkbook buildTemplateWorkbook() throws IOException {
        InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(ins);

        XSSFCellStyle headerStyle = createHeaderStyle(workbook);
        XSSFCellStyle redStyle = createRedStyle(workbook);

        XSSFSheet sheet = workbook.getSheetAt(0);

        // 构建表头，返回隐藏行（planId隐藏行）
        XSSFRow hideRow = buildHeader(sheet, column, headerStyle, redStyle);

        // 填充学生成绩
        fillStudentData(sheet, stuAchievements, hideRow);

        return workbook;
    }

    /**
     * -------------------------
     * 第二阶段：outFail 导出
     * -------------------------
     */
    public XSSFWorkbook buildOutFailWorkbook() throws IOException {
        InputStream ins = ResourceUtil.getStream("template/achievement.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(ins);

        XSSFCellStyle headerStyle = createHeaderStyle(workbook);
        XSSFCellStyle redStyle = createRedStyle(workbook);

        XSSFSheet sheet = workbook.getSheetAt(0);

        // 1. 构建表头，返回隐藏行
        XSSFRow hideRow = buildHeader(sheet, column, headerStyle, redStyle);

        // 2. 写错误数据（带批注），返回最后一行行号
        int lastRow = fillFailData(sheet, failData, hideRow, redStyle);

        // 3. 写分隔行
        lastRow = fillSeparator(sheet, lastRow, redStyle);

        // 4. 写成功数据
        fillSuccessData(sheet, successData, hideRow, lastRow + 1);

        return workbook;
    }

    // -------------------------
    // Excel 私有方法
    // -------------------------

    /**
     * 构建表头
     *
     * @param sheet  当前 sheet
     * @param column 列信息
     * @param style  表头样式
     * @param style1 红色字体样式（说明文字）
     * @return 隐藏行 row4（存 planId，学生成绩填充用）
     */
    private XSSFRow buildHeader(XSSFSheet sheet, List<JSONObject> column, XSSFCellStyle style, XSSFCellStyle style1) {
        XSSFRow row0 = sheet.createRow(0);
        XSSFRow row1 = sheet.createRow(1);
        XSSFRow row2 = sheet.createRow(2);
        XSSFRow row3 = sheet.createRow(3);
        XSSFRow row4 = sheet.createRow(4);
        XSSFRow row5 = sheet.createRow(5);

        // row4 隐藏（存 planId）
        row4.setZeroHeight(true);

        row0.createCell(0).setCellValue("考核方式");
        row1.createCell(0).setCellValue("考核内容");
        row2.createCell(0).setCellValue("课程目标");
        row3.createCell(0).setCellValue("总分值");
        row4.createCell(0).setCellValue("number");
        row4.createCell(1).setCellValue("name");
        row5.createCell(0).setCellValue("学号");
        row5.createCell(1).setCellValue("姓名");

        XSSFCell cell52 = row5.createCell(2);
        cell52.setCellValue("--说明：直接根据学生学号、姓名输入对应的成绩，不要改动表头的任何数据--");
        cell52.setCellStyle(style1);

        // 样式设置
        row0.getCell(0).setCellStyle(style);
        row1.getCell(0).setCellStyle(style);
        row2.getCell(0).setCellStyle(style);
        row3.getCell(0).setCellStyle(style);
        row5.getCell(0).setCellStyle(style);
        row5.getCell(1).setCellStyle(style);

        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 2, 10));

        // 动态表头
        for (int i = 0, index = 2; i < column.size(); i++) {
            JSONObject col = column.get(i);
            List<JSONObject> planList = (List<JSONObject>) col.get("planList");

            XSSFCell cell0Index = row0.createCell(index);
            cell0Index.setCellValue(col.getString("modeName"));
            cell0Index.setCellStyle(style);

            if (planList.size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, index, index + planList.size() - 1));
            }

            for (int j = 0; j < planList.size(); j++) {
                JSONObject obj = planList.get(j);

                XSSFCell c1 = row1.createCell(index + j);
                c1.setCellValue(obj.getString("content"));
                c1.setCellStyle(style);

                XSSFCell c2 = row2.createCell(index + j);
                c2.setCellValue(obj.getString("objectiveName"));
                c2.setCellStyle(style);

                XSSFCell c3 = row3.createCell(index + j);
                c3.setCellValue(obj.getString("score"));
                c3.setCellStyle(style);

                row4.createCell(index + j).setCellValue(obj.getString("planId"));
            }
            index += planList.size();
        }
        return row4;
    }

    /**
     * 填充学生成绩（模板下载用）
     */
    private void fillStudentData(XSSFSheet sheet, List<JSONObject> stuAchievements, XSSFRow hideRow) {
        short totalCols = hideRow.getLastCellNum();
        for (int i = 0; i < stuAchievements.size(); i++) {
            JSONObject stu = stuAchievements.get(i);
            XSSFRow row = sheet.createRow(6 + i);
            row.createCell(0).setCellValue(stu.getString("studentNumber"));
            row.createCell(1).setCellValue(stu.getString("studentName"));
            for (int j = 2; j < totalCols; j++) {
                row.createCell(j)
                        .setCellValue(stu.getString(hideRow.getCell(j).getStringCellValue().toLowerCase()));
            }
        }
    }

    /**
     * 写错误数据（带批注）
     */
    private int fillFailData(XSSFSheet sheet, JSONArray failData, XSSFRow hideRow, XSSFCellStyle redStyle) {
        if (failData == null || failData.isEmpty()) return 5;

        int rowIndex = 6;
        for (Object obj : failData) {
            JSONObject var = (JSONObject) obj;
            XSSFRow row = sheet.createRow(rowIndex++);
            for (int j = 0; j < hideRow.getLastCellNum(); j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(var.getString(hideRow.getCell(j).getStringCellValue()));

                // 批注
                String errorKey = hideRow.getCell(j).getStringCellValue() + "__error";
                if (var.containsKey(errorKey)) {
                    XSSFDrawing drawing = sheet.createDrawingPatriarch();
                    XSSFComment comment = drawing.createCellComment(new XSSFClientAnchor());
                    comment.setString(new XSSFRichTextString(var.getString(errorKey)));
                    cell.setCellComment(comment);
                }
            }
        }
        return rowIndex;
    }

    /**
     * 写分隔行
     */
    private int fillSeparator(XSSFSheet sheet, int rowIndex, XSSFCellStyle style) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("------------------------错误数据分割行，下方是验证通过的数据行----------------------");
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 8));
        return rowIndex;
    }

    /**
     * 写成功数据
     */
    private void fillSuccessData(XSSFSheet sheet, JSONArray successData, XSSFRow hideRow, int startRow) {
        if (successData == null || successData.isEmpty()) return;

        int rowIndex = startRow;
        for (Object obj : successData) {
            JSONObject var = (JSONObject) obj;
            XSSFRow row = sheet.createRow(rowIndex++);
            for (int j = 0; j < hideRow.getLastCellNum(); j++) {
                row.createCell(j)
                        .setCellValue(var.getString(hideRow.getCell(j).getStringCellValue()));
            }
        }
    }

    /**
     * 表头样式（居中+加粗）
     */
    private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    /**
     * 红色字体样式
     */
    private XSSFCellStyle createRedStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        return style;
    }
}
