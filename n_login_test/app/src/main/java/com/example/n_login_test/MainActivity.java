package com.example.n_login_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences userData;
    public static OAuthLogin mOAuthLoginModule;
    public static Context mContext;
    OAuthLoginButton authLoginButton;

    String name;
    String data;

    /*
     * naver login handler
     * OAuthLoginHandler를 startOAuthLoginActivity() 메서드 호출 시 파라미터로 전달하거나 OAuthLoginButton
     객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
     */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                //String accessToken = mOAuthLoginModule.getAccessToken(mContext);  //just in case
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);
//                mOauthAT.setText(accessToken);
//                mOauthRT.setText(refreshToken);
//                mOauthExpires.setText(String.valueOf(expiresAt));
//                mOauthTokenType.setText(tokenType);
//                mOAuthState.setText(mOAuthLoginModule.getState(mContext).toString());
                //new Thread() {
                    //public void run() {
                        String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                        //String data = mOAuthLoginModule.requestApi(mContext, accessToken, "https://openapi.naver.com/v1/nid/me");
                        Profile task = new Profile();
                        task.execute(accessToken);
                        try {
                            JSONObject result = new JSONObject(data);
                            System.out.println("data is...");
                            System.out.println(data);
                            System.out.println(result.toString());
                            name = result.getJSONObject("response").getString("name");
                        } catch (JSONException e) {

                        }
                    //}
                //}.start();
                /*Activity 전환*/
                finish();
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                //intent.putExtra("name", name);
                startActivity(intent);
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };
    //mOAuthLoginModule.startOauthLoginActivity(mContext,mOAuthLoginHandler);
    /*naver login handler*/

    private class Profile {
        public void execute(String... args) {
            String token = args[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                data = response.toString();
                br.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*naver login event*/
        authLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                MainActivity.this
                , "QfYIDj80Yf8wtYt_kcJl"
                , "1bv72Ez8BL"
                , "Client Name"
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );
        if (mOAuthLoginModule.getAccessToken(this) != null) {
            finish();
            startActivity(new Intent(this, MapActivity.class));
            //mOAuthLoginModule.logout(mContext);
        } else {
            authLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
        }/*naver login event*/
    }
}
