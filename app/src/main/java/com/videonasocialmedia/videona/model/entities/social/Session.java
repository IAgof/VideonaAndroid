package com.videonasocialmedia.videona.model.entities.social;


import org.scribe.model.Token;

/**
 * Created by jca on 6/3/15.
 */
public class Session {

    private static Session INSTANCE;

    /**
     * The user of the app
     */
    private final User user;

    /**
     * Oauth authorization accessToken
     */
    private Token authToken;

    /**
     * User password. Used to log in our services. MUST NOT be persisted
     */
    private String password; //TODO comprobar si es necesario tener la contraseña

    /**
     * @hide
     */
    private Session(){user= new User("anonymous");}

    /**
     *
     * @return instance of UserSession
     */
    public static Session getInstance(){
        if (INSTANCE==null){
            INSTANCE=new Session();
        }
        return INSTANCE;
    }

    public Token getAuthToken() {
        return authToken;
    }

    public void setAuthToken(Token authToken) {
        this.authToken = authToken;
    }

    public boolean hasToken() {return authToken!=null;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }
}
