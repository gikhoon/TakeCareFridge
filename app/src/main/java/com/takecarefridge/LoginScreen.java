package com.takecarefridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class LoginScreen extends AppCompatActivity {

    //로그인
    Button mLoginBtn;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    //구글 로그인
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String TAG="mainTag";
    private int RC_SIGN_IN=123;
    private EditText editTextEmail;
    private EditText editTextpassword;
    String ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        editTextEmail = (EditText)findViewById(R.id.loginId);
        editTextpassword = (EditText)findViewById(R.id.loginPassword);
        mLoginBtn = findViewById(R.id.loginButton);

        //로그인을 하였는데 홈화면에서 로그인 화면으로 돌아갈 떄
        if(firebaseAuth.getCurrentUser() != null){
           /* DocumentReference doRef = db.collection("사용자")
                    .document();
            doRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            ID = document.getString("name");
                        }
                    }
                }
            });*/
            Intent intent= new Intent(getApplicationContext(), MainActivity.class); //getApplicationContext(): 어플리케이션의 life cycle
            Toast.makeText(LoginScreen.this, "환영합니다", Toast.LENGTH_SHORT).show();
            /* intent.putExtra("ID", )*/
            startActivity(intent);
        }


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String pwd =editTextpassword.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(LoginScreen.this, MainActivity.class));
                                    Toast.makeText(LoginScreen.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(LoginScreen.this, "로그인 오류 발생", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });




        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        Button googleLogin= (Button)findViewById(R.id.btn_signup);
        //가입 버튼이 눌리면
        googleLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(LoginScreen.this, Signup.class);
                startActivity(intent);
            }
        });
        //아래 주석처리 부분들은 구글 로그인을 사용할 시에 필요한 코드들이다.
        /*editTextEmail = (EditText)findViewById(R.id.loginId);
        editTextpassword = (EditText)findViewById(R.id.loginPassword);
        Button emailLogin = (Button) findViewById(R.id.loginButton);
        emailLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                createUser(editTextEmail.getText().toString(), editTextpassword.getText().toString());
            }
        });*/
    }

    /*private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(LoginScreen.this, MainActivity.class));
                            Toast.makeText(LoginScreen.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginScreen.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]



    // [START auth_with_google]

    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        Toast.makeText(this,"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }


    // 회원탈퇴
    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
    }
}