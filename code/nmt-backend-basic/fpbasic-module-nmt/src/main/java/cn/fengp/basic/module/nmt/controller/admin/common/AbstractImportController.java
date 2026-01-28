package cn.fengp.basic.module.nmt.controller.admin.common;

import cn.fengp.basic.framework.common.pojo.CommonResult;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 抽象导入控制器
 * 仅提供参数解析方法
 */
public abstract class AbstractImportController<T> {

    /** 下载模板 */
    public abstract void downloadTemplate(HttpServletResponse response, String bodyParams) throws Exception;

    /** 验证导入 */
    public abstract CommonResult<JSONObject> validateImport(MultipartFile file, String bodyParams) throws Exception;

    /** 导出错误 */
    public abstract void outFail(HttpServletResponse response, String bodyParams) throws Exception;

    /** 导入数据 */
    public abstract CommonResult<Boolean> importExcel(String bodyParams) throws Exception;

    /** 公共解析前端参数方法 */
    protected JSONObject parseBizParams(String bodyParams) {
        if (bodyParams == null || bodyParams.isEmpty()) return new JSONObject();
        JSONObject params = JSONObject.parseObject(bodyParams);
        if (params == null) return new JSONObject();
        JSONObject bizParams = params.getJSONObject("bizParams");
        return bizParams != null ? bizParams : new JSONObject();
    }
}
