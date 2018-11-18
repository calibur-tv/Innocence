package com.riuir.calibur.net.dns;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/14 11:22 PM
 * version: 1.0
 * description:
 */
public class DnsManagerHelper {
  private static class InstanceHolder {
    private static final DnsManager sInstance = initDnsManager();
  }

  public static DnsManager getDnsManager() {
    return InstanceHolder.sInstance;
  }

  private static DnsManager initDnsManager() {
    ArrayList<IResolver> rs = new ArrayList<IResolver>(2);
    try {
      IResolver r1 = AndroidDnsServer.defaultResolver();
      rs.add(r1);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    try {
      IResolver r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
      rs.add(r2);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (rs.size() == 0) {
      return null;
    }

    return new DnsManager(NetworkInfo.normal, rs.toArray(new IResolver[rs.size()]));
  }
}
