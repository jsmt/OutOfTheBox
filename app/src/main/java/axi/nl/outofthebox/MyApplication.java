package axi.nl.outofthebox;

import android.app.Application;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import java.util.UUID;

/**
 * Created by rdkl on 16-6-2016.
 */
public class MyApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(getApplicationContext(), "outofthebox-4c6",
                "3c9cfc75eed644e9fc39493a2df513c1");

        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);



    }
}