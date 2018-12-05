package com.example.evgenii.myapp2;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import es.dmoral.toasty.Toasty;

public class BlogCreateActivity extends AppCompatActivity  {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Cursor userCursor;
    private static String article_title, article_text, article_author;

    final int TYPE_PHOTO = 1;
    final int REQUEST_CODE_PHOTO = 1;

    final String TAG = "myLogs";


    ImageView photoArticle;// ImageView, содержащий изображение, которое нужно сохранить
    private int article_id = MainActivity.countLine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoArticle = findViewById(R.id.photoArticle);



        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_SHORT;

        // создаем обработчик нажатия
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                startActivityForResult(intent, REQUEST_CODE_PHOTO);

            }
        });



        Button blogCreate = (Button) findViewById(R.id.blogCreate);




        mDBHelper = new DatabaseHelper(this);


        View.OnClickListener oclBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.blogCreate:


                        try {
                            mDb = mDBHelper.getWritableDatabase();
                        } catch (SQLException mSQLException) {
                            throw mSQLException;
                        }


                        EditText titleArticle = (EditText) findViewById(R.id.titleArticle);
                        EditText textArticle = (EditText) findViewById(R.id.textArticle);
                        EditText authorArticle = (EditText) findViewById(R.id.authorArticle);
                        ImageView photoArticle = (ImageView) findViewById(R.id.photoArticle);
                        // Создать переменную для фото
                        // Получаем данные из EditText
                        article_title = titleArticle.getText().toString();
                        article_text = textArticle.getText().toString();
                        article_author = authorArticle.getText().toString();
                        userCursor =  mDb.rawQuery(Querys.query_article_title, null);

                        // Переменная для увеличения счетчика статьи в БД
                        article_id = userCursor.getCount();
                        article_id++;

                        String image = Environment.getExternalStorageDirectory().toString()+ "/Blog_photo_" + article_id + ".jpg";

                        // Контейнер для данных
                        ContentValues cv = new ContentValues();
                        cv.put("article_id", article_id);
                        cv.put("article_title", article_title);
                        cv.put("article_text", article_text);
                        cv.put("article_author", article_author);
                        cv.put("article_likes", 0);
                        cv.put("imageToURL", image);

                        // вставка данных в БД
                      mDb.insert(DatabaseHelper.TABLE_ARTICLE, null, cv);





                      /*//Вспомогательный запрос для удаления статей, можно доделать в дальнейшем
                        mDb.execSQL(Querys.query_delete_article);*/

                        Log.d(TAG, "Данные, добавляемые в БД: Заголовок статьи" + article_title + "Текст статьи" + article_text + "Автор статьи" + authorArticle);




                        try {
                            mDBHelper.updateDataBase();
                        } catch (IOException mIOException) {
                            throw new Error("UnableToUpdateDatabase");
                        }

                        try {
                            mDb = mDBHelper.getWritableDatabase();
                        } catch (SQLException mSQLException) {
                            throw mSQLException;
                        }

                        MainActivity.countLine++;
                        Toasty.success(getApplicationContext(), "Статья успешно добавлена!", Toast.LENGTH_SHORT, true).show();




                }
            }

        };
        blogCreate.setOnClickListener(oclBtn);




    }




    //Получаем изображение
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if (bndl != null) {
                        Object obj = intent.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            photoArticle.setImageBitmap(bitmap);

                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }


    }
    private Uri generateFileUri(int type) {
        article_id++;
        File file = null;

        String folderToSave = Environment.getExternalStorageDirectory().toString();
                file = new File(folderToSave, "Blog_photo_"+ article_id + ".jpg");



        Log.d(TAG, "folderToSave" + folderToSave + "fileName = " + file);
        return Uri.fromFile(file);
    }




}



