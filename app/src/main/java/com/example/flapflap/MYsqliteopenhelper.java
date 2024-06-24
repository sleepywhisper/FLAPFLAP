package com.example.flapflap;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flapflap.javabean.User;

import java.io.ByteArrayOutputStream;

public class MYsqliteopenhelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "flapflap";
    private static final int DATABASE_VERSION = 6;

    private static final String create_users
            = "create table users(" +"id Integer UNIQUE,"+
            "name TEXT," +  "password TEXT)";
    public MYsqliteopenhelper(@Nullable Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(create_users);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        onCreate(sqLiteDatabase);
    }

    public void register(String name, String password){
        SQLiteDatabase db = getWritableDatabase();
        Cursor users = db.query("users", new String[]{"id"},
                "id = ?", new String[]{"1"},null,null,null);

        ContentValues contentValues = new ContentValues();
        contentValues.put("id",1);
        contentValues.put("name",name);
        contentValues.put("password",password);

        if(users!=null && users.moveToFirst()){
            db.update("users", contentValues,"id = 1",null);
        }else {
            db.insert("users", null, contentValues);
        }
        users.close();


    }

    public void delete(){
        SQLiteDatabase db = getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = { "1" };
        Cursor users = db.query("users", new String[]{"id"},
                "id = ?", new String[]{"1"},null,null,null);

        if(users!=null && users.moveToFirst()){
            db.delete("users", selection, selectionArgs);
        }
    }

    boolean login(String name, String password){
        SQLiteDatabase db = getWritableDatabase();
        boolean result = false;
        Cursor users = db.query("users",null,
                "name like ?",new String[]{name},null,null,null);

        if(users!=null){
            while(users.moveToNext()){
                String password1 = users.getString(2);
                result=password1.equals(password);
            }
        }
        return result;
    }

    public byte[] drawableToByteArray(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap loadImageFromDatabase(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT avatar FROM users WHERE name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name});

        Bitmap bitmap = null;
        if (cursor.moveToFirst()) {
            byte[] data = cursor.getBlob(0);
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        cursor.close();
        db.close();

        return bitmap;
    }

    String getName(){
        SQLiteDatabase db = getWritableDatabase();
        String res = null;
        Cursor users = db.query("users", null,
                "id = ?", new String[]{"1"},null,null,null);

        if(users!=null){
            while(users.moveToNext()){
                res = users.getString(1);
            }
        }
        return res;
    }

    String getPassword(){
        SQLiteDatabase db = getWritableDatabase();
        String res = null;
        Cursor users = db.query("users", null,
                "id = ?", new String[]{"1"},null,null,null);

        if(users!=null){
            while(users.moveToNext()){
                res = users.getString(2);
            }
        }
        return res;
    }
}
