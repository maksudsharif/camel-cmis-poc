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
package com.maksudsharif.camelpoc.acm.cmis.component.util;

import com.maksudsharif.camelpoc.acm.cmis.component.model.AcmCamelCMISConstants;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AcmCMISHelper
{
    private AcmCMISHelper()
    {
    }

    public static Map<String, Object> filterCMISProperties(Map<String, Object> properties)
    {
        Map<String, Object> result = new HashMap<>(properties.size());
        for (Map.Entry<String, Object> entry : properties.entrySet())
        {
            if (entry.getKey().startsWith("cmis:"))
            {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static Map<String, Object> objectProperties(CmisObject cmisObject)
    {
        List<Property<?>> propertyList = cmisObject.getProperties();
        return propertyDataToMap(propertyList);
    }

    public static Map<String, Object> propertyDataToMap(List<? extends PropertyData<?>> properties)
    {
        Map<String, Object> result = new HashMap<>();
        for (PropertyData<?> propertyData : properties)
        {
            result.put(propertyData.getId(), propertyData.getFirstValue());
        }
        return result;
    }

    public static boolean isFolder(CmisObject cmisObject)
    {
        return AcmCamelCMISConstants.CMIS_FOLDER.equals(getObjectTypeId(cmisObject));
    }

    public static boolean isDocument(CmisObject cmisObject)
    {
        return AcmCamelCMISConstants.CMIS_DOCUMENT.equals(getObjectTypeId(cmisObject));
    }

    public static Object getObjectTypeId(CmisObject child)
    {
        return child.getPropertyValue(PropertyIds.OBJECT_TYPE_ID); //BASE_TYPE_ID?
    }

}
