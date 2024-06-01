package com.mirea.kt.ribo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class HTTPRunnable implements Runnable {
    private String address; // Адрес URL, к которому будет выполняться запрос.
    private HashMap<String, String> requestBody; // Тело запроса, содержащего параметры запроса.
    private String responseBody; // Ответ сервера.

    public HTTPRunnable(String address, HashMap<String, String> requestBody) {
        this.address = address;
        this.requestBody = requestBody;
    }

    // Метод для получения ответа сервера.
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public void run() {
        if (this.address != null && !this.address.isEmpty()) {
            try {
                URL url = new URL(this.address); // Создание объекта URL.
                URLConnection connection = url.openConnection(); // Открытие соединения.
                HttpURLConnection httpConnection = (HttpURLConnection) connection; // Преобразование к HttpURLConnection.
                httpConnection.setRequestMethod("POST"); // Установка метода запроса POST.
                httpConnection.setDoOutput(true); // Установка возможности записи данных в соединение.

                // Создание потока для записи данных запроса.
                OutputStreamWriter osw = new OutputStreamWriter(httpConnection.getOutputStream());
                osw.write(generateString()); // Запись параметров запроса.
                osw.flush(); // Очистка потока.

                // Получение кода ответа от сервера.
                int responseCode = httpConnection.getResponseCode();
                System.out.println("Response Code: " + responseCode); // Вывод кода ответа.

                if (responseCode == 200) {
                    InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    String currentLine;
                    StringBuilder sbResponse = new StringBuilder();

                    // Построчное считывание ответа.
                    while ((currentLine = br.readLine()) != null) {
                        sbResponse.append(currentLine);
                    }
                    responseBody = sbResponse.toString();
                } else {
                    System.out.println("ERROR! Bad response code!");
                }
            } catch (IOException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
    }

    // Метод для генерации строки параметров запроса.
    private String generateString() {
        StringBuilder sbParams = new StringBuilder();
        if (this.requestBody != null && !requestBody.isEmpty()) {
            int i = 0;
            for (String key : this.requestBody.keySet()) {
                try {
                    if (i != 0) {
                        sbParams.append("&"); // Добавление символа '&' для разделения параметров.
                    }
                    // Кодирование параметров в формат URL.
                    sbParams.append(key).append("=").append(URLEncoder.encode(this.requestBody.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace(); // Вывод стека исключения при ошибке кодирования.
                }
                i++;
            }
        }
        return sbParams.toString(); // Возвращение строки параметров.
    }
}
