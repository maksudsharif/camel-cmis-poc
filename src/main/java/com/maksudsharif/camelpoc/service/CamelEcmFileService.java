package com.maksudsharif.camelpoc.service;

import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCMISAction;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Log4j2
@AllArgsConstructor
@Service
public class CamelEcmFileService implements EcmFileService
{
    private CamelContext camelContext;

    @Override
    public ContentStream downloadById(String nodeId)
    {
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        CompletableFuture<Object> objectCompletableFuture = producerTemplate.asyncRequestBody("direct:" + AcmCMISAction.DOWNLOADFILE, nodeId);
        return producerTemplate.extractFutureBody(objectCompletableFuture, ContentStream.class);
    }
}
