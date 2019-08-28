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

import org.apache.camel.RuntimeCamelException;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public final class AcmSessionFactoryLocator
{
    private static final Logger LOG = LoggerFactory.getLogger(AcmSessionFactoryLocator.class);
    private static SessionFactory sessionFactory;

    private AcmSessionFactoryLocator()
    {
        //Utils class
    }

    public static SessionFactory getSessionFactory()
    {
        if (sessionFactory != null)
        {
            return sessionFactory;
        } else
        {
            // create the sessionFactory in another way
            sessionFactory = loadSessionFactoryFromClassPath();
            return sessionFactory;
        }
    }

    public static void setSessionFactory(SessionFactory factory)
    {
        sessionFactory = factory;
    }

    private static SessionFactory loadSessionFactoryFromClassPath()
    {
        try
        {
            Class<?> factoryClass = null;
            factoryClass = Class.forName("org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl");
            if (factoryClass != null)
            {
                Method method = factoryClass.getMethod("newInstance");
                return (SessionFactory) method.invoke(null, new Object[0]);
            }
        } catch (Exception ex)
        {
            LOG.error("Cannot create the SessionFactoryImpl due to: {0}", ex);
            throw new RuntimeCamelException(ex);
        }
        return null;
    }


}
