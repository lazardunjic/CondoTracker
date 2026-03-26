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
public class CetiriZidaScraper implements Scraper {

    @Value("${scraper.4zida.url}")
    private String url;

    @Override
    public List<Listing> scrape() {
        List<Listing> listings = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            Elements items = doc.select("div.flex.flex-1.flex-col");

            for (Element item : items) {
                try {
                    String href = item.select("a").first().attr("href");
                    String itemUrl = "https://www.4zida.rs" + href;
                    String title = item.select("p.truncate").text();
                    String location = item.select("p.line-clamp-2").text();
                    String priceRaw = item.select("p.rounded-tl").text()
                            .replaceAll("[^0-9]", "");

                    if (title.isEmpty() || href.isEmpty()) continue;

                    Listing listing = new Listing();
                    listing.setTitle(title);
                    listing.setUrl(itemUrl);
                    listing.setLocation(location);
                    listing.setSource("4zida");
                    listing.setSeenAt(LocalDateTime.now());

                    if (!priceRaw.isEmpty())
                        listing.setPrice(Double.parseDouble(priceRaw));

                    listings.add(listing);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("4Zida Scraping Error: " + e.getMessage());
        }

        return listings;
    }
}