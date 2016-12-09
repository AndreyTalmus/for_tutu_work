package ru.homeproduction.andrey.tututest;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = " ru.homeproduction.andrey.tututest";

    private static JSONObject dataJsonObj;
    //Переменные необходимы для сохранения значения и финального вывода всей информации
    //в нужные textview.
    private static String saveFROM = "Не указана";
    private static String saveTO= "Не указана";
    private static String saveDate= "Не указана";
    private TextView tv_station_from,tv_station_to,tv_date;
    private Button btn_station_from,btn_station_to,btn_set_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_station_from = (TextView) findViewById(R.id.textView_station_from);
        tv_station_to = (TextView) findViewById(R.id.textView_station_to);
        btn_station_from = (Button) findViewById(R.id.btn_station_from);
        btn_station_to = (Button) findViewById(R.id.btn_station_to);
        btn_set_date = (Button) findViewById(R.id.btn_set_date);
        tv_date = (TextView) findViewById(R.id.textView_data);

        //Блокируем кнопки до тех пор, пока не получим JSON объект, в случае после выбора станции
        //мы заблокируем их вновь, но так как JSON уже будет не null, сразу разблокируем.
        //Сделано, чтобы обезопасить приложение от вылета.
        btn_station_from.setEnabled(false);
        btn_station_to.setEnabled(false);
        btn_set_date.setEnabled(false);

        if(dataJsonObj == null) {

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Производится загрузка данных.", Toast.LENGTH_LONG);
            toast.show();
            new ParseTask().execute();
        }
        else{
            btn_station_from.setEnabled(true);
            btn_station_to.setEnabled(true);
            btn_set_date.setEnabled(true);
            //При возвращении на первичную активити после выбора станции, необходимо обновить все
            //TextView.
            Intent intent = getIntent();
            String message = intent.getStringExtra(StationActivity.EXTRA_MESSAGE);
            String message2 =  intent.getStringExtra(StationActivity.EXTRA_MESSAGE2);
            if(message != null || !message.equals("")){
                if(message2.equals("FROM")){
                    saveFROM = message;
                    tv_station_from.setText(saveFROM);
                    tv_station_to.setText(saveTO);
                    tv_date.setText(saveDate);
                }
                else{
                    saveTO = message;
                    tv_station_to.setText(saveTO);
                    tv_station_from.setText(saveFROM);
                    tv_date.setText(saveDate);
                }
            }
        }

        //Запуск активити ListActivity, передаем параметр, чтобы использовать один и тотже код
        //для станции отправления и станции назначения.

        btn_station_from.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String message = "FROM";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
        }
        });

        btn_station_to.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String message = "TO";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

        //Для указания даты.
        btn_set_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment dateDialog = new DatePicker();
                dateDialog.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }
    //Сеттинг для нашего времени, так как время устанавливается в DatePicker
    public static void setDate(String date){
        saveDate = date;
    }

    //Геттинг для ListActivity,StationActivity, чтобы они имели доступ к главному json.
    public static JSONObject getMyJSONObject(){
        return dataJsonObj;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        //Никогда не показываем, чтобы создать вертикальное меню.
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            //Меню "Расписание" существует, но ничего не делает, предполагается, что мы уже находимся
            //на нем.Из "О приложении" выходим с помощью кнопки назад.
            case R.id.schedule:
                return true;
            case R.id.about_application:
                Intent intent = new Intent(MainActivity.this, AboutApplication.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //AsyncTask для загрузки JSON по ссылке из тестового задания github
    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        Boolean check = false;

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://raw.githubusercontent.com/tutu-ru/hire_android_test/master/allStations.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            //Сохранение JSON объекта.
            try {
                dataJsonObj = new JSONObject(strJson);

            } catch (JSONException e) {
                e.printStackTrace();
                check = true;
            }
            //Проверка на успешную загрузку данных, в случае успеха разблокировать кнопки.
            //В случае провала, оставить их заблоченными.
            if(!check) {
                btn_station_from.setEnabled(true);
                btn_station_to.setEnabled(true);
                btn_set_date.setEnabled(true);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Данные успешно загружены.", Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                //Для повторной загрузки необходимо перезагрузить приложение.При необходимости можно
                //дописать дополнительную логику для повторной попытки.
                Toast toast = Toast.makeText(getApplicationContext(),
                        "При загрузке данных произошла ошибка, проверьте интернет соединение.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
