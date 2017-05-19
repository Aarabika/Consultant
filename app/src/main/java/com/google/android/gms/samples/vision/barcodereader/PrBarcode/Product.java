package com.google.android.gms.samples.vision.barcodereader.PrBarcode;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.samples.vision.barcodereader.DataBase.DatabaseHelper;
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.ProductsInfo;

import java.util.ArrayList;


public class Product  {
    private static final String TAG = "ProductGetting";
    // поля, для харектеристики товара
    private  String characters = null;// названия характеристик
    private   String typeBD = null;// тип товара(для базы данных)
    private   String type = null;// тип товара
    private  String partNumber = null;// id товара
    // поля для расшифрофки табличных данных
    private static final String available = "+";//если подразумевался булеан true
    private static final String unavailable = "-";//если подразумевался булеан false
    private static final String unset = "Нет данных";//если подразумевался null

    private ArrayList<String> CharactersName = new ArrayList<>();// поле хранения названия характеристик
    private ArrayList<String> Characters = new ArrayList<>();// поле хранения характеристик

    private int charactersCount = 0;//поле для хранения числа характеристик
    private boolean status = false;// поле для хранения статуса баркода


    public String getType (){
        return type;
    }
    public ArrayList<String> getCharactersName (){
        return CharactersName;
    }
    public ArrayList<String> getCharacters (){
        return Characters;
    }
    public int getCharactersCount (){
        return charactersCount;
    }
    public boolean getStatus (){
        return status;
    }

    /** доступ к базе данных
     * @see  DatabaseHelper
     * **/
    private DatabaseHelper db;


    /**
     * <p> Создает объект класса Product, класса DatabaseHelper
     * </p>
     * @param brText текстовое значение разбивается на <var>characters</var>, <var>type</var>,<var>partNumber</var>
     * @param context  текущие настройки приложения
      *                 @see Context
     * **/
    public Product (String brText,Context context){
        String[] words = brText.split(" ");
        db = new DatabaseHelper(context,1);
        Log.d(TAG, "barcode: "+ brText);
        if (words.length == 3) {
            characters = words[0];
            typeBD = words[1];
            partNumber = words[2];
            checkData();
        }
    }


    /**
     * <p> Создает объект класса Product, класса DatabaseHelper
     * </p>
     * <p>Используется для создания объекта, полученного в Parcel</p>
     * @see ProductsInfo
     * **/
    public Product (ArrayList<String> CharactersName,ArrayList<String> Characters,String type){
        this.CharactersName = CharactersName;
        this.Characters = Characters;
        this.type = type;
        status = true;
        charactersCount = CharactersName.size();
    }


    /**
     * <p> Проверка значений полей <var>characters</var>, <var>type</var>,<var>partNumber</var> на валидность
     * результат идет в поле{@link status()}
     * </p>
     * **/
    private void checkData() {
        if(!characters.trim().equals("")&&!typeBD.trim().equals("")&&!partNumber.trim().equals("")) {
            String sql = "SELECT * FROM sqlite_master WHERE type = 'table' and tbl_name = " + "'" + characters + "'";
            Cursor cursor = db.getReadableDatabase()
                    .rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                cursor.close();
                sql = "SELECT name FROM 'sqlite_sequence' WHERE name = '" + typeBD + "'";
                cursor = db.getReadableDatabase()
                        .rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    cursor.close();
                    sql = "SELECT _id FROM '" + typeBD + "' where _id = " + partNumber;
                    cursor = db.getReadableDatabase()
                            .rawQuery(sql, null);
                    if (cursor.moveToFirst()) {
                        Log.d(TAG, "Barcode verified successful");
                        cursor.close();
                        status = true;
                    }
                }
            }
        }

    }


    /**
     * <p> Получение названия характеристик  <var>CharactersName</var> и значений <var>Characters</var>
     * </p>
     *<p>Используется проверка {@link status()}на статус объекта <var>status</var>, который определяется в методе
     *                                                                                          @see #checkData()
     * </p>
     * **/
    public void getProduct(){
        if (status) {
            try {
                Cursor product = db.getAll(characters);
                setCharactersColumns(product);
                product = db.getProduct(typeBD, partNumber);
                setItemCharacters(product);
                prettifyCharacters();
            } catch (Error e) {
                Log.e(TAG, "Invalid barcode format ", e);
            }
        }
        else{
            Log.e(TAG, "Invalid barcode or barcode did not verified");
        }
    }


    /**
     * <p>Метод заполняет список с названиями характреристик <var>CharactersName</var>, и определяет их число <var>charactersCount</var>
     * </p>
     * @param db объект класса <class>Cursor</class> с данными из таблицы названия характеристик
     *           используется в методе
     *           @see #getProduct
     * **/
    private void setCharactersColumns(Cursor db) {
        if (db.moveToFirst()) {

            type = db.getString(1);
            db.moveToNext();

            do {
                CharactersName.add(db.getString(1));

                charactersCount++;
            } while (db.moveToNext());
        }
    }


    /**
     * <p>Метод заполняет список с характреристиками <var>Characters</var>
     * </p>
     * @param db объект класса <class>Cursor</class> с данными из таблицы характеристик
     *           используется в методе
     *           @see #getProduct
     * **/
    private void setItemCharacters(Cursor db) {
        db.moveToFirst();
        for (int count=1;count<=charactersCount;count++) {
            Characters.add(db.getString(count));
        }
    }


    /**
     * <p>Метод обрабатывает данные из списока с характреристиками <var>Characters</var> и валидирует их
     * </p>
     *           используется в методе
     *           @see #getProduct
     * **/
    private void prettifyCharacters() {
        for (int i = 0; i < Characters.size(); i++) {
            switch (Characters.get(i)) {
                case "1":
                    Characters.set(i, available);
                    break;
                case "0":
                    Characters.set(i, unavailable);
                    break;
                case "null":
                    Characters.set(i, unset);
                    break;
                case "1*":
                    Characters.set(i, "1");
                    break;
                case "0*":
                    Characters.set(i, "0");
                    break;
            }
        }
    }
}

