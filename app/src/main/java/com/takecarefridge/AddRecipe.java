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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
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
    String name;
    String ingredient;
    String link;
    String storageImagePath;

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
        imageView = findViewById(R.id.ar_imageView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

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
                name = editText_name.getText().toString();
                ingredient = editText_ingredient.getText().toString();
                link = editText_link.getText().toString();
                //???????????? ?????? ???????????????
                if ((imageUri != null)&&(name.length()>1)&&(ingredient.length()>1)&&(link.length()>1)) {
                    storageImagePath = new String("?????????/"+name+".png");
                    insertData(name, ingredient, link, storageImagePath);
                    uploadToStorage(imageUri);
                    goRecipeMain(view);
                } else {
                        Toast.makeText(AddRecipe.this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private void insertData(String name, String ingredient, String ??????, String ?????????){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Model model = new Model(?????????, ??????);
        HashMap<String,String> map = new HashMap<String,String>(){{//????????? ??????
            put("??????",??????);
            put("?????????",?????????);
        }};
        String ingredientList[] = ingredient.split(", ");

        db.collection("?????????").document(name).set(map);
        for (int i = 0; i < ingredientList.length; i++) {
            ?????? ?????? = new ??????(ingredientList[i]);
            db.collection("?????????").document(name).collection("????????????").document(ingredientList[i]).set(??????);
        }
    }

    //?????? ????????????
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
                            // ????????? ??????????????? ????????? ??????
                            InputStream in = getContentResolver().openInputStream(imageUri);
                            Bitmap img = BitmapFactory.decodeStream(in);
                            in.close();
                            imageView.setImageBitmap(img);
                            // ??????????????? ??????
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            });

    //?????????????????? ????????? ?????????
    private void uploadToStorage(Uri uri) {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = reference.child("?????????/").child(name + "." + "png");

        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(AddRecipe.this, "????????? ??????", Toast.LENGTH_SHORT).show();

                        imageView.setImageResource(R.drawable.background_bar);
                    }
                });

            }
        });
    }
    //???????????? ????????????
    private String getFileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void goRecipeMain(View v){
        startActivity(new Intent(this, RecipeMain.class));
    }
}
