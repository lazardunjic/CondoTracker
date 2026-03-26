package com.condotracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.condotracker.model.Filter;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filter, Long>{
    List<Filter> findByActiveTrue();
}
