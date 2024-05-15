package com.spring.experiment.combine.api.calls.contoller;

import com.spring.experiment.combine.api.calls.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@RequestMapping("/combine")
@RestController
public class CombineController {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private CountryService countryService;
    @GetMapping(value = "/te/country/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCountryData(@PathVariable(name = "code") String code) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        final String[] responses = new String[2];
        Instant begin = Instant.now();
        taskExecutor.execute(() -> {
            responses[0] = countryService.callAPI1(code);
            latch.countDown();
        });
        taskExecutor.execute(() -> {
            responses[1] = countryService.callAPI2(code);
            latch.countDown();
        });
        latch.await();
        Instant end = Instant.now();
        System.out.println("It took " + Duration.between(end, begin).toSeconds());
        return ResponseEntity.ok(  responses[0].substring(0, responses[0].length() - 1) + "," + responses[1].substring(1));
    }

    @GetMapping(value = "/cf/country/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCountryData2(@PathVariable(name = "code") String code) throws InterruptedException, ExecutionException {
        final String[] responses = new String[2];
        Instant begin = Instant.now();
        CompletableFuture<String> response0 = countryService.callAPICF1(code);
        CompletableFuture<String> response1 = countryService.callAPICF2(code);
        responses[0] = response0.get();
        responses[1] = response1.get();
        Instant end = Instant.now();
        System.out.println("It took " + Duration.between(end, begin).toSeconds());
        return ResponseEntity.ok(  responses[0].substring(0, responses[0].length() - 1) + "," + responses[1].substring(1));
    }
}
