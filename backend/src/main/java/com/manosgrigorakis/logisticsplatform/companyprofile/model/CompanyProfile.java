package com.manosgrigorakis.logisticsplatform.companyprofile.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_profile")
@Entity
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "tin", nullable = false, unique = true, length = 9)
    private String tin;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "slogan", length = 100)
    private String slogan;

    @Column(name = "vat_percentage", nullable = false)
    private Integer vatPercentage;

    @Column(name = "representative_title", nullable = false, length = 50)
    private String representativeTitle;

    @Column(name = "representative", nullable = false, length = 150)
    private String representative;

    @Column(name = "street", nullable = false, length = 120)
    private String street;

    @Column(name = "street_number", nullable = false, length = 10)
    private String streetNumber;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "brand_primary_color", nullable = false, length = 7)
    private String brandPrimaryColor;

    @Column(name = "brand_secondary_color", nullable = false, length = 7)
    private String brandSecondaryColor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "phones", nullable = false, columnDefinition = "longtext")
    private List<String> phones;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public CompanyProfile(String name, String tin, String logoUrl, String websiteUrl, String slogan,
                          Integer vatPercentage, String representativeTitle, String representative,
                          String street, String streetNumber, String postalCode, String region, String country,
                          String brandPrimaryColor, String brandSecondaryColor, List<String> phones, String email) {
        this.name = name;
        this.tin = tin;
        this.logoUrl = logoUrl;
        this.websiteUrl = websiteUrl;
        this.slogan = slogan;
        this.vatPercentage = vatPercentage;
        this.representativeTitle = representativeTitle;
        this.representative = representative;
        this.street = street;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
        this.region = region;
        this.country = country;
        this.brandPrimaryColor = brandPrimaryColor == null ? "#0f172a" : brandPrimaryColor;
        this.brandSecondaryColor = brandSecondaryColor == null ? "#2563eb" : brandSecondaryColor;
        this.phones = phones;
        this.email = email;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getFullAddress() {
        return String.format("%s %s, %s %s, %s", this.street, this.streetNumber, this.postalCode, this.region,
                             this.country);
    }

    public String getFormattedPhones() {
        return String.join(", ", this.phones);
    }
}
