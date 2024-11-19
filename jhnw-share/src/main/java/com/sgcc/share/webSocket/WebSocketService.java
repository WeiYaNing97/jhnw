package com.sgcc.share.webSocket;

import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/{userName}",encoders = { ServerEncoder.class })
@Component
public class WebSocketService {
    public static Map<String,String> userMap = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, WebSocketClient> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userName*/
    private String userName="";
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {

        // 将当前会话赋值给类的成员变量session
        this.session = session;
        // 将用户名赋值给类的成员变量userName
        this.userName= userName;
        // 创建一个WebSocketClient对象
        WebSocketClient client = new WebSocketClient();
        // 设置WebSocketClient对象的会话
        client.setSession(session);
        // 设置WebSocketClient对象的URI
        client.setUri(session.getRequestURI().toString());
        // 将用户名和WebSocketClient对象添加到WebSocket连接映射中
        webSocketMap.put(userName, client);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 如果webSocketMap中包含当前用户的用户名
        if(webSocketMap.containsKey(userName)){
            // 从webSocketMap中移除当前用户的WebSocket连接
            webSocketMap.remove(userName);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(Session session,String message) {
        if (message.equals("ping")){
            try {
                session.getBasicRemote().sendText("pong");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (message.equals("接收结束")){
            Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
            List<String> stringList = requestParameterMap.get("userName");
            userMap.put(stringList.get(0),"接收结束");
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {

        /*error.printStackTrace();*/

        AbnormalAlarmInformationMethod.afferent(null,
                getTokenBySession(session),
                "websocket",
                error.getMessage()+"\r\n");
    }

    /**
     * 向指定用户名的WebSocket客户端发送消息
     *
     * @param userName 用户名，用于在WebSocket连接映射中查找对应的WebSocketClient对象
     * @param message  要发送的消息内容
     */
    public void sendMessage(String userName,String message){
        try {
            // 从WebSocket连接映射中获取指定用户名的WebSocketClient对象
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if (webSocketClient != null) {
                // 对WebSocketClient对象进行同步，确保线程安全（如果WebSocketClient内部不是线程安全的）
                synchronized (webSocketClient) {
                    // 发送文本消息给指定的客户端
                    Session session = webSocketClient.getSession();
                    if (session != null && session.isOpen()) {
                        session.getBasicRemote().sendText(message);
                    }else {
                        // 移除已经关闭的连接
                        webSocketMap.remove(userName);
                    }
                }
            }
        } catch (IOException e) {
            // 捕获并打印异常信息，可以选择更合适的日志记录方式
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器成功后主动推送消息
     *
     * @param string 要推送的消息内容
     * @throws IOException 如果在发送消息过程中发生I/O异常
     */
    public void sendMessage(String string) throws IOException{//, EncodeException {
        synchronized (session){
            this.session.getBasicRemote().sendText(string);
        }
    }

    /**
     * 向指定客户端发送消息
     *
     * @param userName 用户名，用于在WebSocket连接映射中查找对应的WebSocketClient对象
     * @param object   要发送的消息对象，支持的对象类型由WebSocket实现决定
     */

    public void sendMessage(String userName,Object object){
        try {
            // 根据用户名从WebSocket连接映射中获取对应的WebSocketClient对象
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if (webSocketClient != null) {
                Session session = webSocketClient.getSession();
                if (session != null && session.isOpen()) {
                    RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
                    if (basicRemote != null) {
                        // 对WebSocketClient对象进行同步，确保线程安全
                        synchronized (webSocketClient) {
                            // 发送消息对象给指定的客户端
                            basicRemote.sendObject(object);
                        }
                    }
                }else {
                    // 移除已经关闭的连接
                    webSocketMap.remove(userName);
                }
            }
        } catch (IOException | EncodeException e) {
            // 捕获并打印异常信息，可以选择更合适的日志记录方式
            e.printStackTrace();
        }
    }

    public void sendMessageAll(String string) {
        try {
            // 获取webSocketMap中的所有WebSocketClient对象
            Collection<WebSocketClient> values = webSocketMap.values();
            if (values == null || values.size() == 0){
                return;
            }
            // 遍历所有的WebSocketClient对象
            for (WebSocketClient value:values){
                // 发送文本消息给每个客户端
                Session session1 = value.getSession();
                if (session1 != null && session1.isOpen()){
                    session1.getBasicRemote().sendText(string);
                }else {
                    // 移除已经关闭的连接
                    webSocketMap.remove(userName);
                }
            }
        } catch (IOException e) {
            // 捕获并打印异常信息
            e.printStackTrace();
        }
    }


    public static String getTokenBySession(Session session) {
        Set<Map.Entry<String, WebSocketClient>> entries = webSocketMap.entrySet();
        for (Map.Entry<String, WebSocketClient> entry : entries) {
            WebSocketClient webSocketClient = entry.getValue();
            Session session1 = webSocketClient.getSession();
            if (session1.equals(session)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
