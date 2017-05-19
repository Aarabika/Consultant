package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.ProductsCompare.ProductsInfo;


public class CompareActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CompareProducts";
    //Объект для вывод характеристик
    private TableLayout compareTable;


    ProductsInfo productsCompareView = null;
    ProductsInfo productView = null;

    public static final String productsCompare = "ProductsCompare";
    public static final String productCharacters = "ProductCharacters";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Получение Parcel из MainActivity
        productsCompareView = getIntent().getParcelableExtra(productsCompare);//текущее сравнение
        productView = getIntent().getParcelableExtra(productCharacters);//выбранный товар

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        compareTable = (TableLayout) findViewById(R.id.tableLayout);

        // Все объектам свойсво onClick
        findViewById(R.id.clear_table).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        Button clearTable = (Button) findViewById(R.id.clear_table);

        if (productView == null) {
            createCharactersTable(productsCompareView);
        } else {
            createCharactersTable(productView);
            clearTable.setEnabled(false);
        }

    }

    @Override
    public void onClick(View v) {
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
            if (view instanceof ListView) {
                ((ListView) view).setAdapter(null);
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

                TextView CharacterName = new TextView(this);
                CharacterName.setGravity(Gravity.CENTER_HORIZONTAL);
                CharacterName.setText(compare.getCharactersName().get(i));
                tableRow.addView(CharacterName);
                compareTable.addView(tableRow, 2 * i);

                // Ряд с значениями характеристик
                tableRow = new TableRow(this);

                for (int count = 0; count < compare.getAllCharacters().get(i).size(); count++) {
                    TextView Character = new TextView(this);
                    Character.setText(compare.getAllCharacters().get(i).get(count));
                    tableRow.addView(Character, count);
                }

                compareTable.addView(tableRow, 2 * i + 1);

            }
        } catch (Exception e) {
            Log.e(TAG, "Invalid Data", e);
        }
    }
}
