package com.br.retcam;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * Classe de aplicação personalizada para suportar MultiDex
 */
public class RetCamApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
