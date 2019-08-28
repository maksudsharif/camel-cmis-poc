package com.maksudsharif.camelpoc;

import com.maksudsharif.camelpoc.acm.cmis.component.AcmCmisSessionConfiguration;
import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCMISComponent;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Log4j2
public class CamelConfiguration
{
    @Value("${cmis.protocol}")
    private String protocol;

    @Value("${cmis.host}")
    private String host;

    @Value("${cmis.port}")
    private int port;

    @Value("${cmis.context}")
    private String context;

    @Value("${cmis.username}")
    private String username;

    @Value("${cmis.password}")
    private String password;

    @Bean
    public CamelContext camelContext(ApplicationContext applicationContext, List<RouteBuilder> routes) throws Exception
    {
        SpringCamelContext springCamelContext = new SpringCamelContext(applicationContext);
        for (RouteBuilder route : routes)
        {
            springCamelContext.addRoutes(route);
        }

        springCamelContext.addComponent("acm-cmis", new AcmCMISComponent());

        return springCamelContext;
    }

    @Bean(value = "acmCamelConfiguration")
    public AcmCmisSessionConfiguration acmCmisSessionConfiguration()
    {
        Map<String, String> parameter = new HashMap<>();
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.USER, this.username);
        parameter.put(SessionParameter.PASSWORD, this.password);

        return new AcmCmisSessionConfiguration(parameter);
    }


}
