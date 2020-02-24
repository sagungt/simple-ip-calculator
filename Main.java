import java.util.Scanner;
import java.util.StringTokenizer;
import java.lang.Math;

public class Main {
    public static void main(final String[] args) {
        final Scanner input = new Scanner(System.in);
        String ip_input;
        int[] ip_address;
        int prefix;
        System.out.print("masukan ip address dengan prefix : ");
        ip_input = input.next();
        final StringTokenizer ip_delim = new StringTokenizer(ip_input, "/");
        ip_address = ipToArray(ip_delim.nextToken());
        prefix = Integer.parseInt(ip_delim.nextToken());
        printData(ip_address, prefix);
        System.out.println(getPrefixFromHost(512));
        System.out.println(getSubnetworkHost(24, 4));
        System.out.println(getActiveOctet(24));
        getSubnetwork(ip_address, prefix, 4);
        input.close();
    }

    public static int[] maskToArray(int prefix) {
        final int[] result = { 0, 0, 0, 0 };
        String mask = "";
        for (int i = 1; i <= 32; i++) {
            if (prefix > 0) {
                mask += "1";
                if (i == 8 || i == 16 || i == 24) {
                    mask += ".";
                }
            } else {
                mask += "0";
                if (i == 8 || i == 16 || i == 24) {
                    mask += ".";
                }
            }
            prefix -= 1;
        }
        final StringTokenizer subnet = new StringTokenizer(mask, ".");
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(subnet.nextToken(), 2);
        }
        return result;
    }

    public static int[] ipToArray(final String ip) {
        final int[] result = { 0, 0, 0, 0 };
        final StringTokenizer ip_byte = new StringTokenizer(ip, ".");
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(ip_byte.nextToken());
        }
        return result;
    }

    public static int[] getWildcard(int prefix) {
        final int[] result = { 0, 0, 0, 0 };
        String mask = "";
        for (int i = 1; i <= 32; i++) {
            if (prefix <= 0) {
                mask += "1";
                if (i == 8 || i == 16 || i == 24) {
                    mask += ".";
                }
            } else {
                mask += "0";
                if (i == 8 || i == 16 || i == 24) {
                    mask += ".";
                }
            }
            prefix -= 1;
        }
        final StringTokenizer wildcard = new StringTokenizer(mask, ".");
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(wildcard.nextToken(), 2);
        }
        return result;
    }

    public static int[] getNetworkAddress(final int[] ip, final int prefix) {
        final int[] result = { 0, 0, 0, 0 };
        final int[] subnet = maskToArray(prefix);
        for (int i = 0; i < result.length; i++) {
            result[i] = (ip[i] & subnet[i]);
        }
        return result;
    }

    public static String toBinary(final int[] data) {
        final String result = String.format("%8s.%8s.%8s.%8s", Integer.toBinaryString(data[0]),
                Integer.toBinaryString(data[1]), Integer.toBinaryString(data[2]), Integer.toBinaryString(data[3]))
                .replace(" ", "0");
        return result;
    }

    public static int getAvailableHost(final int prefix) {
        final int result = (int) Math.pow(2, (32 - prefix)) - 2;
        return result;
    }

    public static int[] getBroadcastAddress(final int[] ip, final int prefix) {
        final int[] result = { 0, 0, 0, 0 };
        final int[] mask = getWildcard(prefix);
        for (int i = 0; i < result.length; i++) {
            result[i] = (ip[i] | mask[i]);
        }
        return result;
    }

    public static int getPrefixMask(final int[] mask) {
        int result = 0;
        final String subnet = toBinary(mask);

        for (int i = 0; i < subnet.length(); i++) {
            if (subnet.charAt(i) == '1') {
                result++;
            }
        }
        return result;
    }

    public static int[] getFirstAddress(final int[] ip, final int prefix) {
        final int[] result = getNetworkAddress(ip, prefix);
        result[3] += 1;
        return result;
    }

    public static int[] getLastAddress(final int[] ip, final int prefix) {
        final int[] result = getBroadcastAddress(ip, prefix);
        result[3] -= 1;
        return result;
    }

    public static void printData(final int[] ip, final int prefix) {
        System.out.printf("IP Address\t\t: %d.%d.%d.%d\n", ip[0], ip[1], ip[2], ip[3]);
        int[] data = maskToArray(prefix);
        System.out.printf("Subnet Mask\t\t: %d.%d.%d.%d\n", data[0], data[1], data[2], data[3]);
        data = getNetworkAddress(ip, prefix);
        System.out.printf("Network Address\t\t: %d.%d.%d.%d\n", data[0], data[1], data[2], data[3]);
        data = getFirstAddress(ip, prefix);
        System.out.printf("Usable Address\t\t: %d.%d.%d.%d -> ", data[0], data[1], data[2], data[3]);
        data = getLastAddress(ip, prefix);
        System.out.printf("%d.%d.%d.%d\n", data[0], data[1], data[2], data[3]);
        data = getBroadcastAddress(ip, prefix);
        System.out.printf("Broadcast Address\t: %d.%d.%d.%d\n", data[0], data[1], data[2], data[3]);
        data = getWildcard(prefix);
        System.out.printf("Wildcard Address\t: %d.%d.%d.%d\n", data[0], data[1], data[2], data[3]);
        System.out.printf("Available host\t\t: %d\n", getAvailableHost(prefix));
    }

    public static int getPrefixFromHost(final int host) {
        int result = 0;
        final long[] valid_host = new long[32];
        for (int i = 0; i < 32; i++) {
            valid_host[i] = (long) Math.pow(2, i);
            if (host == valid_host[i]) {
                result = i;
            }
        }
        return result = 32 - result;
    }

    public static int getSubnetworkHost(final int prefix, final int subnetwork) {
        int host = getAvailableHost(prefix) + 2;
        return host = host / subnetwork;
    }

    public static void getSubnetwork(final int[] ip, final int prefix, final int subnetwork) {
        final int[][] ip_networks = new int[subnetwork][4];
        final int[][] mask_networks = new int[subnetwork][4];
        final int[][] wildcard_networks = new int[subnetwork][4];
        final int[][] broadcast_networks = new int[subnetwork][4];
        final int[][] first_ip_networks = new int[subnetwork][4];
        final int[][] last_ip_networks = new int[subnetwork][4];
        final int host = (getAvailableHost(prefix) + 2) / subnetwork;
        final int prefix_subnetwork = getPrefixFromHost(host);
        for (int i = 0; i < subnetwork; i++) {
            ip_networks[i] = getNetworkAddress(ip, prefix_subnetwork);
            mask_networks[i] = maskToArray(prefix_subnetwork);
            wildcard_networks[i] = getWildcard(prefix_subnetwork);
            broadcast_networks[i] = getBroadcastAddress(ip, prefix_subnetwork);
            first_ip_networks[i] = getFirstAddress(ip, prefix_subnetwork);
            last_ip_networks[i] = getLastAddress(ip, prefix_subnetwork);
            if (i > 0) {
                ip_networks[i] = getNextNetwork(ip_networks[i - 1], prefix_subnetwork);
                broadcast_networks[i] = getBroadcastAddress(getNextNetwork(ip_networks[i - 1], prefix_subnetwork),
                        prefix_subnetwork);
                first_ip_networks[i] = getFirstAddress(getNextNetwork(ip_networks[i - 1], prefix_subnetwork),
                        prefix_subnetwork);
                last_ip_networks[i] = getLastAddress(getNextNetwork(ip_networks[i - 1], prefix_subnetwork),
                        prefix_subnetwork);
            }
        }
        for (int j = 0; j < subnetwork; j++) {
            System.out.println("=================================================");
            System.out.printf("Subnetwork ke %d\n", j + 1);
            System.out.printf("Network Address\t\t: %d.%d.%d.%d\n", ip_networks[j][0], ip_networks[j][1],
                    ip_networks[j][2], ip_networks[j][3]);
            System.out.printf("Subnetmask \t\t: %d.%d.%d.%d\n", mask_networks[j][0], mask_networks[j][1],
                    mask_networks[j][2], mask_networks[j][3]);
            System.out.printf("Usable Address\t\t: %d.%d.%d.%d -> ", first_ip_networks[j][0], first_ip_networks[j][1],
                    first_ip_networks[j][2], first_ip_networks[j][3]);
            System.out.printf("%d.%d.%d.%d\n", last_ip_networks[j][0], last_ip_networks[j][1], last_ip_networks[j][2],
                    last_ip_networks[j][3]);
            System.out.printf("Wildcardmask\t: %d.%d.%d.%d\n", wildcard_networks[j][0], wildcard_networks[j][1],
                    wildcard_networks[j][2], wildcard_networks[j][3]);
            System.out.printf("Broadcast Address\t: %d.%d.%d.%d\n", broadcast_networks[j][0], broadcast_networks[j][1],
                    broadcast_networks[j][2], broadcast_networks[j][3]);
            System.out.printf("Available host\t\t: %d\n", ((getAvailableHost(prefix) + 2) / subnetwork) - 2);
            System.out.println("=================================================");
        }
    }

    public static int[] getNextNetwork(int[] ip, final int prefix) {
        final int active_octet = getActiveOctet(prefix);
        int add = 0;
        ip = getNetworkAddress(ip, prefix);
        for (int i = 1; i <= 32; i++) {
            if (prefix == 1 || prefix == 9 || prefix == 17 || prefix == 25) {
                add = 128;
            } else if (prefix == 2 || prefix == 10 || prefix == 18 || prefix == 26) {
                add = 64;
            } else if (prefix == 3 || prefix == 11 || prefix == 19 || prefix == 27) {
                add = 32;
            } else if (prefix == 4 || prefix == 12 || prefix == 20 || prefix == 28) {
                add = 16;
            } else if (prefix == 5 || prefix == 13 || prefix == 21 || prefix == 29) {
                add = 8;
            } else if (prefix == 6 || prefix == 14 || prefix == 22 || prefix == 30) {
                add = 4;
            } else if (prefix == 7 || prefix == 15 || prefix == 23 || prefix == 31) {
                add = 2;
            } else {
                add = 1;
            }
        }
        ip[active_octet - 1] = ip[active_octet - 1] + add;
        return ip;
    }

    public static int getActiveOctet(final int prefix) {
        return (prefix >= 1 && prefix <= 8) ? 1
                : ((prefix >= 8 && prefix <= 16) ? 2 : ((prefix >= 16 && prefix <= 24) ? 3 : 4));
    }
}