package com.google.android.gms.samples.vision.barcodereader;

// системный класс для управления intent (в данном случае управление переходами между activity)
import android.content.Intent;
// ситемный класс для реализации включения|паузы|выключения Activity
import android.os.Bundle;
// системный класс для реализации логов приложения
import android.util.Log;

// системный класс, используемый для отображения информации в окне приложения и взаимодействия с пользователем
import android.app.Activity;
// системный класс, используемый для определения местоположения объекта класса View в окне приложения
import android.view.Gravity;
// системный класс, для присвоения данных об элементах окна объектам класса
import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

// класс для работы Parcel с объектами класса Product
import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.ProductsInfo;

/**
 * В этой Activity выводятся таблица со сравнением и навигационные кнопки. (очистить, вернуться назад в MainActivity)
 * напрямую связана с MainActivity
 * **/
public class CompareActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CompareProducts";// название процесса для логов
    //Объект для вывод характеристик
    private TableLayout compareTable;

    // объекты для parcel
    ProductsInfo productsCompareView = null;// parcel для данных текущего сравнения (объект класса Compare)
    ProductsInfo productView = null;// parcel для характеритик отдельного товара (объект класса Product)

    // названия соответсвующих parcel
    public static final String productsCompare = "ProductsCompare";/** @see #productsCompareView **/
    public static final String productCharacters = "ProductCharacters";/** @see #productView **/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Получение Parcel из MainActivity
        productsCompareView = getIntent().getParcelableExtra(productsCompare);//текущее сравнение
        productView = getIntent().getParcelableExtra(productCharacters);//выбранный товар

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        compareTable = (TableLayout) findViewById(R.id.tableLayout);
        Button clearTable = (Button) findViewById(R.id.clear_table);

        // Все объектам свойсво onClick
        findViewById(R.id.clear_table).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);


        // если сравниваются продукты, иначе сделать недоступлой кнопку очистить
        if (productView == null) {
            createCharactersTable(productsCompareView);// таблица сравнения характеристик товаров
        } else {
            createCharactersTable(productView);// таблица с характеристиками отдельного товара
            clearTable.setEnabled(false);
        }

    }

    @Override
    public void onClick(View v) {
        // привязываем intent к CompareActivity
        Intent intent = new Intent(this, MainActivity.class);

        if (v.getId() == R.id.clear_table) {
            clearForm(compareTable);
        }
        if (v.getId() == R.id.back) {
            intent.putExtra(productsCompare, productsCompareView);
            if (productView != null) {
                intent.putExtra(productCharacters, productView);
            }
        }
        startActivity(intent);
    }
    /**
     * Метод очищает объекты <class>TextView,ListView</class>
     **/
    public static void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {

            View view = group.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setText("");
            }
            //Если нужно будет удалаять сложные конструкции раскомитить это
            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    /**
     * Метод заполняет таблицу с характеристиками
     **/
    public void createCharactersTable(ProductsInfo compare) {
        try {
            int characters_count = compare.getCharactersName().size();
            for (int i = 0; i < characters_count; i++) {
                // Ряд с названием характеристик
                TableRow tableRow = new TableRow(this);

                TextView CharacterName = new TextView(this);//ячейка ряда в таблице
                CharacterName.setGravity(Gravity.CENTER_HORIZONTAL);//устанавливаем ориентацию
                CharacterName.setText(compare.getCharactersName().get(i));
                tableRow.addView(CharacterName);// добавляем ячейку в ряд
                compareTable.addView(tableRow, 2 * i);// добавляем ячейку в ряд

                // Ряд с значениями характеристик
                tableRow = new TableRow(this);

                for (int count = 0; count < compare.getAllCharacters().get(i).size(); count++) {
                    TextView Character = new TextView(this);//ячейка ряда в таблице
                    Character.setText(compare.getAllCharacters().get(i).get(count));
                    tableRow.addView(Character, count);// добавляем ячейку в ряд
                }

                compareTable.addView(tableRow, 2 * i + 1);

            }
        } catch (Exception e) {
            Log.e(TAG, "Invalid Data", e);
        }
    }
}
