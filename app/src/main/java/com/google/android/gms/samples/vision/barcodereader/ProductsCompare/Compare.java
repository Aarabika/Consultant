package com.google.android.gms.samples.vision.barcodereader.ProductsCompare;

import android.util.Log;
import android.util.SparseArray;


import com.google.android.gms.samples.vision.barcodereader.PrBarcode.Product;

import java.util.ArrayList;


public class Compare {
    private static final String TAG = "ProductCompare";
    // данные для общей таблицы
    private SparseArray<ArrayList<String>> allCharacters = new SparseArray<>();
    // названия характеристик
    private ArrayList<String> charactersName =  new ArrayList<>();
    // Число характеристик
    private int charactersCount;
    // Число товаров в текущем сравнении
    private int inCompareNow = 0;
    // Тип товаров в текущем сравнении
    private String compareType = "";

    public String getCompareType(){
        return compareType;
    }
    public int getInCompareNow() {
        return inCompareNow;
    }
    public ArrayList<String> getCharactersName() {
        return charactersName;
    }
    public SparseArray<ArrayList<String>> getAllCharacters() {
        return allCharacters;
    }


    /**
     * <p> Создает объект класса Product, класса DatabaseHelper
     * </p>
     * @param product Названия характеристик необходимые поля: <var>CharactersName,charactersCount,type</var>
     *                @see Product
     * **/
    public Compare(Product product){
        charactersName = product.getCharactersName();
        charactersCount = product.getCharactersCount();
        compareType = product.getType();
    }


    /**
     * <p> Создает объект класса Product, класса DatabaseHelper
     * </p>
     * <p>Используется для создания объекта, полученного в Parcel</p>
     * @see ProductsInfo
     * **/
    public Compare(ArrayList<String> charactersName,SparseArray<ArrayList<String>> allCharacters,
                   int charactersCount,int inCompareNow,String compareType)
    {
        this.charactersName = charactersName;
        this.allCharacters = allCharacters;
        this.charactersCount = charactersCount;
        this.inCompareNow = inCompareNow;
        this.compareType = compareType;
    }



    /** добавляет продукт (с помощью характеристик) к сравнению
     * @param Characters Значения характеристик
     * **/
    public void addToCompare(ArrayList<String> Characters){
        try {
            inCompareNow++;
            for (int count = 0; count < charactersCount; count++) {


                ArrayList<String>  currentAllCharacters = allCharacters.get(count);

                if (currentAllCharacters == null) {
                    currentAllCharacters = new ArrayList<>();
                }
                currentAllCharacters.add(Characters.get(count));

                allCharacters.put(count, currentAllCharacters);
            }
        }
        catch (Exception e)
            {
                Log.e(TAG, "Invalid Characters",e);
            }
    }

}

