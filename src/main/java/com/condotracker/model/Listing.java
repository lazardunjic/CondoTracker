package com.condotracker.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "listings")

public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private Double price;
    private Double area;
    private String location;
    private String type;
    private String url;
    private String source;

    @Column(name = "seen_at")
    private LocalDateTime seenAt;
}