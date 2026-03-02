package cn.fengp.basic.module.nmt.service.achievementevaluation.word;

import org.apache.poi.xwpf.usermodel.*;

import java.io.InputStream;
import java.util.Map;

/**
 * @program: nmt-backend-basic
 * @description: word报告导出
 * @author: fengpeng
 * @create: 2026-03-02 11:19
 **/

public class WordTemplateHelper {

    /**
     * 核心替换方法：支持段落和表格中的 ${key} 格式占位符
     * @param document 模板文件
     * @param params 替换数据源 (Key 不需要带 ${})
     */
    public static XWPFDocument replaceWordContent(XWPFDocument document, Map<String, String> params) {
        // 替换段落中的内容
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceTextInParagraph(paragraph, params);
        }
        // 替换表格中的内容
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    replaceTextInCell(cell, params);
                }
            }
        }
        return document;
    }

    /**
     * 当前文档替换文字内容
     * @param paragraph
     * @param replacementMap
     */
    private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacementMap) {
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
                    if (text.contains(entry.getKey())) {
                        text = text.replace(entry.getKey(), entry.getValue());
                        run.setText(text, 0);
                    }
                }
            }
        }
    }

    /**
     * 在单元格当中替换文字内容
     * @param cell
     * @param replacementMap
     */
    private static void replaceTextInCell(XWPFTableCell cell, Map<String, String> replacementMap) {
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            replaceTextInParagraph(paragraph, replacementMap);
        }
    }

}
