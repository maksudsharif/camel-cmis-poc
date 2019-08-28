package com.maksudsharif.camelpoc.acm.cmis.component.producers;

import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacade;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacadeFactory;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;

/**
 * The inbound properties must include these properties:
 *             - "inputStream" - an InputStream with the file contents.
 *             - "cmisFolderId" - a string with the CMIS ID of the parent folder. The file will be added to this folder.
 *             The return payload is the new CMIS {@link org.apache.chemistry.opencmis.client.api.Document}.
 */
public class AddFileProducer extends AcmProducer
{

    public AddFileProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory, AcmCMISSessionFacade sessionFacade)
    {
        super(endpoint, sessionFacadeFactory, sessionFacade);
    }

    public AddFileProducer(Endpoint endpoint, AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        super(endpoint, sessionFacadeFactory);
    }

    @Override
    public void process(Exchange exchange) throws Exception
    {
        //TODO: Implement
    }
}
