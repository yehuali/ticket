package com.ticket.servlet;

import com.netflix.config.ConfigurationManager;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitializeServletListener implements ServletContextListener {

    private Logger LOGGER = LoggerFactory.getLogger(InitializeServletListener.class);

    public InitializeServletListener(){

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initZuul();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void initZuul() throws Exception {
        LOGGER.info("Starting Groovy Filter file manager");
        final AbstractConfiguration config = ConfigurationManager.getConfigInstance();
        final String preFiltersPath = config.getString(Constants.ZUUL_FILTER_PRE_PATH);
        final String postFiltersPath = config.getString(Constants.ZUUL_FILTER_POST_PATH);
        final String routeFiltersPath = config.getString(Constants.ZUUL_FILTER_ROUTE_PATH);
        final String errorFiltersPath = config.getString(Constants.ZUUL_FILTER_ERROR_PATH);
        final String customPath = config.getString(Constants.Zuul_FILTER_CUSTOM_PATH);

        //load local filter files
        FilterLoader.getInstance().setCompiler(new GroovyCompiler());
        FilterFileManager.setFilenameFilter(new GroovyFileFilter());
        if (customPath == null) {
            FilterFileManager.init(5, preFiltersPath, postFiltersPath, routeFiltersPath, errorFiltersPath);
        } else {
            FilterFileManager.init(5, preFiltersPath, postFiltersPath, routeFiltersPath, errorFiltersPath, customPath);
        }
        //load filters in DB
//        startZuulFilterPoller();
        LOGGER.info("Groovy Filter file manager started");
    }


}
