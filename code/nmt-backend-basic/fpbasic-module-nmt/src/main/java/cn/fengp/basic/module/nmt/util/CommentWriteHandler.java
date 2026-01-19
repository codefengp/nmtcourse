package cn.fengp.basic.module.nmt.util;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.metadata.Head;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.handler.CellWriteHandler;
import cn.idev.excel.write.metadata.holder.WriteSheetHolder;
import cn.idev.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel导出错误信息，添加单元格错误批注
 */
public class CommentWriteHandler implements CellWriteHandler {

    private final List<JSONObject> dataList;
    private final Map<Integer, String> columnFieldMap;

    public CommentWriteHandler(List<JSONObject> dataList, Class<?> headClass) {
        this.dataList = dataList;
        this.columnFieldMap = getColumnFieldMap(headClass);
    }

    /**
     * 添加错误批注信息，同时添加一行正确数据和错误数据分割行提示
     * @param writeSheetHolder
     * @param writeTableHolder
     * @param cellDataList
     * @param cell
     * @param head
     * @param relativeRowIndex
     * @param isHead
     */
    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 List<WriteCellData<?>> cellDataList, Cell cell,
                                 Head head, Integer relativeRowIndex, Boolean isHead) {
        if (isHead || relativeRowIndex == null) return;

        JSONObject jsonData = dataList.get(relativeRowIndex);

        // 如果是提示行
        if (Boolean.TRUE.equals(jsonData.get("isComment"))) {
            Sheet sheet = cell.getSheet();
            int rowNum = cell.getRowIndex();

            // 只在第一列做合并，避免重复
            if (cell.getColumnIndex() == 0) {
                try {
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));
                } catch (IllegalStateException e) {
                    // 已经合并过就忽略
                }
                // 设置样式
                CellStyle style = sheet.getWorkbook().createCellStyle();
                Font font = sheet.getWorkbook().createFont();
                font.setBold(true);
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                cell.setCellStyle(style);

                cell.setCellValue(jsonData.getString("number")); // 提示文字
            }
            return; // 跳过批注
        }

        // 错误批注逻辑
        String fieldName = columnFieldMap.get(cell.getColumnIndex());
        if (fieldName != null) {
            String errorKey = fieldName + "__error";
            if (jsonData.containsKey(errorKey)) {
                String commentText = jsonData.get(errorKey).toString();
                addComment(cell, commentText);
            }
        }
    }


    private void addComment(Cell cell, String commentText) {
        CreationHelper creationHelper = cell.getSheet().getWorkbook().getCreationHelper();
        Drawing<?> drawing = cell.getSheet().createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,
                cell.getColumnIndex(), cell.getRowIndex(),
                cell.getColumnIndex() + 2, cell.getRowIndex() + 3);
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(creationHelper.createRichTextString(commentText));
        cell.setCellComment(comment);
    }

    /**
     * 根据excel字段注解获取字段名称
     * @param headClass
     * @return
     */
    private Map<Integer, String> getColumnFieldMap(Class<?> headClass) {
        Map<Integer, String> columnFieldMap = new HashMap<>();
        Field[] fields = headClass.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty != null) {
                columnFieldMap.put(index++, field.getName());
            }
        }
        return columnFieldMap;
    }
}