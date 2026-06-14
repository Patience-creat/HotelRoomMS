-- ============================================================
-- 创建酒店房间表 (room)
-- 字段说明:
--   id        : 自增主键
--   room_no   : 房间号，唯一
--   floor     : 所在楼层
--   status    : 0-空闲 1-入住 2-打扫 3-维修
-- ============================================================

CREATE DATABASE ke;
USE ke;

CREATE TABLE IF NOT EXISTS `room` (
    `id`       INT         NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '自增主键',
    `room_no`  VARCHAR(10) NOT NULL                           COMMENT '房间号',
    `floor`    INT         NOT NULL                           COMMENT '楼层',
    `status`   INT         DEFAULT 0                          COMMENT '0-空闲 1-入住 2-打扫 3-维修',
    UNIQUE KEY `uk_room_no` (`room_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店房间表';


-- ============================================================
-- 插入示例数据（可选，按需执行）
-- ============================================================

-- 1楼：101~108
INSERT INTO `room` (`room_no`, `floor`, `status`) VALUES
('101', 1, 0),
('102', 1, 0),
('103', 1, 0),
('104', 1, 0),
('105', 1, 0),
('106', 1, 0),
('107', 1, 0),
('108', 1, 0);

-- 2楼：201~208
INSERT INTO `room` (`room_no`, `floor`, `status`) VALUES
('201', 2, 0),
('202', 2, 0),
('203', 2, 0),
('204', 2, 0),
('205', 2, 0),
('206', 2, 0),
('207', 2, 0),
('208', 2, 0);

-- 3楼：301~308
INSERT INTO `room` (`room_no`, `floor`, `status`) VALUES
('301', 3, 0),
('302', 3, 0),
('303', 3, 0),
('304', 3, 0),
('305', 3, 0),
('306', 3, 0),
('307', 3, 0),
('308', 3, 0);
