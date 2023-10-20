package ru.buspass.buspass;

public class Agent {
    private String phone;
    private String password;
    private String terminalKey;
    private String token;
    private String amount;

    //Конструктор без параметров
    public Agent() {}
    //Конструктор с параметрами
    public Agent(String phone, String password, String terminalKey, String token, String amount) {
        this.phone = phone;
        this.password = password;
        this.terminalKey = terminalKey;
        this.token = token;
        this.amount = amount;
    }

    //Задаём гетеры и сеторы свойств
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTerminalKey() {
        return terminalKey;
    }

    public void setTerminalKey(String terminalKey) {
        this.terminalKey = terminalKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
