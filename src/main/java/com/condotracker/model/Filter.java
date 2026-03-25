package com.condotracker.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "filters")
public class Filter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    private Double minPrice;
    private Double maxPrice;
    private Double minArea;
    private Double maxArea;
    private String location;
    private String type;
    private Boolean active;
}
