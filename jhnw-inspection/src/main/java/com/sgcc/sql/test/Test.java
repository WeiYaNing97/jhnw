package com.sgcc.sql.test;

import com.sgcc.share.util.MyUtils;
import com.sun.applet2.AppletParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        /*String str1 = "CRC: 1234 packets, Symbol: 4444 packets";
        String str2 = "CRC: $ packets";
        String placeholdersContaining = getPlaceholdersContaining(str1, str2);*/

        String str3 = "Eth-Trunk1 current state : UP\n" +
                "Line protocol current state : UP\n" +
                "Description:To_HX_S7506E\n" +
                "Switch Port, Link-type : trunk(configured),\n" +
                "PVID :    1, Hash arithmetic : According to SIP-XOR-DIP,Maximal BW: 2G, Current BW: 2G, The Maximum Frame Length is 9216\n" +
                "IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is f853-2982-8f10\n" +
                "Current system time: 2023-06-20 16:43:57+08:00\n" +
                "Last 300 seconds input rate 3010840 bits/sec, 699 packets/sec\n" +
                "Last 300 seconds output rate 2708856 bits/sec, 489 packets/sec\n" +
                "Input:  843042623 packets, 626161438059 bytes\n" +
                "  Unicast:                  824884018,  Multicast:                     9101170\n" +
                "  Broadcast:                  9057435,  Jumbo:                       294092915\n" +
                "  Discard:                          0,  Pause:                               0\n" +
                "  Frames:                           0\n" +
                "\n" +
                "  Total Error:                      123\n" +
                "  CRC:                              0,  Giants:                              0\n" +
                "  Jabbers:                          0,  Fragments:                           0\n" +
                "  Runts:                            0,  DropEvents:                          0\n" +
                "  Alignments:                       0,  Symbols:                             0\n" +
                "  Ignoreds:                         0\n" +
                "\n" +
                "Output:  2997524508 packets, 539436712102 bytes\n" +
                "  Unicast:                  528937482,  Multicast:                  2441058483\n" +
                "  Broadcast:                 27528543,  Jumbo:                       121392927\n" +
                "  Discard:                          0,  Pause:                               0\n" +
                "\n" +
                "  Total Error:                      456\n" +
                "  Collisions:                       0,  ExcessiveCollisions:                 0\n" +
                "  Late Collisions:                  0,  Deferreds:                           0\n" +
                "  Buffers Purged:                   0\n" +
                "\n" +
                "    Input bandwidth utilization  : 0.15%\n" +
                "    Output bandwidth utilization : 0.14%\n" +
                "-----------------------------------------------------\n" +
                "PortName                      Status      Weight\n" +
                "-----------------------------------------------------\n" +
                "GigabitEthernet2/0/12         UP          1\n" +
                "GigabitEthernet1/0/30         UP          1\n" +
                "-----------------------------------------------------\n" +
                "The Number of Ports in Trunk : 2\n" +
                "The Number of UP Ports in Trunk : 2";
        str3 = MyUtils.trimString(str3);
    }



}
