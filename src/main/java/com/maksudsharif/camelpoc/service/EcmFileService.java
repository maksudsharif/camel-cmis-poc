package com.maksudsharif.camelpoc.service;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

import java.io.InputStream;

public interface EcmFileService
{
    ContentStream downloadById(String nodeId);
}
