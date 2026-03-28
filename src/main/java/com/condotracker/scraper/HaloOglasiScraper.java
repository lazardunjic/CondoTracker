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
public class HaloOglasiScraper implements Scraper {

    @Value("${scraper.halooglasi.url}")
    private String url;

    @Override
    public List<Listing> scrape() {
        List<Listing> listings = new ArrayList<>();

        try {
            int page = 1;
            while (true) {
                if (page > 5) break;

                String pageUrl = url.contains("?")
                        ? url + "&page=" + page
                        : url + "?page=" + page;

                Document doc = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                Elements items = doc.select("div.product-item");
                System.out.println("Scrapujem stranicu: " + page + " | Oglasa: " + items.size());

                if (items.isEmpty()) break;

                for (Element item : items) {
                    try {
                        String title = item.select("h3.product-title a").text();
                        String href = item.select("h3.product-title a").attr("href");
                        String itemUrl = "https://www.halooglasi.com" + href;
                        String location = item.select("ul.subtitle-places li:first-child").text();
                        String priceStr = item.select("div.central-feature span").attr("data-value");
                        String areaText = item.select("ul.product-features li:first-child .value-wrapper").text();
                        String areaStr = "";
                        if (areaText.contains("m2") || areaText.contains("m²")) {
                            areaStr = areaText.split("m")[0].replaceAll("[^0-9,.]", "").replace(",", ".").trim();
                        }

                        if (title.isEmpty() || itemUrl.isEmpty()) continue;

                        Listing listing = new Listing();
                        listing.setTitle(title);
                        listing.setUrl(itemUrl);
                        listing.setLocation(location);
                        listing.setSource("halooglasi");
                        listing.setSeenAt(LocalDateTime.now());

                        if (!priceStr.isEmpty())
                            listing.setPrice(Double.parseDouble(priceStr));
                        if (!areaStr.isEmpty())
                            listing.setArea(Double.parseDouble(areaStr));

                        listings.add(listing);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                page++;
            }
        } catch (IOException e) {
            System.err.println("HaloOglasi Scraping Error: " + e.getMessage());
        }

        return listings;
    }
}