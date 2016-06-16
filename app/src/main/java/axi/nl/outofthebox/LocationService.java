package axi.nl.outofthebox;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationService extends IntentService {

    private static final Integer AVERAGE_OF = 200;

    private BeaconManager beaconManager;

    private Region region1;
    private Region region2;
    private Region region3;

    private LinkedList<Double> valuesRegion1 = new LinkedList<Double>();
    private LinkedList<Double> valuesRegion2 = new LinkedList<Double>();
    private LinkedList<Double> valuesRegion3 = new LinkedList<Double>();

    private Double avgBeacon1 = 0.0;
    private Double avgBeacon2 = 0.0;
    private Double avgBeacon3 = 0.0;

    private int counter = 0;

    private HashMap<String, LinkedList<Double>> averagesPerBeacon = new HashMap<String, LinkedList<Double>>();


    public LocationService() {
        super("LocationService");
    }

    @Override
    public void onCreate() {

        createBeaconManager();

        region1 = new Region("beacon-blauw",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 40988, 23163);
        region2 = new Region("beacon-paars",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 33360, 37117);
        region3 = new Region("beacon-groen",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 44421, 52376);

        // lijst van laatste x waarden koppelen aan beacon
        averagesPerBeacon.put("beacon-blauw", valuesRegion1);
        averagesPerBeacon.put("beacon-groen", valuesRegion2);
        averagesPerBeacon.put("beacon-paars", valuesRegion3);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int i, int b) {
        if (intent != null) {
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    beaconManager.startRanging(region1);
                    beaconManager.startRanging(region2);
                    beaconManager.startRanging(region3);
                }
            });
        }

        return Service.START_STICKY;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private void createBeaconManager() {
        if(beaconManager == null) {
            beaconManager = new BeaconManager(this);
            beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                    for(Beacon b : list) {
                        placesNearBeacon(region, b);
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {

        beaconManager.stopRanging(region1);
        beaconManager.stopRanging(region2);
        beaconManager.stopRanging(region3);

        super.onDestroy();
    }

    private void placesNearBeacon(Region region, Beacon beacon) {
        Double distance = measureDistance(beacon.getMeasuredPower(), beacon.getRssi());
        Double average = getAverage(region, distance);

        if (region1.equals(region)) {
            avgBeacon1 = average;
        }
        else if (region2.equals(region)) {
            avgBeacon2 = average;
        }
        else {
            avgBeacon3 = average;
        }

        ++counter;
        if (counter == 10) {
            counter = 0;
            WebSocketService.sendBeaconDistance(avgBeacon1, avgBeacon2, avgBeacon3);
        }

        Double ref = calcDistanceToReference();

//        Log.d("MainActivity", "region " + region.getIdentifier() + " Distance: " + distance);
//        Log.d("MainActivity", "average: " + average);
    }

    private Double measureDistance(Integer power, Integer rssi) {
        return Math.pow(10, ((power-rssi) / 20.0)); // willebroek samsung 21 / breda (zilver) 20
    }

    private Double getAverage(Region region, Double newDistance) {

        LinkedList<Double> distances = averagesPerBeacon.get(region.getIdentifier());

//        Log.d("MainActivity", "aantal waarden: " + distances.size());

        if(distances.size() == AVERAGE_OF) {
            distances.removeFirst();
        }
        distances.addLast(newDistance);

        List<Double> tmpList = new ArrayList<Double>(distances);
        Collections.sort(tmpList);

//        Log.d("MainActivityAvg", "Lijst heeft nu zoveel elementen: " + tmpList.size());
        if(tmpList.size() >= 10) {
            // percentage wat we negeren
            int perc = tmpList.size() / 10;
//            Log.d("MainActivityAvg", "Groter dan 10 dus aantal wat we gaan negeren (10%) afgerond: " + perc);
            tmpList = tmpList.subList(perc, tmpList.size() - perc);
//            Log.d("MainActivityAvg", "Lijst heeft nu zoveel elementen na percentage eraf: " + tmpList.size());
        }

        Double sum = 0.0;
        for(Double avg : tmpList) {
            sum += avg;
        }

        return (sum / tmpList.size());
    }

    private Double calcDistanceToReference() {
        double x = avgBeacon1;
        double y = avgBeacon2;
        double z = avgBeacon3;

        // X Blauw, Y paars, Z Groen
        return Math.sqrt( Math.pow((x-8), 2) + Math.pow((y-5), 2) + Math.pow((z-1), 2) );
    }
}
