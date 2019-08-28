package com.maksudsharif.camelpoc.acm.cmis.component.producers;

import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacade;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacadeFactory;
import com.maksudsharif.camelpoc.acm.cmis.component.util.AcmCMISHelper;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.util.StopWatch;

import java.util.Optional;

public class DownloadFileProducer extends AcmProducer
{

    public DownloadFileProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory, AcmCMISSessionFacade sessionFacade)
    {
        super(endpoint, sessionFacadeFactory, sessionFacade);
    }

    public DownloadFileProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        super(endpoint, sessionFacadeFactory);
    }

    @Override
    public void process(Exchange exchange) throws Exception
    {
        Optional<CmisObject> content = retrieve(exchange);
        if (!content.isPresent() || !AcmCMISHelper.isDocument(content.get()) || ((Document) content.get()).getContentStream() == null)
        {
            exchange.setException(new CmisObjectNotFoundException("No document found by nodeId"));
        } else
        {
            exchange.getOut().setBody(((Document) content.get()).getContentStream(), ContentStream.class);
        }
    }

    private Optional<CmisObject> retrieve(Exchange exchange) throws Exception
    {
        String nodeId = exchange.getIn().getMandatoryBody(String.class);

        OperationContext operationContext = getSessionFacade().createOperationContext();
        operationContext.setCacheEnabled(false);
        operationContext.setIncludeAcls(false);
        operationContext.setMaxItemsPerPage(1);
        operationContext.setIncludePolicies(false);
        operationContext.setIncludeAllowableActions(false);
        operationContext.setIncludePathSegments(false);
        operationContext.setLoadSecondaryTypeProperties(false);
        operationContext.setFilterString("cmis:objectId,cmis:name");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("With Ops");
        CmisObject objectById = getSessionFacade().getObjectById(nodeId, operationContext);
        stopWatch.stop();

        log.info("\n" + stopWatch.prettyPrint());
        return Optional.ofNullable(objectById);
    }
}
