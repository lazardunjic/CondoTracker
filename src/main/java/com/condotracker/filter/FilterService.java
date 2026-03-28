package com.condotracker.filter;

import java.util.List;
import com.condotracker.model.Listing;
import com.condotracker.model.Filter;
import com.condotracker.repository.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilterService{
    @Autowired
    private FilterRepository filterRepository;

    public List<Listing> applyFilter(List<Listing> listings) {
        List<Filter> activeFilters = filterRepository.findByActiveTrue();

        if (activeFilters.isEmpty()) return listings;

        Filter filter = activeFilters.get(0);

        System.out.println("Ukupno oglasa: " + listings.size());
        System.out.println("Filter: " + filter.getMinPrice() + "-" + filter.getMaxPrice());
        System.out.println("Filter: " + filter.getMinArea() + "-" + filter.getMaxArea());
        System.out.println("Filter: " + filter.getLocation());
        listings.forEach(l -> System.out.println(l.getTitle() + " | cena: " + l.getPrice() + " | area: " + l.getArea()));

        return listings.stream()
                .filter(l -> filter.getMinPrice() == null || l.getPrice() != null && l.getPrice() >= filter.getMinPrice())
                .filter(l -> filter.getMaxPrice() == null || l.getPrice() != null && l.getPrice() <= filter.getMaxPrice())
                .filter(l -> filter.getMinArea() == null || l.getArea() != null && l.getArea() >= filter.getMinArea())
                .filter(l -> filter.getMaxArea() == null || l.getArea() != null && l.getArea() <= filter.getMaxArea())
                .filter(l -> filter.getLocation() == null || l.getLocation() != null && l.getLocation().toLowerCase().contains(filter.getLocation().toLowerCase()))
                .filter(l -> filter.getType() == null || l.getType() != null && l.getType().equalsIgnoreCase(filter.getType()))
                .collect(Collectors.toList());
    }
}
