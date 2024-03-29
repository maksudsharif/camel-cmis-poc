/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maksudsharif.camelpoc.acm.cmis.component.consumers;

import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCMISEndpoint;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacade;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacadeFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.ScheduledPollConsumer;
import org.apache.chemistry.opencmis.client.api.OperationContext;

import java.io.InputStream;
import java.util.Map;

/**
 * The CMIS consumer.
 */
public class AcmCMISConsumer extends ScheduledPollConsumer
{

    private final AcmCMISSessionFacadeFactory sessionFacadeFactory;
    private AcmCMISSessionFacade sessionFacade;

    public AcmCMISConsumer(AcmCMISEndpoint cmisEndpoint, Processor processor, AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        super(cmisEndpoint, processor);
        this.sessionFacadeFactory = sessionFacadeFactory;
        this.sessionFacade = null;
    }

    @Override
    public AcmCMISEndpoint getEndpoint()
    {
        return (AcmCMISEndpoint) super.getEndpoint();
    }

    @Override
    protected int poll() throws Exception
    {
        return getSessionFacade().poll(this);
    }

    public OperationContext createOperationContext() throws Exception
    {
        return getSessionFacade().createOperationContext();
    }

    public int sendExchangeWithPropsAndBody(Map<String, Object> properties, InputStream inputStream)
            throws Exception
    {
        Exchange exchange = getEndpoint().createExchange();
        exchange.getIn().setHeaders(properties);
        exchange.getIn().setBody(inputStream);
        log.debug("Polling node : {}", properties.get("cmis:name"));
        getProcessor().process(exchange);
        return 1;
    }

    public AcmCMISSessionFacade getSessionFacade() throws Exception
    {
        if (sessionFacade == null)
        {
            sessionFacade = sessionFacadeFactory.create(getEndpoint());
            sessionFacade.initSession();
        }

        return sessionFacade;
    }

}
