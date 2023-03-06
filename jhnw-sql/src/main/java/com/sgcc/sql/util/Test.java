package com.sgcc.sql.util;

public class Test {
    public static void main(String[] args) {

        String deviceVersion ="Version AAA";
        String deviceSubversion ="RELEASE bbb";
        String returns_String = "#\n" +
                " version aaa 5.20.99, Release bbb 1106\n" +
                "#\n" +
                " sysname H3C-S2152-1\n" +
                "#\n" +
                " domain default enable system\n" +
                "#\n" +
                " ipv6\n" +
                "#\n" +
                " telnet server enable\n" +
                "#\n" +
                " password-recovery enable\n" +
                "#\n" +
                "vlan 1\n" +
                "#\n" +
                "domain system\n" +
                " access-limit disable\n" +
                " state active\n" +
                " idle-cut disable\n" +
                " self-service-url disable\n" +
                "#\n" +
                "user-group system\n" +
                " group-attribute allow-guest\n" +
                "#\n" +
                "local-user admin\n" +
                " password cipher $c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g\n" +
                " authorization-attribute level 3\n" +
                " service-type ssh telnet\n" +
                "local-user user1\n" +
                " password cipher $c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==\n" +
                " authorization-attribute level 3\n" +
                " service-type ssh telnet\n" +
                "local-user user2\n" +
                " password cipher $c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==\n" +
                " authorization-attribute level 3\n" +
                " service-type ssh telnet\n" +
                "#\n" +
                "interface NULL0\n" +
                "#\n" +
                "interface Vlan-interface1\n" +
                " ip address 192.168.1.100 255.255.255.0\n" +
                "#\n" +
                "interface Ethernet1/0/1\n" +
                "#\n" +
                "interface Ethernet1/0/2\n" +
                "#\n" +
                "interface Ethernet1/0/3\n" +
                "#\n" +
                "interface Ethernet1/0/4\n" +
                "#\n" +
                "interface Ethernet1/0/5\n" +
                "#\n" +
                "interface Ethernet1/0/6\n" +
                "#\n" +
                "interface Ethernet1/0/7\n" +
                "#\n" +
                "interface Ethernet1/0/8\n" +
                "#\n" +
                "interface Ethernet1/0/9\n" +
                "#\n" +
                "interface Ethernet1/0/10\n" +
                "#\n" +
                "interface Ethernet1/0/11\n" +
                "#\n" +
                "interface Ethernet1/0/12\n" +
                "#\n" +
                "interface Ethernet1/0/13\n" +
                "#\n" +
                "interface Ethernet1/0/14\n" +
                "#\n" +
                "interface Ethernet1/0/15\n" +
                "#\n" +
                "interface Ethernet1/0/16\n" +
                "#\n" +
                "interface Ethernet1/0/17\n" +
                "#\n" +
                "interface Ethernet1/0/18\n" +
                "#\n" +
                "interface Ethernet1/0/19\n" +
                "#\n" +
                "interface Ethernet1/0/20\n" +
                "#\n" +
                "interface Ethernet1/0/21\n" +
                "#\n" +
                "interface Ethernet1/0/22\n" +
                "#\n" +
                "interface Ethernet1/0/23\n" +
                "#\n" +
                "interface Ethernet1/0/24\n" +
                "#\n" +
                "interface Ethernet1/0/25\n" +
                "#\n" +
                "interface Ethernet1/0/26\n" +
                "#\n" +
                "interface Ethernet1/0/27\n" +
                "#\n" +
                "interface Ethernet1/0/28\n" +
                "#\n" +
                "interface Ethernet1/0/29\n" +
                "#\n" +
                "interface Ethernet1/0/30\n" +
                "#\n" +
                "interface Ethernet1/0/31\n" +
                "#\n" +
                "interface Ethernet1/0/32\n" +
                "#\n" +
                "interface Ethernet1/0/33\n" +
                "#\n" +
                "interface Ethernet1/0/34\n" +
                "#\n" +
                "interface Ethernet1/0/35\n" +
                "#\n" +
                "interface Ethernet1/0/36\n" +
                "#\n" +
                "interface Ethernet1/0/37\n" +
                "#\n" +
                "interface Ethernet1/0/38\n" +
                "#\n" +
                "interface Ethernet1/0/39\n" +
                "#\n" +
                "interface Ethernet1/0/40\n" +
                "#\n" +
                "interface Ethernet1/0/41\n" +
                "#\n" +
                "interface Ethernet1/0/42\n" +
                "#\n" +
                "interface Ethernet1/0/43\n" +
                "#\n" +
                "interface Ethernet1/0/44\n" +
                "#\n" +
                "interface Ethernet1/0/45\n" +
                "#\n" +
                "interface Ethernet1/0/46\n" +
                "#\n" +
                "interface Ethernet1/0/47\n" +
                "#\n" +
                "interface Ethernet1/0/48\n" +
                "#\n" +
                "interface GigabitEthernet1/0/49\n" +
                "#\n" +
                "interface GigabitEthernet1/0/50\n" +
                "#\n" +
                "interface GigabitEthernet1/0/51\n" +
                "#\n" +
                "interface GigabitEthernet1/0/52\n" +
                "#\n" +
                " undo info-center logfile enable\n" +
                "#\n" +
                " ssh server enable\n" +
                " ssh user admin service-type stelnet authentication-type password\n" +
                "#\n" +
                " load xml-configuration\n" +
                "#\n" +
                " load tr069-configuration\n" +
                "#\n" +
                "user-interface aux 0\n" +
                "user-interface vty 0 4\n" +
                " authentication-mode scheme\n" +
                " user privilege level 3\n" +
                " set authentication password cipher $c$3$sh7XRFVfwCOzWj8YhTw3f7lXKjY8yKhuIQ==\n" +
                "user-interface vty 5 15\n" +
                " authentication-mode scheme\n" +
                "#\n" +
                "return\n" +
                "<H3C-S2152-1>";


        String[] return_word = returns_String.trim().replace("\n"," ").split(" ");


        String firmwareVersion = "";
        String subversionNo = "";

        /** 设备版本 */
        String[] deviceVersionSplit =deviceVersion.split(";");
        for (String version:deviceVersionSplit){
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                if (return_word[number].equalsIgnoreCase(versionSplit[0])){
                    if (versionSplit.length == 1){
                        firmwareVersion = return_word[number+1];
                        break;
                    }else {
                        String device = "";
                        for (int num = 0 ; num < versionNumber ; num++){
                            device = device + return_word[number + num] +" ";
                        }
                        device = device.trim();
                        if (deviceVersion.equalsIgnoreCase(device)){
                            firmwareVersion =  return_word[number + (versionNumber-1) + 1];
                            break;
                        }
                    }
                }
            }

        }


        /** 设备子版本 */
        String[] deviceSubversionSplit =deviceSubversion.split(";");
        for (String version:deviceSubversionSplit){
            String[] versionSplit = version.split(" ");
            int versionNumber = versionSplit.length;
            for (int number = 0 ; number < return_word.length; number++){
                if (return_word[number].equalsIgnoreCase(versionSplit[0])){
                    if (versionSplit.length == 1){
                        subversionNo = return_word[number+1];
                        break;
                    }else {
                        String device = "";
                        for (int num = 0 ; num < versionNumber ; num++){
                            device = device + return_word[number + num] +" ";
                        }
                        device = device.trim();
                        if (deviceSubversion.equalsIgnoreCase(device)){
                            subversionNo =  return_word[number + (versionNumber-1) + 1];
                            break;
                        }
                    }
                }
            }

        }

        System.err.println("版本"+firmwareVersion+"子版本"+subversionNo);
    }
}
