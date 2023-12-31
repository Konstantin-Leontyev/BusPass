package ru.buspass.buspass;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class Variables {
    //Создаем поток оплаты Qr-кодом
    public static Thread qr_payment_thread;
    //Создаем поток оплаты Qr-кодом
    public static Thread card_payment_thread;
    //Объявляем объект типа SharedPreferences для создания конфигурационного файла
    public static SharedPreferences CONFIG;
    //Объявляем объект типа SharedPreferences.Editor для создания редактора конфигурационного файла
    public static SharedPreferences.Editor CONFIG_EDITOR;
    //Создаем флаг фискального режима работы
    public static Boolean FISCl_MODE;
    //Объявляем объект типа JSONObject для хранения ответа сервера
    public static JSONObject response;
    //Создаем и инициализируем объект FirebaseAuth для авторизации в Firebase
    public static FirebaseAuth FIREBASE_AUTH = FirebaseAuth.getInstance();
    //Создаем и инициализируем базу данных
    public static FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    //Создаем и инициализируем ссылку на базу данных
    public static DatabaseReference AGENTS = FIREBASE_DATABASE.getReference("Agents");

    //Объявляем переменные имён JSON запросов
    public static String INIT_REQUEST;
    public static String QR_REQUEST;
    public static String STATUS_REQUEST;

    //Переменные URL адресов
    //Создаём и инициализируем переменную типа String хранящую URl адрес для прохождения инициаализации
    public static String INIT_ADDRESS = "https://securepay.tinkoff.ru/v2/Init/";
    //Создаём и инициализируем переменную типа String хранящую URl адрес для запроса динамического Qr-кода
    public static String QR_ADDRESS = "https://securepay.tinkoff.ru/v2/GetQr";
    //Создаём и инициализируем переменную типа String хранящую URl адрес для получения статуса платежа
    public static String STATUS_ADDRESS = "https://securepay.tinkoff.ru/v2/GetState";
    //Создаём и инициализируем переменную типа String хранящую URl адрес для получения Qr-кода чека
    //String tax_address = "https://rest-api-test.tinkoff.ru/v2/GetStaticQr";
    //Создаём и инициализируем переменную типа String хранящую текстовое описание логотипа СБП
    public static String SBP_SVG =      "<path id=\"svg_2\" fill=\"#575eaa\" d=\"m14.091799,44.239796l5.36213,-9.45225l0,-5.77392l-5.37089,-9.47053l0.00876,24.6967z\"/>       " +
                                        "<path id=\"svg_3\" fill=\"#da195a\" d=\"m39.729499,22.524216l-5.03061,3.04282l0,6.21422l15.32442,-9.26447l-10.29381,0.00743z\"/>       " +
                                        "<path id=\"svg_4\" fill=\"#fbb42d\" d=\"m29.317269,10.020376l0,18.74245l5.38184,3.26189l-0.02817,-12.53628l-5.35367,-9.46806z\"/>       " +
                                        "<path id=\"svg_5\" fill=\"#f37632\" d=\"m34.670879,19.488406l5.05878,3.03569l10.29381,-0.00744l-20.70626,-12.49631l5.35367,9.46806z\"/>       " +
                                        "<path id=\"svg_6\" fill=\"#5fba46\" d=\"m29.318959,53.768056l5.37996,-9.47518l0,-6.08346l-5.38121,-3.20023l0.00125,18.75887z\"/>       " +
                                        "<path id=\"svg_7\" fill=\"#1289ca\" d=\"m19.453859,29.013596l20.26331,12.26454l10.28567,-0.01209l-35.91986,-21.72298l5.37088,9.47053z\"/>       " +
                                        "<path id=\"svg_8\" fill=\"#0b8341\" d=\"m39.717099,41.278166l-5.01808,3.01462l-5.37997,9.47518l20.68372,-12.50189l-10.28567,0.01209z\"/>       " +
                                        "<path id=\"svg_9\" fill=\"#90509f\" d=\"m19.453859,34.787666l-5.36212,9.45226l15.26807,-9.23071l-5.13423,-3.10758l-4.77172,2.88603z\"/>";
    //Создаём и инициализируем переменную типа String хранящую текстовое описание логотипа БасПасс
    public static String BUSPASS_SVG =  "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M13.6459 47.52C10.1347 43.36 8.05985 37.92 8.05985 32.08C8.05985 26.24 10.1347 20.8 13.6459 " +
                                        "16.64L16.5187 19.52C13.7257 22.96 12.0499 27.36 12.0499 32.16C12.0499 36.96 13.7257 41.36 16.5187 44.8L13.6459 47.52ZM32 0C40.0599 0 47.4813 " +
                                        "3.04 53.0673 8L50.2743 10.8C45.3267 6.56 38.9426 4 32 4C25.0574 4 18.6733 6.56 13.7257 10.8L10.8529 8C16.5187 3.04 23.9401 0 32 0ZM56.02 " +
                                        "10.88C60.9676 16.56 64 23.92 64 32C64 40.08 60.9676 47.52 56.02 53.12L53.2269 50.4C57.4564 45.52 60.01 39.12 60.01 32.08C60.01 25.04 57.4564 " +
                                        "18.64 53.2269 13.76L56.02 10.88ZM7.98005 53.2L10.8529 50.32C6.54364 45.44 3.99003 39.04 3.99003 32.08C3.99003 25.12 6.54364 18.72 10.7731 " +
                                        "13.76L7.98005 10.88C3.03242 16.56 0 24 0 32.08C0 40.16 3.03242 47.6 7.98005 53.2ZM32 16C35.6708 16 39.0224 17.2 41.7357 19.28L38.8628 " +
                                        "22.16C36.9476 20.8 34.5536 20 32 20C29.4464 20 27.0524 20.8 25.1372 22.16L22.2643 19.28C24.9776 17.28 28.3292 16 32 16ZM44.6883 " +
                                        "22.32C46.7631 25.04 47.9601 28.4 47.9601 32.08C47.9601 35.76 46.7631 39.12 44.6883 41.84L41.8155 38.96C43.1721 37.04 43.9701 34.64 43.9701 " +
                                        "32.08C43.9701 29.52 43.1721 27.12 41.8155 25.2L44.6883 22.32ZM19.3117 41.84L22.1845 38.96C20.8279 37.04 20.0299 34.64 20.0299 32.08C20.0299 " +
                                        "29.52 20.8279 27.12 22.1845 25.2L19.3117 22.32C17.2369 25.04 16.0399 28.4 16.0399 32.08C16.0399 35.76 17.2369 39.12 19.3117 41.84ZM32 " +
                                        "8C26.0948 8 20.7481 10.08 16.5985 13.6L19.4713 16.48C22.9027 13.68 27.2918 12 32.0798 12C36.8678 12 41.2569 13.68 44.6883 16.48L47.5611 " +
                                        "13.6C43.2519 10.16 37.9052 8 32 8ZM50.3541 16.64L47.4813 19.52C50.2743 22.96 51.9501 27.36 51.9501 32.16C51.9501 36.96 50.2743 41.36 47.4813 " +
                                        "44.8L50.3541 47.68C53.8653 43.52 55.9402 38.08 55.9402 32.24C55.9402 26.4 53.8653 20.8 50.3541 16.64Z\" fill=\"#00B01A\"/>\n" +
                                        "<path fill-rule=\"evenodd\" clip-rule=\"evenodd\" d=\"M25.9352 25.92C29.9252 25.92 34.4738 25.92 38.4638 25.92C39.1821 25.92 39.7407 26.48 " +
                                        "39.7407 27.2L39.8205 39.84H38.1446V39.92V42.8C38.1446 43.12 37.8254 43.44 37.5062 43.44H36.5486C36.2294 43.44 35.9102 43.12 35.9102 " +
                                        "42.8V39.92V39.84H28.2494V42.64C28.2494 42.96 27.9302 43.28 27.611 43.28H26.6534C26.3342 43.28 26.015 42.96 26.015 " +
                                        "42.64V39.84H24.5786C24.5786 35.6 24.5786 31.44 24.5786 27.2C24.5786 26.56 25.217 25.92 25.9352 25.92ZM31.1222 43.12C31.1222 43.36 31.0424 " +
                                        "43.68 31.0424 43.92H33.2768L33.197 43.04C32.8778 43.04 32.4788 43.04 32.1596 43.04C31.7606 43.12 31.4414 43.12 31.1222 43.12ZM33.3566 " +
                                        "44.32C33.4364 44.8 33.5162 45.2 33.5162 45.76H32.9576H30.8828C30.9626 45.28 30.9626 44.8 31.0424 44.32H33.3566ZM33.596 46.24C33.6758 46.88 " +
                                        "33.7556 47.6 33.8354 48.4H30.6434L30.803 46.24H33.596ZM33.9152 49.12C34.0748 50.16 34.1546 51.2 34.3142 52.4H30.3242C30.404 51.2 30.4838 " +
                                        "50.16 30.5636 49.12H33.9152ZM34.5536 53.68L35.1122 58.16V58.4H29.7656V58.16L30.1646 53.68H34.5536ZM35.3516 60.24L35.4314 61.12C35.591 62.08 " +
                                        "35.6708 63.04 35.8304 64H29.2868C29.3666 62.96 29.4464 61.92 29.5262 60.96L29.606 60.16H35.3516V60.24ZM53.2269 56.24L40.4589 " +
                                        "43.36H39.0225L50.3541 58.56C51.3915 57.84 52.3491 57.12 53.2269 56.24ZM13.9651 58.8L25.3766 43.36H23.621L10.8529 56.24C11.8105 57.2 12.8479 " +
                                        "58 13.9651 58.8ZM27.611 36.24C27.0524 36.24 26.5736 36.72 26.5736 37.2C26.5736 37.76 27.0524 38.16 27.611 38.16C28.1696 38.16 28.6484 37.68 " +
                                        "28.6484 37.2C28.6484 36.64 28.1696 36.24 27.611 36.24ZM36.389 36.24C35.8304 36.24 35.4314 36.72 35.4314 37.2C35.4314 37.76 35.9102 38.16 " +
                                        "36.389 38.16C36.9476 38.16 37.3466 37.68 37.3466 37.2C37.3466 36.72 36.9476 36.24 36.389 36.24ZM27.0524 27.44C26.5736 27.44 26.1746 27.84 " +
                                        "26.1746 28.32V32.48C26.1746 32.96 26.5736 33.36 27.0524 33.36H37.2668C37.7456 33.36 38.1446 32.96 38.1446 32.48V28.32C38.1446 27.84 " +
                                        "37.7456 27.44 37.2668 27.44H27.0524Z\" fill=\"#003F98\"/>";
}
