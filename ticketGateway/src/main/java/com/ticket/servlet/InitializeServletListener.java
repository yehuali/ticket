package com.ticket.servlet;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

public class InitializeServletListener implements ServletContextListener {

    private Logger LOGGER = LoggerFactory.getLogger(InitializeServletListener.class);

    private String appName = null;

//    private LogConfigurator logConfigurator;

    public InitializeServletListener(){
        String applicationID = ConfigurationManager.getConfigInstance().getString(Constants.DEPLOYMENT_APPLICATION_ID);
        if (StringUtils.isEmpty(applicationID)) {
            LOGGER.warn("Using default config!");
            ConfigurationManager.getConfigInstance().setProperty(Constants.DEPLOYMENT_APPLICATION_ID, "mobile_zuul");
        }

        System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");
        loadConfiguration();
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

    private void loadConfiguration() {
        appName = ConfigurationManager.getDeploymentContext().getApplicationId();

        if (null != appName) {
            try {
                LOGGER.info(String.format("Loading application properties with app id: %s and environment: %s", appName,
                        ConfigurationManager.getDeploymentContext().getDeploymentEnvironment()));
                ConfigurationManager.loadCascadedPropertiesFromResources(appName);
            } catch (IOException e) {
                LOGGER.error(String.format(
                        "Failed to load properties for application id: %s and environment: %s. This is ok, if you do not have application level properties.",
                        appName, ConfigurationManager.getDeploymentContext().getDeploymentEnvironment()), e);
            }
        } else {
            LOGGER.warn(
                    "Application identifier not defined, skipping application level properties loading. You must set a property 'archaius.deployment.applicationId' to be able to load application level properties.");
        }

    }

    private void configLog() {
//        logConfigurator = new LogConfigurator(appName,ConfigurationManager.getDeploymentContext().getDeploymentEnvironment());
//        logConfigurator.config();
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
