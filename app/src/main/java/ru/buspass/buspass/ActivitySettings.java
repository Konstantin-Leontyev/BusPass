package ru.buspass.buspass;

import static ru.buspass.buspass.Variables.qr_payment_thread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("rawtypes")
public class ActivitySettings extends BroadcastReceiver {
    //Создаем метод автозагрузки приложения при перезапуске устройства
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, SplashScreen.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }

    //Метод смены экранов приложения
    public static void activityChange(Activity currentActivity, Class targetActivityClass) {
        //Создаём объект Intent
        Intent intent = new Intent(currentActivity, targetActivityClass);
        //Запускаем переход с текущего активити на целевую активити
        currentActivity.startActivity(intent);
        //Завершаем текущую активити
        currentActivity.finish();
    }

    //Перегрузка метода activityChange для вызова окна ошибки
    public static void activityChange(Activity currentActivity, Class targetActivityClass, String info) {
        //Создаём объект Intent
        Intent intent = new Intent(currentActivity, targetActivityClass);
        //Передаём текст ошибки в Error screen
        intent.putExtra("Message", info);
        //Запускаем переход с текущего активити на целевую активити
        currentActivity.startActivity(intent);
        //Останавливаем поток оплаты Qr-кодом
        qr_payment_thread.interrupt();
        //Завершаем текущую активити
        currentActivity.finish();
    }

    //Метод устанавливает параметры экрана
    public static void setActivitySettings(Window window)
    {
        //Отключаем спящий режим
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = window.getDecorView();
        int uiOptions =
                //Прячем кнопки навигации
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        //Устанавливаем полноэкранный режим
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
