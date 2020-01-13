package com.example.waygdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

enum Type{ NAVER, GOOGLE }

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    /*naver login field*/
    public static OAuthLogin mOAuthLoginModule;
    public static Context mContext;
    OAuthLoginButton authLoginButton;
    /*Google login field*/
    SignInButton Google_Login;
    public static GoogleSignInClient mGoogleSignInClient;
    /*
     * naver login handler

     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                final String accessToken = mOAuthLoginModule.getAccessToken(mContext);  //user info
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);
                /*
                 * mOauthAT.setText(accessToken);
                 * mOauthRT.setText(refreshToken);
                 * mOauthExpires.setText(String.valueOf(expiresAt));
                 * mOauthTokenType.setText(tokenType);
                 * mOAuthState.setText(mOAuthLoginModule.getState(mContext).toString());
                 */
                new Thread() {
                    public void run() {
                        ProfileTask task = new ProfileTask();
                        task.execute(accessToken);
                    }
                }.start();
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

    };/* naver login handler finish*/
    //mOAuthLoginModule.startOauthLoginActivity(mContext,mOAuthLoginHandler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        naverLogin();
    }


    /*naver login event*////////////////////////////////////////////////////////////////////////
    private void naverLogin(){
        authLoginButton = findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                LoginActivity.this
                , "QfYIDj80Yf8wtYt_kcJl"
                , "1bv72Ez8BL"
                , "Client Name"
                //,OAUTH_CALLBACK_INTENT

        );
        if (mOAuthLoginModule.getAccessToken(this) != null) {
            final String accessToken = mOAuthLoginModule.getAccessToken(mContext);
            new Thread() {
                public void run() {
                    ProfileTask task = new ProfileTask();
                    task.execute(accessToken);
                }
            }.start();
        } else {
            authLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
        }/*naver login event finish*////////////////////////////////////////////////////////////////

    }


    /*get naver user's info*/
    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// naver login access token
            String header = "Bearer " + token;
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // success
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // err
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(result);
                if (object.getString("resultcode").equals("00")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    Toast.makeText(LoginActivity.this,"!!!!!!!!!!!",Toast.LENGTH_SHORT).show();
                   // Move_on_MapActivity(jsonObject.getString("name"), Type.NAVER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }/*ProfileTask class finish*/
}
