package com.manosgrigorakis.logisticsplatform.controller;

import com.manosgrigorakis.logisticsplatform.enums.CustomerType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class MetadataRestController {

    @GetMapping("/customer-types")
    public List<String> getCustomerTypes() {
        return Arrays.stream(CustomerType.values())
                .map(Enum::name)
                .toList();
    }
}
