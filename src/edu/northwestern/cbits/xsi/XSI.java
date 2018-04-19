/* Copyright © 2018 by Northwestern University. All Rights Reserved. */

package edu.northwestern.cbits.xsi;

public class XSI
{
    private static String _userAgent = "XSI Android";

    public static void setUserAgent(String userAgent)
    {
        XSI._userAgent = userAgent;
        System.setProperty("http.agent", userAgent);
    }

    public static String getUserAgent()
    {
        return XSI._userAgent;
    }
}
