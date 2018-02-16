package com.github.kayr.javarosa.autofill.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@WebSocket
public class EventsWebSocket {

    private static final AtomicLong           counter            = new AtomicLong();
    private static final Map<Session, String> sessionUserMap     = new ConcurrentHashMap<>();
    private static final Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();
    private static final Logger               LOG                = LoggerFactory.getLogger(EventsWebSocket.class);


    public EventsWebSocket() {
        Main.eventSocket = this;
    }

    @OnWebSocketConnect
    public void connected(Session session) throws IOException {
        String username = "User" + counter.incrementAndGet();

        sessionUserMap.put(session, username);
        usernameSessionMap.put(username, session);

        session.getRemote().sendString("::user:" + username);

        LOG.info("Logged In: " + username);


    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        String username = sessionUserMap.remove(session);
        if (username != null) {
            usernameSessionMap.remove(username);
            LOG.info("Logging out: " + username);
        }
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
    }

    public void log(String username, String message) {
        Session s = usernameSessionMap.get(username);
        try {
            s.getRemote().sendString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
