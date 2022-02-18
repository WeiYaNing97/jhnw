package com.sgcc.web.controller.sql;

import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 天幕顽主
 * @E-mail: WeiYaNing97@163.com
 * @date 2022年01月18日 14:27
 */
@RestController
@RequestMapping("/sql/websocket")
public class WebSocketController {
    @GetMapping("/pushone")
    public void pushone(){
        HashMap<String,String> has1 = new HashMap<>();
        has1.put("pro","密码是否明文存储");
        has1.put("yichang","正常");

        ArrayList list1 = new ArrayList();
        list1.add(has1);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("protype","安全配置");
        hashMap.put("childs", list1);

        ArrayList listA = new ArrayList();
        listA.add(hashMap);

        HashMap<String,Object> hasMapA= new HashMap<>();
        hasMapA.put("zhuji","192.168.1.1");
        hasMapA.put("childs",listA);

    //        HashMap<String,Object> hashMap1 = new HashMap<>();
    //        hashMap1.put("type","设备缺陷");
    //        hashMap1.put("childs",list1);

        ArrayList list = new ArrayList();
        list.add(hasMapA);
    //        list.add(hashMap1);
        System.err.println("test");
        System.out.println(list);
        WebSocketService.sendMessage("badao",list);
        ArrayList sss = new ArrayList();
        sss.add("asas");
        sss.add("asas");
        sss.add("asas");
        sss.add("asas");
        System.out.println(sss);
        WebSocketService.sendMessage("basicinformation",sss);
//        return list;
    }
}