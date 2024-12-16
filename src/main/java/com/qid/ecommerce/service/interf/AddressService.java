package com.qid.ecommerce.service.interf;

import com.qid.ecommerce.dto.AddressDto;
import com.qid.ecommerce.dto.Response;

public interface AddressService {
    Response saveAndUpdateAddress(AddressDto addressDto);
}
