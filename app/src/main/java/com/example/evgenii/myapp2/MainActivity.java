package com.example.evgenii.myapp2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;

import static com.example.evgenii.myapp2.ArticleContent.articlesId;


public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "myLogs" ;
    TextView textView1, textView2, textView3,textView4,textView6,textView7;
    ImageView img;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    AsyncHttpTask get_news;

    // объявим переменные для работы с БД
    static  String USER_ID = "user_id";
    static  String FIRST_NAME = "first_name";
    static  String LAST_NAME = "last_name";
    static  String AGE = "age";
    static  String ABOUT = "about";
    static  String INFO = "interests";

    // Переменная для создание массива по количеству статей
    public static int countLine = 0;
    public static int countLine1 = 0;
    public static int countLine2 = 0;

    static final int GALLERY_REQUEST = 1;
    static String TAG = "ss";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:

                    return true;

                case R.id.navigation_edit_profile:
                    Intent intent2 = new Intent(MainActivity.this, EditProfile.class);
                    startActivity(intent2);
                    return true;

                case R.id.navigation_articles:
                    Intent intent = new Intent(MainActivity.this, BlogsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_create:
                    Intent intent3 = new Intent(MainActivity.this, BlogCreateActivity.class);
                    startActivity(intent3);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int duration = Toast.LENGTH_SHORT;

        img = (ImageView) findViewById(R.id.profile_image);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);


        View.OnClickListener oclbtn = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            switch (v.getId()) {

                    case R.id.profile_image:

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

/*                        Toast toast = Toast.makeText(getApplicationContext(),"Кликнули на имагу", duration);
                        toast.show();*/
                }
            }
        };
        img.setOnClickListener(oclbtn);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mDBHelper = new DatabaseHelper(this);

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


        /*Выполняем запрос, выводящий информацию о пользователе приложения*/

        userCursor =  mDb.rawQuery(Querys.query_users , null);
         userCursor.moveToLast();
        // определяем, какие столбцы из курсора будут выводиться в ListView
        USER_ID = userCursor.getString(userCursor.getColumnIndex(USER_ID));
        FIRST_NAME = userCursor.getString(userCursor.getColumnIndex(FIRST_NAME));
        LAST_NAME = userCursor.getString(userCursor.getColumnIndex(LAST_NAME));
        AGE = userCursor.getString(userCursor.getColumnIndex(AGE));
        ABOUT = userCursor.getString(userCursor.getColumnIndex(ABOUT));
        INFO = userCursor.getString(userCursor.getColumnIndex(INFO));

        userCursor.close();
        textView1.setText(USER_ID);
        textView2.setText( FIRST_NAME);
        textView3.setText( LAST_NAME);
        textView4.setText("Age: " + AGE);
        textView6.setText(ABOUT);
        textView7.setText( INFO);

        /*Конец запроса информации о пользователе*/



        /*Запрос, выводящий все статьи*/
        userCursor =  mDb.rawQuery(Querys.query_article_title , null);
        countLine = userCursor.getCount();
        userCursor.close();

        /*Конец запроса, выводящего статьи*/

        /*Запрос, выводящий все комментарии*/
        userCursor =  mDb.rawQuery(Querys.query_article_comments , null);
        countLine1 = userCursor.getCount();
        userCursor.close();

        /*Конец запроса, выводящего комментарии*/

        // Очистка БД от старых данных

        // Загрузка данных с NewsAPI

        String NEWS_API_KEY = "https://newsapi.org/v2/top-headlines?country=ru&apiKey=967800b5356e4e57942f8f6adf11f731";

        get_news = new AsyncHttpTask();
        get_news.execute(NEWS_API_KEY);

    }

    public class AsyncHttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();


                if (result != null) {

                    String response = streamToString(urlConnection.getInputStream());


                    parseResult(response);


                    return result;


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Download complete. Let us update UI
            if (result != null) {

                Toasty.success(getApplicationContext(), "Приложение готово к работе!", Toast.LENGTH_SHORT, true).show();

            } else {
                Toasty.error(getApplicationContext(), "Ошибка получения данных!", Toast.LENGTH_SHORT, true).show();
            }

        }
    }

        String streamToString(InputStream stream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            // Close stream
            if (null != stream) {
                stream.close();
            }
            return result;
        }

        private void parseResult(String result) {

            try {
                JSONObject response = new JSONObject(result);
                JSONArray articles = response.optJSONArray("articles");
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject post = articles.optJSONObject(i);
                    String article_title = post.optString("title");
                    String image = post.optString("urlToImage");
                    String article_text = post.optString("description");
                    String url = post.optString("url");
                    String article_author = post.optString("name");

                    try {
                        mDb = mDBHelper.getWritableDatabase();
                    } catch (SQLException mSQLException) {
                        throw mSQLException;
                    }

                    userCursor =  mDb.rawQuery(Querys.query_article_title, null);

                    // Переменная для увеличения счетчика статьи в БД
                    int article_id = userCursor.getCount();
                    article_id++;
                    userCursor.close();
                    userCursor =  mDb.rawQuery("SELECT * FROM article WHERE article_title = ?", new String[] {article_title});

                    if (!userCursor.moveToFirst()) {

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

                    }
                    userCursor.close();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }






























    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.profile_image);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d(TAG, "Uri изображения:" + selectedImage);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imageView.setImageBitmap(bitmap);
                }
        }
    }

   @Override
    public void onResume() {
        super.onResume();
        // Необходимо обновить данные, выводящиеся из базы данных

       mDBHelper = new DatabaseHelper(this);

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



    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        //mDb.execSQL(Querys.query_delete_article);
        mDb.close();
        userCursor.close();
    }




}
