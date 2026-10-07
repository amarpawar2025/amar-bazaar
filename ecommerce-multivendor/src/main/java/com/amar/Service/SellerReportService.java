package com.amar.Service;

import com.amar.modal.Seller;
import com.amar.modal.SellerReport;

public interface SellerReportService {

    SellerReport getSellerReportById(
            Seller sellerId);

    SellerReport UpdateSellerReport(
            SellerReport sellerReport);

    SellerReport getSellerReport(Seller seller);

    void updateSellerReport(SellerReport report);
}