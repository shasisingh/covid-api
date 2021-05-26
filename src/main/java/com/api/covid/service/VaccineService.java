package com.api.covid.service;

import com.api.covid.model.Root;
import com.api.covid.model.Session;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VaccineService {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public boolean isAvailable(String pinCode) {
        HttpGet request = new HttpGet(getUriSearchByPinCode(pinCode, LocalDate.now()));
        attacheHeader(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            // return it as a String
            String result = EntityUtils.toString(entity);
            Root root = objectMapper.readValue(result, Root.class);

            boolean isAvailable = false;
            for (int i = 0; i < root.centers.size(); i++) {
                List<Session> sessions = root.centers.get(i).getSessions();
                for (Session session : sessions) {
                    if (session.min_age_limit == 18 && session.available_capacity > 0) {
                        isAvailable = true;
                        break;
                    }
                }
            }
            return isAvailable;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getUriSearchByPinCode(final String pinCode, final LocalDate date) {
        String newDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String baseUrl = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=%s&date=%s";
        return String.format(baseUrl, pinCode, newDate);
    }

    private void attacheHeader(HttpGet request) {
        request.addHeader("authority", "cdn-api.co-vin.in");
        request.addHeader("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"");
        request.addHeader("accept", "application/json, text/plain, */*");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
        request.addHeader("origin", "https://www.cowin.gov.in");
        request.addHeader("sec-fetch-mode", "cors");
        request.addHeader("sec-fetch-dest", "empty");
        request.addHeader("referer", "https://www.cowin.gov.in/");
        request.addHeader("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
        request.addHeader("if-none-match", "W/\"755-kSDAGwS0dhuJu/VuZ3UJpZ2STnc\"");
    }

    public void notifyOnMac() throws IOException {
        Runtime.getRuntime().exec(String.format("terminal-notifier -message Vaccine_Available"));
    }
}
