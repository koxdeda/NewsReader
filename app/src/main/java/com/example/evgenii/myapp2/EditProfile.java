package com.example.evgenii.myapp2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class EditProfile extends Activity {


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Cursor userCursor;

    private Button buttonBack, buttonClear, buttonSave;
    private EditText EditText1,EditText2,EditText3,EditText4,EditText6,EditText7;
    private static String name1, first_name, last_name, age, foto, about, interests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonSave = (Button) findViewById(R.id.buttonSave);




        mDBHelper = new DatabaseHelper(this);

        EditText1 = (EditText) findViewById(R.id.EditText1);
        EditText2 = (EditText) findViewById(R.id.EditText2);
        EditText3 = (EditText) findViewById(R.id.EditText3);
        EditText4 = (EditText) findViewById(R.id.EditText4);
        EditText6 =(EditText)  findViewById(R.id.EditText6);
        EditText7 =(EditText)  findViewById(R.id.EditText7);


        View.OnClickListener oclBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.buttonSave:
                        try {
                            mDb = mDBHelper.getWritableDatabase();
                        } catch (SQLException mSQLException) {
                            throw mSQLException;
                        }



                        // Получаем значения полей
                        name1 = EditText1.getText().toString();
                        first_name = EditText2.getText().toString();
                        last_name = EditText3.getText().toString();
                        age = EditText4.getText().toString();
                        about = EditText6.getText().toString();
                        interests = EditText7.getText().toString();

                        //Кладем данные в контейнер, для записи в БД
                        ContentValues cv = new ContentValues();
                        cv.put("user_id", MainActivity.USER_ID);
                        cv.put("first_name", first_name);
                        cv.put("last_name", last_name);
                        cv.put("age", age);
                        cv.put("about", about);
                        cv.put("interests", interests);
                        String where = "user_id=?";
                        String[] whereArgs = new String[] {String.valueOf(MainActivity.USER_ID)};

                        Log.d(TAG, MainActivity.USER_ID + " " + first_name + "  " + last_name + " " + age + " " + about + " " + interests );


                        //Запрос, реализующий обновление таблицы user
                        mDb.update(DatabaseHelper.TABLE_USER, cv, where, whereArgs);




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


                        Toasty.success(getApplicationContext(), "Изменения сохранены!", Toast.LENGTH_SHORT, true).show();
                        onBackPressed();
                        break;


                    case R.id.buttonClear:
                        Toasty.info(getApplicationContext(), "Для внесения изменений заполните поля", Toast.LENGTH_SHORT, true).show();
                        EditText1.getText().clear();
                        EditText2.getText().clear();
                        EditText3.getText().clear();
                        EditText4.getText().clear();
                        EditText6.getText().clear();
                        EditText7.getText().clear();
                        break;


                    case R.id.buttonBack:
                        onBackPressed();
                        break;

                }
            }

        };
        buttonSave.setOnClickListener(oclBtn);
        buttonClear.setOnClickListener(oclBtn);
        buttonBack.setOnClickListener(oclBtn);
    }




}

