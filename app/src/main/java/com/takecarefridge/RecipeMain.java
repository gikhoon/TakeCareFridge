package com.takecarefridge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/*class listIngredient{
    String[] Data;
    int count;

    listIngredient (int count){
        this.count = count;
    }
}*/

public class RecipeMain extends AppCompatActivity {

    RecyclerView mRecipeList;
    RecipeListAdapter mRecipeListAdapter;
    String[] listIngredient;
    int count = 0;
    ArrayList<RecipeData> recipeDataList = new ArrayList<>();
    ArrayList<RecipeData> search_list = new ArrayList<>();
    EditText editText;
    String searchIngredient;
    boolean searchIngredientCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_main);
        editText = findViewById(R.id.rm_editText);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent getIntent = getIntent();
        searchIngredientCheck = getIntent.getBooleanExtra("searchIngredientCheck",false);

        db.collection("레시피")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    count += 1;
                                }
                            }
                            listIngredient = new String[count];
                        }
                    }
                });
        db.collection("레시피")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    StringBuffer listIngredientBuffer = new StringBuffer();
                                    String imagePath = document.getString("이미지");
                                    String link = document.getString("링크");
                                    String name = document.getId();

                                    FirebaseFirestore db2 = FirebaseFirestore.getInstance();

                                    db2.collection("레시피").document(name).collection("재료목록").
                                            get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        int i = 0;
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            if (document.exists()) {
                                                                String li = document.getId();
                                                                listIngredientBuffer.append(li);
                                                                listIngredientBuffer.append(", ");
                                                            }
                                                        }
                                                        int length = listIngredientBuffer.length();
                                                        listIngredientBuffer.delete(length - 2, length);
                                                        putString(i,listIngredientBuffer);
                                                        RecipeData rd = new RecipeData(name, imagePath, listIngredient[i], link);
                                                        recipeDataList.add(rd);
                                                        i++;
                                                        setAdapter();
                                                        mRecipeListAdapter.setOnItemClickListener(new RecipeListAdapter.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(int pos) {
                                                                String link = recipeDataList.get(pos).getLink();
                                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                                startActivity(intent);
                                                            }
                                                        });

                                                        mRecipeListAdapter.setOnLongItemClickListener(new RecipeListAdapter.OnLongItemClickListener() {
                                                            @Override
                                                            public void onLongItemClick(int pos) {
                                                                String link = recipeDataList.get(pos).getLink();
                                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        if(searchIngredientCheck) {
                                                            searchIngredient = getIntent.getStringExtra("searchIngredient");
                                                            editText.setText(searchIngredient);
                                                            String searchText = editText.getText().toString();
                                                            search_list.clear();
                                                            if (searchIngredientCheck) {
                                                                searchText = searchIngredient;
                                                            }
                                                            for (int a = 0; a < recipeDataList.size(); a++) {
                                                                if ((recipeDataList.get(a).name.toLowerCase().contains(searchText.toLowerCase())) ||
                                                                        (recipeDataList.get(a).listIngredient.toLowerCase().contains(searchText.toLowerCase()))) {
                                                                    search_list.add(recipeDataList.get(a));
                                                                }
                                                                mRecipeListAdapter.setItems(search_list);
                                                                Log.d("11111", "11111");
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
        //엔터키로 키보드 내리기
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        search();
    }

    @Override
    protected void onStart() {
        super.onStart();
        search();
    }

    @Override
    protected void onResume() {
        super.onResume();
        search();
    }

    public void search(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editText.getText().toString();
                search_list.clear();
                if (searchText.equals("")) {
                    mRecipeListAdapter.setItems(recipeDataList);
                } else {
                    // 검색 단어를 포함하는지 확인
                    for (int a = 0; a < recipeDataList.size(); a++) {
                        if ((recipeDataList.get(a).name.toLowerCase().contains(searchText.toLowerCase())) ||
                                (recipeDataList.get(a).listIngredient.toLowerCase().contains(searchText.toLowerCase()))) {
                            search_list.add(recipeDataList.get(a));
                        }
                        mRecipeListAdapter.setItems(search_list);
                    }
                }
            }

        });
    }

    public void putString(int i, StringBuffer buffer){
        listIngredient[i] = buffer.toString();
    }

    public void setAdapter(){
        mRecipeList = findViewById(R.id.rv_recipeListRecyclerView);
        mRecipeListAdapter = new RecipeListAdapter(recipeDataList);

        mRecipeList.setAdapter(mRecipeListAdapter);
        mRecipeList.setLayoutManager(new LinearLayoutManager(RecipeMain.this));
    }

    public void goMainActivity(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void goAddRecipeActivity(View v){
        startActivity(new Intent(this, AddRecipe.class));
    }
}