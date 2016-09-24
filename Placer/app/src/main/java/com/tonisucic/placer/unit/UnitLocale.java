package com.tonisucic.placer.unit;

import java.util.Locale;

/**
 * Created by tonisucic on 16.09.2016.
 *
 * Class taken from:
 * https://stackoverflow.com/questions/4898237/using-locale-settings-to-detect-wheter-to-use-imperial-units
 */
public class UnitLocale {

    public static UnitLocale imperial = new UnitLocale();
    public static UnitLocale metric = new UnitLocale();

    public static UnitLocale getDefault() {
        return getFrom(Locale.getDefault());
    }

    public static UnitLocale getFrom(Locale locale) {
        String countryCode = locale.getCountry();

        if ("US".equals(countryCode)) return imperial; // USA
        if ("LR".equals(countryCode)) return imperial; // liberia
        if ("MM".equals(countryCode)) return imperial; // burma

        return metric;
    }
}
