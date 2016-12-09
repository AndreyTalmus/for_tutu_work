package ru.homeproduction.andrey.tututest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;


public class ListActivity extends Activity {

    //Передача текстовой необходимой информации
    public final static String EXTRA_MESSAGE = " ru.homeproduction.andrey.tututest";
    //Передача информации о том,что выводить(или станции отправления или прибытия)
    public final static String EXTRA_MESSAGE2 = "WHERE";

    private ListView lv;
    private Button btn_search;
    private EditText et_search;
    //Условно создаем массив длинною 5000, можно было создать фиксированный
    //массив длинною количество станций, но проблема заключается в том, что станций в одном городе
    //может быть несколько.При необходимости можно сначала пускать int переменную для подсчета
    //Количества записей и после создавать массив фиксированный длинны.
    private String[] stringmas = new String[5000];
    //Так как массив на 5000 элементов, естественно не все элементы заполнены из представленного
    //JSON файла, в следствии лишнию часть необходимо обрезать,для этого и заводим этот массив уже
    // фиксированной длинны.
    private String[] masstring;
    //Фактически дублируем массив для поиска.Его размер меньше максимального и не может превышать
    //masstring[].Но это можно оптимизировать в других версиях при необходимости.
    private String[] stringsafterssearch = new String[5000];
    //Количество записей, необходимо для создания массива String[] фиксированной длинны.
    private int count = 0;
    private boolean from = false;
    private JSONObject myJSONObject;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.listview);

        lv = (ListView) findViewById(R.id.station_listview);
        btn_search = (Button) findViewById(R.id.btn_search);
        et_search = (EditText)findViewById(R.id.editText_search);

        //Получаем главный JSONобъект
        myJSONObject = MainActivity.getMyJSONObject();

        //Получаем информацию о том, какая кнопка была нажата, на станцию отправления или прибытия.
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        from = message.equals("FROM");

        //В зависимости от этого считываем разные JSON массивы.
        if(from) {

            try {
                JSONArray citiesFrom = myJSONObject.getJSONArray("citiesFrom");

                for (int i = 0; i < citiesFrom.length(); i++) {

                    JSONObject city = citiesFrom.getJSONObject(i);
                    JSONArray stations = city.getJSONArray("stations");

                    for(int k = 0;k < stations.length();k++){
                        JSONObject station = stations.getJSONObject(k);
                        String countryTitle = station.getString("countryTitle");
                        String cityTitle = station.getString("cityTitle");
                        String stationTitle = station.getString("stationTitle");
                        stringmas[count] = countryTitle + "," + cityTitle + "," + stationTitle;
                        count++;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Здесь обрезаем лишнию часть записей, так как известен count можно создать уже
            //массив фиксированной длинны.
            masstring = new String[count];
            for(int i =0;i < count; i++){
                masstring[i] = stringmas[i];
            }



        }
        //Аналогичные действия только если была выбрана Станция прибытия.
        else
        {
            try {
                JSONArray citiesTo = myJSONObject.getJSONArray("citiesTo");

                for (int i = 0; i < citiesTo.length(); i++) {

                    JSONObject city = citiesTo.getJSONObject(i);
                    JSONArray stations = city.getJSONArray("stations");

                    for(int k = 0;k < stations.length();k++){
                        JSONObject station = stations.getJSONObject(k);
                        String countryTitle = station.getString("countryTitle");
                        String cityTitle = station.getString("cityTitle");
                        String stationTitle = station.getString("stationTitle");
                        stringmas[count] = countryTitle + "," + cityTitle + "," + stationTitle;
                        count++;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            masstring = new String[count];
            for(int i =0;i < count; i++){
                masstring[i] = stringmas[i];
            }

        }

        //Готовый массив передаем адаптеру.
        //Используем свой layout элемент для отображения ListView.Этот элемент используется
        //Во всех listview этого задания.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
               R.layout.listviewlayout, masstring);
        lv.setAdapter(adapter);


        btn_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int k = 0;

                String word_for_search = et_search.getText().toString();

                //Считается, что если мы ничего не ввели, то нам нужно все вывести.
                if(word_for_search.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Слово для поиска отсутствует,выведен весь список", Toast.LENGTH_LONG);
                    toast.show();

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.listviewlayout, stringmas);
                    lv.setAdapter(adapter);
                }
                else
                {
                    //Проверяем содержат ли наш главный массив необходимый набор символов
                    //В случае успеха сохраняем его.
                    for(int i=0; i < count ; i++)
                    {
                        if(masstring[i].contains(word_for_search)){
                            stringsafterssearch[k] = masstring[i];
                            k++;
                        }
                    }


                    //Обрезаем лишнию часть массива.
                    String[] StringsArrayForAdapter = new String[k];

                    for(int i =0;i < k; i++){
                        StringsArrayForAdapter[i] =  stringsafterssearch[i];
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            R.layout.listviewlayout, StringsArrayForAdapter);
                    lv.setAdapter(adapter);

                    if(k== 0){
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Совпадений не найдено", Toast.LENGTH_SHORT);
                        toast.show();
                    }


                }
            }
        });

        //При нажатии на элемент мы хотим получить всю информацию о станции
        //Для этого мы передаем String элемента который был нажат, а также параметр
        //Станция отправления или станция прибытия это.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                    TextView textView = (TextView) itemClicked;
                    Intent intent = new Intent(getApplicationContext(), StationActivity.class);
                    String message = textView.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, message);
                    if(from){
                        intent.putExtra(EXTRA_MESSAGE2,"FROM");
                    }
                    else{
                        intent.putExtra(EXTRA_MESSAGE2,"TO");
                    }
                    startActivity(intent);


            }
        });


     }

}

