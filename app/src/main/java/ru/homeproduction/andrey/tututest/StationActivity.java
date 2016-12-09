package ru.homeproduction.andrey.tututest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Intent;


public class StationActivity extends Activity {

    public final static String EXTRA_MESSAGE = " ru.homeproduction.andrey.tututest";
    public final static String EXTRA_MESSAGE2 = "WHERE";

    private ListView lv;
    private Button btn_choose;
    private String[] stringmas = new String[5000];
    //Используем все тотже механизм, что в ListActivity
    //Формат вывода не был представлен в задании поэтому информацию о станции я решил также
    //выводить в listview.Так как количество параметров у станции фиксированно то,создаем массив
    //фиксированной длинны.
    private String[] stringsorstation = new String[10];
    private boolean from = false;
    private JSONObject myJSONObject;
    private int count = 0;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.station_layout);

        lv = (ListView) findViewById(R.id.station_listview);
        btn_choose = (Button) findViewById(R.id.station_btn);

        myJSONObject = MainActivity.getMyJSONObject();

        Intent intent = getIntent();
        String message = intent.getStringExtra(ListActivity.EXTRA_MESSAGE);
        String message2 = intent.getStringExtra(ListActivity.EXTRA_MESSAGE2);

        if (message2.equals("FROM")) {
            from = true;
        } else {
            from = false;
        }

        if(from) {

            try {
                JSONArray citiesFrom = myJSONObject.getJSONArray("citiesFrom");

                for (int i = 0; i < citiesFrom.length(); i++) {

                    JSONObject city = citiesFrom.getJSONObject(i);
                    JSONArray stations = city.getJSONArray("stations");

                    for (int k = 0; k < stations.length(); k++) {

                        JSONObject station = stations.getJSONObject(k);

                        String countryTitle = station.getString("countryTitle");
                        String cityTitle = station.getString("cityTitle");
                        String stationTitle = station.getString("stationTitle");

                        stringmas[count] = countryTitle + "," + cityTitle + "," + stationTitle;

                        //Это почти единственное отличие от ListActivity, в прошлой активити мы выбрали элемент
                        //получили его String, теперь когда мы сформируем точно такой же String, можно
                        //считать что мы нашли нужный нам элемент
                        //Остается дело получить полные параметры элемента.
                        if (message.equals(stringmas[count])) {
                            stringsorstation[0] = "Страна: " + station.getString("countryTitle");
                            stringsorstation[1] = "Координаты:";

                            JSONObject point = station.getJSONObject("point");
                            stringsorstation[2] = "Долгота: " + point.getString("longitude");
                            stringsorstation[3] = "Широта: " + point.getString("latitude");

                            stringsorstation[4] = "Район: " + station.getString("districtTitle");
                            stringsorstation[5] = "Номер города:" + station.getString("cityId");
                            stringsorstation[6] = "Название города: " + station.getString("cityTitle");
                            stringsorstation[7] = "Название страны: " + station.getString("regionTitle");
                            stringsorstation[8] = "Номер станции:" + station.getString("stationId");
                            stringsorstation[9] = "Название станции: " + station.getString("stationTitle");

                        }
                        count++;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                JSONArray citiesTo= myJSONObject.getJSONArray("citiesTo");

                for (int i = 0; i < citiesTo.length(); i++) {

                    JSONObject city = citiesTo.getJSONObject(i);
                    JSONArray stations = city.getJSONArray("stations");

                    for (int k = 0; k < stations.length(); k++) {

                        JSONObject station = stations.getJSONObject(k);

                        String countryTitle = station.getString("countryTitle");
                        String cityTitle = station.getString("cityTitle");
                        String stationTitle = station.getString("stationTitle");

                        stringmas[count] = countryTitle + "," + cityTitle + "," + stationTitle;


                        if (message.equals(stringmas[count])) {
                            stringsorstation[0] = "Страна: " + station.getString("countryTitle");
                            stringsorstation[1] = "Координаты:";

                            JSONObject point = station.getJSONObject("point");
                            stringsorstation[2] = "Долгота: " + point.getString("longitude");
                            stringsorstation[3] = "Широта: " + point.getString("latitude");

                            stringsorstation[4] = "Район: " + station.getString("districtTitle");
                            stringsorstation[5] = "Номер города:" + station.getString("cityId");
                            stringsorstation[6] = "Название города: " + station.getString("cityTitle");
                            stringsorstation[7] = "Название страны: " + station.getString("regionTitle");
                            stringsorstation[8] = "Номер станции:" + station.getString("stationId");
                            stringsorstation[9] = "Название станции: " + station.getString("stationTitle");

                        }
                        count++;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
               R.layout.listviewlayout, stringsorstation);
        lv.setAdapter(adapter);

        btn_choose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Передаем необходимые параметры в MainActivity,а именно полную информацию в String
                //для дальнешейго его отображения в TextView.
                String message = "";
                Intent intent = new Intent(StationActivity.this, MainActivity.class);
                for(int i = 0; i < 10; i++){
                    message = message + stringsorstation[i] + "\n";
                }
                intent.putExtra(EXTRA_MESSAGE, message);
                if(from) {
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


