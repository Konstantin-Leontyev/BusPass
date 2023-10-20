package ru.buspass.buspass;

import static ru.buspass.buspass.ActivitySettings.activityChange;
import static ru.buspass.buspass.ActivitySettings.setActivitySettings;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erorr_screen);
        //Задаём настройки окна приложения
        setActivitySettings(getWindow());
        //Устанавливаем связь с объектом поля ImageButton для хранения изображения на основном экране
        ImageButton main_image_button = findViewById(R.id.image_button);
        //Устанавливаем связь с объектом поля TextView для вывода статуса работы приложения
        TextView status_info = findViewById(R.id.status_info);
        //Устанавливаем полученное изображение
        main_image_button.setImageResource(R.drawable.error);
        //Устанавливаем полученное сообщение
        status_info.setText(getIntent().getStringExtra("Message"));

        //Устанавливаем обработчик события на main_image_button с условием входа в сервисное меню
        main_image_button.setOnClickListener(new View.OnClickListener() {
            int count;
            @Override
            public void onClick(View v) {
                count++;
                if (count == 5) {
                    activityChange(ErrorScreen.this, ServiceScreen.class);
                }
            }
        });

        //Перезапускаем MainScreen через 10 секунд
        new Handler().postDelayed(() -> activityChange(ErrorScreen.this, MainActivity.class), 10000);
    }
}