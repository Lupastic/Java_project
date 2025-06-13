package com.example.serving_web_content.models;

import jakarta.persistence.*;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long city_id;
    @Column(nullable = false, unique = true)
    private String name;
    public City() {
    }
    public City( String location) {
        this.name = location;
    }
    public Long getId() { return city_id; }
    public void setId(Long id) { this.city_id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
