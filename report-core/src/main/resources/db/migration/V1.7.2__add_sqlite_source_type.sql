-- Add SQLite source type into dictionary
DELETE FROM gaea_dict_item WHERE dict_code = 'SOURCE_TYPE' AND item_value = 'sqlite';

INSERT INTO gaea_dict_item
    (dict_code, item_name, item_value, item_extend, enabled, locale, remark, sort, create_by, create_time, update_by, update_time, version)
VALUES
    ('SOURCE_TYPE', 'sqlite', 'sqlite',
     '[{"label":"driverName","value":"org.sqlite.JDBC","labelValue":"驱动类"},
       {"label":"jdbcUrl","value":"jdbc:sqlite:/app/data/sqlite/ajreport.db","labelValue":"连接串"},
       {"label":"username","value":"","labelValue":"用户名"},
       {"label":"password","value":"","labelValue":"密码"}]',
     1, 'zh', NULL, 35, 'admin', NOW(), 'admin', NOW(), 1);
