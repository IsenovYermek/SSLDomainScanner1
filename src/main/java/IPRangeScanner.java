import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IPRangeScanner {
    public void scanIPRange(String ipRange, Consumer<String> domainConsumer) {
        List<String> ipAddresses = getIPAddressesFromRange(ipRange);
        ipAddresses.parallelStream().forEach(ip -> {
            try {
                List<String> domainNames = getDomainNamesFromIP(ip);
                domainNames.forEach(domainConsumer);
            } catch (IOException e) {
                System.out.println("Error scanning IP: " + ip);
            }
        });
    }

    private List<String> getIPAddressesFromRange(String ipRange) {
        String[] rangeParts = ipRange.split("/");
        String baseIP = rangeParts[0];
        int subnetMask = Integer.parseInt(rangeParts[1]);
        int hostsCount = (int) Math.pow(2, 32 - subnetMask);

        List<String> ipAddresses = new ArrayList<>();
        IntStream.range(0, hostsCount).forEach(i -> {
            int ipAsInt = ipToInt(baseIP);
            ipAsInt += i;
            ipAddresses.add(intToIp(ipAsInt));
        });

        return ipAddresses;
    }

    private List<String> getDomainNamesFromIP(String ip) throws IOException {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        List<String> domainNames = new ArrayList<>();
        try {
            InetAddress address = InetAddress.getByName(ip);
            String hostname = address.getHostName();
            domainNames.add(hostname);

            httpClient.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return domainNames;
    }

    private int ipToInt(String ipAddress) {
        String[] ipAddressParts = ipAddress.split("\\.");
        int result = 0;
        for (String part : ipAddressParts) {
            result = result << 8 | Integer.parseInt(part);
        }
        return result;
    }

    private String intToIp(int ipAddress) {
        return (ipAddress >> 24 & 0xFF) + "." +
                (ipAddress >> 16 & 0xFF) + "." +
                (ipAddress >> 8 & 0xFF) + "." +
                (ipAddress & 0xFF);
    }
}
