package ru.buspass.buspass;

import static ru.buspass.buspass.ActivitySettings.activityChange;
import static ru.buspass.buspass.ActivitySettings.setActivitySettings;
import static ru.buspass.buspass.Functions.CreateRequestBody;
import static ru.buspass.buspass.Variables.BUSPASS_SVG;
import static ru.buspass.buspass.Variables.CONFIG;
import static ru.buspass.buspass.Variables.CONFIG_EDITOR;
import static ru.buspass.buspass.Variables.FISCl_MODE;
import static ru.buspass.buspass.Variables.INIT_ADDRESS;
import static ru.buspass.buspass.Variables.INIT_REQUEST;
import static ru.buspass.buspass.Variables.QR_ADDRESS;
import static ru.buspass.buspass.Variables.QR_REQUEST;
import static ru.buspass.buspass.Variables.SBP_SVG;
import static ru.buspass.buspass.Variables.STATUS_ADDRESS;
import static ru.buspass.buspass.Variables.STATUS_REQUEST;
import static ru.buspass.buspass.Variables.qr_payment_thread;
import static ru.buspass.buspass.Variables.response;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    //Переменные элементов окна MainActivity
    //Создаем переменную глобальной области видимости на основе класса ImageView для хранения изображения на основном экране
    private ImageView horizontal_logo;
    //Создаём переменную глобальной области видимости на основе класса ImageButton для хранения изображения на основном экране
    private ImageButton main_image_button;
    //Создаём переменную глобальной области видимости на основе класса ImageView для хранения изображения QR-кода чека на основном экране
    public ImageView fiscal_qr_field;
    //Создаём переменную глобальной области видимости на основе класса TextView для вывода статуса работы приложения
    private TextView status_info;
    //Создаём переменные глобальной области видимости на основе класса MediaPlayer для звуковых эффектов
    private MediaPlayer SUCCESS_SOUND, ERROR_SOUND;
    //Создаём переменную-флаг статуса оплаты
    public static boolean STATUS_SUCCESS = false;
    //Создаём переменную-флаг статуса фискализации
    //boolean tax_success = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Задаём настройки окна приложения
        setActivitySettings(getWindow());

        //Устанавливаем связь с объектом поля ImageView
        horizontal_logo = findViewById(R.id.horizontal_logo);
        //Устанавливаем связь с объектом поля ImageButton
        main_image_button = findViewById(R.id.image_button);
        //Устанавливаем связь с объектом поля ImageView
        fiscal_qr_field = findViewById(R.id.fiscal_qr);
        //Устанавливаем связь с объектом поля TextView
        status_info = findViewById(R.id.status_info);
        //Устанавливаем связь с медиа файлом звука успеха
        SUCCESS_SOUND = MediaPlayer.create(this, R.raw.success);
        //Устанавливаем связь с медиа файлом звука ошибки
        ERROR_SOUND = MediaPlayer.create(this, R.raw.error);
        //Устанавливаем .gif файл в качестве прогресс бара загрузки
        Glide.with(this).load(R.drawable.gif).into(main_image_button);
        //Задаём текст загрузки
        status_info.setText(R.string.loading);

        //Устанавливаем обработчик события на main_image_button с условием входа в сервисное меню
        main_image_button.setOnClickListener(new View.OnClickListener() {
            int count;
            @Override
            public void onClick(View v) {
                count++;
                if (count == 5) {
                    activityChange(MainActivity.this, ServiceScreen.class);
                }
            }
        });

        //Инициализируем поток оплаты картой
        //card_payment_thread = new Thread(){};

        //Инициализируем поток оплаты динамическим Qr-кодом
        qr_payment_thread = new Thread(() -> {
            do {
                //Запускаем бесконечный цикл оплаты Qr-кодом
                //Проверяем содержит ли файл SharedPreferences ранее полученный Qr-код
                if (CONFIG.getString("Image", "").isEmpty()) {
                    //Вызываем редактор
                    CONFIG_EDITOR = CONFIG.edit();
                    //Создаем уникальный номер заказа используя текущее время
                    CONFIG_EDITOR.putString("OrderID", String.valueOf(new Date().getTime()));
                    //Подтверждаем запись
                    CONFIG_EDITOR.apply();
                    //Задаём структуру и содержание JSON запроса для прохождения инициализации
                    INIT_REQUEST = CreateRequestBody();
                    //Создаем JSONObject с ответом сервера
                    response = GetResponse(INIT_ADDRESS, INIT_REQUEST);
                    //Отправляем запрос инициализации транзакции на сервер банка
                    if(response != null) {
                        GetInit(response);
                        //Задаём структуру и содержание JSON запроса для получения описания изображения динамического Qr-кода
                        QR_REQUEST = CreateRequestBody();
                        //Создаем JSONObject с ответом сервера
                        response = GetResponse(QR_ADDRESS, QR_REQUEST);
                        //Отправляем запрос на получение динамического Qr-кода
                        if (response != null) {
                            GetQr(response);
                            //Задаём структуру и содержание JSON запроса для получения ссылки для скачивания динамического Qr-кода
                            STATUS_REQUEST = CreateRequestBody();
                            //Задаём цикл GetStatus который будет прерван по возвращению Success, либо возникшей в рамках исполнение методов GetInit или GetResponse ошибкой
                            do {
                                //Создаем JSONObject с ответом сервера
                                response = GetResponse(STATUS_ADDRESS, STATUS_REQUEST);
                                //Отправляем запрос на получение динамического Qr-кода
                                if(response != null) GetStatus(response);
                            } while (!STATUS_SUCCESS && !qr_payment_thread.isInterrupted());
                        }
                    }
                } else {
                    //Задаём структуру и содержание JSON запроса для получения ссылки для скачивания динамического Qr-кода
                    STATUS_REQUEST = CreateRequestBody();
                    //Создаем JSONObject с ответом сервера
                    response = GetResponse(STATUS_ADDRESS, STATUS_REQUEST);
                    //Вызываем метод установки изображения Qr-кода в основной поток
                    if (response != null) {
                        SetQr(CONFIG.getString("Image", ""));
                        //Задаём цикл GetStatus который будет прерван по возвращению Success, либо возникшей в рамках исполнение методов GetInit или GetResponse ошибкой
                        do {
                            //Создаем JSONObject с ответом сервера
                            response = GetResponse(STATUS_ADDRESS, STATUS_REQUEST);
                            //Отправляем запрос на получение динамического Qr-кода
                            if (response != null) GetStatus(response);
                        } while (!STATUS_SUCCESS && !qr_payment_thread.isInterrupted());
                    }
                }

                //Если включён фискальный режим
                if(FISCl_MODE && !qr_payment_thread.isInterrupted()) {
                    //Задаём структуру тела уведомления фискального сервера
                    String note = "{\"TerminalKey\":\"TerminalKey\"," +
                            "\"OrderId\":\"OrderId\"," +
                            "\"Success\":\"true\"," +
                            "\"Status\":\"RECEIPT\"," +
                            "\"PaymentId\":\"" + CONFIG.getString("PaymentId", "") + "\", " +
                            "\"ErrorCode\":\"0\"," +
                            "\"Amount\":\"Amount\"," +
                            "\"FiscalNumber\":\"FiscalNumber\"," +
                            "\"ShiftNumber\":\"ShiftNumber\"," +
                            "\"ReceiptDatetime\":\"ReceiptDatetime\"," +
                            "\"FnNumber\":\"FnNumber\"," +
                            "\"EcrRegNumber\":\"EcrRegNumber\"," +
                            "\"FiscalDocumentNumber\":\"FiscalDocumentNumber\"," +
                            "\"FiscalDocumentAttribute\":\"FiscalDocumentAttribute\"," +
                            "\"Token\":\"Token\"," +
                            "\"Type\":\"Income\"," +
                            "\"Ofd\":\"Ofd\"," +
                            "\"QrCodeUrl\":\"https://qr.cloudpayments.ru/receipt?q=t%3d20181205T185000%26s%3d99.00%26fn%3d9999078900005430%26i%3d157347%26fp%3d1016954666%26n%3d1\"," +
                            "\"Receipt\":{" +
                            "\"Items\":[" +
                            "{" +
                            "\"Name\":\"Name\"," +
                            "\"Price\":\"Price\"," +
                            "\"Quantity\":\"Quantity\"," +
                            "\"Amount\":\"Amount\"," +
                            "\"Tax\":\"Tax\"," +
                            "\"PaymentObject\":\"PaymentObject\"" +
                            "}" +
                            "]," +
                            "\"Email\":\"email@domen.ru\"," +
                            "\"Taxation\":\"Taxation\"," +
                            "\"EmailCompany\":\"emailcompany@domen.ru\"" +
                            "}" +
                            "}";

                    //Отправляем уведомление для создание файла на сервере (эмулируем работу сервера уведомлений)
                    SetFile(note);
                    //Формируем JSON команду на получения данных из уведомления
                    String NOTIFICATION = "{\"FileName\":\"" + CONFIG.getString("PaymentId", "") + ".json" + "\", \"Action\":\"GetNotification\"}";
                    //Проверка наличия уведомления от фискального сервера о текущей транзакции
                    response = GetResponse("https://buspass.ru/check.php?action=notify", NOTIFICATION);
                    //Если уведомление по данной транзакции получено извлекаем из него QR-код фискального чека
                    if (response != null) GetFiscalQr(response);
                    //Формируем JSON команду на удаление файла на сервере
                    String Delete = "{\"FileName\":\"" + CONFIG.getString("PaymentId", "") + ".json" + "\", \"Action\":\"Delete\"}";
                    //Отправляем команду на удаление файла
                    response = GetResponse("https://buspass.ru/check.php?action=notify", Delete);
                    //Вызываем метод удаления данных о текущей транзакции
                    QrInformationDelete();

                }
                //Устанавливаем флаги в положение false и удаляем данные о транзакции из файла SharedPreferences
                STATUS_SUCCESS = false;
                //tax_success = false;
                response = null;
                //Показываем логотип даём уйти пассажиру
            } while(!qr_payment_thread.isInterrupted());
        });
        //Запускаем поток оплаты динамическим Qr-кодом
        qr_payment_thread.start();
    }

    //Метод запроса ответа с сервера
    protected JSONObject GetResponse(String url_address, String request_struct) {
        try {
            //Создаем объект на основе класса HttpURLConnection
            HttpURLConnection connection;
            //Создаем объект на основе класса BufferedReader для промежуточной записи считываемых данных
            BufferedReader bufferedReader;
            //Создаем и инициализируем объект класса URL
            URL url =  new URL(url_address);
            //Создаем Http объект
            connection = (HttpURLConnection) url.openConnection();
            //Задаём параметры соединения
            //Задаём метод запросов типа Post
            connection.setRequestMethod("POST");
            //Задаём тип запроса JSON
            connection.setRequestProperty("Content-Type", "application/json");
            //Задаём тип ответа JSON
            connection.setRequestProperty("Accept", "application/json");
            //Включаем исходящий трафик
            connection.setDoOutput(true);
            //Включаем входящий трафик
            connection.setDoInput(true);
            //Устанавливаем ограничение по времени на установление соединения
            connection.setConnectTimeout(5000);
            //устанавливаем ограничение по времени на считывание ответа среврвера
            connection.setReadTimeout(5000);
            //Устанавливаем соединение
            connection.connect();

            //Отправка запроса
            //Создаем объект исходящий поток
            OutputStream outputStream;
            //Инициализируем объект исходящего потока данными получеными по URL
            outputStream = connection.getOutputStream();
            //Создаем массив байт
            byte[] request = request_struct.getBytes();
            //Побайтово записываем структуру и содержание JSON запроса в исходящий поток
            outputStream.write(request, 0, request.length);
            //Закрываем исходящий поток
            outputStream.close();

            //Получение ответа
            //Создаем объект входящий поток
            InputStream inputStream;
            //Инициализируем объект входящего потока данными получеными по URL
            inputStream = connection.getInputStream();
            //Помещаем объект входящего потока в объект класса BufferedReader с целью дальнейшей обработки
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //Создаем переменную типа String для хранения ответа сервера
            String answer;
            //StringBuilder используется с целью доступа к специальным методам
            StringBuilder stringBuffer = new StringBuilder();
            //Записываем в answer данные ответа построчно пока сторки не закончаться
            while ((answer = bufferedReader.readLine()) != null) {
                stringBuffer.append(answer);
            }
            //Помещяем содержимое из буфера в строку ответа
            //answer = stringBuffer.toString();
            //Закрываем буферный объект
            bufferedReader.close();
            //Закрываем соединение
            connection.disconnect();
            //Возвращяем JSON строку в качестве результата работы функции
            //Создаем новый объект типа JSON и помещаем в него ответ сервера
            return new JSONObject(stringBuffer.toString());
        } catch (JSONException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка обработки JSON объекта");
            return null;
        } catch (IOException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Нет интернета");
            return null;
        }
    }

    //Метод запроса ответа с сервера
    protected void SetFile(String request_struct) {
        try {
            //Создаем объект на основе класса HttpURLConnection
            HttpURLConnection connection;
            //Создаем объект на основе класса BufferedReader для промежуточной записи считываемых данных
            BufferedReader bufferedReader;
            //Создаем и инициализируем объект класса URL
            URL url =  new URL("https://buspass.ru/check.php?action=notify");
            //Создаем Http объект
            connection = (HttpURLConnection) url.openConnection();
            //Задаём параметры соединения
            //Задаём метод запросов типа Post
            connection.setRequestMethod("POST");
            //Задаём тип запроса JSON
            connection.setRequestProperty("Content-Type", "application/json");
            //Задаём тип ответа JSON
            connection.setRequestProperty("Accept", "application/json");
            //Включаем исходящий трафик
            connection.setDoOutput(true);
            //Включаем входящий трафик
            connection.setDoInput(true);
            //Устанавливаем ограничение по времени на установление соединения
            connection.setConnectTimeout(5000);
            //Устанавливаем ограничение по времени на считывание ответа среврвера
            connection.setReadTimeout(5000);
            //Устанавливаем соединение
            connection.connect();
            //Отправка запроса
            //Создаем объект исходящий поток
            OutputStream outputStream;
            //Инициализируем объект исходящего потока данными получеными по URL
            outputStream = connection.getOutputStream();
            //Создаем массив байт
            byte[] request = request_struct.getBytes();
            //Побайтово записываем структуру и содержание JSON запроса в исходящий поток
            outputStream.write(request, 0, request.length);
            //Закрываем исходящий поток
            outputStream.close();
            //Создаем объект входящий поток
            InputStream inputStream;
            //Инициализируем объект входящего потока данными получеными по URL
            inputStream = connection.getInputStream();
            //Помещаем объект входящего потока в объект класса BufferedReader с целью дальнейшей обработки
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //Создаем переменную типа String для хранения ответа сервера
            String answer;
            //StringBuilder используется с целью доступа к специальным методам
            StringBuilder stringBuffer = new StringBuilder();
            //Записываем в answer данные ответа построчно пока сторки не закончаться
            while ((answer = bufferedReader.readLine()) != null) {
                stringBuffer.append(answer);
            }
            //Закрываем соединение
            connection.disconnect();
            //Закрываем буферный объект
            bufferedReader.close();
        } catch (IOException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Нет интернета");
        }
    }

    //Метод инициализации транзакции на сервере банка
    protected void GetInit(JSONObject response) {
        //Обработка ответа
        try {
            //Проверяем статуса ответа
            if (response.getBoolean("Success")) {
                //Записываем PaymentId в файл конфигурации на случай прерывания работы программы
                SharedPreferences.Editor register_data_editor = CONFIG.edit();
                //Записываем PaymentId
                register_data_editor.putString("PaymentId", response.getString("PaymentId"));
                //Подтверждаем запись
                register_data_editor.apply();
            } else {
                activityChange(MainActivity.this, ErrorScreen.class,response.getString("Details"));
            }
        } catch (JSONException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка обработки JSON объекта");
        }
    }

    //Метод получения динамического Qr-кода
    protected void GetQr(JSONObject response) {
        //Обработка ответа
        try {
            //Проверяем статус ответа
            if (response.getBoolean("Success")) {
                //Помещаем текстовое описание Qr-кода во временную переменную
                String tmp = response.getString("Data");
                //Заменяем логотип СБР на логотип БасПасс
                String Image = tmp.replace(SBP_SVG, BUSPASS_SVG);
                //Вызываем редактор
                CONFIG_EDITOR = CONFIG.edit();
                //Записываем описание Qr-кода в файл конфигурации на случай прерывания работы программы
                CONFIG_EDITOR.putString("Image", Image);
                //Подтверждаем запись
                CONFIG_EDITOR.apply();
                //Вызываем метод установки изображения Qr-кода в основной поток
                SetQr(CONFIG.getString("Image", ""));
            } else {
                activityChange(MainActivity.this, ErrorScreen.class,response.getString("Details"));
            }
        } catch (JSONException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка обработки JSON объекта");
        }
    }
    //Метод получения статуса транзакции
    protected void GetStatus(JSONObject response) {
        //Обработка ответа
        try {
            //Проверяем статуса ответа
            if (response.getString("Status").equals("CONFIRMED")) {
                //Выполняем в основном потоке
                runOnUiThread(() -> {
                    //Убираем баннер
                    horizontal_logo.setVisibility(View.INVISIBLE);
                    //Выводим иконку успеха
                    main_image_button.setImageResource(R.drawable.success);
                    //Выводим уведомление
                    status_info.setText(R.string.successful_payment);
                });
                //Воспроизводим звук успеха
                SUCCESS_SOUND.start();
                //Останавливаем поток чтобы дать отобразиться уведомлению
                Thread.sleep(2500);
                //Вызываем метод удаления данных о текущей транзакции
                if(!FISCl_MODE) QrInformationDelete();
                //Задаём флагу STATUS_SUCCESS статус true;
                STATUS_SUCCESS = true;
            }else if (response.getString("Status").equals("CANCELED")) {
                //Вызываем метод удаления данных о текущей транзакции
                QrInformationDelete();
                activityChange(MainActivity.this, ErrorScreen.class,"QR-код отменён");
            } else if (response.getString("Status").equals("REJECTED")) {
                //Вызываем метод удаления данных о текущей транзакции
                QrInformationDelete();
                activityChange(MainActivity.this, ErrorScreen.class, "QR-код отклонён банком");
            } else if (response.getString("Status").equals("DEADLINE_EXPIRED")) {
                //Вызываем метод удаления данных о текущей транзакции
                QrInformationDelete();
                activityChange(MainActivity.this, ErrorScreen.class, "QR-код просрочен");
            } else if (!response.getBoolean("Success")) {
                //Воспроизводим звук ошибки
                //ERROR_SOUND.start();
                activityChange(MainActivity.this, ErrorScreen.class,response.getString("Details"));
            }
        } catch (JSONException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка обработки JSON объекта");
        } catch (InterruptedException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка приостановки потока");
        }
    }


    //Метод установки изображения Qr-кода в основной поток
    protected void SetQr(String qr_image_code) {
        //Создаем из строки SVG объект
        SVG svg = null;
        try {
            svg = SVG.getFromString(qr_image_code);
        } catch (SVGParseException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка Создания SVG объекта");
        }
        //Создаем объект класса Picture в качестве переходного объекта
        assert svg != null;
        Picture picture = svg.renderToPicture();
        //Создаем объект класса Drawable
        Drawable drawable = new PictureDrawable(picture);
        //Выводим QR-код на экран
        runOnUiThread(() -> {
            main_image_button.setImageDrawable(drawable);
            horizontal_logo.setVisibility(View.VISIBLE);
            main_image_button.setVisibility(View.VISIBLE);
            status_info.setText(R.string.Qr_pay);
            });
    }

    //Метод удаления данных о текущем Qr-коде
    public static void QrInformationDelete() {
        //Вызываем редактор
        CONFIG_EDITOR = CONFIG.edit();
        //Удаляем информацию о PaymentId
        CONFIG_EDITOR.putString("PaymentId", "");
        //Удаляем информацию о Qr-коде
        CONFIG_EDITOR.putString("Image", "");
        //Подтверждаем запись
        CONFIG_EDITOR.apply();
    }

    //Метод инициализации транзакции на сервере банка
    protected void GetFiscalQr(JSONObject response) {
        //Обработка ответа
        try {
            //Проверяем статуса ответа
            if (response.getBoolean("Success")) {
                //Открываем входящий поток по заданному URL
                InputStream inputStream = new URL(response.getString("QrCodeUrl")).openStream();
                //Декодируем поток и создаем графический объект
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //Выводим объект статус на экран
                runOnUiThread(() -> {
                    fiscal_qr_field.setImageBitmap(bitmap);
                    main_image_button.setVisibility(View.INVISIBLE);
                    fiscal_qr_field.setVisibility(View.VISIBLE);
                    status_info.setText(R.string.fiscal_Qr);
                });
                //Останавливаем поток чтобы дать отобразиться уведомлению
                Thread.sleep(2500);
                //Выводим объект статус на экран
                runOnUiThread(() -> {
                    Glide.with(this).load(R.drawable.gif).into(main_image_button);
                    main_image_button.setVisibility(View.VISIBLE);
                    fiscal_qr_field.setVisibility(View.INVISIBLE);
                    status_info.setText(R.string.loading);
                });
                Thread.sleep(2500);
            } else {
                activityChange(MainActivity.this, ErrorScreen.class,response.getString("Details"));
            }
        } catch (JSONException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка обработки JSON объекта");
        } catch (MalformedURLException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка получения URL чека!");
        } catch (IOException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка скачивания чека!");
        } catch (InterruptedException e) {
            activityChange(MainActivity.this, ErrorScreen.class,"Ошибка прерывания потока");
        }
    }
}