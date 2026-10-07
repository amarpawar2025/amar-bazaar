package com.amar.Service.impl;

import com.amar.Service.SellerReportService;
import com.amar.modal.Seller;
import com.amar.modal.SellerReport;
import com.amar.repository.SellerReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {

    private final SellerReportRepository sellerReportRepository;

    @Override
    public SellerReport getSellerReportById(Seller sellerId) {

        SellerReport sr =
                sellerReportRepository.findBySellerId(sellerId.getId());

        if (sr == null) {

            SellerReport newReport = new SellerReport();

            newReport.setSeller(sellerId);

            return sellerReportRepository.save(newReport);
        }

        return sr;
    }

    @Override
    public SellerReport UpdateSellerReport(
            SellerReport sellerReport) {

        return sellerReportRepository.save(sellerReport);
    }

    @Override
    public SellerReport getSellerReport(Seller seller) {

        SellerReport report =
                sellerReportRepository.findBySellerId(seller.getId());

        if (report == null) {

            report = new SellerReport();

            report.setSeller(seller);
            report.setTotalEarnings(0L);
            report.setTotalSales(0L);
            report.setTotalRefunds(0L);
            report.setTotalTax(0L);
            report.setNetEarnings(0L);
            report.setTotalOrders(0);
            report.setCanceledOrders(0);
            report.setTotalTransactions(0);

            report = sellerReportRepository.save(report);
        }

        return report;
    }

    @Override
    public void updateSellerReport(SellerReport report) {

        sellerReportRepository.save(report);
    }
}