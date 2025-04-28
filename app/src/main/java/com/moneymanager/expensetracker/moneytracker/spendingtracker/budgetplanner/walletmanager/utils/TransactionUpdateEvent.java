package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils;

import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.model.TransactionModel;

import java.util.List;

public class TransactionUpdateEvent {


    private List<TransactionModel> transactionList;

    public TransactionUpdateEvent(List<TransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    public List<TransactionModel> getTransactionList() {
        return transactionList;
    }
}