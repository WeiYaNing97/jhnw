package com.sgcc.web.controller.sql;

import com.sgcc.web.controller.webSocket.WebSocketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        for (int number=0;number<=5;number++){
            try {
                //睡眠1s
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /*WebSocketService.sendMessage("badao","WebSocket成功:loophole"+number);*/
            WebSocketService webSocketService = new WebSocketService();
            webSocketService.sendMessage("badao","WebSocket成功:badao"+number+"\r\n");
            webSocketService.onClose();
            WebSocketService webSocketService1 = new WebSocketService();
            webSocketService1.sendMessage("basicinformation","WebSocket成功:basicinformation"+number+"\r\n");
            webSocketService1.onClose();
            WebSocketService webSocketService2 = new WebSocketService();
            webSocketService2.sendMessage("loophole","WebSocket成功:loophole"+number+"\r\n");
            webSocketService2.onClose();
        }
    }
}