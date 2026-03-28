package com.condotracker.scraper;

import com.condotracker.model.Listing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NekretnineRsScraper implements Scraper {

    @Value("${scraper.nekretnine.url}")
    private String url;

    @Override
    public List<Listing> scrape() {
        List<Listing> listings = new ArrayList<>();

        try {
            int page = 1;
            while (true) {
                if (page > 5) break;

                String pageUrl = url.contains("?")
                        ? url + "&strana=" + page
                        : url + "?strana=" + page;

                Document doc = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                Elements items = doc.select("div.row.offer");
                System.out.println("Nekretnine.rs stranica: " + page + " | Oglasa: " + items.size());

                if (items.isEmpty()) break;

                for (Element item : items) {
                    try {
                        String href = item.select("h2.offer-title a").attr("href");
                        String itemUrl = "https://www.nekretnine.rs" + href;
                        String title = item.select("h2.offer-title a").text();
                        String location = item.select("p.offer-location").text();
                        String priceRaw = item.select("p.offer-price span").first().text()
                                .replaceAll("[^0-9]", "").trim();
                        String areaRaw = item.select("p.offer-price--invert span").text()
                                .replaceAll("[^0-9.,]", "").replace(",", ".").trim();

                        if (title.isEmpty() || href.isEmpty()) continue;

                        Listing listing = new Listing();
                        listing.setTitle(title);
                        listing.setUrl(itemUrl);
                        listing.setLocation(location);
                        listing.setSource("nekretnine.rs");
                        listing.setSeenAt(LocalDateTime.now());

                        if (!priceRaw.isEmpty())
                            listing.setPrice(Double.parseDouble(priceRaw));
                        if (!areaRaw.isEmpty())
                            listing.setArea(Double.parseDouble(areaRaw));

                        listings.add(listing);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                page++;
            }

        } catch (IOException e) {
            System.err.println("Nekretnine.rs Scraping Error: " + e.getMessage());
        }

        return listings;
    }
}