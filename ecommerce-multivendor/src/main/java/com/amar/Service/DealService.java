package com.amar.Service;

import com.amar.modal.Deal;

import java.util.List;

public interface DealService {

    List<Deal> getDeals();
    Deal create(Deal deal);
    Deal update(Deal deal,Long id) throws Exception;
    void delete(Long id ) throws  Exception;


}
