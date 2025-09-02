# AJ-Report SQLite 数据库使用指南

## 快速开始

### 1. 启动应用
确保应用已经启动并且可以访问管理界面。

### 2. 添加 SQLite 数据源

#### 方法一：使用现有 SQLite 文件
1. 进入"数据源管理"页面
2. 点击"新增"按钮
3. 选择数据源类型为 "sqlite"
4. 填写配置信息：
   - **数据源编码**：`sqlite_demo`（唯一标识）
   - **数据源名称**：`SQLite演示数据库`
   - **驱动类**：`org.sqlite.JDBC`（自动填充）
   - **连接串**：`jdbc:sqlite:/workspace/sample_report_data.db`

#### 方法二：上传 SQLite 文件
1. 在连接串字段点击"上传文件"按钮
2. 选择 .db、.sqlite 或 .sqlite3 文件
3. 系统会自动上传文件并填充连接串

### 3. 测试连接
点击"测试"按钮验证连接是否成功。

### 4. 保存数据源
测试成功后点击"确定"保存数据源配置。

## 示例 SQL 查询

使用提供的示例数据库 `sample_report_data.db`，您可以执行以下查询：

### 基础查询
```sql
-- 查询所有员工
SELECT * FROM employees;

-- 查询各部门员工数量
SELECT department, COUNT(*) as count FROM employees GROUP BY department;

-- 查询薪资统计
SELECT 
    department,
    AVG(salary) as avg_salary,
    MAX(salary) as max_salary,
    MIN(salary) as min_salary
FROM employees 
GROUP BY department;
```

### 复杂查询
```sql
-- 员工和部门关联查询
SELECT 
    e.name as employee_name,
    e.salary,
    d.name as department_name,
    d.budget as department_budget
FROM employees e
LEFT JOIN departments d ON e.department = d.name
ORDER BY e.salary DESC;

-- 使用视图查询
SELECT * FROM employee_summary;
```

## 报表设计示例

### 1. 创建员工薪资分析报表

#### 数据集配置
```sql
SELECT 
    department as '部门',
    COUNT(*) as '员工数量',
    ROUND(AVG(salary), 2) as '平均薪资',
    ROUND(MAX(salary), 2) as '最高薪资',
    ROUND(MIN(salary), 2) as '最低薪资'
FROM employees 
GROUP BY department
ORDER BY AVG(salary) DESC;
```

#### 图表配置
- **图表类型**：柱状图
- **X轴**：部门
- **Y轴**：平均薪资
- **系列**：员工数量

### 2. 创建部门预算分析报表

#### 数据集配置
```sql
SELECT 
    d.name as '部门名称',
    d.budget as '部门预算',
    COUNT(e.id) as '员工数量',
    ROUND(SUM(e.salary * 12), 2) as '年度薪资总额',
    ROUND(d.budget - SUM(e.salary * 12), 2) as '预算余额'
FROM departments d
LEFT JOIN employees e ON d.name = e.department
GROUP BY d.name, d.budget
ORDER BY d.budget DESC;
```

#### 图表配置
- **图表类型**：饼图
- **分类字段**：部门名称
- **数值字段**：部门预算

## 高级功能

### 1. 参数化查询
支持在 SQL 中使用参数：
```sql
SELECT * FROM employees 
WHERE department = '${department}' 
AND salary >= ${min_salary};
```

### 2. 时间范围查询
```sql
SELECT 
    strftime('%Y-%m', hire_date) as '入职月份',
    COUNT(*) as '入职人数'
FROM employees 
WHERE hire_date BETWEEN '${start_date}' AND '${end_date}'
GROUP BY strftime('%Y-%m', hire_date)
ORDER BY hire_date;
```

### 3. 数据转换
可以在数据集中添加数据转换脚本，对 SQLite 查询结果进行后处理。

## 最佳实践

### 1. 数据库设计
- 合理设计表结构和索引
- 使用外键约束保证数据完整性
- 考虑数据类型的选择（SQLite 支持动态类型）

### 2. 查询优化
- 为经常查询的字段创建索引
- 避免使用 `SELECT *`，明确指定需要的字段
- 合理使用 LIMIT 进行分页

### 3. 文件管理
- 定期备份 SQLite 数据库文件
- 监控数据库文件大小
- 考虑数据归档策略

### 4. 安全考虑
- 限制数据库文件的访问权限
- 定期更新和维护数据
- 考虑数据加密需求

## 故障排除

### 常见问题

#### 1. 连接失败
- **问题**：无法连接到 SQLite 数据库
- **解决方案**：
  - 检查文件路径是否正确
  - 确认应用有读写权限
  - 验证文件是否存在且未损坏

#### 2. 查询错误
- **问题**：SQL 查询执行失败
- **解决方案**：
  - 检查 SQL 语法是否正确
  - 确认表名和字段名存在
  - 注意 SQLite 的数据类型特性

#### 3. 文件上传失败
- **问题**：无法上传 SQLite 文件
- **解决方案**：
  - 检查文件格式（.db, .sqlite, .sqlite3）
  - 确认文件大小不超过 50MB
  - 检查服务器磁盘空间

### 日志查看
查看应用日志以获取详细的错误信息：
```bash
tail -f logs/application.log | grep -i sqlite
```

## API 参考

### 数据源相关 API

#### 测试连接
```javascript
POST /dataSource/testConnection
{
  "sourceType": "sqlite",
  "sourceConfig": "{\"driverName\":\"org.sqlite.JDBC\",\"jdbcUrl\":\"jdbc:sqlite:/path/to/database.db\"}"
}
```

#### 获取表列表
```javascript
POST /dataSource/getSQLiteTables
{
  "sourceType": "sqlite",
  "sourceConfig": "{\"driverName\":\"org.sqlite.JDBC\",\"jdbcUrl\":\"jdbc:sqlite:/path/to/database.db\"}"
}
```

#### 获取表字段信息
```javascript
POST /dataSource/getSQLiteTableColumns?tableName=employees
{
  "sourceType": "sqlite",
  "sourceConfig": "{\"driverName\":\"org.sqlite.JDBC\",\"jdbcUrl\":\"jdbc:sqlite:/path/to/database.db\"}"
}
```

#### 上传文件
```javascript
POST /dataSource/uploadSQLiteFile
Content-Type: multipart/form-data
file: [SQLite database file]
```

## 示例数据库

项目中提供了一个示例数据库 `sample_report_data.db`，包含以下表：

### employees 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| name | TEXT | 员工姓名 |
| department | TEXT | 所属部门 |
| salary | REAL | 薪资 |
| hire_date | DATE | 入职日期 |

### departments 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| name | TEXT | 部门名称 |
| manager | TEXT | 部门经理 |
| budget | REAL | 部门预算 |

### employee_summary 视图
提供部门员工统计信息的汇总视图。

使用这个示例数据库，您可以快速体验 SQLite 在 AJ-Report 中的功能。