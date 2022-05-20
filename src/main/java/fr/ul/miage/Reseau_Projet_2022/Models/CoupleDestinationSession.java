package fr.ul.miage.Reseau_Projet_2022.Models;

import javax.websocket.Session;

public class CoupleDestinationSession {
    private Session session;
    private String destination;

    public CoupleDestinationSession(Session s, String d){
        session = s;
        destination = d;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
