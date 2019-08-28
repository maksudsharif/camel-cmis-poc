package com.maksudsharif.camelpoc.acm.cmis.component;

import java.util.HashMap;
import java.util.Map;

public class AcmCmisSessionConfiguration
{
    private Map<String, String> sessionParameters;

    public AcmCmisSessionConfiguration(Map<String, String> sessionParameters)
    {
        this.sessionParameters = sessionParameters;
    }

    public Map<String, String> getSessionParameters()
    {
        return sessionParameters;
    }

    public boolean isEmpty()
    {
        return sessionParameters.isEmpty();
    }
}
