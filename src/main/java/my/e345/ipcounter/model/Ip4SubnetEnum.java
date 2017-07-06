/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.model;

/**
 * Private IPv4 address spaces https://en.wikipedia.org/wiki/Private_network
 *
 * @author localEvg
 */
public enum Ip4SubnetEnum {

    NETWORK_C('C', parseIp("192.168.0.0"), parseIp("192.168.255.255")),
    NETWORK_B('B', parseIp("172.16.0.0"), parseIp("172.31.255.255")),
    NETWORK_A('A', parseIp("10.0.0.0"), parseIp("10.255.255.255"));

    public char code;
    public long minIpAddress;
    public long maxIpAddress;

    Ip4SubnetEnum(char code, long minIpAddress, long maxIpAddress) {
        this.code = code;
        this.minIpAddress = minIpAddress;
        this.maxIpAddress = maxIpAddress;
    }

    public static Ip4SubnetEnum fromCode(char code) {
        for (Ip4SubnetEnum itm : values()) {
            if (itm.code == code) {
                return itm;
            }
        }
        return null;
    }

    /**
     * Найти класс сети по IPv4 адресу
     *
     * @param ipAddr IPv4 адрес
     * @return класс подсети или null
     */
    public static Ip4SubnetEnum fromIpAddr(long ipAddr) {
        for (Ip4SubnetEnum itm : values()) {
            if (ipAddr >= itm.minIpAddress && ipAddr <= itm.maxIpAddress) {
                return itm;
            }
        }
        return null;
    }

    // from https://stackoverflow.com/questions/12057853/how-to-convert-string-ip-numbers-to-integer-in-java
    public static long parseIp(String address) {
        long result = 0;

        // iterate over each octet
        for (String part : address.split("\\.")) {
            // shift the previously parsed bits over by 1 byte
            result = result << 8;
            // set the low order bits to the current octet
            result |= Long.parseLong(part);
        }
        return result;
    }

    // from  https://teneo.wordpress.com/2008/12/23/java-ip-address-to-integer-and-back/
    public static String intToIp(int i) {
        return ((i >> 24) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "."
                + ((i >> 8) & 0xFF) + "."
                + (i & 0xFF);
    }

    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }
}
