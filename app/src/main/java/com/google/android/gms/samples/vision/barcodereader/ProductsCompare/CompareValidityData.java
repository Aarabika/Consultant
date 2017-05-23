package com.google.android.gms.samples.vision.barcodereader.ProductsCompare;


import android.util.Log;

import com.google.android.gms.samples.vision.barcodereader.R;

public class CompareValidityData {

    private boolean validity_status;
    private int description;

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
