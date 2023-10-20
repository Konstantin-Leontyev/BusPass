<h1>БасПасс</h1>

<span>Многофункциональный программно-аппаратный комплекс,
предназначенный для автоматизации оплаты проезда
и учёта движения транспортных средств.
Готовое решение для предприятий общественного транспорта и такси. <a href="https://www.buspass.ru/">подробнее ...</a></span>


<h2>Описание</h2>

**Посредствам официальных API в приложении осуществлена интеграция с:**

- банком Тинькофф (эквайер);
- облачной фискальной кассой cloudKassa (для ознакомления с работой приложения, работа сервера облачной кассы эмулирована на локальном сервере);
- облачной базой данных Firebaze.

**Функционал приложения:**

- автономная работа в многопоточном режиме;
- безналичная оплата Qr-кодом;
- фискальный режим работы;
- скрытый вход в сервисное меню;
- хранение информации о настройках в SharedPrefrece файле;
- GIF-скринсейвер;
- звуковая сигнализация о статусе транзакции.

<div>
    <img src="images/scr1.jpg" height="425px">
    <img src="images/scr2.jpg" height="425px">
    <img src="images/scr3.jpg" height="425px">
    <img src="images/scr4.jpg" height="425px">
</div>

<h2>FAQ</h2>

**В случае возникновении ошибки создания local/properties выполниете следующие действия:**

1. В меню File -> project Structure
2. Cлева -> SDK Location
3. Выберите расположение Sdk (по умолчанию C:\Users\\**YourUser**\AppData\Local\Android\Sdk)
4. Нажмите OK или Apply 
