package ru.buspass.buspass;

import static ru.buspass.buspass.ActivitySettings.activityChange;
import static ru.buspass.buspass.ActivitySettings.setActivitySettings;
import static ru.buspass.buspass.Variables.CONFIG;
import static ru.buspass.buspass.Variables.FISCl_MODE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Задаём настройки окна приложения
        setActivitySettings(getWindow());
        //Проверяем наличие файла конфигурации
        //Если файл отсутствует, будет создан файл SharedPreferences с именем Config
        CONFIG = getSharedPreferences("Config", MODE_PRIVATE);
        //Задаём отложенное выполнение задачи
        new Handler().postDelayed(() -> {
            //Проверяем содержит ли файл данные:
            //Если нет
            if(CONFIG.getString("TerminalKey", "").isEmpty()) {
                //Переходим на AuthorizationScreen
                activityChange(SplashScreen.this, AuthorizationScreen.class);
            //Если да
            } else {
                //Считываем значение текущего статуса фискального режима
                FISCl_MODE = CONFIG.getBoolean("FiscalMode", false);
                //Переходим на MainActivity
                activityChange(SplashScreen.this, MainActivity.class);
            }
        //Даём время отобразиться всплывающему окну с уведомлением
        }, 1000);
    }
}


