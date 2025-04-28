package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;


import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.R;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.databinding.LayoutLanguageCustomBinding;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.model.LanguageModel;

import java.util.ArrayList;


public class UILanguageCustom extends RelativeLayout implements LanguageCustomAdapter.OnItemClickListener {
    private LanguageCustomAdapter adapterEng;
    private LanguageCustomAdapter adapterPor;
    private LanguageCustomAdapter adapterHindi;

    boolean isVisibleHindi = false;
    boolean isVisibleEng = false;
    boolean isVisiblePor = false;

    private Context context;
    private final ArrayList<LanguageModel> dataEng = new ArrayList<>();
    private final ArrayList<LanguageModel> dataPor = new ArrayList<>();
    private final ArrayList<LanguageModel> dataHindi = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private LayoutLanguageCustomBinding binding;
    private boolean isItemLanguageSelected = false;

    private static final String PREF_NAME = "language_preferences";
    private static final String KEY_SELECTED_LANGUAGE_TYPE = "selected_language_type";
    private static final String KEY_SELECTED_LANGUAGE_POSITION = "selected_language_position";

    // Language type constants
    private static final int TYPE_FIXED_SPANISH = 1;
    private static final int TYPE_FIXED_FRENCH = 2;
    private static final int TYPE_ENGLISH = 3;
    private static final int TYPE_HINDI = 4;
    private static final int TYPE_PORTUGUESE = 5;

    public UILanguageCustom(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public UILanguageCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        binding = LayoutLanguageCustomBinding.inflate(LayoutInflater.from(context), this, true);

        binding.languageES.ivAvatar.setImageResource(R.drawable.flag_es);
        binding.languageES.tvTitle.setText("Spanish");

        binding.languageHindi.ivAvatar.setImageResource(R.drawable.flag_hi);
        binding.languageHindi.tvTitle.setText("Hindi");

        binding.languageFR.tvTitle.setText("French");
        binding.languageFR.ivAvatar.setImageResource(R.drawable.flag_fr);

        binding.languagePor.tvTitle.setText("Portuguese");
        binding.languagePor.ivAvatar.setImageResource(R.drawable.flag_portugese);
        binding.languagePor.imgCountries.setImageResource(R.drawable.img_por);

        adapterEng = new LanguageCustomAdapter(dataEng);
        adapterEng.setOnItemClickListener(this);
        binding.rcvLanguageCollap1.setAdapter(adapterEng);

        adapterHindi = new LanguageCustomAdapter(dataHindi);
        adapterHindi.setOnItemClickListener(this);
        binding.rcvLanguageCollap2.setAdapter(adapterHindi);

        adapterPor = new LanguageCustomAdapter(dataPor);
        adapterPor.setOnItemClickListener(this);
        binding.rcvLanguageCollap3.setAdapter(adapterPor);

        binding.languageHindi.imgCountries.setVisibility(GONE);
        binding.languageHindi.animHand.setVisibility(GONE);
        binding.languagePor.animHand.setVisibility(GONE);

        binding.languageES.llNotColap.setOnClickListener(v -> {
            binding.languageES.imgSelected.setImageResource(R.drawable.ic_checked_language);
            binding.languageFR.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
            adapterPor.unselectAll();
            adapterEng.unselectAll();
            adapterHindi.unselectAll();
            isItemLanguageSelected = true;
            saveSelectedLanguage(TYPE_FIXED_SPANISH, -1);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(0, isItemLanguageSelected);
            }
            binding.languageEnglishCollapse.animHand.setVisibility(GONE);
        });

        binding.languageFR.llNotColap.setOnClickListener(v -> {
            binding.languageES.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
            binding.languageFR.imgSelected.setImageResource(R.drawable.ic_checked_language);
            adapterPor.unselectAll();
            adapterEng.unselectAll();
            adapterHindi.unselectAll();
            isItemLanguageSelected = true;
            saveSelectedLanguage(TYPE_FIXED_FRENCH, -1);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(0, isItemLanguageSelected);
            }
            binding.languageEnglishCollapse.animHand.setVisibility(GONE);
        });

        binding.languageHindi.itemCollap.setOnClickListener(v -> {
            isVisibleHindi = !isVisibleHindi;
            binding.rcvLanguageCollap2.setVisibility(isVisibleHindi ? View.VISIBLE : View.GONE);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(0, isItemLanguageSelected);
            }
            binding.languageEnglishCollapse.animHand.setVisibility(GONE);
        });

        binding.languageEnglishCollapse.itemCollap.setOnClickListener(v -> {
            isVisibleEng = !isVisibleEng;
            binding.rcvLanguageCollap1.setVisibility(isVisibleEng ? View.VISIBLE : View.GONE);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(0, isItemLanguageSelected);
            }
            binding.languageEnglishCollapse.animHand.setVisibility(GONE);
        });

        binding.languagePor.itemCollap.setOnClickListener(v -> {
            isVisiblePor = !isVisiblePor;
            binding.rcvLanguageCollap3.setVisibility(isVisiblePor ? View.VISIBLE : View.GONE);
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickListener(0, isItemLanguageSelected);
            }
            binding.languageEnglishCollapse.animHand.setVisibility(GONE);
        });

        loadSavedLanguage();
    }

    public void upDateData(ArrayList<LanguageModel> dataEng1, ArrayList<LanguageModel> hindi, ArrayList<LanguageModel> dataPor1) {
        dataPor.clear();
        dataHindi.clear();
        dataEng.clear();
        if (dataPor1 != null && !dataPor1.isEmpty()) {
            dataPor.addAll(dataPor1);
        }
        if (hindi != null && !hindi.isEmpty()) {
            dataHindi.addAll(hindi);
        }
        if (dataEng1 != null && !dataEng1.isEmpty()) {
            dataEng.addAll(dataEng1);
        }
        adapterPor.notifyDataSetChanged();
        adapterHindi.notifyDataSetChanged();
        adapterEng.notifyDataSetChanged();

        loadSavedLanguage();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onItemNewClick(int position, LanguageModel itemTabModel) {
        isItemLanguageSelected = true;

        int type = -1;
        if (dataEng.contains(itemTabModel)) {
            type = TYPE_ENGLISH;
        } else if (dataHindi.contains(itemTabModel)) {
            type = TYPE_HINDI;
        } else if (dataPor.contains(itemTabModel)) {
            type = TYPE_PORTUGUESE;
        }

        saveSelectedLanguage(type, position);

        if (onItemClickListener != null) {
            onItemClickListener.onItemClickListener(position, isItemLanguageSelected);
        }

        binding.languageFR.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
        binding.languageES.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
    }

    @Override
    public void onPreviousPosition(int pos) {
        if (onItemClickListener != null) {
            onItemClickListener.onPreviousPosition(pos);
        }
    }

    /**
     * Save the selected language to SharedPreferences
     */
    private void saveSelectedLanguage(int type, int position) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECTED_LANGUAGE_TYPE, type);
        editor.putInt(KEY_SELECTED_LANGUAGE_POSITION, position);
        editor.apply();
    }

    /**
     * Load and apply the previously selected language
     */
    private void loadSavedLanguage() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int type = prefs.getInt(KEY_SELECTED_LANGUAGE_TYPE, -1);
        int position = prefs.getInt(KEY_SELECTED_LANGUAGE_POSITION, -1);

        if (type != -1) {
            isItemLanguageSelected = true;

            switch (type) {
                case TYPE_FIXED_SPANISH:
                    binding.languageES.imgSelected.setImageResource(R.drawable.ic_checked_language);
                    binding.languageFR.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    adapterPor.unselectAll();
                    adapterEng.unselectAll();
                    adapterHindi.unselectAll();
                    break;

                case TYPE_FIXED_FRENCH:
                    binding.languageES.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    binding.languageFR.imgSelected.setImageResource(R.drawable.ic_checked_language);
                    adapterPor.unselectAll();
                    adapterEng.unselectAll();
                    adapterHindi.unselectAll();
                    break;

                case TYPE_ENGLISH:
                    binding.languageES.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    binding.languageFR.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    adapterEng.selectPosition(position);
                    adapterPor.unselectAll();
                    adapterHindi.unselectAll();

                    if (!isVisibleEng) {
                        isVisibleEng = true;
                        binding.rcvLanguageCollap1.setVisibility(View.VISIBLE);
                    }
                    break;

                case TYPE_HINDI:
                    binding.languageES.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    binding.languageFR.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    adapterHindi.selectPosition(position);
                    adapterEng.unselectAll();
                    adapterPor.unselectAll();

                    if (!isVisibleHindi) {
                        isVisibleHindi = true;
                        binding.rcvLanguageCollap2.setVisibility(View.VISIBLE);
                    }
                    break;

                case TYPE_PORTUGUESE:
                    binding.languageES.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    binding.languageFR.imgSelected.setImageResource(R.drawable.ic_unchecked_language);
                    adapterPor.selectPosition(position);
                    adapterEng.unselectAll();
                    adapterHindi.unselectAll();

                    if (!isVisiblePor) {
                        isVisiblePor = true;
                        binding.rcvLanguageCollap3.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, boolean isItemLanguageSelected);
        void onPreviousPosition(int pos);
    }
}