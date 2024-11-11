package com.sgcc.share.webSocket;
import lombok.Data;

import javax.websocket.Session;
import java.time.LocalDateTime;

/**
 * 客户端实体类
 *
 * @author qf
 * @since 2024/08/29 19:50
 */
@Data
public class ClientInfoEntity {

    /**
     * 客户端唯一标识
     */
    private String token;
    /**
     * 客户端连接的session
     */
    private Session session;
    /**
     * 连接存活时间
     */
    private LocalDateTime existTime;
}
