/**
 * Sumpay.cn.
 * Copyright (c) 2007-2015 All Rights Reserved.
 */
package com.ulab.config;

import java.util.ArrayList;
import java.util.List;

import org.beetl.core.GroupTemplate;
import org.beetl.ext.jfinal.BeetlRenderFactory;

import cn.dreampie.quartz.QuartzPlugin;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.ext.plugin.tablebind.AutoTableBindPlugin;
import com.jfinal.ext.route.AutoBindRoutes;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.ulab.core.BaseController;

/**
 * 
 * @time   2017年4月10日 下午4:29:50
 * @author zuoqb
 * @todo   配置文件
 */
public class UlabCofig extends JFinalConfig {
	
    @Override
    public void configConstant(Constants me) {
    	loadPropertyFile("config.txt");
	    me.setDevMode(getPropertyToBoolean("devMode", true));
		//me.setError404View("${tpl_dir}404.htm");
		//me.setError500View("${tpl_dir}500.htm");
		me.setEncoding("UTF-8");  
		me.setMaxPostSize(536870912);//512M
		//设置根页面路径
		me.setBaseViewPath("/WEB-INF/pages");
	    me.setMainRenderFactory(new BeetlRenderFactory());
	    
    }

    @Override
    public void configRoute(Routes me) {
    	// 自动装载Controller
		AutoBindRoutes routes = new AutoBindRoutes();
		List<Class<? extends Controller>> temp = new ArrayList<Class<? extends Controller>>(1);
		temp.add(BaseController.class);
		routes.addExcludeClasses(temp);
		me.add(routes);
    }

	@Override
	public void configPlugin(Plugins me) {
		AutoTableBindPlugin arp = null;
		DruidPlugin dp = new DruidPlugin(this.getProperty("oracle.url"),
				this.getProperty("oracle.user"),
				this.getProperty("oracle.password"),
				getProperty("oracle.driver"));
		dp.setInitialSize(5);
		dp.setMaxActive(5);
		dp.setMinIdle(3);
		dp.setValidationQuery("select 1 from dual");
		me.add(dp);
		arp = new AutoTableBindPlugin(dp);// 设置数据库方言
		arp.setDialect(new OracleDialect());
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));// 忽略大小写
		arp.setShowSql(true);
		me.add(arp);
		//定时器
		QuartzPlugin quartzPlugin = new QuartzPlugin();
		quartzPlugin.setJobs("quartz.properties");
		me.add(quartzPlugin);

	}

    @Override
    public void configInterceptor(Interceptors me) {
    	//设置全局拦截器
    	me.add(new SessionInViewInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
    	me.add(new ContextPathHandler("contextPath"));
    }
    //main方法启动 需要放开pom中jetty-server的注释，并改beetl.properties中RESOURCE.root= /src/main/webapp
    public static void main(String[] args) {
    	PathKit.setWebRootPath("src/main/webapp/");
		JFinal.start("src/main/webapp", 8080, "/hlht", 5);
    }
}
