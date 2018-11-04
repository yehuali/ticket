package com.ticket.config;

import com.ticket.properties.PermitAllUrlProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author keets
 * @date 2017/9/29
 */
@Configuration
public class ServiceConfig {


    @Value("${server.port}")
    private int securePort;


    /**
     * 服务启动时，读入相应的Configuration，下面的配置属性读取以auth开头的配置
     */
    @Bean
    @ConfigurationProperties(prefix = "auth")
    public PermitAllUrlProperties getPermitAllUrlProperties() {
        return new PermitAllUrlProperties();
    }


    // for https config

    /*
    @Value("${server.http}")
    private int httpPort;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //Connector监听的http的端口号
        connector.setPort(httpPort);
        connector.setSecure(false);
        //监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(securePort);
        return connector;
    }*/
}
