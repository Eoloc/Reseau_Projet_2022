package fr.ul.miage.Application_Reseau;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@javax.websocket.ClientEndpoint
public class ClientEndpoint {
    private static Session ses;
    private static String receipt;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println ("--- Connected " + session.getId());
        session.getBasicRemote().sendText("CONNECT\n" +
                "accept-version:1.2\n" +
                "host:127.0.0.1:9999\n" +
                "^@");
        ses = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        System.out.println (message);
        receipt = message;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("--- Session: " + session.getId());
        System.out.println("--- Closing because: " + closeReason);
    }

    public static Session getSes() {
        return ses;
    }

    public static String getReceipt() {
        return receipt;
    }
}
