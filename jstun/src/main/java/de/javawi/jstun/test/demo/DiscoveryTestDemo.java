/*
 * This file is part of JSTUN.
 *
 * Copyright (c) 2005 Thomas King <king@t-king.de> - All rights
 * reserved.
 *
 * This software is licensed under either the GNU Public License (GPL),
 * or the Apache 2.0 license. Copies of both license agreements are
 * included in this distribution.
 */

package de.javawi.jstun.test.demo;

import de.javawi.jstun.test.DiscoveryTest;

import java.net.BindException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class DiscoveryTestDemo implements Runnable {
    InetAddress iaddress;

    public DiscoveryTestDemo(InetAddress iaddress) {
        this.iaddress = iaddress;
    }

    public void run() {
        try {
            DiscoveryTest test = new DiscoveryTest(iaddress, "stun.ekiga.net", 3478);
            System.out.println(test.test());
        } catch (BindException be) {
            System.out.println(iaddress.toString() + ": " + be.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
                while (iaddresses.hasMoreElements()) {
                    InetAddress iaddress = iaddresses.nextElement();

//                    System.out.println(iaddress);

                    if (Class.forName("java.net.Inet4Address").isInstance(iaddress)) {

//                        if (iaddress.toString().contains("118.178.236.183"))

                        if ((!iaddress.isLoopbackAddress()) && (!iaddress.isLinkLocalAddress())) {
                            Thread thread = new Thread(new DiscoveryTestDemo(iaddress));
                            thread.start();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
