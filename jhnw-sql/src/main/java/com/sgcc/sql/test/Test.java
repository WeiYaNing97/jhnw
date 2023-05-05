package com.sgcc.sql.test;

import com.sgcc.sql.domain.Constant;
import com.sgcc.sql.util.CustomConfigurationUtil;
import com.sgcc.sql.util.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String string = "华三 3600\n" +
                "<TuanMa_H3C_3600>display interface GigabitEthernet 1/0/25\n" +
                " GigabitEthernet1/0/25 current state: UP\n" +
                " IP Packet Frame Type: PKTFMT_ETHNT_2, Hardware Address: 0cda-41de-4e33\n" +
                " Description: To_ShuJuWangHuLian_G1/0/18\n" +
                " Loopback is not set\n" +
                " Media type is twisted pair\n" +
                " Port hardware type is  1000_BASE_T\n" +
                " 1000Mbps-speed mode, full-duplex mode\n" +
                " Link speed type is autonegotiation, link duplex type is autonegotiation\n" +
                " Flow-control is not enabled\n" +
                " The Maximum Frame Length is 10000\n" +
                " Broadcast MAX-ratio: 100%\n" +
                " Unicast MAX-ratio: 100%\n" +
                " Multicast MAX-ratio: 100%\n" +
                " Allow jumbo frame to pass\n" +
                " PVID: 1\n" +
                " Mdi type: auto\n" +
                " Port link-type: trunk\n" +
                "  VLAN passing  : 118, 602\n" +
                "  VLAN permitted: 118, 602\n" +
                "  Trunk port encapsulation: IEEE 802.1q\n" +
                " Port priority: 0\n" +
                " Last clearing of counters:  Never\n" +
                " Peak value of input: 207721 bytes/sec, at 2022-11-08 06:26:00\n" +
                " Peak value of output: 33198 bytes/sec, at 2023-03-27 10:50:33\n" +
                " Last 300 seconds input:  2 packets/sec 282 bytes/sec 0%\n" +
                " Last 300 seconds output:  2 packets/sec 290 bytes/sec 0%\n" +
                " Input (total):  56148368 packets, 6611001881 bytes\n" +
                "         56111416 unicasts, 36952 broadcasts, 0 multicasts, 0 pauses\n" +
                " Input (normal):  56148368 packets, - bytes\n" +
                "         56111416 unicasts, 36952 broadcasts, 0 multicasts, 0 pauses\n" +
                " Input:  0 input errors, 0 runts, 0 giants, 0 throttles\n" +
                "         0 CRC, 0 frame, - overruns, 0 aborts\n" +
                "         - ignored, - parity errors\n" +
                " Output (total): 46229751 packets, 4553563599 bytes\n" +
                "         43884692 unicasts, 911492 broadcasts, 1433567 multicasts, 0 pauses\n" +
                " Output (normal): 46229751 packets, - bytes\n" +
                "         43884692 unicasts, 911492 broadcasts, 1433567 multicasts, 0 pauses\n" +
                " Output: 0 output errors, - underruns, - buffer failures\n" +
                "         0 aborts, 0 deferred, 0 collisions, 0 late collisions\n" +
                "         0 lost carrier, - no carrier\n" +
                "\n" +
                "\n" +
                "华为NE8000路由器\n" +
                "<HS-XianJuHuiJu-2.HW.NE8000M8>display interface GigabitEthernet 0/4/0\n" +
                "GigabitEthernet0/4/0 current state : UP (ifindex: 23)\n" +
                "Line protocol current state : UP \n" +
                "Last line protocol up time : 2023-03-21 18:45:26+08:00\n" +
                "Link quality grade : GOOD\n" +
                "Description: To:HS-XinXiJF-HW.S9303:G1/0/0\n" +
                "Route Port,The Maximum Transmit Unit is 1500 \n" +
                "Internet Address is 10.122.119.9/30\n" +
                "IP Sending Frames' Format is PKTFMT_ETHNT_2, Hardware address is 3890-524f-3c4c\n" +
                "The Vendor PN is LTD1302-BC+-H3C\n" +
                "The Vendor Name is Hisense\n" +
                "Port BW: 1G, Transceiver max BW: 1G, Transceiver Mode: SingleMode\n" +
                "WaveLength: 1310nm, Transmission Distance: 10km\n" +
                "Rx Power:  -6.81dBm, Warning range: [-16.989,  -5.999]dBm\n" +
                "Tx Power:  -5.94dBm, Warning range: [-9.500,  -2.999]dBm\n" +
                "Loopback: none, full-duplex mode, negotiation: enable, Pause Flowcontrol: Receive Enable and Send Enable\n" +
                "Last physical up time   : 2023-03-21 18:45:26+08:00\n" +
                "Last physical down time : 2023-03-21 18:45:26+08:00\n" +
                "Current system time: 2023-04-27 16:04:16+08:00\n" +
                "Statistics last cleared:never\n" +
                "    Last 300 seconds input rate: 22304 bits/sec, 26 packets/sec\n" +
                "    Last 300 seconds output rate: 22833 bits/sec, 27 packets/sec\n" +
                "    Input peak rate 24325015 bits/sec, Record time: 2023-03-31 09:16:03+08:00\n" +
                "    Output peak rate 67266600 bits/sec, Record time: 2023-03-31 09:19:23+08:00\n" +
                "    Input: 22789602665 bytes, 122755407 packets\n" +
                "    Output: 86753318501 bytes, 163119446 packets\n" +
                "    Input:\n" +
                "      Unicast: 122755405 packets, Multicast: 0 packets\n" +
                "      Broadcast: 2 packets, JumboOctets: 0 packets\n" +
                "      CRC: 0 packets, Symbol: 0 packets\n" +
                "      Overrun: 0 packets, InRangeLength: 0 packets\n" +
                "      LongPacket: 0 packets, Jabber: 0 packets, Alignment: 0 packets\n" +
                "      Fragment: 0 packets, Undersized Frame: 0 packets\n" +
                "      RxPause: 0 packets\n" +
                "    Output:\n" +
                "      Unicast: 162694713 packets, Multicast: 424712 packets\n" +
                "      Broadcast: 21 packets, JumboOctets: 0 packets\n" +
                "      Lost: 0 packets, Overflow: 0 packets, Underrun: 0 packets\n" +
                "      System: 0 packets, Overruns: 0 packets\n" +
                "      TxPause: 0 packets\n" +
                "    Last 300 seconds input utility rate:  0.01%\n" +
                "Last 300 seconds output utility rate: 0.01%\n" +
                "锐捷\n" +
                "HengShui_RuiJie_2#show interface gigabitEthernet 9/21\n" +
                "Index(dec):85 (hex):55\n" +
                "GigabitEthernet 9/21 is UP  , line protocol is UP    \n" +
                "  Hardware is Broadcom 5464 GigabitEthernet, address is 8005.88d0.88e1 (bia 8005.88d0.88e1)\n" +
                "  Description: To_3-3\n" +
                "  Interface address is: no ip address\n" +
                "  Interface IPv6 address is:\n" +
                "    No IPv6 address\n" +
                "  MTU 1500 bytes, BW 1000000 Kbit\n" +
                "  Encapsulation protocol is Ethernet-II, loopback not set\n" +
                "  Keepalive interval is 10 sec , set\n" +
                "  Carrier delay is 2 sec\n" +
                "  Ethernet attributes:\n" +
                "    Last link state change time: 2023-04-14 12:36:50\n" +
                "    Time duration since last link state change: 13 days,  3 hours,  4 minutes, 30 seconds\n" +
                "    Priority is 0\n" +
                "    Medium-type is Fiber\n" +
                "    Admin duplex mode is AUTO, oper duplex is Full\n" +
                "    Admin speed is AUTO, oper speed is 1000M\n" +
                "    Flow control admin status is OFF, flow control oper status is OFF\n" +
                "    Admin negotiation mode is OFF, oper negotiation state is ON\n" +
                "    Storm Control: Broadcast is OFF, Multicast is OFF, Unicast is OFF\n" +
                "  Bridge attributes:\n" +
                "    Port-type: trunk\n" +
                "    Native vlan: 1\n" +
                "    Allowed vlan lists: 102,108\n" +
                "    Active vlan lists: 102,108\n" +
                "  Rxload is 1/255, Txload is 1/255\n" +
                "  Input peak rate: 382686514 bits/sec, at 2022-12-26 11:20:33\n" +
                "  Output peak rate: 331887732 bits/sec, at 2023-01-29 09:44:45\n" +
                "   10 seconds input rate 111813 bits/sec, 52 packets/sec\n" +
                "   10 seconds output rate 167735 bits/sec, 93 packets/sec\n" +
                "    3487807636 packets input, 999812040559 bytes, 0 no buffer, 0 dropped\n" +
                "    Received 99341659 broadcasts, 0 runts, 1 giants\n" +
                "    0 input errors, 0 CRC, 0 frame, 0 overrun, 0 abort\n" +
                "    7199756199 packets output, 3722563655864 bytes, 0 underruns , 0 dropped\n" +
                "    0 output errors, 0 collisions, 0 interface resets\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "迈普\n" +
                "XianLuLou_MaiP_107.6#show interface tengigabitethernet 0/49 statistics \n" +
                "tengigabitethernet0/49 statistics information:\n" +
                "        RxOctets                 : 43516714723\n" +
                "        RxUcastPkts              : 102096653\n" +
                "        RxMulticastPkts          : 2346925\n" +
                "        RxBroadcastPkts          : 1982316\n" +
                "        RxErrorPkts              : 0\n" +
                "        RxDiscardPkts            : 0\n" +
                "        TxOctets                 : 100999690836\n" +
                "        TxUcastPkts              : 159583248\n" +
                "        TxMulticastPkts          : 2699068\n" +
                "        TxBroadcastPkts          : 4590472\n" +
                "        TxErrorPkts              : 0\n" +
                "        TxDiscardPkts            : 0\n" +
                "        TotalOctets              : 144516405559\n" +
                "        TotalPkts                : 273298682\n" +
                "        TotalUcastPkts           : 261679901\n" +
                "        TotalBroadcastPkts       : 6572788\n" +
                "        TotalMulticastPkts       : 5045993\n" +
                "        TotalCRCErrors           : 0\n" +
                "        TotalSymbolErrors        : 0\n" +
                "        TotalAlignmentErrors     : 0\n" +
                "        TotalUndersizePkts       : 0\n" +
                "        TotalOversizePkts        : 62487868\n" +
                "        TotalFragments           : 0\n" +
                "        TotalJabbers             : 0\n" +
                "        TotalCollisions          : 0\n" +
                "        TotalPkts64Octets        : 3288143\n" +
                "        TotalPkts65to127Octets   : 142319703\n" +
                "        TotalPkts128to255Octets  : 12062843\n" +
                "        TotalPkts256to511Octets  : 25379722\n" +
                "        TotalPkts512to1023Octets : 18500619\n" +
                "        TotalPkts1024to1518Octets: 9259784\n" +
                "        RxBandwidthUtilization   : 0%\n" +
                "        RxDiscardRatio           : 0%\n" +
                "        RxErrorRatio             : 0%\n" +
                "        TxBandwidthUtilization   : 0%\n" +
                "        TxDiscardRatio           : 0%\n" +
                "        TxErrorRatio             : 0%\n" +
                "\n" +
                "     input rate 367153 bits/sec, 51 packets/sec\n" +
                "     output rate 87169 bits/sec, 50 packets/sec\n" +
                "\n" +
                "思科\n" +
                "HeXinJiaoHuanJi_2#show interfaces gigabitEthernet 3/19\n" +
                "GigabitEthernet3/19 is up, line protocol is up (connected)\n" +
                "  Hardware is C6k 1000Mb 802.3, address is 0019.07d2.b400 (bia 0019.07d2.b400)\n" +
                "  Description: To_HengShuiZhan_C3560\n" +
                "  Internet address is 10.122.114.38/30\n" +
                "  MTU 1500 bytes, BW 1000000 Kbit, DLY 10 usec, \n" +
                "     reliability 255/255, txload 1/255, rxload 1/255\n" +
                "  Encapsulation ARPA, loopback not set\n" +
                "  Keepalive set (10 sec)\n" +
                "  Full-duplex, 1000Mb/s, media type is T\n" +
                "  input flow-control is off, output flow-control is on \n" +
                "  Clock mode is auto\n" +
                "  ARP type: ARPA, ARP Timeout 04:00:00\n" +
                "  Last input 00:00:04, output 00:00:07, output hang never\n" +
                "  Last clearing of \"show interface\" counters never\n" +
                "  Input queue: 0/75/104615/11 (size/max/drops/flushes); Total output drops: 0\n" +
                "  Queueing strategy: fifo\n" +
                "  Output queue: 0/40 (size/max)\n" +
                "  5 minute input rate 117000 bits/sec, 118 packets/sec\n" +
                "  5 minute output rate 37000 bits/sec, 44 packets/sec\n" +
                "  L2 Switched: ucast: 28502242 pkt, 1952813915 bytes - mcast: 33248581 pkt, 3308319308 bytes\n" +
                "  L3 in Switched: ucast: 17710151593 pkt, 9225698264540 bytes - mcast: 0 pkt, 0 bytes mcast\n" +
                "  L3 out Switched: ucast: 18142624773 pkt, 5972361107434 bytes mcast: 0 pkt, 0 bytes\n" +
                "     17820830356 packets input, 9237019629279 bytes, 0 no buffer\n" +
                "     Received 15542368 broadcasts (0 IP multicast)\n" +
                "     0 runts, 0 giants, 0 throttles\n" +
                "     104615 input errors, 0 CRC, 0 frame, 0 overrun, 0 ignored\n" +
                "     0 watchdog, 0 multicast, 0 pause input\n" +
                "     0 input packets with dribble condition detected\n" +
                "     18215168063 packets output, 5980116193635 bytes, 0 underruns\n" +
                "     0 output errors, 0 collisions, 4 interface resets\n" +
                "     0 babbles, 0 late collision, 0 deferred\n" +
                "     0 lost carrier, 0 no carrier, 0 PAUSE output\n" +
                "     0 output buffer failures, 0 output buffers swapped out";
        string = MyUtils.trimString(string);
        List<String> returnList = new ArrayList<>();
        String[] split = string.split("\r\n");
        for (String information:split){
            List<String> parameters = getParameters(information);
            returnList.addAll(parameters);
        }
        returnList.stream().forEach(x ->System.err.println(x.trim()));
    }

    /**
     * 查看交换机错误包数量
     * @param information
     * @return
     */
    public static List<String> getParameters(String information) {
        String[] keyword = {"input errors","output errors","CRC,","CRC:","RxErrorPkts","TxErrorPkts"};
        List<String> keyList = new ArrayList<>();
        for (String key:keyword){
            if (information.toUpperCase().indexOf(key.toUpperCase())!=-1){
                keyList.add(key);
            }
        }
        List<String> returnList = new ArrayList<>();
        for (String key:keyList){
            switch (key){
                case "input errors":
                case "output errors":
                case "CRC,":
                    String[] inputoutputCRC = MyUtils.splitIgnoreCase(information, key);
                    String[] inputoutputCRCsplit = inputoutputCRC[0].split(",");
                    returnList.add(inputoutputCRCsplit[inputoutputCRCsplit.length-1]+key);
                    break;
                case "CRC:":
                    String[] CRC = MyUtils.splitIgnoreCase(information, key);
                    String[] CRCplit = CRC[1].split(",");
                    returnList.add(key + CRCplit[0]);
                    break;
                case "RxErrorPkts":
                case "TxErrorPkts":
                    String[] rxtx = information.split(":");
                    returnList.add(rxtx[0] +" : "+  rxtx[1]);
                    break;
            }
        }
        return returnList;
    }
}
