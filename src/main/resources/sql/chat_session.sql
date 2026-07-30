CREATE TABLE `chat_session` (
                                `id` BIGINT(19) NOT NULL COMMENT '数据id',
                                `session_id` VARCHAR(100) NOT NULL COMMENT '会话id' COLLATE 'utf8mb4_bin',
                                `user_id` BIGINT(19) NOT NULL COMMENT '用户id',
                                `title` VARCHAR(100) NULL DEFAULT NULL COMMENT '会话标题' COLLATE 'utf8mb4_bin',
                                `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `creater` BIGINT(19) NOT NULL COMMENT '创建人',
                                `updater` BIGINT(19) NOT NULL COMMENT '更新人',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE INDEX `session_id_index` (`session_id`) USING BTREE,
                                INDEX `user_update_time_index` (`user_id`, `update_time`) USING BTREE
)
    COMMENT='会话记录'
    COLLATE='utf8mb4_bin'
    ENGINE=InnoDB
    ROW_FORMAT=DYNAMIC
;
