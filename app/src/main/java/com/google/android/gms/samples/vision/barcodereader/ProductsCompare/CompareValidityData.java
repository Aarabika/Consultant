package com.google.android.gms.samples.vision.barcodereader.ProductsCompare;


import com.google.android.gms.samples.vision.barcodereader.R;

public class CompareValidityData {

    private boolean validity_status;
    private int description;

    private static final int different_categories = 1;
    private static final int equal_products = 2;

    public CompareValidityData(){

    }

     void setValidityStatus(boolean validity_status){
        this.validity_status = validity_status;
    }

     void setDescription(int description){
        this.description = description;
    }

    public int getDescriptionText(){
        int return_value;

        switch (description){
            case different_categories: return_value = R.string.cant_compare_different;break;
            case equal_products: return_value =  R.string.cant_compare_itself;break;
            default: return_value = 0;
        }

        return return_value;
    }

    public boolean getValidityStatus(){
        return validity_status;
    }

}
