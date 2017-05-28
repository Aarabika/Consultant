package com.google.android.gms.samples.vision.barcodereader;

// системный класс для  реализации диалоговых окон
import android.app.Dialog;
// системный класс для управления intent (в данном случае управление переходами между activity)
import android.content.Intent;
// системный класс для доступа к ресурсам приложения
import android.content.res.Resources;
// ситемный класс для реализации включения|паузы|выключения Activity
import android.os.Bundle;
// системный класс для реализации логов приложения
import android.util.Log;

// системный класс, используемый для отображения информации в окне приложения и взаимодействия с пользователем
import android.app.Activity;
// системный класс, используемый для определения местоположения объекта класса View в окне приложения
import android.view.Gravity;
// системный класс, для заполнения объекта класса View xml данными
import android.view.LayoutInflater;
// системный класс, для присвоения данных об элементах окна объектам класса
import android.view.View;
import android.view.ViewGroup;
// системный класс, для присвоения данных об окне объектам класса
import android.view.Window;
// класс, объект которого служит контейнером для хранения данных списка ( объект класса ArrayList) в объекты Listview
import android.widget.ArrayAdapter;


import android.widget.ListView;
import android.widget.TextView;
// всплывающее окно
import android.widget.Toast;

// класс для работы с информациией о статусе, считанного ранее баркода
import com.google.android.gms.common.api.CommonStatusCodes;
// класс для работы с продуктами
import com.google.android.gms.samples.vision.barcodereader.PrBarcode.Product;
// класс для работы со сравнением
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.Compare;
// класс для работы с данными в сравнении ( тип товаров в сравнении и их названия (модели)
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.CompareValidityData;
// класс для работы Parcel с объектами класса Product
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.ProductsInfo;
// класс, объект которого хранит всю информацию о баркодах
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;


/**
 * В этой Activity хранятся и обрабатываются все текущие данные приложения.
 * С помощью данной Activity происходит взаимодействие со всеми остальным Activity.
 * **/
public class MainActivity extends Activity implements View.OnClickListener {
    // объекты для parcel
    ProductsInfo productsTotalCompareData = null;// parcel для данных текущего сравнения (объект класса Compare)
    ProductsInfo productCharactersData = null;// parcel для характеритик отдельного товара (объект класса Product)

    // обьекты MainActivity для отображения характеристик текущего сравнения
    private TextView InCompareNow;// текстовое поле для отображения числа товаров в сравнении
    private TextView Category;// текстовое поле для отображения типа текущего сравнения
    private ListView modelsInCompare;// список для отображения моделей всех товаров в сравнении
    private ArrayAdapter<String> adapter = null;// список, для отображения в ListView

    // контрольное число для получения данных из BarcodeCaptureActivity
    private static final int RC_BARCODE_CAPTURE = 9001;

    private static final String TAG = "BarcodeMain";// название процесса для логов

    private Product product = null;// экземпляр класса для хранения данных об отсканированном товаре
    private Compare compare = null;// экземпляр класса для хранения данных о текущем сравнении

    // объекты для диалогового окна
    private Dialog dialog = null;// объект диалогового окна
    private TextView model;// текстовое поле для отображения

    private Resources res;// экземплаяр класса для хранения информации о ресурсах приложения


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // LayoutInflater для диалогового окна
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_signin,
                (ViewGroup) findViewById(R.id.window));

        res = getResources();//получение ресурсов

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
        //в диалогово окне будут отображаться все обьекты диалогового окна
        // в данном случае dialog_signin.xml
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setLayout(1000, 850);// устанавливаем размеры диалогового окна
        window.setGravity(Gravity.CENTER);// устанавливаем ориентацию
        model = (TextView) dialog.findViewById(R.id.model);

        // получение текущего сравнения из <activity>CompareActivity</activity>
        productsTotalCompareData = getIntent().getParcelableExtra(CompareActivity.productsCompare);
        productCharactersData = getIntent().getParcelableExtra(CompareActivity.productCharacters);


        // Если в <activity>CompareActivity</activity> сравнивались товары
        if (productsTotalCompareData != null) {
            int charactersCount = productsTotalCompareData.getCharactersName().size();// получение числа характеристик
            int inCompareNow = productsTotalCompareData.getAllCharacters().get(0).size();// получение числа товаров в сравнении
            compare = new Compare(productsTotalCompareData.getCharactersName(), productsTotalCompareData.getAllCharacters(),
                                    charactersCount, inCompareNow, productsTotalCompareData.getCompareType());

            updateTotalCompareView();
        } else {

            clearCompare();

        }


        // Если в <activity>CompareActivity</activity> просматривались характеристики
        if (productCharactersData != null) {
            int charactersCount = productCharactersData.getCharactersName().size();// получение числа характеристик

            // получение всех характеристик
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
        switch (v.getId()) {

            case R.id.read_barcode:
                // launch barcode activity.

                // привязываем intent к BarcodeCaptureActivity
                Intent intent = new Intent(this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);// запускаем Activity, для получения результатов
                break;

            case R.id.close:
                //закрыть диалоговое окно

                dialog.cancel();
                break;

            case R.id.clear_compare:
                //отчистить текущее сравнение

                clearCompare();
                break;

            case R.id.characters:
                // посмотреть характеристики товара

                viewCharacters();
                dialog.cancel();
                break;

            case R.id.add_to_compare:
                // добавляет продукт к сравнению

                // проверяем корректность срабатывания добавления к сравнению
                boolean checkAdded = true;

                if (product != null) {

                    checkAdded = addToCompare();

                } else {
                    makeToast(R.string.have_no_added);
                }
                if (checkAdded) {
                    dialog.cancel();
                }
                break;

            case R.id.compare:
                // сравнивает ранее добавленные продукты

                if (compare != null) {
                    if (compare.getInCompareNow() > 1) {
                        compareProducts();
                    } else {
                        makeToast(R.string.have_no_product);
                    }
                } else {
                    makeToast(R.string.have_no_choice);
                }
                break;

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
     *
     * @return возвращает статус добавления
     **/
    public boolean addToCompare() {
        compare = compare == null ? new Compare(product) : compare;

        // получаем информацию о валидности товара, которого добавляем к сравнению
        CompareValidityData validity_information = compare.CheckValidityAndAddToCompareProduct(product);

        // проверяем добавлен ли выбранный товар к сравнению
        if (validity_information.getValidityStatus()) {
            product = null;
        } else {
            createDialog();
        }

        //получаем данные для описания статуса обновления
        if(validity_information.getDescriptionId() != 0) {
            makeToast(validity_information.getDescriptionId());
        }

        updateTotalCompareView();

        return validity_information.getValidityStatus();
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
            // привязываем intent к CompareActivity
            Intent intent = new Intent(this, CompareActivity.class);
            prepareTotalCompareToParcel(intent);
            startActivity(intent);//запускаем CompareActivity
    }


    /**
     * <p>Метод  для оправки объектов в <activity>CompareActivity</activity>, для просмотра характеристик конкретного
     * продукта</p>
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
        // создаем объект класса высплывающее окно
        Toast toast = Toast.makeText(this,id,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,50);// устанавливаем положение в окне
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

            // проверяем есть ли в ListView список с объектами
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
        // проверяем корректность срабатывания BarcodeCaptureActivity
        if (requestCode == RC_BARCODE_CAPTURE) {
            // проверяем корректность отсканированного баркода
            if (resultCode == CommonStatusCodes.SUCCESS) {
                // проверяем пришли ли данные из BarcodeCaptureActivity
                if (data != null) {
                    // получаем информацию для создания объекта класса Barcode
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.productCode);

                    Log.d(TAG, "Barcode value: " + barcode.displayValue);

                    product = new Product(barcode.displayValue,this);

                    // Проверяем есть ли указанный товар в базе данных
                    if(product.getExist()) {

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
