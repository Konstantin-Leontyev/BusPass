package ru.buspass.buspass;

import static ru.buspass.buspass.ActivitySettings.activityChange;
import static ru.buspass.buspass.ActivitySettings.setActivitySettings;
import static ru.buspass.buspass.Variables.CONFIG;
import static ru.buspass.buspass.Variables.CONFIG_EDITOR;
import static ru.buspass.buspass.Variables.FISCl_MODE;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_screen);
        //Задаём настройки окна приложения
        setActivitySettings(getWindow());

        //Создаём объект и устанавливаем связь с кнопкой удаления конфигурации
        Button config_file_delete_button = findViewById(R.id.config_file_delete_button);
        //Создаём объект и устанавливаем связь с кнопкой включения/выключения фискального режима
        Button fiscal_mode_button = findViewById(R.id.fiscal_mode_button_button);
        //Создаём объект и устанавливаем связь с кнопкой стоимости проезда
        Button set_amount_button = findViewById(R.id.set_amount_button);
        //Создаём объект и устанавливаем связь с кнопкой возврата в MainActivity
        Button start_button = findViewById(R.id.start_button);
        //Задаём видимость для кнопки "Старт"
        start_button.setVisibility(View.VISIBLE);

        //Считываем значение текущего статуса фискального режима
        FISCl_MODE = CONFIG.getBoolean("FiscalMode", false);
        //Если режим отключён, задаём текст "Включить фискальный режим"
        if(!FISCl_MODE) fiscal_mode_button.setText(R.string.fiscal_mode_button_off_text);
        //Если режим включён задаём текст кнопки "Выключить фискальный режим"
        else fiscal_mode_button.setText(R.string.fiscal_mode_button_on_text);

        //Кнопка удаления данных конфигурации
        config_file_delete_button.setOnClickListener(v -> {
            start_button.setVisibility(View.INVISIBLE);
            //Очищаем файл конфигурации
            //Вызываем редактор
            CONFIG_EDITOR = CONFIG.edit();
            //Стираем данные
            CONFIG_EDITOR.clear();
            //Подтверждаем изменения
            CONFIG_EDITOR.apply();
            //Выводим уведомление
            Toast.makeText(ServiceScreen.this, R.string.config_file_delete, Toast.LENGTH_SHORT).show();
            //Задаём отложенное выполнение задачи
            new Handler().postDelayed(() -> {
                //Переходим на AuthorizationScreen
                activityChange(ServiceScreen.this, AuthorizationScreen.class);
            //Даём время отобразиться всплывающему окну с уведомлением
            }, 2500);

        });

        //Кнопка удаления данных о Qr-коде
        fiscal_mode_button.setOnClickListener(v -> {
            //Если фискальный режим выключен
            if(!FISCl_MODE) {
                //Вызываем редактор
                CONFIG_EDITOR = CONFIG.edit();
                //Задаём значение true
                CONFIG_EDITOR.putBoolean("FiscalMode", true);
                //Подтверждаем изменения
                CONFIG_EDITOR.apply();
                //Устанавливаем флаг режима в положение включён
                FISCl_MODE = true;
                //Меняем заголовок на кнопке
                fiscal_mode_button.setText(R.string.fiscal_mode_button_on_text);
                //Выводим уведомление
                Toast.makeText(ServiceScreen.this, R.string.fiscal_mode_on, Toast.LENGTH_SHORT).show();
            //Если фискальный режим включён
            } else {
                //Вызываем редактор
                CONFIG_EDITOR = CONFIG.edit();
                //Задаём значение true
                CONFIG_EDITOR.putBoolean("FiscalMode", false);
                //Подтверждаем изменения
                CONFIG_EDITOR.apply();
                //Устанавливаем флаг режима в положение выключен
                FISCl_MODE = false;
                //Меняем заголовок на кнопке
                fiscal_mode_button.setText(R.string.fiscal_mode_button_off_text);
                //Выводим уведомление
                Toast.makeText(ServiceScreen.this, R.string.fiscal_mode_off, Toast.LENGTH_SHORT).show();
            }
        });

        //Кнопка изменения стоимости проезда
        set_amount_button.setOnClickListener(v -> {

            //Выводим уведомление
            Toast.makeText(ServiceScreen.this,R.string.amount_set, Toast.LENGTH_SHORT).show();
        });

        //Кнопка запуска программы
        start_button.setOnClickListener(v -> {
            //Переходим на MainActivity
            activityChange(ServiceScreen.this, MainActivity.class);
        });
    }
}

