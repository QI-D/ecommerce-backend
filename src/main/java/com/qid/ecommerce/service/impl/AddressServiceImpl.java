package com.qid.ecommerce.service.impl;

import com.qid.ecommerce.dto.AddressDto;
import com.qid.ecommerce.dto.Response;
import com.qid.ecommerce.entity.Address;
import com.qid.ecommerce.entity.User;
import com.qid.ecommerce.repository.AddressRepo;
import com.qid.ecommerce.service.interf.AddressService;
import com.qid.ecommerce.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepo addressRepo;
    private final UserService userService;

    @Override
    public Response saveAndUpdateAddress(AddressDto addressDto) {
        User user = userService.getLoginUser();
        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
            address.setUser(user);
        }

        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getProvince() != null) address.setProvince(addressDto.getProvince());
        if (address.getPostalCode() != null) address.setPostalCode(addressDto.getPostalCode());
        if (address.getCountry() != null) address.setCountry(addressDto.getCountry());

        addressRepo.save(address);

        String message = (user.getAddress() == null) ? "Address successfully created" : "Address successfully updated";

        return Response.builder()
                .status(200)
                .message(message)
                .build();
    }
}
