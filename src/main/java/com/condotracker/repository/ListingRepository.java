package com.condotracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.condotracker.model.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long>{
    boolean existsByUrl(String url);
}
