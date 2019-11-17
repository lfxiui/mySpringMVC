# 简单版SpringMVC

[toc]

## 设计一个MVC框架

### 1. 读取配置
### 2. 初始化
1. 加载配置文件
2. 扫描用户配置包下的类
3. 通过反射机制实例化包下的类
4. 初始化HandlerMapping

### 3. 运行
1. 通过初始化好的handlerMapping中拿出url对应的方法，反射调用