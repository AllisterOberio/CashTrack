package com.example.budgetify1;

import android.content.Context;
import android.content.SharedPreferences;

public class FinanceManager {
    private static final String PREFS_NAME = "FinancePrefs";
    private static final String KEY_TOTAL_BALANCE = "total_balance";
    private static final String KEY_TOTAL_EXPENSE = "total_expense";

    private static FinanceManager instance;
    private SharedPreferences prefs;

    private FinanceManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized FinanceManager getInstance(Context context) {
        if (instance == null) {
            instance = new FinanceManager(context);
        }
        return instance;
    }

    public double getTotalBalance() {
        return Double.longBitsToDouble(prefs.getLong(KEY_TOTAL_BALANCE, Double.doubleToLongBits(0.0)));
    }

    public void updateTotalBalance(double newBalance) {
        prefs.edit().putLong(KEY_TOTAL_BALANCE, Double.doubleToLongBits(newBalance)).apply();
    }

    public double getTotalExpense() {
        return Double.longBitsToDouble(prefs.getLong(KEY_TOTAL_EXPENSE, Double.doubleToLongBits(0.0)));
    }

    public void updateTotalExpense(double newExpense) {
        prefs.edit().putLong(KEY_TOTAL_EXPENSE, Double.doubleToLongBits(newExpense)).apply();
    }
}
