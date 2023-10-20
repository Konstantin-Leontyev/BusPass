package ru.buspass.buspass;

import static ru.buspass.buspass.Variables.CONFIG;

public class Functions {

    public static String CreateRequestBody() {
        return "{\"TerminalKey\":\"" + CONFIG.getString("TerminalKey", "") + "\", " +
                "\"OrderId\":\"" + CONFIG.getString("OrderID", "") + "\", " +
                "\"Amount\":\"" + CONFIG.getString("Amount", "") + "\", " +
                "\"PaymentId\":\"" + CONFIG.getString("PaymentId", "") + "\", " +
                "\"DataType\":\"IMAGE\", " +
                "\"Token\":\"" + CONFIG.getString("Token", "") + "\"}";
    }
}
