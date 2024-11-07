package com.sgcc.share.webSocket;

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

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {

        // 创建一个WebSocketClient对象
        WebSocketClient client = new WebSocketClient();
        // 设置WebSocketClient对象的会话
        client.setSession(session);
        // 设置WebSocketClient对象的URI
        client.setUri(session.getRequestURI().toString());
        // 将用户名和WebSocketClient对象添加到WebSocket连接映射中
        webSocketMap.put(userName, client);

        // 启动心跳线程
        new Thread(() -> {
            while (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText("heartbeat");
                    Thread.sleep(30000); // 每30秒发送一次心跳
                } catch (IOException | InterruptedException e) {
                    //logger.error("Heartbeat failed: " + e.getMessage(), e);
                    break;
                }
            }
        }).start();

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {

        String userNameBySession = getUserNameBySession(session);

        // 如果webSocketMap中包含当前用户的用户名
        if(webSocketMap.containsKey(userNameBySession)){
            // 从webSocketMap中移除当前用户的WebSocket连接
            webSocketMap.remove(userNameBySession);
        }

    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {

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
        error.printStackTrace();

        String userNameBySession = getUserNameBySession(session);

        // 尝试恢复连接
        if (session != null && !session.isOpen()) {
            try {
                // 重新打开连接
                session.close();
                onOpen(session, userNameBySession );
                System.err.println("尝试重新打开连接: " + userNameBySession );
                log.info("尝试重新打开连接: " + userNameBySession );
            } catch (IOException e) {
                System.err.println("重新打开连接失败: " + userNameBySession + ",原因:" + e.getMessage());
                log.error("重新打开连接失败: " + userNameBySession + ",原因:" + e.getMessage());
                e.printStackTrace();
            }
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
            if (webSocketClient != null) {
                Session session = webSocketClient.getSession();
                if (session.isOpen() && session != null) {
                    RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
                    if (basicRemote != null) {
                        // 对WebSocketClient对象进行同步，确保线程安全
                        synchronized (webSocketClient) {
                            // 发送消息对象给指定的客户端
                            // 检查session状态是否允许发送消息
                            if (session.isOpen()) {
                                basicRemote.sendObject(object);
                            } else {
                                System.err.println("Session is not open for sending message to user: " + userName);
                            }
                        }
                    } else {
                        reconnectionSession(session,userName);
                        sendMessage(userName,object);

                        // todo 处理getBasicRemote()返回null的情况
                        System.err.println("Failed to get BasicRemote for user: " + userName);
                    }
                } else {
                    // todo  处理session为null或未打开的情况
                    System.err.println("Session is null or not open for user: " + userName);
                }
            } else {
                // todo  处理webSocketClient为null的情况
                System.err.println("No WebSocketClient found for user: " + userName);
            }
        } catch (IOException | EncodeException e) {
            // 捕获并打印异常信息，可以选择更合适的日志记录方式
            e.printStackTrace();
            // 根据异常类型进行更具体的异常处理，而不是直接抛出RuntimeException
            if (e instanceof EncodeException) {
                // todo  处理编码异常，例如记录更详细的日志或进行重试等操作
                System.err.println("EncodeException occurred while sending message to user: " + userName);
            } else {
                // todo  处理其他IOException异常，可以选择抛出自定义异常或进行其他操作
                System.err.println("IOException occurred while sending message to user: " + userName);
            }
            // 可以选择不抛出异常，而是记录错误并优雅地处理异常情况（根据具体需求决定）
            // throw new RuntimeException("Failed to send message to user: " + userName, e);
        }
    }
    public static void sendMessage(String userName,String message){
        try {
            // 从WebSocket连接映射中获取指定用户名的WebSocketClient对象
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if (webSocketClient != null) {
                // 对WebSocketClient对象进行同步，确保线程安全（如果WebSocketClient内部不是线程安全的）
                synchronized (webSocketClient) {
                    // 发送文本消息给指定的客户端
                    Session session = webSocketClient.getSession();
                    if (session.isOpen()  &&  session != null) {
                        session.getBasicRemote().sendText(message);
                    } else {
                        reconnectionSession(session,userName);
                        sendMessage(userName,message);
                        // todo 处理session为null或未打开的情况（可选）
                        System.err.println("Session is null or not open for user: " + userName);
                    }
                }
            } else {
                // todo  处理webSocketClient为null的情况（可选）
                System.err.println("No WebSocketClient found for user: " + userName);
            }
        } catch (IOException e) {
            // 捕获并打印异常信息，可以选择更合适的日志记录方式
            e.printStackTrace();
            // 可以考虑不抛出RuntimeException，而是记录错误并优雅地处理（可选）
            throw new RuntimeException("Failed to send message to user: " + userName, e);
        }
    }
    public static void sendMessageAll(String string) {
        // 获取webSocketMap中的所有WebSocketClient对象
        Collection<WebSocketClient> values = webSocketMap.values();
        try {
            // 遍历所有的WebSocketClient对象
            for (WebSocketClient value:values){
                if (value.getSession().isOpen()){
                    // 发送文本消息给每个客户端
                    value.getSession().getBasicRemote().sendText(string);
                }else {
                    // todo 处理session为null或未打开的情况（可选）
                    System.err.println("Session is null or not open for user: ");
                }
            }
        } catch (IOException e) {
            // 捕获并打印异常信息
            e.printStackTrace();
        }
    }

    private static void reconnectionSession(Session Websession, String userName){

        // 创建一个WebSocketClient对象
        WebSocketClient client = new WebSocketClient();
        // 设置WebSocketClient对象的会话
        client.setSession(Websession);
        // 设置WebSocketClient对象的URI
        client.setUri(Websession.getRequestURI().toString());
        // 将用户名和WebSocketClient对象添加到WebSocket连接映射中
        webSocketMap.put(userName, client);
        // 打印日志分隔线
        log.info("----------------------------------------------------------------------------");
        // 打印日志，显示用户连接信息和当前在线人数
        log.info("用户连接:"+userName+",当前在线人数为:" + webSocketMap.size());

        // 启动心跳线程
        new Thread(() -> {
            while (Websession.isOpen()) {
                try {
                    Websession.getBasicRemote().sendText("heartbeat");
                    Thread.sleep(30000); // 每30秒发送一次心跳
                } catch (IOException | InterruptedException e) {
                    //logger.error("Heartbeat failed: " + e.getMessage(), e);
                    break;
                }
            }
        }).start();
    }

    /**
     * 通过WebSocket会话获取用户名
     *
     * @param session WebSocket会话对象
     * @return 如果找到匹配的会话，则返回对应的用户名；否则返回null
     */
    public static String getUserNameBySession(Session session) {
        // 获取webSocketMap中的所有条目
        Set<Map.Entry<String, WebSocketClient>> entries = webSocketMap.entrySet();
        for (Map.Entry<String, WebSocketClient> entry : entries) {
            // 获取条目的键
            String key = entry.getKey();
            // 获取条目的值
            WebSocketClient value = entry.getValue();
            // 如果条目的值中的会话与传入的会话相同
            if (value.getSession().equals(session)){
                // 返回键作为用户名
                return key;
            }
        }
        // 如果没有找到匹配的会话，返回null
        return null;
    }

}
