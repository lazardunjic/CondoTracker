package com.condotracker;

import com.condotracker.model.Listing;
import com.condotracker.scraper.CetiriZidaScraper;
import com.condotracker.scraper.HaloOglasiScraper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class CondoTrackerApplication {

    @Autowired
    private HaloOglasiScraper halooglasiScraper;

    @Autowired
    private CetiriZidaScraper cetiriZidaScraper;

    public static void main(String[] args) {
        SpringApplication.run(CondoTrackerApplication.class, args);
    }

    @PostConstruct
    public void test() {
        System.out.println("=== HALOOGLASI ===");
        halooglasiScraper.scrape().forEach(l -> System.out.println(l.getTitle() + " - " + l.getPrice()));

        System.out.println("=== 4ZIDA ===");
        cetiriZidaScraper.scrape().forEach(l -> System.out.println(l.getTitle() + " - " + l.getPrice()));
    }
}