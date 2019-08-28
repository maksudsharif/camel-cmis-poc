package com.maksudsharif.camelpoc;

import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCMISAction;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component(value = "cmisCamelProcessor")
@Log4j2
public class CamelDownloadRoute extends RouteBuilder implements Processor
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

    @Override
    public void configure() throws Exception
    {
        URI cmis = new URIBuilder()
                .setScheme(protocol)
                .setHost(host)
                .setPort(port)
                .setPath(context)
                .addParameter("username", username)
                .addParameter("password", password)
                .addParameter("cmisAction", AcmCMISAction.DOWNLOADFILE.toString())
                .build();
        String url = "acm-cmis://" + cmis.toURL().toExternalForm();
        log.info("Alfresco: {}", url);

        from("direct:" + AcmCMISAction.DOWNLOADFILE)
                .routeId(AcmCMISAction.DOWNLOADFILE.toString())
                .log("Downloading file: ${body}")
                .to(url)
                .log("Download complete");
    }

    @Override
    public void process(Exchange exchange) throws Exception
    {
        log.info(exchange);
    }
}
