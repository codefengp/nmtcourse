package cn.fengp.basic.module.nmt.service.studentachievement.importer;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.ImportExcelData;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.module.nmt.service.classstudent.ClassStudentService;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class StudentAchievementValidator {

    @Resource
    private ClassStudentService classStudentService;

    /**
     * 校验导入数据
     */
    public JSONObject validate(ImportExcelData importData, Long classId) {
        String[][] column = importData.getColumnKeys();
        List<JSONObject> rows = importData.getRowData();

        if (rows == null || rows.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("导入数据为空");
        }

        List<ClassStudentDO> students = classStudentService.listClassStudent(classId);

        List<JSONObject> successData = new ArrayList<>();
        List<JSONObject> failData = new ArrayList<>();

        for (JSONObject row : rows) {
            boolean pass = true;

            ClassStudentDO student = matchStudent(row, students);
            if (student == null) {
                row.put("number__error", "该教学班级未找到学生关联数据！");
                pass = false;
            } else {
                row.put("studentId", student.getId());
                pass = validateScores(row, column, pass);
            }

            if (pass) {
                successData.add(row);
            } else {
                failData.add(row);
            }
        }

        JSONObject result = new JSONObject();
        result.put("successData", successData);
        if (!failData.isEmpty()) {
            result.put("failData", failData);
        }
        return result;
    }

    /** 匹配学生 */
    private ClassStudentDO matchStudent(JSONObject row, List<ClassStudentDO> students) {
        String number = row.getString("number");
        String name = row.getString("name");

        if (!StringUtils.hasText(number) || !StringUtils.hasText(name)) return null;

        for (ClassStudentDO stu : students) {
            if (number.equals(stu.getNumber()) && name.equals(stu.getName())) {
                return stu;
            }
        }
        return null;
    }

    /** 校验成绩 */
    private boolean validateScores(JSONObject row, String[][] column, boolean pass) {
        int hideRowNum = 4;

        for (int i = 2; i < column[hideRowNum].length; i++) {
            String planKey = column[hideRowNum][i];
            String totalScore = column[hideRowNum - 1][i];
            String score = row.getString(planKey);

            if (!isNumeric(totalScore) || !isNumeric(score)) {
                row.put(planKey + "__error", "成绩必须为数字");
                pass = false;
                continue;
            }

            BigDecimal max = new BigDecimal(totalScore);
            BigDecimal val = new BigDecimal(score);

            if (val.compareTo(BigDecimal.ZERO) < 0 || val.compareTo(max) > 0) {
                row.put(planKey + "__error", "成绩范围应在 0 到总分之间");
                pass = false;
            }
        }
        return pass;
    }

    private boolean isNumeric(String str) {
        if (!StringUtils.hasText(str)) return false;
        try {
            new BigDecimal(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
