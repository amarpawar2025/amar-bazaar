package com.amar.Service;

import com.amar.domain.AccountStatus;
import com.amar.exceptions.SellerException;
import com.amar.modal.Seller;

import java.util.List;

public interface SellerService {

    Seller getSellerProfile(String jwt) throws Exception;
    Seller CreateSeller(Seller seller) throws Exception;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email) throws Exception;
    List<Seller>getAllSellers(AccountStatus status);

    Seller updateSeller(Long id,Seller seller) throws Exception;
    void DeleteSeller(Long id) throws Exception;
    Seller verifyEmail(String email,String otp) throws Exception;
    Seller updateSellerAccountStatus(Long SellerId,AccountStatus status) throws Exception;
}
