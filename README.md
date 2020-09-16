# web-spring-boot-starter
## 使用方法：  
Step 1. Add the JitPack repository to your build file  
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories> 
```
Step 2. Add the dependency  
```
	<dependency>
	    <groupId>com.github.jiannan083</groupId>
	    <artifactId>web-spring-boot-starter</artifactId>
	    <version>Tag</version>
	</dependency>
```
## 2.1.0
基于springboot 2.3.2.RELEASE  

## 2.1.1
### 一. 版本说明
spring-boot  `2.3.2.RELEASE`  
lombok  `--`  
### 二. 支持全局异常处理  
1. 自定义异常枚举需要实现 ErrorType  
2. 自定义异常抛出，打印warn日志： throw new BizBaseException(自定义异常)  
3. 自定义异常抛出，打印error日志： throw new BizException(自定义异常)  
### 三. 支持Controller统一返回结果处理
1 . BaseResult：基础结果  
使用void即可  
```
    @PostMapping("/del")
    public void del(param) {
        service.del(param);
    }
```  
2 . ListResult：列表结果  
```
    @GetMapping("/")
    public ListResult<Vo> get(param) {
       return new ListResult<>(result);
    }
```
3 . PageResult：分页结果  
```
    @GetMapping("/")
    public PageResult<Vo> get() {
        
    }
```
4 . 返回结果自动封装成下面结构
```
    {
      "code": "0",
      "message": "success",
      "data": 数据项
    }
```
### 四. 支持跨域处理，默认不开启  
使用方式：  
第一步：启用，配置文件添加 szyx.web.cors-enabled=true  
第二步：不配置默认全放行；配置放行域名，配置文件添加 szyx.web.cors-allowed-origin=放行域名 多个使用英文逗号隔开  
### 五. 支持Xss攻击处理  
### 六. trace日志  
1. 默认支持trace日志打印  
2. 添加日志标识。默认标识参数是userid；配置文件添加 szyx.web.user-sign=标识名 自定义用户标识符。
依次从"request参数"-->"header"-->"cookie"中获取用户标识。  