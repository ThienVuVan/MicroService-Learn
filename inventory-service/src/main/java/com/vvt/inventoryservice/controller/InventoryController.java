package com.vvt.inventoryservice.controller;

import com.vvt.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    //http://localhost:8082/api/inventory?SkuCode=1&SkuCode=2
    @GetMapping
    public ResponseEntity<?> isInStock(@RequestParam List<String> SkuCode){
        return new ResponseEntity<>(inventoryService.isInStock(SkuCode), HttpStatus.OK);
    }
}
