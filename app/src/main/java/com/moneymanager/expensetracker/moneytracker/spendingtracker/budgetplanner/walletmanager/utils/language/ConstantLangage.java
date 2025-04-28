package com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.utils.language;



import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.R;
import com.moneymanager.expensetracker.moneytracker.spendingtracker.budgetplanner.walletmanager.model.LanguageModel;

import java.util.ArrayList;

public class ConstantLangage {

    public static ArrayList<LanguageModel> getLanguage1() {
        ArrayList<LanguageModel> listLanguage = new ArrayList<>();
        listLanguage.add(new LanguageModel("English (US)", "en", false, R.drawable.flag_us));
        listLanguage.add(new LanguageModel("English (UK)", "en", false, R.drawable.flag_en_en));
        listLanguage.add(new LanguageModel("English (Canada)", "en", false, R.drawable.flag_ca));
        listLanguage.add(new LanguageModel("English (South Africa)", "en", false, R.drawable.flag_sou));
        return listLanguage;
    }

    public static ArrayList<LanguageModel> getLanguage2() {
        ArrayList<LanguageModel> listLanguage = new ArrayList<>();
        listLanguage.add(new LanguageModel("Hindi", "hi", false,0));
        listLanguage.add(new LanguageModel("Bengani", "hi", false, 0));
        listLanguage.add(new LanguageModel("Marathi", "hi", false, 0));
        listLanguage.add(new LanguageModel("Telugu", "hi", false,0));
        listLanguage.add(new LanguageModel("Tamil", "hi", false,0));
        listLanguage.add(new LanguageModel("Urdu", "hi", false, 0));
        listLanguage.add(new LanguageModel("Kannada", "hi", false, 0));
        listLanguage.add(new LanguageModel("Odia", "hi", false,0));
        listLanguage.add(new LanguageModel("Malayalam", "hi", false,0));
        return listLanguage;
    }

    public static ArrayList<LanguageModel> getLanguage3() {
        ArrayList<LanguageModel> listLanguage = new ArrayList<>();
        listLanguage.add(new LanguageModel("Portuguese (Brazil)", "hi", false, R.drawable.flag_bra));
        listLanguage.add(new LanguageModel("Portuguese(Europeu)", "hi", false,  R.drawable.flag_euro));
        listLanguage.add(new LanguageModel("Portuguese(Angona)", "hi", false,  R.drawable.flag_angola));
        listLanguage.add(new LanguageModel("Portuguese(Mozambique)", "hi", false, R.drawable.flag_mozam));
        return listLanguage;
    }
}

