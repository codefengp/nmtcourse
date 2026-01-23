package cn.fengp.basic.module.nmt.enums;

import cn.fengp.basic.framework.common.exception.ErrorCode;

// TODO 待办：请将下面的错误码复制到 fpbasic-module-nmt 模块的 ErrorCodeConstants 类中。注意，请给“TODO 补充编号”设置一个错误码编号！！！
// ========== 课程基本信息 TODO 补充编号 ==========
public interface ErrorCodeConstants {
    ErrorCode COURSE_INFO_NOT_EXISTS = new ErrorCode(1_010_000_001, "课程基本信息不存在");
    ErrorCode COURSE_OBJECTIVE_NOT_EXISTS = new ErrorCode(1_010_000_002, "课程目标不存在");
    ErrorCode EVALUATE_MODE_NOT_EXISTS = new ErrorCode(1_010_000_003, "考核评价方式不存在");
    ErrorCode OBJECTIVE_MODE_NOT_EXISTS = new ErrorCode(1_010_000_004, "课程目标考核评价不存在");
    ErrorCode EVALUATE_PLAN_NOT_EXISTS = new ErrorCode(1_010_000_005, "课程考核计划不存在");
    ErrorCode TEACH_CLASS_NOT_EXISTS = new ErrorCode(1_010_000_006, "教学班级不存在");
    ErrorCode CLASS_STUDENT_NOT_EXISTS = new ErrorCode(1_010_000_007, "班级学生不存在");
    ErrorCode IMPORT_LIST_IS_EMPTY = new ErrorCode(1_010_000_008, "导入数据不能为空！");
    ErrorCode CLASS_STUDENT_NUMBER_EXISTS = new ErrorCode(1_010_000_009, "学号已存在");
    ErrorCode CLASS_STUDENT_TABlE_NUMBER_EXISTS = new ErrorCode(1_010_000_010, "Excel文件中学号重复");
    ErrorCode STUDENT_ACHIEVEMENT_NOT_EXISTS = new ErrorCode(1_010_000_011, "学生成绩不存在");
}
