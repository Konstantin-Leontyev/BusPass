package ru.buspass.buspass;

import static ru.buspass.buspass.ActivitySettings.activityChange;
import static ru.buspass.buspass.ActivitySettings.setActivitySettings;
import static ru.buspass.buspass.Variables.AGENTS;
import static ru.buspass.buspass.Variables.CONFIG;
import static ru.buspass.buspass.Variables.CONFIG_EDITOR;
import static ru.buspass.buspass.Variables.FIREBASE_AUTH;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AuthorizationScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_screen);
        //Задаём настройки окна приложения
        setActivitySettings(getWindow());
        //Создаём объект и устанавливаем связь с полем ввода номера телефона
        //EditText phone_edittext_field = findViewById(R.id.login_editText);
        //Создаём объект и устанавливаем связь с полем ввода электронной почты
        EditText email_edittext_field = findViewById(R.id.email_editText);
        //Создаём объект и устанавливаем связь с полем ввода пароля
        EditText password_edittext_field = findViewById(R.id.password_edit_text);
        //Создаём объект и устанавливаем связь с кнопкой "Войти"
        Button authorization = findViewById(R.id.authorization);

        //Задаём маску ввода поля номера телефона используя библиотеку от Тинькофф
        //MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        //mask.setHideHardcodedHead(true);
        //MaskFormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        //formatWatcher.installOn(phone_edittext_field);

       //Создаём слушатель нажатий на кнопку "Войти"
        authorization.setOnClickListener(v -> {
                //Авторизуем пользователя по адресу электронной почты и паролю
                FIREBASE_AUTH.signInWithEmailAndPassword(email_edittext_field.getText().toString(), password_edittext_field.getText().toString())
                        //В случае успешной авторизации
                        .addOnSuccessListener(authResult1 -> {
                            //Считываем данные из Firebase
                            AGENTS.child(Objects.requireNonNull(FIREBASE_AUTH.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {

                                @Override
                                //Метод onDataChange вызывается при создании или внесении изменений в базу данных
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //Вызываем редактор
                                    CONFIG_EDITOR = CONFIG.edit();
                                    //Записываем id терминала в хранилище SharedPreferences
                                    CONFIG_EDITOR.putString("TerminalKey", Objects.requireNonNull(snapshot.child("terminalKey").getValue()).toString());
                                    //Записываем token в хранилище SharedPreferences
                                    CONFIG_EDITOR.putString("Token", Objects.requireNonNull(snapshot.child("token").getValue()).toString());
                                    //Записываем стоимость проезда в хранилище SharedPreferences
                                    CONFIG_EDITOR.putString("Amount", Objects.requireNonNull(snapshot.child("amount").getValue()).toString());
                                    CONFIG_EDITOR.putBoolean("FiscalMode", false);
                                    //Подтверждаем внесение изменений
                                    CONFIG_EDITOR.apply();
                                    activityChange(AuthorizationScreen.this, ServiceScreen.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(AuthorizationScreen.this, R.string.firebase_connection_error, Toast.LENGTH_LONG).show();
                                }
                            });
                        //В случае неуспешной авторизации
                        }).addOnFailureListener(e -> {
                            //Вытаскиваем сообщение из объекта выброшенной ошибки
                            String msg = e.getMessage();
                            //Фильтруем и выводим уведомление
                            assert msg != null;
                            if(msg.contains("There is no user record corresponding to this identifier"))
                                Toast.makeText(AuthorizationScreen.this, R.string.user_not_found, Toast.LENGTH_LONG).show();
                            if(msg.contains("The password is invalid "))
                                Toast.makeText(AuthorizationScreen.this, R.string.wrong_password, Toast.LENGTH_LONG).show();
                        });
        });
    }
}