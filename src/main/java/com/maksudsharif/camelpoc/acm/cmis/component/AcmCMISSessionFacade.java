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
package com.maksudsharif.camelpoc.acm.cmis.component;

import com.maksudsharif.camelpoc.acm.cmis.component.consumers.AcmCMISConsumer;
import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCamelCMISConstants;
import com.maksudsharif.camelpoc.acm.cmis.component.util.AcmCMISHelper;
import com.maksudsharif.camelpoc.acm.cmis.component.util.AcmSessionFactoryLocator;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// TODO: Implement multiple cmis session providers
@UriParams
@Log4j2
public class AcmCMISSessionFacade
{
    private static final Logger LOG = LoggerFactory.getLogger(AcmCMISSessionFacade.class);
    private final String url;
    private transient Session session;
    @UriParam(defaultValue = "100")
    private int pageSize = 100;
    @UriParam
    private int readCount;
    @UriParam
    private boolean readContent;
    @UriParam(label = "security", secret = true)
    private String username;
    @UriParam(label = "security", secret = true)
    private String password;
    @UriParam
    private String repositoryId;
    @UriParam(label = "consumer")
    private String query;

    private ConcurrentMap<String, Session> sessionRegistry = new ConcurrentHashMap<>();

    private AcmCmisSessionConfiguration cmisSessionParameters;

    public AcmCMISSessionFacade(String url)
    {
        this.url = url;
    }

    public AcmCMISSessionFacade(String url, AcmCmisSessionConfiguration cmisSessionParameters)
    {
        this.url = url;
        this.cmisSessionParameters = cmisSessionParameters;
    }

    public void initSession()
    {
        if (cmisSessionParameters.isEmpty())
        {
            log.info("Creating session...");
            initSessionInternal();
        } else
        {
            log.info("Creating session...");
            initSessionInternal(cmisSessionParameters.getSessionParameters());
        }
    }

    private void initSessionInternal(Map<String, String> cmisSessionParameters)
    {
        Map<String, String> parameter = new HashMap<>(cmisSessionParameters);
        if (BindingType.BROWSER.equals(BindingType.fromValue(cmisSessionParameters.get(SessionParameter.BINDING_TYPE))))
        {
            parameter.put(SessionParameter.BROWSER_URL, this.url);
        } else
        {
            parameter.put(SessionParameter.ATOMPUB_URL, this.url);
        }


        if (this.repositoryId != null)
        {
            parameter.put(SessionParameter.REPOSITORY_ID, this.repositoryId);
            this.session = AcmSessionFactoryLocator.getSessionFactory().createSession(parameter);
            sessionRegistry.put(cmisSessionParameters.get(AcmCamelCMISConstants.CMIS_SESSION_REF), session);
        } else
        {
            this.session = AcmSessionFactoryLocator.getSessionFactory().getRepositories(parameter).get(0).createSession();
            sessionRegistry.put(cmisSessionParameters.get(AcmCamelCMISConstants.CMIS_SESSION_REF), session);
        }
    }

    private void initSessionInternal()
    {
        Map<String, String> parameter = new HashMap<>();
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameter.put(SessionParameter.ATOMPUB_URL, this.url);
        parameter.put(SessionParameter.USER, this.username);
        parameter.put(SessionParameter.PASSWORD, this.password);
        if (this.repositoryId != null)
        {
            parameter.put(SessionParameter.REPOSITORY_ID, this.repositoryId);
            this.session = AcmSessionFactoryLocator.getSessionFactory().createSession(parameter);
        } else
        {
            this.session = AcmSessionFactoryLocator.getSessionFactory().getRepositories(parameter).get(0).createSession();
        }
    }

    public int poll(AcmCMISConsumer cmisConsumer) throws Exception
    {
        if (query != null)
        {
            return pollWithQuery(cmisConsumer);
        }
        return pollTree(cmisConsumer);
    }

    private int pollTree(AcmCMISConsumer cmisConsumer) throws Exception
    {
        Folder rootFolder = session.getRootFolder();
        AcmRecursiveTreeWalker treeWalker = new AcmRecursiveTreeWalker(cmisConsumer, readContent, readCount,
                pageSize);
        return treeWalker.processFolderRecursively(rootFolder);
    }

    private int pollWithQuery(AcmCMISConsumer cmisConsumer) throws Exception
    {
        int count = 0;
        int pageNumber = 0;
        boolean finished = false;
        ItemIterable<QueryResult> itemIterable = executeQuery(query);
        while (!finished)
        {
            ItemIterable<QueryResult> currentPage = itemIterable.skipTo(count).getPage();
            LOG.debug("Processing page {}", pageNumber);
            for (QueryResult item : currentPage)
            {
                Map<String, Object> properties = AcmCMISHelper.propertyDataToMap(item.getProperties());
                Object objectTypeId = item.getPropertyValueById(PropertyIds.OBJECT_TYPE_ID);
                InputStream inputStream = null;
                if (readContent && AcmCamelCMISConstants.CMIS_DOCUMENT.equals(objectTypeId))
                {
                    inputStream = getContentStreamFor(item);
                }

                cmisConsumer.sendExchangeWithPropsAndBody(properties, inputStream);
                count++;
                if (count == readCount)
                {
                    finished = true;
                    break;
                }
            }
            pageNumber++;
            if (!currentPage.getHasMoreItems())
            {
                finished = true;
            }
        }
        return count;
    }

    //some duplication
    public List<Map<String, Object>> retrieveResult(Boolean retrieveContent, Integer readSize,
                                                    ItemIterable<QueryResult> itemIterable)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        boolean queryForContent = retrieveContent != null ? retrieveContent : readContent;
        int documentsToRead = readSize != null ? readSize : readCount;
        int count = 0;
        int pageNumber = 0;
        boolean finished = false;
        while (!finished)
        {
            ItemIterable<QueryResult> currentPage = itemIterable.skipTo(count).getPage();
            LOG.debug("Processing page {}", pageNumber);
            for (QueryResult item : currentPage)
            {
                Map<String, Object> properties = AcmCMISHelper.propertyDataToMap(item.getProperties());
                if (queryForContent)
                {
                    InputStream inputStream = getContentStreamFor(item);
                    properties.put(AcmCamelCMISConstants.CAMEL_CMIS_CONTENT_STREAM, inputStream);
                }

                result.add(properties);
                count++;
                if (count == documentsToRead)
                {
                    finished = true;
                    break;
                }
            }
            pageNumber++;
            if (!currentPage.getHasMoreItems())
            {
                finished = true;
            }
        }
        return result;
    }

    public ItemIterable<QueryResult> executeQuery(String query)
    {
        OperationContext operationContext = session.createOperationContext();
        operationContext.setMaxItemsPerPage(pageSize);
        return session.query(query, false, operationContext);
    }

    public Document getDocument(QueryResult queryResult)
    {
        if (AcmCamelCMISConstants.CMIS_DOCUMENT.equals(queryResult.getPropertyValueById(PropertyIds.OBJECT_TYPE_ID))
                || AcmCamelCMISConstants.CMIS_DOCUMENT.equals(queryResult.getPropertyValueById(PropertyIds.BASE_TYPE_ID)))
        {
            String objectId = (String) queryResult.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue();
            return (Document) session.getObject(objectId);
        }
        return null;
    }

    public InputStream getContentStreamFor(QueryResult item)
    {
        Document document = getDocument(item);
        if (document != null)
        {
            ContentStream contentStream = document.getContentStream();
            if (contentStream != null)
            {
                return contentStream.getStream();
            }
        }
        return null;
    }

    public CmisObject getObjectByPath(String path)
    {
        return session.getObjectByPath(path);
    }

    public CmisObject getObjectById(String nodeId) {
        return session.getObject(nodeId);
    }

    public CmisObject getObjectById(String nodeId, OperationContext operationContext) {
        return session.getObject(nodeId, operationContext);
    }

    public boolean isObjectTypeVersionable(String objectType)
    {
        if (AcmCamelCMISConstants.CMIS_DOCUMENT.equals(getCMISTypeFor(objectType)))
        {
            ObjectType typeDefinition = session.getTypeDefinition(objectType);
            return ((DocumentType) typeDefinition).isVersionable();
        }
        return false;
    }

    public boolean supportsSecondaries()
    {
        if (session.getRepositoryInfo().getCmisVersion() == CmisVersion.CMIS_1_0)
        {
            return false;
        }
        for (ObjectType type : session.getTypeChildren(null, false))
        {
            if (BaseTypeId.CMIS_SECONDARY.value().equals(type.getId()))
            {
                return true;
            }
        }
        return false;
    }

    public ContentStream createContentStream(String fileName, byte[] buf, String mimeType) throws Exception
    {
        return buf != null ? session.getObjectFactory()
                .createContentStream(fileName, buf.length, mimeType, new ByteArrayInputStream(buf)) : null;
    }

    public String getCMISTypeFor(String customOrCMISType)
    {
        ObjectType objectBaseType = session.getTypeDefinition(customOrCMISType).getBaseType();
        return objectBaseType == null ? customOrCMISType : objectBaseType.getId();
    }

    public Set<String> getPropertiesFor(String objectType)
    {
        return session.getTypeDefinition(objectType).getPropertyDefinitions().keySet();
    }

    public OperationContext createOperationContext()
    {
        return session.createOperationContext();
    }

    /**
     * Username for the cmis repository
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Password for the cmis repository
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * The Id of the repository to use. If not specified the first available repository is used
     */
    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    /**
     * If set to true, the content of document node will be retrieved in addition to the properties
     */
    public void setReadContent(boolean readContent)
    {
        this.readContent = readContent;
    }

    /**
     * Max number of nodes to read
     */
    public void setReadCount(int readCount)
    {
        this.readCount = readCount;
    }

    /**
     * The cmis query to execute against the repository.
     * If not specified, the consumer will retrieve every node from the content repository by iterating the content tree recursively
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /**
     * Number of nodes to retrieve per page
     */
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }
}
