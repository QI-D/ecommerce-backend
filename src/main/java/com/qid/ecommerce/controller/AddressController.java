package com.qid.ecommerce.controller;

import com.qid.ecommerce.dto.AddressDto;
import com.qid.ecommerce.dto.Response;
import com.qid.ecommerce.service.interf.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/save")
    public ResponseEntity<Response> saveAndUpdateAddress(@RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(addressService.saveAndUpdateAddress(addressDto));
    }
}
