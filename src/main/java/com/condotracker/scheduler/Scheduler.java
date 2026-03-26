package com.condotracker.scheduler;

import com.condotracker.detector.ChangeDetection;
import com.condotracker.filter.FilterService;
import com.condotracker.model.Listing;
import com.condotracker.scraper.HaloOglasiScraper;
import com.condotracker.scraper.CetiriZidaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Scheduler {
    @Autowired
    private HaloOglasiScraper halooglasiScraper;

    @Autowired
    private CetiriZidaScraper cetiriZidaScraper;

    @Autowired
    private FilterService filterService;

    @Autowired
    private ChangeDetection changeDetector;

    @Scheduled(fixedDelayString = "${scraper.interval.ms}")
    public void runScraper() {
        List<Listing> all = new ArrayList<>();
        all.addAll(halooglasiScraper.scrape());
        all.addAll(cetiriZidaScraper.scrape());

        List<Listing> filtered = filterService.applyFilter(all);
        List<Listing> newListings = changeDetector.detectNew(filtered);

        System.out.println("New Ads: " + newListings.size());
    }
}
