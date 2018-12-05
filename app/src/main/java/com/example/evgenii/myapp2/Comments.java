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

public class Comments extends ListActivity {


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
    private static String userId [] = new String[MainActivity.countLine1];
    private String comment [] = new String[MainActivity.countLine1];
    public String comment_id [] = new String[MainActivity.countLine1];
    public String artId [] = new String[MainActivity.countLine1];




    private Comments.ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new Comments.ArticleAdapter(this);
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

        Intent intent = getIntent();

        //ArticleContent.articlesId = intent.getStringExtra("ArticlesId");

        userCursor =  mDb.rawQuery("SELECT * FROM comment WHERE article_id = ?", new String[] {ArticleContent.articlesId});



        if (userCursor.moveToFirst()) {
            int i=0;
            do {
                Log.d(TAG, "Номер комментария:" + userCursor.getInt(0) + "\nКомментарий:" + userCursor.getString(1)+ "\nНомер статьи:" + userCursor.getString(2));
                comment_id[i] = userCursor.getString(0);
                comment[i] = userCursor.getString(1);
                artId[i] = userCursor.getString(1);

                i++;

            }
            while (userCursor.moveToNext());

        }

        userCursor.close();




    }
    private class ArticleAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        ArticleAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return comment.length;
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
                convertView = mLayoutInflater.inflate(R.layout.activity_comments, null);



            TextView userID = (TextView) convertView.findViewById(R.id.userID);
           // userID.setText(userId[position]);

            TextView c1 = (TextView) convertView.findViewById(R.id.articleComment);
            c1.setText(comment[position]);




            return convertView;
        }

        String getString(int position) {
            return userId[position] + " (" + comment[position] + ")";
        }
    }

}
