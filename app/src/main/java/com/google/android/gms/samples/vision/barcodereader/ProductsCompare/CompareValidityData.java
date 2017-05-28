package com.google.android.gms.samples.vision.barcodereader.ProductsCompare;
import com.google.android.gms.samples.vision.barcodereader.R;//класс, с ресурсами приложения

public class CompareValidityData {
    // статус добавления к сравнению
    private boolean validity_status;
    // расширенное описание статуса добаления
    private int description;

    // константы для описания статусов
    private static final int different_categories = 1;
    private static final int equal_products = 2;

     CompareValidityData(){

    }

     void setValidityStatus(boolean validity_status){
        this.validity_status = validity_status;
    }

     void setDescription(int description){
        this.description = description;
    }

    public int getDescriptionId(){
        int return_value = 0;

        switch (description){
            case different_categories: return_value = R.string.cant_compare_different;break;
            case equal_products: return_value =  R.string.cant_compare_itself;break;
        }

        return return_value;
    }

    public boolean getValidityStatus(){
        return validity_status;
    }

}
