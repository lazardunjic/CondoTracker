package com.condotracker.scraper;

import java.util.List;
import com.condotracker.model.Listing;

public interface Scraper {
    List<Listing> scrape();
}
