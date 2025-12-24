package cn.fengp.basic.module.nmt.enums;

import cn.fengp.basic.framework.common.exception.ErrorCode;

// TODO 待办：请将下面的错误码复制到 fpbasic-module-nmt 模块的 ErrorCodeConstants 类中。注意，请给“TODO 补充编号”设置一个错误码编号！！！
// ========== 课程基本信息 TODO 补充编号 ==========
public interface ErrorCodeConstants {
    ErrorCode COURSE_INFO_NOT_EXISTS = new ErrorCode(1_010_000_001, "课程基本信息不存在");
    ErrorCode COURSE_OBJECTIVE_NOT_EXISTS = new ErrorCode(1_010_000_002, "课程目标不存在");
}
