package com.spring.experiment.combine.api.calls.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class CountryService {

    @Autowired
    RestTemplate restTemplate;

    public String callAPI1(String countryCode) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " bringing up the name");
        return restTemplate.getForObject("https://restcountries.com/v3.1/alpha/"+countryCode + "?fields=name", String.class);
    }

    public String callAPI2(String countryCode) {
        System.out.println(Thread.currentThread().getName() + " bringing up the capital");
        return restTemplate.getForObject("https://restcountries.com/v3.1/alpha/"+countryCode + "?fields=capital", String.class);
    }
    public CompletableFuture<String> callAPICF1(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " CF bringing up the name");
            return restTemplate.getForObject("https://restcountries.com/v3.1/alpha/"+countryCode + "?fields=name", String.class);
        });
    }

    public CompletableFuture<String> callAPICF2(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " CF bringing up the capital");
            return restTemplate.getForObject("https://restcountries.com/v3.1/alpha/"+countryCode + "?fields=capital", String.class);
        });
    }
}
