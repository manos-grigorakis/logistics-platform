package com.manosgrigorakis.logisticsplatform.customers.model;

import com.manosgrigorakis.logisticsplatform.customers.enums.CustomerType;
import com.manosgrigorakis.logisticsplatform.model.Quote;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@ToString(exclude = {"quotes"})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "tin", nullable = false, unique = true, length = 9)
    private String tin;

    @Column(name = "company_name", nullable = false, unique = true, length = 80)
    private String companyName;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "email", length = 320)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Quote> quotes;

    public Customer() {
    }

    @Builder
    public Customer(String tin, String companyName, String firstName, String lastName, String email,
                    CustomerType customerType, String location, String phone) {
        this.tin = tin;
        this.companyName = companyName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.customerType = customerType;
        this.location = location;
        this.phone = phone;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addQuote(Quote quote) {
        quotes.add(quote);
        quote.setCustomer(this);
    }

    public void removeQuote(Quote quote) {
        quotes.remove(quote);
        quote.setCustomer(null);
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
