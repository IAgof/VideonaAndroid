package com.videonasocialmedia.videona.domain.social;

import android.os.Looper;
import android.util.Log;

import com.videonasocialmedia.videona.model.entities.social.Session;
import com.videonasocialmedia.videona.model.entities.social.User;
import com.videonasocialmedia.videona.model.sources.VideonaRestSource;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnLoginFinishedListener;
import com.videonasocialmedia.videona.utils.Constants;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.VideonaTwoLeggedApi;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import retrofit.RetrofitError;

/**
 * Created by jca on 6/3/15.
 */
public class LoginUseCase {

    public enum ThirdPartyOauthServers {
        facebook, twitter, google
    }

    Session session;
    OAuthService oAuthService;

    public LoginUseCase() {
        this.session = Session.getInstance();
    }


    public void videonaLogin(final String userName, final String password, final OnLoginFinishedListener listener) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                boolean correct = obtainVideonaAccessToken(userName, password);
                if (correct)
                    correct = updateUser();

                if (correct) {
                    listener.onLoginSuccess();
                } else {
                    listener.onLoginCredentialsError();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();


    }

    public void thirdPartyLogin(OnLoginFinishedListener listener) {
        //TODO llamar al tercero que corresponda en cada caso
        boolean correct = obtainThirdPartyToken(ThirdPartyOauthServers.facebook);
        if (correct)
            correct = updateUser();

        if (correct) {
            listener.onLoginSuccess();
        } else {
            listener.onLoginCredentialsError();
        }
    }

    /**
     * Obtain a new access token and stores in the session object
     *
     * @param userName the name of the user of the app
     * @param password the password of the user
     */
    private boolean obtainVideonaAccessToken(String userName, String password) {
        boolean result;
        oAuthService = new ServiceBuilder()
                .provider(VideonaTwoLeggedApi.class)
                .apiKey(Constants.OAUTH_CLIENT_ID)
                .apiSecret(Constants.OAUTH_CLIENT_SECRET)
                .userID(userName)
                .userPassword(password)
                .build();
        try {
            Token accessToken = oAuthService.getAccessToken(null, null);
            Log.d("LoginUseCase", "TOKEN: " + accessToken.getToken());
            Session session = Session.getInstance();
            session.setAuthToken(accessToken);
            result = true;
        } catch (OAuthException error) {
            Log.d("LoginUseCase", "error al obtener el token", error);
            result = false;
        }
        return result;
    }

    private boolean obtainThirdPartyToken(ThirdPartyOauthServers server) {
        boolean result = false;
        return result;
    }


    private boolean updateUser() {
        boolean result;
        User sessionUser = session.getUser();
        VideonaRestSource videonaRestSource = VideonaRestSource.getInstance();
        //TODO tratar errores en la obtención de datos
        try {
            User user = videonaRestSource.getUser(10);
            //sessionUser.setName(user.getName());
//            sessionUser.setEmail(user.getEmail());
//            sessionUser.setAvatarPath(user.getAvatarPath());
//            sessionUser.setId(user.getId());
            result = true;
        } catch (RetrofitError error) {
            Log.d("LOGINUSECASE", "error while login", error);
            result = false;
        }
        return result;
    }


}
