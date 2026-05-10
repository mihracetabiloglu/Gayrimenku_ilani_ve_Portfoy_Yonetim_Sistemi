package com.gayrimenkul.system.entity;

import jakarta.persistence.*;

@Entity
public class Neighborhood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // İlişkiler (örneğin District ile)
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    // Getter ve Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
}