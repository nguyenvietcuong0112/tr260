package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.gson.Gson;
import com.mallegan.ads.callback.InterCallback;
import com.mallegan.ads.util.Admob;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.R;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.AssistiveTouch;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.Constant;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.LoadNativeFullNew;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.SharePreferenceUtils;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.TimerManager;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.TransactionUpdateEvent;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.fragment.BudgetFragment;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.fragment.HomeFragment;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.fragment.SettingsFragment;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.fragment.StatisticsFragment;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.model.TransactionModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.ak1.BubbleTabBar;
import io.ak1.OnBubbleClickListener;

public class MainActivity extends AppCompatActivity {


    private static final int ADD_TRANSACTION_REQUEST = 1;
    private LinearLayout navHome, navStatistic, navBudget, navSettings, navAdd;
    private ImageView fabAdd;
    private ImageView ivHome, ivStatistic, ivBudget, ivSettings;
    private TextView tvHome, tvStatistic, tvBudget, tvSettings;

    private Fragment activeFragment;
    private SharePreferenceUtils sharePreferenceUtils;
    private List<TransactionModel> transactionList;
    private boolean isFirstClick = true;
    private long lastAdTime = 0;
    private static final long AD_COOLDOWN_PERIOD = 30000;
    private Handler handler = new Handler();

    private BubbleTabBar bubbleTabBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initializeViews();
        setupClickListeners();

        sharePreferenceUtils = new SharePreferenceUtils(this);
        sharePreferenceUtils.incrementCounter();
        transactionList = sharePreferenceUtils.getTransactionList();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            updateIcons(0);
        }



        loadInterNavBar();
        loadInterAddTrans();

        if (!SharePreferenceUtils.isOrganic(this)) {
            TimerManager.getInstance().startTimer();
        }
    }

    private void initializeViews() {
        navHome = findViewById(R.id.nav_home);
        navStatistic = findViewById(R.id.nav_statistic);
        navAdd = findViewById(R.id.nav_add);
        navBudget = findViewById(R.id.nav_budget);
        navSettings = findViewById(R.id.nav_settings);

        ivHome = findViewById(R.id.iv_home);
        ivStatistic = findViewById(R.id.iv_statistic);
        ivBudget = findViewById(R.id.iv_budget);
        ivSettings = findViewById(R.id.iv_settings);

        tvHome = findViewById(R.id.tv_home);
        tvStatistic = findViewById(R.id.tv_statistic);
        tvBudget = findViewById(R.id.tv_budget);
        tvSettings = findViewById(R.id.tv_settings);
    }

    private void setupClickListeners() {
        navHome.setOnClickListener(v -> {
            handleNavClick(() -> {
                loadFragment(new HomeFragment());
                updateIcons(0);
            });
        });

        navStatistic.setOnClickListener(v -> {
            handleNavClick(() -> {
                loadFragment(new StatisticsFragment());
                updateIcons(1);
            });
        });

        navAdd.setOnClickListener(v -> {
            if (Constant.interAddTransaction != null) {
                Admob.getInstance().showInterAds(MainActivity.this, Constant.interAddTransaction, new InterCallback() {
                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        startAddTransactionActivity();
                    }
                });
            } else {
                startAddTransactionActivity();
            }
        });

        navBudget.setOnClickListener(v -> {
            handleNavClick(() -> {
                loadFragment(new BudgetFragment());
                updateIcons(2);
            });
        });

        navSettings.setOnClickListener(v -> {
            handleNavClick(() -> {
                loadFragment(new SettingsFragment());
                updateIcons(3);
            });
        });
    }

    private void loadFragment(Fragment fragment) {
        if (activeFragment != null && activeFragment.getClass() == fragment.getClass()) {
            return;
        }

        if (fragment instanceof HomeFragment || fragment instanceof StatisticsFragment || fragment instanceof BudgetFragment) {
            Bundle bundle = new Bundle();
            transactionList = sharePreferenceUtils.getTransactionList();
            bundle.putString("transactionList", new Gson().toJson(transactionList));
            fragment.setArguments(bundle);
        }

        activeFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateIcons(int selectedIndex) {
        ivHome.setColorFilter(getResources().getColor(R.color.icon_inactive));
        ivStatistic.setColorFilter(getResources().getColor(R.color.icon_inactive));
        ivBudget.setColorFilter(getResources().getColor(R.color.icon_inactive));
        ivSettings.setColorFilter(getResources().getColor(R.color.icon_inactive));

        tvHome.setTextColor(getResources().getColor(R.color.icon_inactive));
        tvStatistic.setTextColor(getResources().getColor(R.color.icon_inactive));
        tvBudget.setTextColor(getResources().getColor(R.color.icon_inactive));
        tvSettings.setTextColor(getResources().getColor(R.color.icon_inactive));

        switch (selectedIndex) {
            case 0:
                ivHome.setColorFilter(getResources().getColor(R.color.green_nav));
                tvHome.setTextColor(getResources().getColor(R.color.green_nav));
                break;
            case 1:
                ivStatistic.setColorFilter(getResources().getColor(R.color.green_nav));
                tvStatistic.setTextColor(getResources().getColor(R.color.green_nav));
                break;
            case 2:
                ivBudget.setColorFilter(getResources().getColor(R.color.green_nav));
                tvBudget.setTextColor(getResources().getColor(R.color.green_nav));
                break;
            case 3:
                ivSettings.setColorFilter(getResources().getColor(R.color.green_nav));
                tvSettings.setTextColor(getResources().getColor(R.color.green_nav));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TRANSACTION_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("transactionData")) {
                String transactionJson = data.getStringExtra("transactionData");
                TransactionModel newTransaction = TransactionModel.fromJson(transactionJson);

                if (newTransaction != null) {
                    transactionList = sharePreferenceUtils.getTransactionList();

                    if (activeFragment instanceof HomeFragment) {
                        ((HomeFragment) activeFragment).onTransactionUpdated(new TransactionUpdateEvent(transactionList));
                    } else if (activeFragment instanceof StatisticsFragment) {
                        ((StatisticsFragment) activeFragment).onTransactionUpdated(new TransactionUpdateEvent(transactionList));
                    } else if (activeFragment instanceof BudgetFragment) {
                        ((BudgetFragment) activeFragment).onTransactionUpdated(new TransactionUpdateEvent(transactionList));
                    }

                    EventBus.getDefault().post(new TransactionUpdateEvent(transactionList));

                    Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void startAddTransactionActivity() {
        Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
        startActivityForResult(intent, ADD_TRANSACTION_REQUEST);
    }


    private void handleNavClick(Runnable action) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAdTime > AD_COOLDOWN_PERIOD) {
            if (Constant.interNavBar != null) {
                Admob.getInstance().showInterAds(this, Constant.interNavBar, new InterCallback() {
                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        lastAdTime = System.currentTimeMillis();
                        action.run();
                    }
                    @Override
                    public void onAdClosedByUser() {
                        super.onAdClosedByUser();
                        Intent intent = new Intent(MainActivity.this, LoadNativeFullNew.class);
                        intent.putExtra(LoadNativeFullNew.EXTRA_NATIVE_AD_ID, getString(R.string.native_full_navbar));
                        startActivity(intent);
                    }
                });
                return;
            }
        }
        action.run();
    }

    private void showInterstitialAd(Runnable action) {
        if (!SharePreferenceUtils.isOrganic(MainActivity.this) && Constant.interNavBar != null) {
            Admob.getInstance().showInterAds(MainActivity.this, Constant.interNavBar, new InterCallback() {
                @Override
                public void onNextAction() {
                    super.onNextAction();
                    action.run();
                    handler.postDelayed(() -> loadInterNavBar(), 1000);
                }

                @Override
                public void onAdClosedByUser() {
                    super.onAdClosedByUser();
                    Intent intent = new Intent(MainActivity.this, LoadNativeFullNew.class);
                    intent.putExtra(LoadNativeFullNew.EXTRA_NATIVE_AD_ID, getString(R.string.native_full_navbar));
                    startActivity(intent);
                }
            });
        } else {
            action.run();
        }
    }

    private void loadInterNavBar() {
        if (!SharePreferenceUtils.isOrganic(MainActivity.this)) {
            Admob.getInstance().loadInterAds(this, getString(R.string.inter_navbar), new InterCallback() {
                @Override
                public void onInterstitialLoad(InterstitialAd interstitialAd) {
                    super.onInterstitialLoad(interstitialAd);
                    Constant.interNavBar = interstitialAd;
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharePreferenceUtils.isOrganic(this)) {
            TimerManager.getInstance().startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        TimerManager.getInstance().stopTimer();

    }

    private void loadInterAddTrans() {
        if (!SharePreferenceUtils.isOrganic(MainActivity.this)) {
            Admob.getInstance().loadInterAds(this, getString(R.string.inter_add_transaction), new InterCallback() {
                @Override
                public void onInterstitialLoad(InterstitialAd interstitialAd) {
                    super.onInterstitialLoad(interstitialAd);
                    Constant.interAddTransaction = interstitialAd;
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}