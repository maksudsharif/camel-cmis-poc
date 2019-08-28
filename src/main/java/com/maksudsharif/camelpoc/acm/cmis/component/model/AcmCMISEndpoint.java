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
import com.maksudsharif.camelpoc.acm.cmis.component.AcmDefaultCMISSessionFacadeFactory;
import com.maksudsharif.camelpoc.acm.cmis.component.consumers.AcmCMISConsumer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.AddFileProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.AddNewFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.CopyFileProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.CreateFolderByPathProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.CreateFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.DeleteFileProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.DeleteFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.DeleteFolderTreeProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.DownloadFileProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.GetFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.GetObjectByIdProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.GetObjectByPathProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.ListFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.MoveFileProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.MoveFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.RenameFileProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.RenameFolderProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.ReturnParentProducer;
import com.maksudsharif.camelpoc.acm.cmis.component.producers.UpdateFileProducer;
import org.apache.camel.Consumer;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

import java.util.Map;

/**
 * The cmis component uses the Apache Chemistry client API and allows you to add/read nodes to/from a CMIS compliant content repositories.
 */
@UriEndpoint(scheme = "acm-cmis", title = "ACM CMIS", syntax = "acm-cmis:cmisUrl")
public class AcmCMISEndpoint extends DefaultEndpoint
{

    @UriPath(description = "URL to the cmis repository")
    @Metadata(required = true)
    private final String cmisUrl;

    @UriParam(label = "producer")
    private AcmCMISAction cmisAction;

    @UriParam
    private AcmCMISSessionFacade sessionFacade; // to include in component documentation

    @UriParam(label = "advanced")
    private AcmCMISSessionFacadeFactory sessionFacadeFactory;

    private Map<String, Object> properties; // properties for each session facade instance being created

    public AcmCMISEndpoint(String uri, AcmCMISComponent component, String cmisUrl)
    {
        this(uri, component, cmisUrl, new AcmDefaultCMISSessionFacadeFactory());
    }

    public AcmCMISEndpoint(String uri, AcmCMISComponent component, String cmisUrl, AcmCMISSessionFacadeFactory sessionFacadeFactory)
    {
        super(uri, component);
        this.cmisUrl = cmisUrl;
        this.sessionFacadeFactory = sessionFacadeFactory;
    }

    @Override
    public Producer createProducer() throws Exception
    {
        switch (getCmisAction())
        {
            case ADDFILE:
                return new AddFileProducer(this, sessionFacadeFactory);
            case COPYFILE:
                return new CopyFileProducer(this, sessionFacadeFactory);
            case DELETEFILE:
                return new DeleteFileProducer(this, sessionFacadeFactory);
            case MOVEFILE:
                return new MoveFileProducer(this, sessionFacadeFactory);
            case RENAMEFILE:
                return new RenameFileProducer(this, sessionFacadeFactory);
            case UPDATEFILE:
                return new UpdateFileProducer(this, sessionFacadeFactory);
            case DOWNLOADFILE:
                return new DownloadFileProducer(this, sessionFacadeFactory);
            case RENAMEFOLDER:
                return new RenameFolderProducer(this, sessionFacadeFactory);
            case GETFOLDER:
                return new GetFolderProducer(this, sessionFacadeFactory);
            case LISTFOLDER:
                return new ListFolderProducer(this, sessionFacadeFactory);
            case MOVEFOLDER:
                return new MoveFolderProducer(this, sessionFacadeFactory);
            case ADDNEWFOLDER:
                return new AddNewFolderProducer(this, sessionFacadeFactory);
            case CREATEFOLDER:
                return new CreateFolderProducer(this, sessionFacadeFactory);
            case DELETEFOLDER:
                return new DeleteFolderProducer(this, sessionFacadeFactory);
            case DELETEFOLDERTREE:
                return new DeleteFolderTreeProducer(this, sessionFacadeFactory);
            case CREATEFOLDERBYPATH:
                return new CreateFolderByPathProducer(this, sessionFacadeFactory);
            case GETOBJECTBYID:
                return new GetObjectByIdProducer(this, sessionFacadeFactory);
            case RETURNPARENT:
                return new ReturnParentProducer(this, sessionFacadeFactory);
            case GETOBJECTBYPATH:
                return new GetObjectByPathProducer(this, sessionFacadeFactory);
            default:
                throw new NoSuchEndpointException("Invalid query mode supplied, no endpoint found.");
        }
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception
    {
        AcmCMISConsumer consumer = new AcmCMISConsumer(this, processor, sessionFacadeFactory);
        configureConsumer(consumer);

        return consumer;
    }

    public AcmCMISAction getCmisAction()
    {
        return cmisAction;
    }

    /**
     * TODO: UPDATE DOC
     * If true, will execute the cmis query from the message body and return result, otherwise will create a node in the cmis repository
     */
    public void setCmisAction(AcmCMISAction cmisAction)
    {
        this.cmisAction = cmisAction;
    }

    public String getCmisUrl()
    {
        return cmisUrl;
    }

    public AcmCMISSessionFacade getSessionFacade()
    {
        return sessionFacade;
    }

    /**
     * Session configuration
     */
    public void setSessionFacade(AcmCMISSessionFacade sessionFacade)
    {
        this.sessionFacade = sessionFacade;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
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
