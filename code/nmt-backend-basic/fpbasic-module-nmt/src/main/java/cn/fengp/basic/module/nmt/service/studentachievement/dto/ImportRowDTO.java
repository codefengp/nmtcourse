package cn.fengp.basic.module.nmt.service.studentachievement.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)//生成 equals 和 hashCode 时，要把父类的字段也一起参与比较。
public class ImportRowDTO extends RowDataDTO {
    private Integer rowIndex; 
    private Map<String, String> errorMap = new HashMap<>();

    public void addError(String key, String msg) { this.errorMap.put(key, msg); }
    public boolean hasError() { return !this.errorMap.isEmpty(); }
}