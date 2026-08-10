package com.university.dorm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.university.dorm.mapper")
@EnableAsync
public class DormitoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryApplication.class, args);
        System.out.println("========================================");
        System.out.println("  🏠 大学宿舍管理系统启动成功！");
        System.out.println("  📡 访问地址：http://localhost:8080/api");
        System.out.println("  📖 API文档：http://localhost:8080/api/doc.html");
        System.out.println("========================================");
    }
}