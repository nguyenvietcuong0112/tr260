package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void bind(int position);
}
