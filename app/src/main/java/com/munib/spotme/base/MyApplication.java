package com.munib.spotme.base;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_live_51HWjStDSyQAULGzF2rsOAVgCzGRefNmBV1ixjfXsEFF1CoCFrdtXkbMFC4PUFPh0VcXQyVXIqYGQC4w91E806jLh00KNeqJi3F"
        );
//        PaymentConfiguration.init(
//                getApplicationContext(),
//                "pk_test_51HWjStDSyQAULGzF03YAQkjNChkFTY4HEPrVTIkktmW8DSLzy4TxMMDJ5yFzt1av6heDDcalEOlh4W9zoHX1Yrzb00y6kH76fJ"
//        );

    }
}