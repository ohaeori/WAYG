package com.example.n_login_test;

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

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    /*naver login field*/
    public static OAuthLogin mOAuthLoginModule;
    public static Context mContext;
    OAuthLoginButton authLoginButton;
    /*Google login field*/
    SignInButton Google_Login;
    GoogleSignInClient mGoogleSignInClient;
    /*
     * naver login handler
     * OAuthLoginHandler를 startOAuthLoginActivity() 메서드 호출 시 파라미터로 전달하거나 OAuthLoginButton
     객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다.
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

        /*naver login event*////////////////////////////////////////////////////////////////////////
        authLoginButton = findViewById(R.id.buttonOAuthLoginImg);
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

        /*Google login event*///////////////////////////////////////////////////////////////////////
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        Google_Login = findViewById(R.id.Google_Login);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {//already google login
            String name = account.getDisplayName();
            Move_on_MapActivity(name, Type.GOOGLE);
        }
        Google_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });/*Google login event fin*////////////////////////////////////////////////////////////////
    }

    /*Google login functions*///////////////////////////////////////////////////////////////////////
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /*Google login handler*/
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String name = account.getDisplayName();
            Move_on_MapActivity(name, Type.GOOGLE);
        } catch (ApiException e) {
        }
    }/*fin*/
    /*Google login functions*///////////////////////////////////////////////////////////////////////

    /*move on MainActivity -> MapActivity*/
    private void Move_on_MapActivity(String name, Type t) {
        MainActivity.this.finish();
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("type", t.toString());
        startActivity(intent);
    }

    /*네이버 로그인한 유저의 정보를 가져오는 ProfileTask class*/
    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;

        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
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
                    Move_on_MapActivity(jsonObject.getString("name"), Type.NAVER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }/*ProfileTask class finish*/
}
