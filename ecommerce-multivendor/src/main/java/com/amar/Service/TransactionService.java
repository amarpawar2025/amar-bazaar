package com.amar.Service;

import com.amar.modal.Order;
import com.amar.modal.Seller;
import com.amar.modal.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Order order);

    List<Transaction> getTransactionBySellerId(Seller seller);

    List<Transaction> getAllTransaction();

}