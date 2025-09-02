
package com.anjiplus.template.gaea.business.modules.datasource.controller;

import com.anji.plus.gaea.annotation.Permission;
import com.anji.plus.gaea.bean.ResponseBean;
import com.anji.plus.gaea.curd.controller.GaeaBaseController;
import com.anji.plus.gaea.curd.service.GaeaBaseService;
import com.anjiplus.template.gaea.business.modules.datasource.controller.dto.DataSourceDto;
import com.anjiplus.template.gaea.business.modules.datasource.controller.param.ConnectionParam;
import com.anjiplus.template.gaea.business.modules.datasource.controller.param.DataSourceParam;
import com.anjiplus.template.gaea.business.modules.datasource.dao.entity.DataSource;
import com.anjiplus.template.gaea.business.modules.datasource.service.DataSourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
* @desc 数据源 controller
* @website https://gitee.com/anji-plus/gaea
* @author Raod
* @date 2021-03-18 12:09:57.728203200
**/
@RestController
@Slf4j
@Permission(code = "datasourceManage", name = "数据源管理")
@RequestMapping("/dataSource")
public class DataSourceController extends GaeaBaseController<DataSourceParam, DataSource, DataSourceDto> {

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public GaeaBaseService<DataSourceParam, DataSource> getService() {
        return dataSourceService;
    }

    @Override
    public DataSource getEntity() {
        return new DataSource();
    }

    @Override
    public DataSourceDto getDTO() {
        return new DataSourceDto();
    }

    /**
     * 获取所有数据源
     * @return
     */
    @GetMapping("/queryAllDataSource")
    public ResponseBean queryAllDataSource() {
        return responseSuccessWithData(dataSourceService.queryAllDataSource());
    }

    /**
     * 测试 连接
     * @param connectionParam
     * @return
     */
    @Permission( code = "query", name = "测试数据源")
    @PostMapping("/testConnection")
    public ResponseBean testConnection(@Validated @RequestBody ConnectionParam connectionParam) {
        return responseSuccessWithData(dataSourceService.testConnection(connectionParam));
    }

    /**
     * 获取SQLite数据库表信息
     * @param dto
     * @return
     */
    @Permission( code = "query", name = "获取SQLite表信息")
    @PostMapping("/getSQLiteTables")
    public ResponseBean getSQLiteTables(@RequestBody DataSourceDto dto) {
        return responseSuccessWithData(dataSourceService.getSQLiteTables(dto));
    }

    /**
     * 获取SQLite表字段信息
     * @param dto
     * @return
     */
    @Permission( code = "query", name = "获取SQLite表字段信息")
    @PostMapping("/getSQLiteTableColumns")
    public ResponseBean getSQLiteTableColumns(@RequestBody DataSourceDto dto, @RequestParam String tableName) {
        return responseSuccessWithData(dataSourceService.getSQLiteTableColumns(dto, tableName));
    }

    /**
     * 上传SQLite数据库文件
     * @param file
     * @return
     */
    @Permission( code = "insert", name = "上传SQLite数据库文件")
    @PostMapping("/uploadSQLiteFile")
    public ResponseBean uploadSQLiteFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseBean.builder().code("400").message("请选择要上传的文件").build();
        }
        
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.toLowerCase().endsWith(".db") && 
                                        !originalFilename.toLowerCase().endsWith(".sqlite") && 
                                        !originalFilename.toLowerCase().endsWith(".sqlite3"))) {
            return ResponseBean.builder().code("400").message("请上传有效的SQLite数据库文件 (.db, .sqlite, .sqlite3)").build();
        }
        
        try {
            // 创建上传目录
            String uploadDir = System.getProperty("user.home") + "/aj-report/sqlite/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);
            
            // 保存文件
            Files.write(filePath, file.getBytes());
            
            // 返回文件路径
            String absolutePath = filePath.toAbsolutePath().toString();
            return responseSuccessWithData(absolutePath);
            
        } catch (IOException e) {
            log.error("SQLite文件上传失败", e);
            return ResponseBean.builder().code("500").message("文件上传失败: " + e.getMessage()).build();
        }
    }

}
