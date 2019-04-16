package com.sxhalo.PullCoal.retrofithttp;

import com.sxhalo.PullCoal.common.MyAppLication;
import com.sxhalo.PullCoal.utils.GHLog;
import com.sxhalo.PullCoal.utils.SharedTools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;

/**
 * Created by amoldZhang on 2019/3/13.
 */
public class MyDns implements Dns{
        @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (hostname == null) {
            throw new UnknownHostException("hostname == null");
        } else {
            try {
                List<InetAddress> inetAddressList = new ArrayList<>();
                InetAddress[] inetAddresses = InetAddress.getAllByName(hostname);
                for (InetAddress inetAddress : inetAddresses) {
                    System.out.println(inetAddress.getHostAddress());
                    if (inetAddress instanceof Inet4Address) {
                        GHLog.e("APP_ip___Inet4Address  ",inetAddress.getHostAddress());
                        inetAddressList.add(0, inetAddress);
                        SharedTools.putStringValue(MyAppLication.app,"dns_ip",inetAddress + "");
                    } else {
                        inetAddressList.add(inetAddress);
                        GHLog.e("APP_ip___Inet6Address  ",inetAddress.getHostAddress());
                    }
                }
                return inetAddressList;
            } catch (UnknownHostException e) {
                UnknownHostException unknownHostException = new UnknownHostException("Broken system behaviour for ");
                unknownHostException.initCause(e);
                throw unknownHostException;
            }
        }
    }
}
