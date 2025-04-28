package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.activity;


import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.mallegan.ads.util.AdType;
import com.mallegan.ads.util.FirebaseUtil;
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
    private AdView adView;

    @Override
    public void bind() {
        binding = ActivityCurrencyUnitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean fromSettings = getIntent().getBooleanExtra(EXTRA_FROM_SETTINGS, false);

        if (!SharePreferenceUtils.isOrganic(this)) {
            adView = new AdView(CurrencyUnitActivity.this);
            adView.setAdSize(AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(this, getAdWidth()));
            adView.setAdUnitId(getString(R.string.banner_inline_currency));
            adView.setAdListener(
                    new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            adView.setOnPaidEventListener(adValue -> {
                                FirebaseUtil.logPaidAdImpression(CurrencyUnitActivity.this,
                                        adValue,
                                        adView.getAdUnitId(), AdType.BANNER);
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.d("23312321", "truomhj ");
                        }
                    });

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            adView = null;
        }

        currencyUnitAdapter = new CurrencyUnitAdapter(this, Utils.getCurrencyUnit(), data -> {
            binding.ivSelect.setEnabled(true);
            binding.ivSelect.setAlpha(1.0f);

        },adView);
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
            if(fromSettings) {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                startActivity(new Intent(CurrencyUnitActivity.this, MainActivity.class));
            }

        });
//        if (!SharePreferenceUtils.isOrganic(this)) {
//            Admob.getInstance().loadInlineBanner(this, getString(R.string.banner_inline_currency), Admob.BANNER_INLINE_LARGE_STYLE);
//        } else {
//            binding.adCardView.setVisibility(View.GONE);
//        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public int getAdWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = this.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        float density = displayMetrics.density;
        return (int) (adWidthPixels / density);
    }
}

