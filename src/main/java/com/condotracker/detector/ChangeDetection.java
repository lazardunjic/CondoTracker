package com.condotracker.detector;

import java.util.ArrayList;
import java.util.List;
import com.condotracker.model.Listing;
import com.condotracker.model.Filter;
import com.condotracker.repository.FilterRepository;
import com.condotracker.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChangeDetection {
    @Autowired
    private ListingRepository listingRepository;

    public List<Listing> detectNew(List<Listing> listings){
        List<Listing> notInData = new ArrayList<>();

        for(Listing l: listings){
            if(!listingRepository.existsByUrl(l.getUrl())){
                notInData.add(l);
                listingRepository.save(l);
            }
        }

        return notInData;
    }
}
