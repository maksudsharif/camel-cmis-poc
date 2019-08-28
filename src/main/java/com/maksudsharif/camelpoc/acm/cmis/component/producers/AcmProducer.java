package com.maksudsharif.camelpoc.acm.cmis.component.producers;

import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacade;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacadeFactory;
import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCMISEndpoint;
import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultProducer;

public abstract class AcmProducer extends DefaultProducer
{
    private AcmCMISSessionFacadeFactory sessionFacadeFactory;
    private AcmCMISSessionFacade sessionFacade;

    public AcmProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory, AcmCMISSessionFacade sessionFacade)
    {
        super(endpoint);
        this.sessionFacadeFactory = sessionFacadeFactory;
        this.sessionFacade = sessionFacade;
    }

    public AcmProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        super(endpoint);
        this.sessionFacadeFactory = sessionFacadeFactory;
        this.sessionFacade = null;
    }

    public AcmCMISSessionFacadeFactory getSessionFacadeFactory()
    {
        return sessionFacadeFactory;
    }

    public void setSessionFacadeFactory(AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        this.sessionFacadeFactory = sessionFacadeFactory;
    }

    public AcmCMISSessionFacade getSessionFacade() throws Exception
    {
        if (sessionFacade == null)
        {
            AcmCMISSessionFacade sessionFacade = sessionFacadeFactory.create((AcmCMISEndpoint) getEndpoint());
            sessionFacade.initSession();
            // make sure to set sessionFacade to the field after successful initialisation
            // so that it has a valid session
            this.sessionFacade = sessionFacade;
        }

        return sessionFacade;
    }

    public void setSessionFacade(AcmCMISSessionFacade sessionFacade)
    {
        this.sessionFacade = sessionFacade;
    }
}
