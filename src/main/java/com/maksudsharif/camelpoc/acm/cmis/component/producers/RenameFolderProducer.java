package com.maksudsharif.camelpoc.acm.cmis.component.producers;

import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacade;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacadeFactory;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;

public class RenameFolderProducer extends AcmProducer
{

    public RenameFolderProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory, AcmCMISSessionFacade sessionFacade)
    {
        super(endpoint, sessionFacadeFactory, sessionFacade);
    }

    public RenameFolderProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        super(endpoint, sessionFacadeFactory);
    }

    @Override
    public void process(Exchange exchange) throws Exception
    {
        //TODO: Implement

    }
}
