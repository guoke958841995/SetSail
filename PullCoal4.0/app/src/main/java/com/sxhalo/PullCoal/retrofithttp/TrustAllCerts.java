package com.sxhalo.PullCoal.retrofithttp;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 *
 * Created by amoldZhang on 2019/3/23.
 */
public class TrustAllCerts implements X509TrustManager {

    @Override
    public void checkClientTrusted (java.security.cert.X509Certificate[] x509Certificates,
                                    String s) throws java.security.cert.CertificateException {
    }

    @Override
    public void checkServerTrusted (java.security.cert.X509Certificate[] x509Certificates,
                                    String s) throws java.security.cert.CertificateException {
    }


    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers () {
        return new X509Certificate[0];
    }
}
