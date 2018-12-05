package com.example.evgenii.myapp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import es.dmoral.toasty.Toasty;

public class ArticleContent extends AppCompatActivity {

    private static final String TAG = "a" ;
    private TextView mTextMessage;
    private ImageView imageView;
    AlertDialog.Builder ad;
    Context context;
    Dialog dialog;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Cursor userCursor;
    public static String articlesId;
    private URI myURI;

    private int GALLERY_REQUEST = 1;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_comment:

                    ad.show();


                    return true;
                case R.id.navigation_like:
                    Toasty.success(getApplicationContext(), "Success!", Toast.LENGTH_SHORT, true).show();


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


                    String [] where_id = new String[]{String.valueOf(articlesId)};
                    //String articlesId1 = articlesId.toString();
                    // получаем количество лайков у текущей статьи
                    userCursor =  mDb.rawQuery("SELECT * FROM article WHERE article_id = ?", new String[] {articlesId});
                    userCursor.moveToFirst();
                    int likes = userCursor.getInt(4);
                    // увеличиваем счетчик лайков
                    likes++;

                    ContentValues cv = new ContentValues();
                    cv.put("article_id", userCursor.getString(0));
                    cv.put("article_title", userCursor.getString(1));
                    cv.put("article_text", userCursor.getString(2));
                    cv.put("article_author", userCursor.getString(3));
                    cv.put("article_likes", likes);
                    String where = "article_id=?";
                    String[] whereArgs = new String[] {String.valueOf(userCursor.getString(0))};

                    // обновляем данные в таблице
                    mDb.update(DatabaseHelper.TABLE_ARTICLE, cv, where, whereArgs);


                    return true;
                case R.id.navigation_back:
                    onBackPressed();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_content);

        mDBHelper = new DatabaseHelper(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView5 = (TextView) findViewById(R.id.textView5);
        TextView textView2 = (TextView) findViewById(R.id.articleAuthor);
        TextView textView3 = (TextView) findViewById(R.id.articleLikes);
        ImageView articleImage = (ImageView) findViewById(R.id.articleImage);
        Button buttonComment = (Button) findViewById(R.id.buttonComment);

        View.OnClickListener oclbtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.buttonComment:

                        Intent intent = new Intent(ArticleContent.this, Comments.class);
                        startActivity(intent);

                        break;
                }
            }
        };
        buttonComment.setOnClickListener(oclbtn);



        Intent intent = getIntent();

        articlesId = intent.getStringExtra("ArticlesId");
        String Title = intent.getStringExtra("Title");
        String Content = intent.getStringExtra("Content");
        String Author = intent.getStringExtra("Author");
        String Likes = intent.getStringExtra("Likes");
        String imageToURL = intent.getStringExtra("imageToURL");


        Log.d(TAG, "ИД артикля активного:" + articlesId + "URL изображения" + imageToURL);

        textView.setText(Title);
        textView5.setText(Content);
        textView2.setText("Автор статьи:" + Author);
        textView3.setText("Мне нравится:" + Likes);


        new DownloadImageTask((ImageView) findViewById(R.id.articleImage))
                .execute(imageToURL);

/*        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);*/

/*        Bitmap bitmap = null;
        String filename = Environment.getExternalStorageDirectory().toString()+ "/Blog_photo_" + articlesId + ".jpg";

        Uri uri = Uri.parse(imageToURL);
        Log.d(TAG, "Имя файла:" + filename + "__Uri:" + uri );


        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        articleImage.setImageBitmap(bitmap);*/



/*        //String filename ="3.jpg";
        InputStream inputStream = null;
        try{
            inputStream = getApplicationContext().getAssets().open(filename);
            Drawable d = Drawable.createFromStream(inputStream, null);
            articleImage.setImageDrawable(d);
            articleImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(inputStream!=null)
                    inputStream.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }*/

        mDBHelper = new DatabaseHelper(this);


        context = ArticleContent.this;
        String title = "Оставьте ваш комментарий";
        String buttonOk = "Отправить";
        String buttonCancel = "Отмена";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        final EditText input = new EditText(ArticleContent.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        ad.setView(input);
        ad.setPositiveButton(buttonOk, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Спасибо за ваш комментарий",
                        Toast.LENGTH_LONG).show();
                String comment = input.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put("comment_id", MainActivity.countLine1);
                cv.put("comment_text", comment);
                cv.put("article_id", articlesId);

                try {
                    mDb = mDBHelper.getWritableDatabase();
                } catch (SQLException mSQLException) {
                    throw mSQLException;
                }

                mDb.insert(DatabaseHelper.TABLE_COMMENT, null, cv);
                MainActivity.countLine1++;
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



            }
        });
        ad.setNegativeButton(buttonCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                ad.setCancelable(true);
            }
        });


        };




}






