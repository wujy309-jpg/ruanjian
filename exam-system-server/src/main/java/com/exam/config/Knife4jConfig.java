package com.exam.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置类
 * 配置API文档的基本信息和展示内容
 * Knife4j是基于Swagger的增强版本，提供更美观的文档界面
 * 
 * @author exam-system
 * @date 2024
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI文档信息
     * 设置API文档的标题、描述、版本等基本信息
     * Knife4j会自动读取这些信息并生成美观的文档页面
     * 
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🎓 AI云学智讯平台API文档") // API文档标题，添加emoji图标
                        .description("📚 AI云学智讯平台后端接口文档，提供完整的RESTful API服务\n\n" +
                                   "✨ 主要功能模块：\n" +
                                   "• 🧠 题目管理：支持选择题、判断题、简答题的增删改查\n" +
                                   "• 📝 试卷管理：手动组卷和AI智能组卷\n" +
                                   "• 🎨 轮播图管理：首页轮播图的图片上传和管理\n" +
                                   "• 📊 考试记录：考试结果统计和分析\n" +
                                   "• 🔔 公告管理：系统公告的发布和管理") // API文档描述，使用markdown格式
                        .version("v1.0.0") // API版本号
                        .contact(new Contact()
                                .name("🏫 AI云学智讯平台开发团队") // 联系人姓名
                                .email("exam@example.com") // 联系人邮箱
                                .url("http://localhost:8080")) // 联系人网址
                        .license(new License()
                                .name("Apache 2.0") // 许可证名称
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))); // 许可证URL
    }
} 