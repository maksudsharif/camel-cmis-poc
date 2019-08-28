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
package com.maksudsharif.camelpoc.acm.cmis.component.model;

import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacade;
import com.maksudsharif.camelpoc.acm.cmis.component.AcmCMISSessionFacadeFactory;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the component that manages {@link AcmCMISComponent}.
 */
@Component("cmis")
public class AcmCMISComponent extends DefaultComponent
{

    private AcmCMISSessionFacadeFactory sessionFacadeFactory;

    public AcmCMISComponent()
    {
    }

    protected Endpoint createEndpoint(String uri, final String remaining, final Map<String, Object> parameters) throws Exception
    {
        AcmCMISEndpoint endpoint = new AcmCMISEndpoint(uri, this, remaining);

        // create a copy of parameters which we need to store on the endpoint which are in use from the session factory
        Map<String, Object> copy = new HashMap<>(parameters);
        endpoint.setProperties(copy);
        if (sessionFacadeFactory != null)
        {
            endpoint.setSessionFacadeFactory(sessionFacadeFactory);
        }

        // create a dummy AcmCMISSessionFacade which we set the properties on
        // so we can validate if they are all known options and fail fast if there are unknown options
        AcmCMISSessionFacade dummy = new AcmCMISSessionFacade(remaining);
        setProperties(dummy, parameters);

        // and the remainder options are for the endpoint
        setProperties(endpoint, parameters);

        return endpoint;
    }

    public AcmCMISSessionFacadeFactory getSessionFacadeFactory()
    {
        return sessionFacadeFactory;
    }

    /**
     * To use a custom AcmCMISSessionFacadeFactory to create the AcmCMISSessionFacade instances
     */
    public void setSessionFacadeFactory(AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        this.sessionFacadeFactory = sessionFacadeFactory;
    }
}
