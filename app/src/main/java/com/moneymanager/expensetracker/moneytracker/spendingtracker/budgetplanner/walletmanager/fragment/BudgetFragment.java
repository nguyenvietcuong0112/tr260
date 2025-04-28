package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mallegan.ads.callback.InterCallback;
import com.mallegan.ads.util.Admob;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.R;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.BudgetManager;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.CircularProgressView;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.Constant;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.LoadNativeFullNew;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.SharePreferenceUtils;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.TransactionUpdateEvent;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.activity.BudgetDetailActivity;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.adapter.BudgetAdapter;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.model.BudgetItem;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.model.TransactionModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment implements BudgetAdapter.BudgetItemListener {
    private BudgetManager budgetManager;
    private CircularProgressView mainProgressView;
    private TextView tvTotalBudget, tvExpenses;
    private LinearLayout btnBudgetDetail;
    private List<TransactionModel> allTransactionList;
    ImageView ivEditBalance;
    String currentCurrency;
    LinearLayout llBanner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        initializeViews(view);
        setupBudgetManager();
        loadTransactionData();
        setupListeners();
        updateUI();
        loadBanner(view);
        loadInterBudgetDetail();
        return view;
    }

    private void initializeViews(View view) {
        currentCurrency = SharePreferenceUtils.getSelectedCurrencyCode(getContext());
        if (currentCurrency.isEmpty()) currentCurrency = "$";

        mainProgressView = view.findViewById(R.id.main_progress_view);
        tvTotalBudget = view.findViewById(R.id.tv_total_budget);
        ivEditBalance = view.findViewById(R.id.iv_edit_balance);
        tvExpenses = view.findViewById(R.id.tv_expenses);
        btnBudgetDetail = view.findViewById(R.id.btn_budget_detail);
        llBanner = view.findViewById(R.id.ll_banner);

    }

    private void loadBanner(View view) {
        if (!SharePreferenceUtils.isOrganic(getContext())) {
            Admob.getInstance().loadCollapsibleBannerFragment(
                    requireActivity(),
                    getString(R.string.banner_collap),
                    view,
                    "top"
            );
        } else {
            llBanner.setVisibility(View.GONE);
        }
    }

    private void loadInterBudgetDetail() {
        if (!SharePreferenceUtils.isOrganic(getContext())) {
            Admob.getInstance().loadInterAds(getContext(), getString(R.string.inter_budget_details), new InterCallback() {
                @Override
                public void onInterstitialLoad(InterstitialAd interstitialAd) {
                    super.onInterstitialLoad(interstitialAd);
                    Constant.interBudgetDetail = interstitialAd;
                }
            });
        }

    }


    private void setupBudgetManager() {
        budgetManager = new BudgetManager(requireContext());
        if (budgetManager.getTotalBudget() == 0) {
            budgetManager.setTotalBudget(0);
        }
    }


    private void setupListeners() {
        View.OnClickListener editBudgetListener = v -> showEditBudgetDialog();
        ivEditBalance.setOnClickListener(editBudgetListener);

        btnBudgetDetail.setOnClickListener(v -> {
            if (Constant.interBudgetDetail != null) {
                Admob.getInstance().showInterAds(getActivity(), Constant.interBudgetDetail, new InterCallback() {
                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        Intent intent = new Intent(requireContext(), BudgetDetailActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAdClosedByUser() {
                        super.onAdClosedByUser();
                        Intent intent = new Intent(getContext(), LoadNativeFullNew.class);
                        intent.putExtra(LoadNativeFullNew.EXTRA_NATIVE_AD_ID, getString(R.string.native_full_budget_details));
                        startActivity(intent);
                    }
                });

            } else {
                Intent intent = new Intent(requireContext(), BudgetDetailActivity.class);
                startActivity(intent);
            }

        });
    }

    private void loadTransactionData() {
        if (getArguments() == null || !getArguments().containsKey("transactionList")) {
            return;
        }

        String transactionListJson = getArguments().getString("transactionList");
        if (transactionListJson == null || transactionListJson.isEmpty()) {
            return;
        }

        Type type = new TypeToken<List<TransactionModel>>() {
        }.getType();
        allTransactionList = new Gson().fromJson(transactionListJson, type);

        if (allTransactionList == null) {
            allTransactionList = new ArrayList<>();
        }

        calculateAndUpdateTotalExpenses();
    }

    private void calculateAndUpdateTotalExpenses() {
        if (allTransactionList == null || allTransactionList.isEmpty()) {
            budgetManager.setTotalExpenses(0);
            updateUI();
            return;
        }

        double totalExpenseAmount = 0.0;
        for (TransactionModel transaction : allTransactionList) {
            if ("Expense".equals(transaction.getTransactionType())) {
                totalExpenseAmount += Double.parseDouble(transaction.getAmount());
            }
        }

        budgetManager.setTotalExpenses(totalExpenseAmount);

        updateUI();
    }

    private void showEditBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_budget, null);
        builder.setView(dialogView);

        EditText inputBudget = dialogView.findViewById(R.id.input_budget);
        inputBudget.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    return;
                }

                try {
                    isUpdating = true;

                    String str = s.toString();
                    if (str.equals(current)) {
                        isUpdating = false;
                        return;
                    }

                    String cleanString = str.replaceAll("[,]", "");

                    if (cleanString.isEmpty()) {
                        inputBudget.setText("");
                        isUpdating = false;
                        return;
                    }

                    long parsed = Long.parseLong(cleanString);
                    String formatted = NumberFormat.getNumberInstance(Locale.US).format(parsed);

                    current = formatted;
                    inputBudget.setText(formatted);
                    inputBudget.setSelection(formatted.length());

                } catch (NumberFormatException e) {
                } finally {
                    isUpdating = false;
                }
            }
        });
        TextView btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView btnSave = dialogView.findViewById(R.id.btn_save);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newBudget = inputBudget.getText().toString().replaceAll(",", "");
            ;
            if (!newBudget.isEmpty()) {
                double budgetAmount = Double.parseDouble(newBudget);
                budgetManager.setTotalBudget(budgetAmount);
                updateUI();
                dialog.dismiss();
            }
        });
    }

    private void updateUI() {
        double totalBudget = budgetManager.getTotalBudget();
        double totalExpenses = budgetManager.getTotalExpenses();
        double remaining = totalBudget - totalExpenses;

        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        tvTotalBudget.setText(currentCurrency + " " + formatter.format(totalBudget));

        tvExpenses.setText("Expenses: " + currentCurrency + formatter.format(totalExpenses));

        int progress = totalBudget > 0 ? (int) ((remaining / totalBudget) * 100) : 0;

        progress = Math.max(0, Math.min(100, progress));

        // Cập nhật progress view
        mainProgressView.setProgress(progress);
        mainProgressView.setShowRemainingText(true);
    }

    @Override
    public void onBudgetItemClick(BudgetItem item) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionUpdated(TransactionUpdateEvent event) {
        allTransactionList = event.getTransactionList();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInterBudgetDetail();
    }
}