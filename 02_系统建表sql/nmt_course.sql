-- 创建课程表
CREATE TABLE nmt_course_info (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    number VARCHAR(64) NOT NULL COMMENT '编号',
    name VARCHAR(255) NOT NULL COMMENT '名称',
    major_type INT NOT NULL COMMENT '所属专业',
    course_type INT NOT NULL COMMENT '课程类型',
	course_property INT NOT NULL COMMENT '课程属性',
    course_hour DECIMAL(5,2) NOT NULL COMMENT '总学时',
    course_score DECIMAL(5,2) NOT NULL COMMENT '总学分',
    grade INT NOT NULL COMMENT '授课年级',
    term INT NOT NULL COMMENT '授课学期',
    teacher_name VARCHAR(200) COMMENT '授课教师',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) DEFAULT b'0' COMMENT '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程基本信息';
