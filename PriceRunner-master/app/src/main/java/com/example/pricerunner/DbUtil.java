package com.example.pricerunner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbUtil extends SQLiteOpenHelper {
    private String product = "Product";
    private String productId = "id";
    private String productBarcode = "barcode";
    private String productName = "name";
    private String productPrice = "price";
    private String productMarketName = "marketName";

    public DbUtil(@Nullable Context context) {
        super(context, "MyDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" CREATE TABLE " + product + "(" +
                productId + " TEXT PRIMARY KEY," +
                productBarcode + " TEXT," +
                productName + " TEXT," +
                productPrice + " REAL," +
                productMarketName + " Text)");

        ContentValues value1 = new ContentValues();
        value1.put(productId, "1");
        value1.put(productBarcode, "8690637921100");
        value1.put(productName, "Mayonez");
        value1.put(productPrice, 8.5);
        value1.put(productMarketName, "Bim");
        sqLiteDatabase.insert(product, null, value1);
        ContentValues value2 = new ContentValues();
        value2.put(productId, "2");
        value2.put(productBarcode, "8690637921100");
        value2.put(productName, "Mayonez");
        value2.put(productPrice, 9.5);
        value2.put(productMarketName, "Şok");
        sqLiteDatabase.insert(product, null, value2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + product);
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public List<String> findProduct(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + product + " WHERE " + productBarcode + " = " + barcode;
        Cursor cursor = db.rawQuery(query, null);
        List<String> list = new ArrayList<>();
        while(cursor.moveToNext()) {
            list.add("Ürün Adı: " + cursor.getString(cursor.getColumnIndex(productName)) + " Market: " + cursor.getString(cursor.getColumnIndex(productMarketName)) + " Fiyat: " + cursor.getDouble(cursor.getColumnIndex(productPrice)));
        }
        return list;
    }
}
