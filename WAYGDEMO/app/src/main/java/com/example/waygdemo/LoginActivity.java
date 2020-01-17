package com.example.waygdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

enum Type{ NAVER, GOOGLE }

public class LoginActivity extends AppCompatActivity {

    public static String email;

    private static final int RC_SIGN_IN = 100;
    /*naver login field*/
    public static OAuthLogin mOAuthLoginModule;
    public static Context mContext;
    OAuthLoginButton authLoginButton;
    /*Google login field*/
    SignInButton Google_Login;
    public static GoogleSignInClient mGoogleSignInClient;
    /*Firestore access field*/
    FirebaseFirestore db;
    private static final String TAG = "DocSnippets";
    /* naver login handler */
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
        googleLogin();
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
        }
    }
    /*naver login event finish*////////////////////////////////////////////////////////////////////

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
                    email = jsonObject.getString("email");
                    Move_on_MapActivity(jsonObject.getString("name"), Type.NAVER);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }/*ProfileTask class finish*/

    /*Google login event*///////////////////////////////////////////////////////////////////////
    private void googleLogin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
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
        });
    }/*Google login event fin*//////////////////////////////////////////////////////////////////////

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
            email = account.getEmail();
            Move_on_MapActivity(name, Type.GOOGLE);
        } catch (ApiException e) {
        }
    }/*fin*/
    /*Google login functions*///////////////////////////////////////////////////////////////////////

    /*move on MainActivity -> MapActivity*/
    private void Move_on_MapActivity(String name, Type t) {
        if(email != NULL)
                checkId();
        else LoginActivity.this.finish();
        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("type", t.toString());
        startActivity(intent);
    }

    /* check id's existence in server*/
    public void checkId() {
        db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("id", email).get()   // find id in server by global variable email
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {                          // if task which executed Query is empty??
                                Toast myToast = Toast.makeText(LoginActivity.this, "아이디 생성", Toast.LENGTH_SHORT);
                                myToast.show();
                                Map<String, Object> map2 = new HashMap<>();
                                map2.put("gradenum", 0);
                                map2.put("gradesum", 0);
                                map2.put("id", email);
                                db.collection("Users").document(email)  // store id and initialize data because it was first login
                                        .set(map2)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot written with ID: ");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {           //print text if there are data in server by email
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Toast myToast = Toast.makeText(LoginActivity.this, "아이디 이미 있음", Toast.LENGTH_SHORT);
                                    myToast.show();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        LoginActivity.this.finish();
    }

}
