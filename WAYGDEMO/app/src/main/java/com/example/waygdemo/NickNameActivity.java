package com.example.waygdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.waygdemo.LoginActivity.mContext;
import static com.example.waygdemo.LoginActivity.mGoogleSignInClient;
import static com.example.waygdemo.LoginActivity.mOAuthLoginModule;

public class NickNameActivity extends Activity {
    TextView user_id;
    TextView state_message;
    EditText nickname;
    Button create_button;
    LoginActivity loginActivity;
    String email;
    String type;
    private static final String TAG = "DocSnippets";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name);
        loginActivity = (LoginActivity)LoginActivity.loginActivity;

        user_id = (TextView)findViewById(R.id.activity_nickname_user_id);
        state_message = (TextView)findViewById(R.id.activity_nickname_state);
        nickname = (EditText)findViewById(R.id.activity_nickname_nickname);
        create_button = (Button)findViewById(R.id.activity_nickname_create_button);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        email = bundle.getString("email");
        type = bundle.getString("type");

        user_id.setText(email);

        create_button.setOnClickListener(mOnClickListener);

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(state_message.getText().toString().equals("사용 가능한 닉네임입니다.")){
                    create_button.setText("중복확인");
                    state_message.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(create_button.getText().toString().equals("생성")){
                /*save in database*/
                setUserInfo();
                /*move on next intent*/
                loginActivity.finish();
                NickNameActivity.this.finish();
                Intent intent = new Intent(NickNameActivity.this, MapActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("nickname", nickname.getText().toString());
                intent.putExtra("type",type);
                startActivity(intent);
            }
            else if(nickname.getText().toString().equals("")) {
                state_message.setTextColor(getResources().getColor(R.color.holo_red_light));
                state_message.setText("유효하지 않은 닉네임입니다.");
                return;
            }
            else if(nickname.getText().toString().equals("존재")){
                state_message.setTextColor(getResources().getColor(R.color.holo_red_light));
                state_message.setText("이미 존재하는 닉네임입니다.");
                return;
            }
            else{
                state_message.setTextColor(getResources().getColor(R.color.holo_green_light));
                state_message.setText("사용 가능한 닉네임입니다.");
                create_button.setText("생성");
            }
        }
    };

    /*back event*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (type.equals("NAVER")) mOAuthLoginModule.logout(mContext);
        else if (type.equals("GOOGLE")) mGoogleSignInClient.signOut();
    }
    public void setUserInfo()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(email);
        String nick = nickname.getText().toString();
        Map<String, Object> map2 = new HashMap<>();
        map2.put("gradenum", 0);
        map2.put("gradesum", 0);
        map2.put("id", email);
        map2.put("nickName",nick);
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
    }
}