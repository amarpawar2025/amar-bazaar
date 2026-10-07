package com.amar.Service.impl;
import com.amar.Service.SellerService;
import com.amar.config.JWTProvider;
import com.amar.domain.AccountStatus;
import com.amar.domain.USER_ROLE;
import com.amar.exceptions.SellerException;
import com.amar.modal.Address;
import com.amar.modal.Seller;
import com.amar.repository.AddressRepository;
import com.amar.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    @Override
    public Seller getSellerProfile(String jwt) throws Exception {

        String email = jwtProvider.getEmailFromJWTToken(jwt);

        return getSellerByEmail(email);
    }

    @Override
    public Seller CreateSeller(Seller seller) throws Exception {

        Seller existingSeller =
                sellerRepository.findByEmail(seller.getEmail());

        if (existingSeller != null) {
            throw new Exception(
                    "Seller already exists, use different email"
            );
        }

        Address savedAddress =
                addressRepository.save(
                        seller.getPickupAddress()
                );

        Seller newSeller = new Seller();

        newSeller.setEmail(seller.getEmail());

        newSeller.setPassword(
                passwordEncoder.encode(
                        seller.getPassword()
                )
        );

        newSeller.setSellerName(
                seller.getSellerName()
        );

        newSeller.setPickupAddress(
                savedAddress
        );

        newSeller.setGSTIN(
                seller.getGSTIN()
        );

        newSeller.setRole(
                USER_ROLE.ROLE_SELLER
        );

        newSeller.setMobile(
                seller.getMobile()
        );

        newSeller.setBankDetails(
                seller.getBankDetails()
        );

        newSeller.setBusinessDetails(
                seller.getBusinessDetails()
        );

        return sellerRepository.save(newSeller);
    }

    @Override
    public Seller getSellerById(Long id)
            throws SellerException {

        return sellerRepository.findById(id)
                .orElseThrow(() ->
                        new SellerException(
                                "Seller not found with id "
                                        + id
                        )
                );
    }

    @Override
    public Seller getSellerByEmail(String email)
            throws Exception {

        Seller seller =
                sellerRepository.findByEmail(email);

        if (seller == null) {
            throw new Exception(
                    "Seller not found"
            );
        }

        return seller;
    }

    @Override
    public List<Seller> getAllSellers(
            AccountStatus status) {

        return sellerRepository.findByAccountStatus(
                status
        );
    }

    @Override
    public Seller updateSeller(
            Long id,
            Seller seller)
            throws Exception {

        Seller existingSeller =
                getSellerById(id);

        if (seller.getSellerName() != null) {

            existingSeller.setSellerName(
                    seller.getSellerName()
            );
        }

        if (seller.getMobile() != null) {

            existingSeller.setMobile(
                    seller.getMobile()
            );
        }

        if (seller.getEmail() != null) {

            existingSeller.setEmail(
                    seller.getEmail()
            );
        }

        if (seller.getBusinessDetails() != null) {

            if (seller.getBusinessDetails()
                    .getBusinessName() != null) {

                existingSeller
                        .getBusinessDetails()
                        .setBusinessName(
                                seller.getBusinessDetails()
                                        .getBusinessName()
                        );
            }
        }

        if (seller.getBankDetails() != null) {

            if (seller.getBankDetails()
                    .getAccountHolderName() != null) {

                existingSeller
                        .getBankDetails()
                        .setAccountHolderName(
                                seller.getBankDetails()
                                        .getAccountHolderName()
                        );
            }

            if (seller.getBankDetails()
                    .getAccountNumber() != null) {

                existingSeller
                        .getBankDetails()
                        .setAccountNumber(
                                seller.getBankDetails()
                                        .getAccountNumber()
                        );
            }

            if (seller.getBankDetails()
                    .getIfscCode() != null) {

                existingSeller
                        .getBankDetails()
                        .setIfscCode(
                                seller.getBankDetails()
                                        .getIfscCode()
                        );
            }
        }

        if (seller.getPickupAddress() != null) {

            if (seller.getPickupAddress()
                    .getAddress() != null) {

                existingSeller
                        .getPickupAddress()
                        .setAddress(
                                seller.getPickupAddress()
                                        .getAddress()
                        );
            }

            if (seller.getPickupAddress()
                    .getCity() != null) {

                existingSeller
                        .getPickupAddress()
                        .setCity(
                                seller.getPickupAddress()
                                        .getCity()
                        );
            }

            if (seller.getPickupAddress()
                    .getState() != null) {

                existingSeller
                        .getPickupAddress()
                        .setState(
                                seller.getPickupAddress()
                                        .getState()
                        );
            }

            if (seller.getPickupAddress().getMobile() != null) {

                existingSeller.getPickupAddress().setMobile(
                                seller.getPickupAddress()
                                        .getMobile()
                        );
            }

            if (seller.getPickupAddress()
                    .getPinCode() != null) {

                existingSeller
                        .getPickupAddress()
                        .setPinCode(
                                seller.getPickupAddress()
                                        .getPinCode()
                        );
            }
        }

        if (seller.getGSTIN() != null) {existingSeller.setGSTIN(seller.getGSTIN());
        }

        return sellerRepository.save(existingSeller
        );
    }

    @Override
    public void DeleteSeller(Long id) throws Exception {
        Seller seller = getSellerById(id);
        sellerRepository.delete(seller);
    }

    @Override
    public Seller verifyEmail(String email, String otp) throws Exception {

        Seller seller = getSellerByEmail(email);
        seller.setEmailVerified(true);

        return sellerRepository.save(seller);
    }

    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws Exception {

        Seller seller = getSellerById(sellerId);
        seller.setAccountStatus(status);
        return sellerRepository.save(seller);
    }
}