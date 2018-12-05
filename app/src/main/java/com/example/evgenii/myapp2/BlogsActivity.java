package com.example.evgenii.myapp2;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlogsActivity extends ListActivity {


    private static final String TAG = "myLogs" ;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

/*    // объявим переменные для работы с БД
    static  String article_id= "article_id";
    static  String article_title = "article_title ";
    static  String article_text = "article_text";*/


    // определяем массив типа String
    public static String articlesId [] = new String[MainActivity.countLine];
    private String articlesTitle1 [] = new String[MainActivity.countLine];
    private String articlesContent1 [] = new String[MainActivity.countLine];
    private String articlesAuthor [] = new String[MainActivity.countLine];
    private String articlesLikes [] = new String[MainActivity.countLine];
    private String imageToURL [] = new String[MainActivity.countLine];



    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArticleAdapter(this);
        setListAdapter(mAdapter);

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


        //Запрос, выводящий все статьи
        userCursor =  mDb.rawQuery(Querys.query_article_title , null);



        if (userCursor.moveToFirst()) {
            int i=0;
            do {
                Log.d(TAG, "Col1:" + userCursor.getInt(0) + "\nCol2:" + userCursor.getString(1)+ "\n:Col3" + userCursor.getString(2) + userCursor.getString(3 ) + userCursor.getString(4) +"URL" + userCursor.getString(5));
                articlesId[i] = userCursor.getString(0);// кладем полученные данные в массив, после чего выводим данный массив в ListActivity
                articlesTitle1[i] = userCursor.getString(1);
                articlesContent1[i] = userCursor.getString(2);
                articlesAuthor[i] = userCursor.getString(3);
                articlesLikes[i] = userCursor.getString(4);
                imageToURL[i] = userCursor.getString(5);
                i++;

            }
            while (userCursor.moveToNext());

        }
        Log.d(TAG, "ID" + articlesId + "Заголовок:" + articlesTitle1 + "Контент:" + articlesContent1 + "Автор" + articlesAuthor + "Лайки" +articlesLikes + "URL" + imageToURL);
        userCursor.close();

        //ArrayReverse.reverse(articlesId);
       // ArrayReverse.reverse(articlesTitle1);
       // ArrayReverse.reverse(articlesContent1);
       // ArrayReverse.reverse(articlesAuthor);
       // ArrayReverse.reverse(articlesLikes);
       // ArrayReverse.reverse(imageToURL);


        }








    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Не забыть раскоментить
/*        String selection = mAdapter.getString(position);
        Toast.makeText(this, selection, Toast.LENGTH_LONG).show();*/


        Intent intent = new Intent(BlogsActivity.this, ArticleContent.class);
        intent.putExtra("ArticlesId", articlesId[position]);
        intent.putExtra("Title", articlesTitle1[position]);
        intent.putExtra("Content", articlesContent1[position]);
        intent.putExtra("Author", articlesAuthor[position]);
        intent.putExtra("Likes", articlesLikes[position]);
        intent.putExtra("imageToURL", imageToURL[position]);

        startActivity(intent);
    }



    private class ArticleAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        ArticleAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return articlesTitle1.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mLayoutInflater.inflate(R.layout.activity_blogs, null);



            TextView articleTitle = (TextView) convertView.findViewById(R.id.articleTitle);
            articleTitle.setText(articlesTitle1[position]);

            TextView articleContent = (TextView) convertView.findViewById(R.id.articleContent);
            articleContent.setText(articlesContent1[position]);

            TextView articleAuthor = (TextView) convertView.findViewById(R.id.articleAuthor);
            articleAuthor.setText("Автор статьи:" + articlesAuthor[position]);

            TextView articleLikes = (TextView) convertView.findViewById(R.id.articleLikes);
            articleLikes.setText("Мне нравится:" + articlesLikes[position]);




            return convertView;
        }

        String getString(int position) {
            return articlesTitle1[position] + " (" + articlesContent1[position] + ")";
        }
    }

}