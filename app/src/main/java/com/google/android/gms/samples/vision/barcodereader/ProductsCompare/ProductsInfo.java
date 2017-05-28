package com.google.android.gms.samples.vision.barcodereader.ProductsCompare;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import java.util.ArrayList;

// класс для отправки данных о продуктах между Activity
public class ProductsInfo implements Parcelable {

    //private static final String TAG = "ProductsCompareParcel";

    // данные для общей таблицы
    private SparseArray<ArrayList<String>> allCharacters = new SparseArray<>();
    // названия характеристик
    private ArrayList<String> charactersName =  new ArrayList<>();
    // Число характеристик
    private String compareType;

    /**
     * <p>Принимает основную информацию о товарах
     * </p>
     **/
    public ProductsInfo(SparseArray<ArrayList<String>> allCharacters,ArrayList<String> charactersName,
                        String compareType) {
        this.allCharacters = allCharacters;
        this.charactersName = charactersName;
        this.compareType = compareType;
    }
    /**
     * <p>Принимает основную информацию о товаре, с приведением к SparseArray
     * </p>
     * <p>с приведением списка характеристик к SparseArray
     * </p>
     * <p>Нужен для просмотра характеристик конкретного продукта, приводится к SparseArray,
     * чтобы не нарущать общую логику
     * </p>
     **/

    public ProductsInfo(ArrayList<String> characters,ArrayList<String> charactersName,
                        String compareType)
    {
        arrayListToSparseArray(characters);
        this.charactersName = charactersName;
        this.compareType = compareType;
    }
    /**
     * <p>Приводит ArrayList<String> к типу SparseArray<ArrayList<String>>
     * </p>
     **/
    private void arrayListToSparseArray(ArrayList<String> characters){
        for (int count = 0;count<characters.size();count++){
            ArrayList<String> array = new ArrayList<>();
            array.add(characters.get(count));
            allCharacters.put(count,array);
        }
    }
    public String getCompareType() {
        return compareType;
    }
    public ArrayList<String> getCharactersName() {
        return charactersName;
    }
    public SparseArray<ArrayList<String>> getAllCharacters() {
        return allCharacters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        writeSparseArray(dest,allCharacters);
        dest.writeStringList(charactersName);
        dest.writeString(compareType);

    }


    /**
     * <p>Запаковывает SparseArray, путем последовательного запаковывания размера, ключей и значений
     * </p>
     **/
    private void writeSparseArray(Parcel dest, SparseArray<ArrayList<String>> sparseArray) {
            if (sparseArray == null) {
            dest.writeInt(-1);
            return;
        }
        int size = sparseArray.size();
        dest.writeInt(size);// запаковывает размер хеш таблицы
        int i=0;
        while (i < size) {
            dest.writeInt(sparseArray.keyAt(i));// запаковывает ключ
            dest.writeStringList(sparseArray.valueAt(i));//запаковывает значение
            i++;
        }
    }


    /**
     * <p>Распаковывает SparseArray
     * </p>
     * @see #writeSparseArray(Parcel, SparseArray<ArrayList<String>>)
     **/
    private SparseArray<ArrayList<String>> readSparseArrayFromParcel(Parcel source){
        int size = source.readInt();
        if (size < 0) {
            return null;
        }
        SparseArray <ArrayList<String>> sa = new SparseArray<>(size);

        while (size > 0) {
            int key = source.readInt();//распаковывает ключ
            ArrayList<String> value = source.createStringArrayList();// распаковывает список
            sa.put(key, value);
            size--;
        }
        return sa;
    }

    public static final Parcelable.Creator<ProductsInfo> CREATOR = new Parcelable.Creator<ProductsInfo>() {


        @Override
        public ProductsInfo createFromParcel(Parcel source) {
            return new ProductsInfo(source);
        }

        @Override
        public ProductsInfo[] newArray(int size) {
            return new ProductsInfo[size];
        }
    };

    private ProductsInfo(Parcel in) {
        allCharacters = readSparseArrayFromParcel(in);
        in.readStringList(charactersName);
        compareType = in.readString();

    }
}
