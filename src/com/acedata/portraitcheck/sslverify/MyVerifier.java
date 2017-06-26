package com.acedata.portraitcheck.sslverify;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Administrator on 2017/5/3.
 */
public class MyVerifier implements HostnameVerifier{
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
