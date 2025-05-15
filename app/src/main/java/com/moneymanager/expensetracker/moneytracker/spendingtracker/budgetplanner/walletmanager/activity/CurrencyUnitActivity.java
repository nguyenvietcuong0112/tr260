package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.mallegan.ads.callback.NativeCallback;
import com.mallegan.ads.util.Admob;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.R;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.SharePreferenceUtils;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.Utils;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.adapter.CurrencyUnitAdapter;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.base.AbsBaseActivity;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.databinding.ActivityCurrencyUnitBinding;

public class CurrencyUnitActivity extends AbsBaseActivity {

    public static final String EXTRA_FROM_SETTINGS = "extra_from_settings";

    CurrencyUnitAdapter currencyUnitAdapter;
    private ActivityCurrencyUnitBinding binding;

    @Override
    public void bind() {
        binding = ActivityCurrencyUnitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean fromSettings = getIntent().getBooleanExtra(EXTRA_FROM_SETTINGS, false);

        currencyUnitAdapter = new CurrencyUnitAdapter(this, Utils.getCurrencyUnit(), data -> {
            binding.ivSelect.setEnabled(true);
            binding.ivSelect.setAlpha(1.0f);
        }, null);

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list when text changes
                currencyUnitAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        binding.rvCurrencyUnit.setAdapter(currencyUnitAdapter);

        if (fromSettings) {
            binding.ivSelect.setEnabled(true);
            binding.ivSelect.setAlpha(1.0f);
        } else {
            binding.ivSelect.setEnabled(false);
            binding.ivSelect.setAlpha(0.3f);
        }

        binding.ivSelect.setOnClickListener(v -> {
            if (fromSettings) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                startActivity(new Intent(CurrencyUnitActivity.this, MainActivity.class));
            }
        });
        loadAds();
    }


    private void loadAds() {
        Admob.getInstance().loadNativeAd(this, getString(R.string.native_currency), new NativeCallback() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                super.onNativeAdLoaded(nativeAd);
                NativeAdView adView;
                if (SharePreferenceUtils.isOrganic(CurrencyUnitActivity.this)) {
                    adView = (NativeAdView) LayoutInflater.from(CurrencyUnitActivity.this)
                            .inflate(R.layout.layout_native_language, null);
                } else {
                    adView = (NativeAdView) LayoutInflater.from(CurrencyUnitActivity.this)
                            .inflate(R.layout.layout_native_language_non_organic, null);
                }
                binding.frAds.removeAllViews();
                binding.frAds.addView(adView);
                Admob.getInstance().pushAdsToViewCustom(nativeAd, adView);
            }

            @Override
            public void onAdFailedToLoad() {
                super.onAdFailedToLoad();
                binding.frAds.setVisibility(View.GONE);
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}