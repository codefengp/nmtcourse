package cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class ImportExcelData {

    private final String[][] columnKeys;
    private final List<JSONObject> rowData;

    public ImportExcelData(String[][] columnKeys, List<JSONObject> rowData) {
        this.columnKeys = columnKeys;
        this.rowData = rowData;
    }

    public String[][] getColumnKeys() {
        return columnKeys;
    }

    public List<JSONObject> getRowData() {
        return rowData;
    }
}
