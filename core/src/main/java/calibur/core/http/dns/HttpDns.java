package calibur.core.http.dns;

import com.orhanobut.logger.Logger;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.Domain;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Dns;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/15 3:21 PM
 * version: 1.0
 * description:
 */
public class HttpDns implements Dns {
  private static final Dns SYSTEM = Dns.SYSTEM;

  @Override public List<InetAddress> lookup(String hostname) throws UnknownHostException {
    DnsManager dns = DnsManagerHelper.getDnsManager();
    try {
      InetAddress[] queryInetAdress = dns.queryInetAdress(new Domain(hostname));
      if (queryInetAdress.length > 0) {
        List<InetAddress> inetAddresses = new ArrayList<>();
        for (InetAddress inetAddress : queryInetAdress) {
          inetAddresses.add(inetAddress);
          //if (BuildConfig.DEBUG) {
            Logger.d("httpDns lookup:" + hostname + " -> ip:" + inetAddress.getHostAddress());
          //}
        }
        return inetAddresses;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    List<InetAddress> inetAddresses = SYSTEM.lookup(hostname);

    String localDnsResolve = "";
    //if (BuildConfig.DEBUG) {
      if (inetAddresses != null && inetAddresses.size() > 0) {
        for (InetAddress inetAddress : inetAddresses) {
          localDnsResolve = "localDns lookup:" + hostname + " -> ip:" + inetAddress.getHostAddress();
          Logger.d(localDnsResolve);
        }
      //}
    }

    return inetAddresses;
  }
}
