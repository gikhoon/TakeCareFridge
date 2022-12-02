package com.takecarefridge;

import static java.lang.System.in;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddRecipe extends AppCompatActivity {
    EditText editText_name;
    EditText editText_ingredient;
    EditText editText_link;
    Button button_setImage;
    Button button_uploadRecipe;
    Uri imageUri;
    ImageView imageView;
    ProgressBar progressBar;
    String name;
    String ingredient;
    String link;
    String storageImagePath;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        editText_name = findViewById(R.id.name_editText);
        editText_ingredient = findViewById(R.id.ingredient_editText);
        editText_link = findViewById(R.id.link_editText);
        button_setImage = findViewById(R.id.setImage_button);
        button_uploadRecipe = findViewById(R.id.uploadRecipe);
        progressBar = findViewById(R.id.progress_View);
        imageView = findViewById(R.id.ar_imageView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //프로그래스바 숨기기
        progressBar.setVisibility(View.INVISIBLE);

        button_setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        button_uploadRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("tag",imageUri.toString());
                //선택한 이미지가 있다면
                if (imageUri != null) {
                    name = editText_name.getText().toString();
                    ingredient = editText_ingredient.getText().toString();
                    link = editText_link.getText().toString();
                    storageImagePath = new String("레시피/"+name+".png");
                    insertData(name, ingredient, link, storageImagePath);
                    uploadToStorage(imageUri);
                    goRecipeMain(view);
                } else {
                    Log.e("tag",imageUri.toString());
                    Toast.makeText(AddRecipe.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*editText_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_name.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        editText_ingredient.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_ingredient.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });*/
        editText_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_link.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        editText_ingredient.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_link.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        editText_link.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText_link.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
    }
    private void insertData(String name, String ingredient, String 링크, String 이미지){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Model model = new Model(이미지, 링크);
        HashMap<String,String> map = new HashMap<String,String>(){{//초기값 지정
            put("링크",링크);
            put("이미지",이미지);
        }};
        String ingredientList[] = ingredient.split(", ");

        db.collection("레시피").document(name).set(map);
        for (int i = 0; i < ingredientList.length; i++) {
            재료 재료 = new 재료(ingredientList[i]);
            db.collection("레시피").document(name).collection("재료목록").document(ingredientList[i]).set(재료);
        }
    }

    //사진 가져오기
    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        imageView = findViewById(R.id.ar_imageView);
                        imageView.setImageURI(imageUri);
                        /*try {
                            // 선택한 이미지에서 비트맵 생성
                            InputStream in = getContentResolver().openInputStream(imageUri);
                            Bitmap img = BitmapFactory.decodeStream(in);
                            in.close();
                            imageView.setImageBitmap(img);
                            // 이미지뷰에 세팅
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            });

    //파이어베이스 이미지 업로드
    private void uploadToStorage(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = reference.child("레시피/").child(name + "." + "png");

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //프로그래스바 숨김
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(AddRecipe.this, "업로드 성공", Toast.LENGTH_SHORT).show();

                        imageView.setImageResource(R.drawable.background_bar);
                    }
                });

            }
        });
    }
    //파일타입 가져오기
    private String getFileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void goRecipeMain(View v){
        startActivity(new Intent(this, RecipeMain.class));
    }
}
