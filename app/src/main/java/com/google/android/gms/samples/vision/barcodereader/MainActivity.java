package com.google.android.gms.samples.vision.barcodereader;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.PrBarcode.Product;
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.Compare;
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.ProductsInfo;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener {
    // use a compound button so either checkbox or switch widgets work.
    ProductsInfo productsTotalCompareData = null;
    ProductsInfo productCharactersData = null;

    // обьекты MainActivity для отображения характеристик текущего сравнения
    private TextView InCompareNow;
    private TextView Category;
    private ListView modelsInCompare;
    private ArrayAdapter<String> adapter = null;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    private Product product = null;
    private Compare compare = null;

    // объекты для диалогового окна
    private Dialog dialog = null;
    private Resources res;
    private TextView model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // LayoutInflater для диалогового окна
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_signin,
                (ViewGroup) findViewById(R.id.window));

        res = getResources();

        // Объекты для отображения характеристик текущего сравнения
        InCompareNow = (TextView) findViewById(R.id.nowInCompare);
        Category = (TextView) findViewById(R.id.category);
        modelsInCompare = (ListView) findViewById(R.id.models);


        // Все объектам свойсво onClick
        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.compare).setOnClickListener(this);
        findViewById(R.id.clear_compare).setOnClickListener(this);


        // Инициализация диалогового окна
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setLayout(1000, 850);
        window.setGravity(Gravity.CENTER);
        model = (TextView) dialog.findViewById(R.id.model);

        // получение текущего сравнения из <activity>CompareActivity</activity>
        productsTotalCompareData = getIntent().getParcelableExtra(CompareActivity.productsCompare);
        productCharactersData = getIntent().getParcelableExtra(CompareActivity.productCharacters);


        // Если в <activity>CompareActivity</activity> сравнивались товары
        if (productsTotalCompareData != null) {
            int charactersCount = productsTotalCompareData.getCharactersName().size();
            int inCompareNow = productsTotalCompareData.getAllCharacters().get(0).size();
            compare = new Compare(productsTotalCompareData.getCharactersName(), productsTotalCompareData.getAllCharacters(),
                                    charactersCount, inCompareNow, productsTotalCompareData.getCompareType());

            updateTotalCompareView();
        } else {

            clearCompare();

        }


        // Если в <activity>CompareActivity</activity> просматривались характеристики
        if (productCharactersData != null) {
            int charactersCount = productCharactersData.getCharactersName().size();

            ArrayList<String> characters_list = new ArrayList<>();
            for (int count = 0; count < charactersCount; count++) {
                characters_list.add(productCharactersData.getAllCharacters().get(count).get(0));
            }

            product = new Product(productCharactersData.getCharactersName(), characters_list, productCharactersData.getCompareType());
            createDialog();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.

            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.close) {
            //закрыть диалоговое окно

            dialog.cancel();
        }
        if (v.getId() == R.id.clear_compare) {
            //отчистить текущее сравнение
            clearCompare();
        }
        if (v.getId() == R.id.characters) {
            // посмотреть характеристики товара

            viewCharacters();
            dialog.cancel();
        }
        if (v.getId() == R.id.add_to_compare) {
            // добавляет продукт к сравнению

            int checkAdded =0;
            if(product != null){
                if (product.getStatus()) {
                    checkAdded = addToCompare();
                }
            }
            else {
                makeToast(R.string.have_no_added);
            }
            if (checkAdded != -1) {
                dialog.cancel();
            }
        }
        if (v.getId() == R.id.compare) {
            // сравнивает ранее добавленные продукты

            if(compare != null){
                if(compare.getInCompareNow() > 1){
                    compareProducts();
                }
                else{
                    makeToast(R.string.have_no_product);
                }
            }
            else {
                makeToast(R.string.have_no_choice);
            }
        }


    }

    /**
     * <p>Присваивает объектку текущего сравнения null
     * </p>
     *
     * <p>Отчищает все объекты View, отображающие характеристики текущего сравнения</p>
     **/
    public void clearCompare(){
        compare = null;
        InCompareNow.setText(res.getString(R.string.compare, 0));
        Category.setText(res.getString(R.string.category, ""));
        adapter = null;
        modelsInCompare.setAdapter(null);
    }


    /**
     * <p>Метод добавляет продукт (объект класса <class>Product</class>) к сравнению
     * </p>
     *
     * <p>проверяет объект на валидность</p>
     **/
    public int addToCompare(){
        compare = compare == null ? new Compare(product) : compare;

        //Проверка на тип товара
        if (compare.getCompareType().equals(product.getType())) {
            //проверка на наличие товаров в текущем сравнении

            if (compare.getAllCharacters().get(0) != null) {
                ArrayList<String> productList = compare.getAllCharacters().get(0);
                // проверка на наличие одноименного товара в текущем сравнении

                if (productList.indexOf(product.getCharacters().get(0)) == -1) {
                    compare.addToCompare(product.getCharacters());
                    updateTotalCompareView();
                } else {
                    makeToast(R.string.cant_compare_itself);
                }
            } else {
                compare.addToCompare(product.getCharacters());
                updateTotalCompareView();
            }
            product = null;
            return 0;
        } else {
            makeToast(R.string.cant_compare_different);
            createDialog();
            return -1;
        }
    }


    /**
     * <p>Метод инициализирует диалоговое окно </p>
     * <p>(необходимо, чтобы экземпляр класса <class>Product</class>
     * был инициализирован)
     * </p>
     * **/
    public void createDialog() {
        model.setText(res.getString(R.string.your_choice, product.getCharacters().get(0)));
        dialog.show();
    }


    /**
     * <p>Метод  для оправки объектов в <activity>CompareActivity</activity>, который сравнивает продукты,
     * добавленные в данный момент</p>
     ** @see ProductsInfo класс для Parcel распаковки и запаковки текущего сравнения
     * @see Intent
     * **/
    public void compareProducts() {
            Intent intent = new Intent(this, CompareActivity.class);

            prepareTotalCompareToParcel(intent);
            startActivity(intent);
    }


    /**
     * <p>Метод  для оправки объектов в <activity>CompareActivity</activity>, который сравнивает продукты,
     * добавленные в данный момент</p>
     *<p>Посмотреть характеристики конкретного товара</p>
     *
     * @see Intent
     * **/
    public void viewCharacters() {
        Intent intent = new Intent(this, CompareActivity.class);
        prepareTotalCompareToParcel(intent);
        intent.putExtra(CompareActivity.productCharacters, new ProductsInfo(product.getCharacters(),
                        product.getCharactersName(), product.getType()));
        startActivity(intent);
    }


    /**
     * <p>Метод инициализирует всплывающее окно </p>
     * **/
    public void makeToast(int id){
        Toast toast = Toast.makeText(this,id,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,50);
        toast.show();
    }


    /**
     * <p>потготавливает и запаковывает в Parcel текущее сравнение (объект класса ProductInfo)</p>
     *
     * @see ProductsInfo класс для Parcel распаковки и запаковки текущего сравнения
     * @param intent текущее намерение, для перехода к <activity>CompareActivity</activity>
     *
     * **/
    public void prepareTotalCompareToParcel(Intent intent){
        if (compare != null) {
            intent.putExtra(CompareActivity.productsCompare, new ProductsInfo(compare.getAllCharacters(),
                    compare.getCharactersName(), compare.getCompareType()));
        }
    }

    /**
     * <p> Отображает характеристики текущего сравнения
     * </p>
     *
     * <p>1. Всего добавлено товаров InCompareNow</p>
     * <p>2. Текущая категория Category</p>
     * <p>3. Список текущих товаров modelsInCompare</p>
     *
     **/
    public void updateTotalCompareView() {
        try {

            InCompareNow.setText(res.getString(R.string.compare, compare.getInCompareNow()));
            Category.setText(res.getString(R.string.category, compare.getCompareType()));

            if (adapter == null) {
                adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, compare.getAllCharacters().get(0));
                modelsInCompare.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.e(TAG, "Invalid Data", e);
        }
    }


    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.productCode);


                    Log.d(TAG, "Barcode value: " + barcode.displayValue);
                    product = new Product(barcode.displayValue,this);


                    if(product.getStatus()) {

                        product.getProduct();
                        createDialog();

                    }
                    else {
                        makeToast(R.string.incorrect);
                    }

                } else {
                    makeToast(R.string.barcode_failure);

                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Log.d(TAG, "Can't read barcode" + CommonStatusCodes.getStatusCodeString(resultCode));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
