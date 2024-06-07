package com.sgcc.share.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/{userName}",encoders = { ServerEncoder.class })
@Component
public class WebSocketService {
    public static Map<String,String> userMap = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
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
        // 判断用户名是否存在于WebSocket连接映射中
        if(!webSocketMap.containsKey(userName)) {
            // 如果不存在，则在线数 +1
            addOnlineCount(); // 在线数 +1
        }
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
        // 打印日志分隔线
        log.info("----------------------------------------------------------------------------");
        // 打印日志，显示用户连接信息和当前在线人数
        log.info("用户连接:"+userName+",当前在线人数为:" + getOnlineCount());
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

            // 从在线人数中减去一个
            //if(webSocketMap.size()>0) {
                // 从set中删除
                subOnlineCount();
            //}
        }

        // 打印日志，表示调用了关闭方法onClose()
        log.info("--------------------------------调用关闭方法 onClose()--------------------------------------------");
        // 打印日志，显示用户退出以及当前在线人数
        log.info(userName+"用户退出,当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户消息:"+userName+",报文:"+message);
        //可以群发消息
        //消息保存到数据库、redis
        /*if(StringUtils.isNotBlank(message)){
        }*/
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

        log.error("用户错误:"+this.userName+",原因:"+error.getMessage());
        error.printStackTrace();
    }

    /**
     * 连接服务器成功后主动推送
     */
    public void sendMessage(String string) throws IOException{//, EncodeException {
        synchronized (session){
            this.session.getBasicRemote().sendText(string);
        }
    }

    /**
     * 向指定客户端发送消息
     *
     * @param userName 用户名
     * @param object   要发送的消息对象
     */
    public static void sendMessage(String userName,Object object){
        try {
            // 根据用户名从WebSocket连接映射中获取对应的WebSocketClient对象
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if(webSocketClient!=null){
                // 对WebSocketClient对象进行同步，确保线程安全
                synchronized(webSocketClient) {
                    // 发送消息对象给指定的客户端
                    webSocketClient.getSession().getBasicRemote().sendObject(object);
                }
            }
        } catch (IOException | EncodeException e) {
            // 捕获并打印异常信息
            e.printStackTrace();
            // 将异常信息转换为RuntimeException并抛出
            throw new RuntimeException(e.getMessage());
        }
    }
    public static void sendMessage(String userName,String string){
        try {
            // 从WebSocket连接映射中获取指定用户名的WebSocketClient对象
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if(webSocketClient!=null){
                // 对WebSocketClient对象进行同步，确保线程安全
                synchronized(webSocketClient){
                    // 发送文本消息给指定的客户端
                    webSocketClient.getSession().getBasicRemote().sendText(string);
                }
            }
        } catch (IOException e) {
            // 捕获并打印异常信息
            e.printStackTrace();
            // 将异常信息转换为RuntimeException并抛出
            throw new RuntimeException(e.getMessage());
        }
    }
    public static void sendMessageAll(String string) {
        // 获取webSocketMap中的所有WebSocketClient对象
        Collection<WebSocketClient> values = webSocketMap.values();
        try {
            // 遍历所有的WebSocketClient对象
            for (WebSocketClient value:values){
                // 发送文本消息给每个客户端
                value.getSession().getBasicRemote().sendText(string);
            }
        } catch (IOException e) {
            // 捕获并打印异常信息
            e.printStackTrace();
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
    public static synchronized void addOnlineCount() {
        WebSocketService.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        WebSocketService.onlineCount--;
    }
    public static void setOnlineCount(int onlineCount) {
        WebSocketService.onlineCount = onlineCount;
    }
    public static ConcurrentHashMap<String, WebSocketClient> getWebSocketMap() {
        return webSocketMap;
    }
    public static void setWebSocketMap(ConcurrentHashMap<String, WebSocketClient> webSocketMap) {WebSocketService.webSocketMap = webSocketMap;}
    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
