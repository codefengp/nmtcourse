package cn.fengp.basic.module.nmt.service.achievementevaluation.word;

import cn.fengp.basic.framework.common.util.http.HttpUtils;
import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.MergeCellRule;
import com.deepoove.poi.data.MergeCellRule.Grid;
import com.deepoove.poi.data.MergeCellRule.MergeCellRuleBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * @program: nmt-backend-basic
 * @description: word报告导出
 * @author: fengpeng
 * @create: 2026-03-02 11:19
 **/

public class WordTemplateHelper {

    /**
     * 导出word文件
     * @param response
     * @param filePath
     * @param wordContentMap
     * @param filename
     * @throws IOException
     */
    public static void write(HttpServletResponse response, String filePath, Map wordContentMap,String filename) throws IOException {
        // 1. 设置响应头
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=\"" + HttpUtils.encodeUtf8(filename) + "\"");
        // 2. 渲染word,并导出
        try (InputStream in = ResourceUtil.getStream(filePath);
             XWPFTemplate template = XWPFTemplate.compile(in).render(wordContentMap);
             BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
            // 执行渲染并写出
            if (null != template) {
                template.write(bos);
                bos.flush();
            }
        } catch (Exception e) {
            // 建议增加日志记录
            throw new IOException("生成Word文档失败", e);
        }
    }

    /**
     * 根据数据列表自动生成第1列和第5列的行合并规则
     * @param key 合并规则
     * @param rowList 业务数据集合（不包含表头）
     * @return 配置好的 MergeCellRule 对象
     */
    public static MergeCellRule getObjectiveMergeRule(List<JSONObject> rowList,String key) {
        MergeCellRuleBuilder builder = MergeCellRule.builder();
        // 1. 获取每组连续相同 key 的个数
        List<Integer> counts = calcTableMergeRows(rowList,key);
        // 2. 定义起始行。如果 Word 表格中第 1 行是表头，则数据从索引 1 开始
        int currentRow = 1;
        for (Integer count : counts) {
            // 只有当连续行数大于 1 时才需要合并
            if (count > 1) {
                int startRow = currentRow;
                int endRow = currentRow + count - 1;
                // 合并第 1 列 (索引为 0)
                builder.map(Grid.of(startRow, 0), Grid.of(endRow, 0));
                // 合并第 5 列 (索引为 4)
                builder.map(Grid.of(startRow, 4), Grid.of(endRow, 4));
            }
            // 指针下移
            currentRow += count;
        }
        return builder.build();
    }

    /**
     * 计算相同key需要合并的行
     * @return
     */
    private static List<Integer> calcTableMergeRows(List<JSONObject> rowList,String key){
        List<Integer> result = new ArrayList<>();
        String lastId = null;
        int count = 0;
        for (JSONObject obj : rowList) {
            String currentId = obj.getString(key);
            // 如果 ID 变了且不是第一次进入，就记录上一次的计数
            if (lastId != null && !currentId.equals(lastId)) {
                result.add(count);
                count = 0; // 重置
            }
            lastId = currentId;
            count++;
        }
        result.add(count); // 添加最后一组
        return result;
    }

}
