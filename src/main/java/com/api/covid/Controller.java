package com.api.covid;

import com.api.covid.service.VaccineService;

import java.io.IOException;

public class Controller {
    public static void main(String[] args) throws IOException, InterruptedException {
        VaccineService vaccineService = new VaccineService();
        while (true) {
            boolean isAvailable = vaccineService.isAvailable("133001");
            if (isAvailable) {
                vaccineService.notifyOnMac();
            }
            Thread.sleep(5*60*1000);
        }

    }
}
