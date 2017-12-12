package com.tuandai.architecture.logs;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class MyLogMvcEndpoint extends EndpointMvcAdapter {

    String format = "change package name [%s] logging level to [%s]";

    public MyLogMvcEndpoint(MyLogEndpoint delegate) {
        super(delegate);
    }

    // 注意该 path 不是 http://localhost:8080/mgrlogging/{level}/{pkn}
    // 而是构造方法中 MyLogEndpoint 的 id 对应 path 下的请求节点。
    // http://localhost:8080/mgrlogging/{level}/{pkn}

    @RequestMapping(value = "/{level}/{pkn}")
    @ResponseBody
    public Object changeLog(@PathVariable LogLevel level, @PathVariable("pkn") String packageName) {
        System.err.println(String.format(format, packageName, level));

        try {
            // 处理日志 level 改变逻辑，根据个人需求改变
            LogbackLoggingSystem logbackLoggingSystem = new LogbackLoggingSystem(this.getClass().getClassLoader());
            logbackLoggingSystem.setLogLevel(packageName, level);

            // 处理成功（未抛出异常）返回 success
            return "success";
        } catch (Exception e) {
            // 处理失败返回异常信息
            return e.getMessage();
        }
    }

}