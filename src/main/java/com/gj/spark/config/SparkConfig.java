package com.gj.spark.config;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.port;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;

@Component
public class SparkConfig implements InitializingBean {

	@Autowired
	private RouteRegisterService routeRegisterService;


	private int port = 8080;

	@Override
	public void afterPropertiesSet() throws Exception {
		port(port);

		notFound((req, res) -> {
			res.type("application/json");
			return "{\"code\": 404,\"message\": \"not found\"}";
		});

		internalServerError((req, res) -> {
			res.type("application/json");
			return "{\"code\": 500,\"message\":\"Internal Server Error\"}";
		});

		// 安全效验
		before((req, res) -> {
			if (!checkPass(req)) {// 过滤掉不需要安全效验的路由
				//TODO 校验权限
			}
		});
		routeRegisterService.init();

	}

	public void commence(Request req, Response res) throws IOException, ServletException {
		res.type("application/json");
		halt("{\"code\": 401, \"message\": \"Session has expired !\"}");
	}

	private String[] getExcludeUrls() {
		String urls = "/css/.*,/fonts/.*,/img/.*,/js/.*,/pay/.*,/verify/.*";
		return urls.split(",");
	}

	private boolean checkPass(Request req) {
		String[] excludeUrls = getExcludeUrls();
		String uri = req.uri();
		// 验证uri是否不需要经验安全验证
		for (String excludeUrl : excludeUrls) {
			Pattern _patten = Pattern.compile(excludeUrl);
			Matcher matcher = _patten.matcher(uri);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
	
}
