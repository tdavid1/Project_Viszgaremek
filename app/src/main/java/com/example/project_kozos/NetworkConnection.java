package com.example.project_kozos;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkConnection {
    public static String getBackendUrl() {
        return "http://192.168.11.11:3000";
    }
}
