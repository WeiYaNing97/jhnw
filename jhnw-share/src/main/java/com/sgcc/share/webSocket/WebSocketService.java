package com.sgcc.share.webSocket;
import cn.hutool.core.util.ObjectUtil;
import com.sgcc.share.method.AbnormalAlarmInformationMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 该类负责监听客户端的连接、断开连接、接收消息、发送消息等操作。
 *
 * @author qf
 * @since 2024/08/29 19:50
 */
@Slf4j
@Component
@CrossOrigin(origins = "*")
@ServerEndpoint(value = "/websocket/{userName}", configurator = GetHttpSessionConfig.class)
public class WebSocketService {

    public static Map<String,String> userMap = new HashMap<>();


    //key：客户端连接唯一标识(token)
    //value：ClientInfoEntity
    private static final Map<String, ClientInfoEntity> uavWebSocketInfoMap = new ConcurrentHashMap<String, ClientInfoEntity>();

    private static final int EXIST_TIME_HOUR = 6;

    /**
     * 连接建立成功调用的方法
     *
     * @param session 第一个参数必须是session
     * @param sec
     * @param token   代表客户端的唯一标识
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig sec, @PathParam("userName") String token) {
        if (uavWebSocketInfoMap.containsKey(token)) {
            //throw new ServiceException("token已建立连接");
            // 如果token已存在，不抛出异常，而是关闭新连接或做其他处理
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Token already connected"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        //把成功建立连接的会话在实体类中保存
        ClientInfoEntity entity = new ClientInfoEntity();
        entity.setToken(token);
        entity.setSession(session);
        //默认连接6个小时
        entity.setExistTime(LocalDateTime.now().plusHours(EXIST_TIME_HOUR));
        uavWebSocketInfoMap.put(token, entity);
        //之所以获取http session 是为了获取获取httpsession中的数据 (用户名 /账号/信息)
        log.info("WebSocket 连接建立成功: " + token);
    }

    /**
     * 当断开连接时调用该方法
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session, @PathParam("userName") String token) {
        // 找到关闭会话对应的用户 ID 并从 uavWebSocketInfoMap 中移除
        if (ObjectUtil.isNotEmpty(token) && uavWebSocketInfoMap.containsKey(token)) {
            ClientInfoEntity clientInfoEntity = uavWebSocketInfoMap.get(token);

            try {
                clientInfoEntity.getSession().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            uavWebSocketInfoMap.remove(token);
            log.info("WebSocket 连接关闭成功: " + token);
        }
        // 执行清理工作
        log.info("Session " + session.getId() + " closed because of " + token);
    }

    /**
     * 接受消息
     * 这是接收和处理来自用户的消息的地方。我们需要在这里处理消息逻辑，可能包括广播消息给所有连接的用户。
     *
     */
    @OnMessage
    public void onMessage(Session session, @PathParam("userName") String token, String message) throws IOException {
        log.info("接收到消息：" + message);

        ClientInfoEntity entity = uavWebSocketInfoMap.get(token);
        //如果是心跳包
        if("heartbeat".equals(message)){
            //只要接受到客户端的消息就进行续命(时间)
            entity.setExistTime(LocalDateTime.now().plusHours(EXIST_TIME_HOUR));
            if (entity.getSession().isOpen()) {
                entity.getSession().getBasicRemote().sendText("{\"msg\": \"success\", \"code\": 0}");
            }
            return;
        }

        // 心跳包 ping pong
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

        //业务逻辑
        //只要接受到客户端的消息就进行续命(时间)
        entity.setExistTime(LocalDateTime.now().plusHours(EXIST_TIME_HOUR));
        try {
            if (entity.getSession().isOpen()) {
                entity.getSession().getBasicRemote().sendText("{\"msg\": \"success\", \"code\": 0}");
            }
        } catch (IOException e) {
            // 记录异常信息
            log.error("Failed to send message to client", e);
            // 可以在此处添加更多的异常处理逻辑
            System.err.println("Failed to send message to client: " + e.getMessage());
        }
    }

    /**
     * 处理WebSocket中发生的任何异常。可以记录这些错误或尝试恢复。
     */
    @OnError
    public void onError(Throwable error, Session session) {

        if (error.getMessage().toString().contains("java.io.IOException:")
            || error.getMessage().toString().contains("Unable to wrap data, invalid status [CLOSED]")) {
            System.err.println("进来了" + "java.io.IOException: Unable to wrap data, invalid status [CLOSED]");
            log.error("进来了" + "java.io.IOException: Unable to wrap data, invalid status [CLOSED]");

            /**
             * 将交换机异常告警信息插入到数据库并发送WebSocket消息
             *
             * @param ip          交换机IP
             * @param name        系统登录用户名
             * @param categories  问题名
             * @param information 问题参数
             * @throws IOException 写入文件时发生异常
             */
            AbnormalAlarmInformationMethod.afferent(null,
                    getTokenBySession(session),
                    "问题日志",
                    error.getMessage());
        }else {
            // 记录异常信息
            log.error("报错信息：" + error.getMessage());
            error.printStackTrace();
        }

    }

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");

    /**
     * 发生消息定时器
     */
    @PostConstruct
    @Scheduled(cron = "0/1 * *  * * ? ")
    public void refreshDate() {
        //开启定时任务，1秒一次向前台发送当前时间
        //当没有客户端连接时阻塞等待
        if (!uavWebSocketInfoMap.isEmpty()) {
            //超过存活时间进行删除
            Iterator<Map.Entry<String, ClientInfoEntity>> iterator = uavWebSocketInfoMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ClientInfoEntity> entry = iterator.next();
                if (entry.getValue().getExistTime().compareTo(LocalDateTime.now()) <= 0) {
                    log.info("WebSocket " + entry.getKey() + " 已到存活时间，自动断开连接");
                    try {
                        entry.getValue().getSession().close();
                    } catch (IOException e) {
                        log.error("WebSocket 连接关闭失败: " + entry.getKey() + " - " + e.getMessage());
                    }
                    //过期则进行移除
                    iterator.remove();
                }
            }
            sendMessageAll(FORMAT.format(new Date()));
        }
    }

    /**
     * 群发信息的方法
     *
     * @param message 消息
     */
    public void sendMessageAll(String message) {
        /*System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                + "发送全体消息:" + message);*/
        //循环客户端map发送消息
        uavWebSocketInfoMap.values().forEach(item -> {
            //向每个用户发送文本信息。这里getAsyncRemote()解释一下，向用户发送文本信息有两种方式，
            // 一种是getBasicRemote，一种是getAsyncRemote
            //区别：getAsyncRemote是异步的，不会阻塞，而getBasicRemote是同步的，会阻塞，由于同步特性，第二行的消息必须等待第一行的发送完成才能进行。
            // 而第一行的剩余部分消息要等第二行发送完才能继续发送，所以在第二行会抛出IllegalStateException异常。所以如果要使用getBasicRemote()同步发送消息
            // 则避免尽量一次发送全部消息，使用部分消息来发送，可以看到下面sendMessageToTarget方法内就用的getBasicRemote，因为这个方法是根据用户id来私发的，所以不是全部一起发送。
            Session session = item.getSession();
            if (session.isOpen()){
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    /**
     * 单发信息的方法
     *
     * @param message 消息
     */
    public void sendMessage(String token,String message) {
        try {
            ClientInfoEntity clientInfoEntity = uavWebSocketInfoMap.get(token);
            if (clientInfoEntity != null) {
                Session session = clientInfoEntity.getSession();
                if (session.isOpen()){
                    session.getAsyncRemote().sendText(message);
                }else {
                    uavWebSocketInfoMap.remove(token);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error sending message to client: " + token, e);
        }
    }

    /**
     * 单发信息的方法
     *
     * @param message 消息
     */
    public void sendMessage(String token,Object message) {
        try {
            ClientInfoEntity clientInfoEntity = uavWebSocketInfoMap.get(token);
            if (clientInfoEntity != null) {
                Session session = clientInfoEntity.getSession();
                if (session.isOpen()){
                    session.getAsyncRemote().sendObject(message);
                }else {
                    uavWebSocketInfoMap.remove(token);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected error sending message to client: " + token, e);
        }
    }

    public static String getTokenBySession(Session session) {
        Collection<ClientInfoEntity> values = uavWebSocketInfoMap.values();
        for (ClientInfoEntity clientInfoEntity : values) {
            if (clientInfoEntity.getSession().equals(session)) {
                System.out.println("token:" + clientInfoEntity.getToken());
                return clientInfoEntity.getToken();
            }
        }
        return null;
    }
}

