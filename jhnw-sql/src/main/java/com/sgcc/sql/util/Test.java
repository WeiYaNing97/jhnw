package com.sgcc.sql.util;

import com.sgcc.sql.controller.Configuration;

import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {

        String device ="<HS-YangCunZ-PE-RT-HW.NE40EX8>display version \n" +
                "Huawei Versatile Routing Platform Software\n" +
                "VRP (R) software, Version 5.150 (NE40E&80E V600R007C00SPC300)\n" +
                "Copyright (C) 2000-2013 Huawei Technologies Co., Ltd.\n" +
                "HUAWEI NE40E-X8 uptime is 1041 days, 4 hours, 36 minutes\n" +
                "NE40E-X8 version information:\n" +
                "\n" +
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
                "\n" +
                "BKP 1 version information:\n" +
                "  PCB         Version : CR56BKP08B REV C\n" +
                "  MPU  Slot  Quantity : 0\n" +
                "  SRU  Slot  Quantity : 2\n" +
                "  SFU  Slot  Quantity : 1\n" +
                "  LPU  Slot  Quantity : 8\n" +
                "\n" +
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
                "MPU version information:\n" +
                "\n" +
                "MPU(Master) 9  : uptime is 1041 days, 4 hours, 36 minutes\n" +
                "         StartupTime   2048/01/01   11:11:11 \n" +
                "  SDRAM Memory Size   : 3904M bytes\n" +
                "  FLASH Memory Size   : 32M  bytes\n" +
                "  NVRAM Memory Size   : 4096K bytes\n" +
                "  CFCARD Memory Size : 1920M bytes \n" +
                "  CFCARD2 Memory Size : 1773M bytes       \n" +
                "  CR5D0SRUA570 version information\n" +
                "  PCB         Version : CR56RPUA REV B\n" +
                "  EPLD        Version : 104\n" +
                "  FPGA        Version : 019\n" +
                "  BootROM     Version : 41.0\n" +
                "  BootLoad    Version : 37.7\n" +
                "  Software    Version : Version 5.150 RELEASE 0089\n" +
                "  FRA version information\n" +
                "  PCB         Version : CR57FRA200A REV C\n" +
                "  FPGA        Version : 105\n" +
                "  MonitorBUS version information:\n" +
                "  Software    Version : 8.55\n" +
                "MPU(Slave) 10 : uptime is 1041 days, 4 hours, 34 minutes\n" +
                "         StartupTime   2048/01/01   11:13:05 \n" +
                "  SDRAM Memory Size   : 3904M bytes\n" +
                "  FLASH Memory Size   : 32M  bytes\n" +
                "  NVRAM Memory Size   : 4096K bytes\n" +
                "  CFCARD Memory Size : 1920M bytes \n" +
                "  CFCARD2 Memory Size : 1773M bytes \n" +
                "  CR5D0SRUA570 version information\n" +
                "  PCB         Version : CR56RPUA REV B\n" +
                "  EPLD        Version : 104\n" +
                "  FPGA        Version : 019\n" +
                "  BootROM     Version : 41.0              \n" +
                "  BootLoad    Version : 37.7\n" +
                "  Software    Version : Version 5.150 RELEASE 0089\n" +
                "  FRA version information\n" +
                "  PCB         Version : CR57FRA200A REV C\n" +
                "  FPGA        Version : 105\n" +
                "  MonitorBUS version information:\n" +
                "  Software    Version : 8.55\n" +
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
                "LPU/SPU version information:\n" +
                "\n" +
                "LPU 1  : uptime is 1041 days, 4 hours, 29 minutes\n" +
                "         StartupTime   2048/01/01   11:17:43 \n" +
                "  Host    processor :\n" +
                "  SDRAM Memory Size: 1024M  bytes\n" +
                "  Flash Memory Size:   32M  bytes\n" +
                "  Network processor :\n" +
                "  1. RDRAM Memory Size:   96M  bytes\n" +
                "  2. SRAM  Memory Size:   36M  bytes(ingress)\n" +
                "  1. RDRAM Memory Size:  192M  bytes\n" +
                "  2. SRAM  Memory Size:   27M  bytes(egress)\n" +
                "  LPU version information\n" +
                "  PCB         Version : CR53LPUF REV D\n" +
                "  EPLD        Version : 102\n" +
                "  FPGA        Version : 714               \n" +
                "  NAPA        Version : 308\n" +
                "  NP          Version : 048\n" +
                "  BootROM     Version : 273.0\n" +
                "  BootLoad    Version : 275.0\n" +
                "  FSURTP      Version : Version 2.1 RELEASE 0372\n" +
                "  FSUKERNEL   Version : Version 2.1 RELEASE 0372\n" +
                "  EFURTP      Version : Version 2.1 RELEASE 0372\n" +
                "  EFUKERNEL   Version : Version 2.1 RELEASE 0372\n" +
                "  TM          Version : 510\n" +
                "  TCM version information\n" +
                "  PCB         Version : CR53TCMB REV D\n" +
                "  PIC0:CR5M0E8GFA30 version information\n" +
                "  PCB         Version : CR53E8GFB REV A\n" +
                "  FPGA        Version : 085\n" +
                "  PIC1:CR5M0E8GFA30 version information\n" +
                "  PCB         Version : CR53E8GFB REV A\n" +
                "  FPGA        Version : 085\n" +
                "  MonitorBUS version information:\n" +
                "  PCB         Version : CR31MBSA REV A\n" +
                "  EPLD        Version : 019\n" +
                "  Software    Version : 3.16\n" +
                "  Configure license items:\n" +
                "  NULL                                    \n" +
                "LPU 7  : uptime is 875 days, 6 hours, 24 minutes\n" +
                "         StartupTime   2020/11/16   10:22:24 \n" +
                "  Host    processor :\n" +
                "  SDRAM Memory Size: 1024M  bytes\n" +
                "  Flash Memory Size:   32M  bytes\n" +
                "  Network processor :\n" +
                "  1. RDRAM Memory Size:   96M  bytes\n" +
                "  2. SRAM  Memory Size:   36M  bytes(ingress)\n" +
                "  1. RDRAM Memory Size:  192M  bytes\n" +
                "  2. SRAM  Memory Size:   27M  bytes(egress)\n" +
                "  LPU version information\n" +
                "  PCB         Version : CR53LPUF REV D\n" +
                "  EPLD        Version : 102\n" +
                "  FPGA        Version : 714\n" +
                "  NAPA        Version : 308\n" +
                "  NP          Version : 048\n" +
                "  BootROM     Version : 273.0\n" +
                "  BootLoad    Version : 275.0\n" +
                "  FSURTP      Version : Version 2.1 RELEASE 0372\n" +
                "  FSUKERNEL   Version : Version 2.1 RELEASE 0372\n" +
                "  EFURTP      Version : Version 0.0 RELEASE 0372\n" +
                "  EFUKERNEL   Version : Version 0.0 RELEASE 0372\n" +
                "  TM          Version : 510\n" +
                "  TCM version information\n" +
                "  PCB         Version : CR53TCMB REV D    \n" +
                "  PIC0:CR5M0E8GFA30 version information\n" +
                "  PCB         Version : CR53E8GFB REV A\n" +
                "  FPGA        Version : 085\n" +
                "  MonitorBUS version information:\n" +
                "  PCB         Version : CR31MBSA REV A\n" +
                "  EPLD        Version : 019\n" +
                "  Software    Version : 3.16\n" +
                "  Configure license items:\n" +
                "  NULL                                    \n" +
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
                "SFU version information:\n" +
                "\n" +
                "SFU 11 : uptime is 1041 days, 4 hours, 34 minutes\n" +
                "         StartupTime   2048/01/01   11:12:43 \n" +
                "  SDRAM Memory Size   : 256M bytes\n" +
                "  Flash Memory Size   : 64M  bytes\n" +
                "  CR5DSFUIE07C version information\n" +
                "  PCB         Version : CR57SFU200C REV B\n" +
                "  EPLD        Version : 101\n" +
                "  BootROM     Version : 54.0\n" +
                "  BootLoad    Version : 54.0\n" +
                "  Software    Version : Version 3.0 RELEASE 0033\n" +
                "  MonitorBUS version information:\n" +
                "  Software    Version : 6.42              \n" +
                "SFU 12 : uptime is 1041 days, 4 hours, 35 minutes\n" +
                "         StartupTime   2048/01/01   11:12:23 \n" +
                "  SDRAM Memory Size   : 256M bytes\n" +
                "  Flash Memory Size   : 64M  bytes\n" +
                "  SFU version information\n" +
                "  PCB         Version : CR57FRA200A REV C\n" +
                "  EPLD        Version : 101\n" +
                "  BootROM     Version : 54.0\n" +
                "  BootLoad    Version : 54.0\n" +
                "  Software    Version : Version 3.0 RELEASE 0033\n" +
                "  MonitorBUS version information:\n" +
                "  Software    Version : 8.55\n" +
                "SFU 13 : uptime is 1041 days, 4 hours, 35 minutes\n" +
                "         StartupTime   2048/01/01   11:12:23 \n" +
                "  SDRAM Memory Size   : 256M bytes\n" +
                "  Flash Memory Size   : 64M  bytes\n" +
                "  SFU version information\n" +
                "  PCB         Version : CR57FRA200A REV C\n" +
                "  EPLD        Version : 101\n" +
                "  BootROM     Version : 54.0\n" +
                "  BootLoad    Version : 54.0\n" +
                "  Software    Version : Version 3.0 RELEASE 0033\n" +
                "  MonitorBUS version information:\n" +
                "  Software    Version : 8.55              \n" +
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
                "CLK version information:\n" +
                "  \n" +
                "Slot 14 : uptime is 1041 days, 4 hours, 35 minutes\n" +
                "         StartupTime   2048/01/01   11:12:25 \n" +
                "  1. FPGA     Version : 015\n" +
                "  2. DSP      Version : 221\n" +
                "  \n" +
                "Slot 15 : uptime is 1041 days, 4 hours, 34 minutes\n" +
                "         StartupTime   2048/01/01   11:12:58 \n" +
                "  1. FPGA     Version : 015\n" +
                "  2. DSP      Version : 221\n" +
                "\n" +
                "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
                "FAN version information:\n" +
                "\n" +
                "FAN18's MonitorBUS version information:\n" +
                "  PCB      Version : CR52FCBH REV A\n" +
                "  Software Version :  1.13\n" +
                "\n" +
                "FAN19's MonitorBUS version information:\n" +
                "  PCB      Version : CR52FCBH REV A\n" +
                "  Software Version :  1.13\n" +
                "                                          \n" +
                "<HS-YangCunZ-PE-RT-HW.NE40EX8>";

        String string = MyUtils.trimString(device.trim());
        String[] return_word = string.split("\r\n| ");
        String routerFlag = "NE";
        String[] flagSplit = routerFlag.toUpperCase().split(";");
        for (int number = 0 ; number < return_word.length; number++){
            for (String flag:flagSplit){
                if (return_word[number].startsWith(flag)){
                    if (MyUtils.isNumeric(return_word[number])){
                        System.err.println(return_word[number]);
                        break;
                    }
                }
            }
        }
    }
}
