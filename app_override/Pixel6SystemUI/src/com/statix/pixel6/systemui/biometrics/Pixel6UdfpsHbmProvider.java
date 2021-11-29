package com.statix.pixel6.systemui.biometrics;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.Surface;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.systemui.biometrics.UdfpsHbmProvider;
import com.android.systemui.biometrics.UdfpsHbmTypes.HbmType;
import com.android.systemui.dagger.SysUISingleton;

import com.google.hardware.pixel.display.IDisplay;

import com.statix.android.systemui.biometrics.StatixUdfpsHbmProvider;

import javax.inject.Inject;

@SysUISingleton
public class Pixel6UdfpsHbmProvider extends StatixUdfpsHbmProvider implements IBinder.DeathRecipient, UdfpsHbmProvider {

    private volatile IDisplay mDisplayHal;

    @Inject
    public Pixel6UdfpsHbmProvider() {
        super();
        getDisplayHal();
    }

    private IDisplay getDisplayHal() {
        IDisplay iDisplay = this.mDisplayHal;
        if (iDisplay != null) {
            return iDisplay;
        }
        IBinder waitForDeclaredService = ServiceManager.waitForDeclaredService("com.google.hardware.pixel.display.IDisplay/default");
        if (waitForDeclaredService == null) {
            Log.e("UdfpsLhbmProvider", "getDisplayHal | Failed to find the Display HAL");
            return null;
        }
        try {
            waitForDeclaredService.linkToDeath(this, 0);
            this.mDisplayHal = IDisplay.Stub.asInterface(waitForDeclaredService);
            return this.mDisplayHal;
        } catch (RemoteException e) {
            Log.e("UdfpsLhbmProvider", "getDisplayHal | Failed to link to death", e);
            return null;
        }
    }

    @Override
    public void binderDied() {
        Log.e("UdfpsLhbmProvider", "binderDied | Display HAL died");
        this.mDisplayHal = null;
    }

    @Override
    public void enableHbm(@HbmType int hbmType, @Nullable Surface surface,
            @Nullable Runnable onHbmEnabled) {
        enableLhbm();
        if (onHbmEnabled != null) {
            mHandler.post(onHbmEnabled);
        }
    }

    @Override
    public void disableHbm(@Nullable Runnable onHbmDisabled) {
        disableLhbm();
        if (onHbmDisabled != null) {
            mHandler.post(onHbmDisabled);
        }
    }

    private void disableLhbm() {
        Log.v("UdfpsLhbmProvider", "disableLhbm");
        IDisplay displayHal = getDisplayHal();
        if (displayHal == null) {
            Log.e("UdfpsLhbmProvider", "disableLhbm | displayHal is null");
            return;
        }
        try {
            displayHal.setLhbmState(false);
        } catch (RemoteException e) {
            Log.e("UdfpsLhbmProvider", "disableLhbm | RemoteException", e);
        }
    }

    private void enableLhbm() {
        Log.v("UdfpsLhbmProvider", "enableLhbm");
        IDisplay displayHal = getDisplayHal();
        if (displayHal == null) {
            Log.e("UdfpsLhbmProvider", "enableLhbm | displayHal is null");
            return;
        }
        try {
            displayHal.setLhbmState(true);
        } catch (RemoteException e) {
            Log.e("UdfpsLhbmProvider", "enableLhbm | RemoteException", e);
        }
    }

}
