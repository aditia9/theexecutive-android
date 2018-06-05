package com.tsongkha.spinnerdatepicker;

import android.content.Context;

import java.util.Calendar;

public class SpinnerDatePickerDialogBuilder {

    private Context context;
    private DatePickerDialog.OnDateSetListener callBack;
    private int year = 1980;
    private int finalYear = Calendar.getInstance().get(Calendar.YEAR);
    private int monthOfYear = 0;            //months are indexed from 0
    private int dayOfMonth = 1;
    private boolean yearOptional = false;
    private int theme = -1;                 //default theme
    private int spinnerTheme = -1;          //default theme
    private int defaultStartYear = 1900;

    public SpinnerDatePickerDialogBuilder context(Context context) {
        this.context = context;
        return this;
    }

    public SpinnerDatePickerDialogBuilder callback(DatePickerDialog.OnDateSetListener callBack) {
        this.callBack = callBack;
        return this;
    }

    public SpinnerDatePickerDialogBuilder year(int year) {
        this.year = year;
        return this;
    }

    public SpinnerDatePickerDialogBuilder finalYear(int finalYear) {
        this.finalYear = finalYear;
        return this;
    }

    public SpinnerDatePickerDialogBuilder monthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
        return this;
    }

    public SpinnerDatePickerDialogBuilder dayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        return this;
    }


    public SpinnerDatePickerDialogBuilder defaultStartYear(int defaultStartYear) {
        this.defaultStartYear = defaultStartYear;
        return this;
    }
    public SpinnerDatePickerDialogBuilder dialogTheme(int theme) {
        this.theme = theme;
        return this;
    }

    public SpinnerDatePickerDialogBuilder spinnerTheme(int spinnerTheme) {
        this.spinnerTheme = spinnerTheme;
        return this;
    }

    public SpinnerDatePickerDialogBuilder yearOptional(boolean b) {
        this.yearOptional = b;
        return this;
    }

    public DatePickerDialog build() {
        if (context == null) throw new IllegalArgumentException("Context must not be null");

        return new DatePickerDialog(context, theme, spinnerTheme, callBack, year, finalYear, monthOfYear, dayOfMonth, yearOptional, defaultStartYear, finalYear);
    }
}