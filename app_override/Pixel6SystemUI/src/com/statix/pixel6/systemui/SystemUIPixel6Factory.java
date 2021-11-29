package com.statix.pixel6.systemui;

import android.content.Context;

import com.statix.pixel6.systemui.dagger.DaggerGlobalRootComponentPixel6;
import com.statix.pixel6.systemui.dagger.GlobalRootComponentPixel6;

import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.GlobalRootComponent;

public class SystemUIPixel6Factory extends SystemUIFactory {
    @Override
    protected GlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerGlobalRootComponentPixel6.builder()
                .context(context)
                .build();
    }
}
