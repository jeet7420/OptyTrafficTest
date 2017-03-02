package com.optytraffictest;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

//import Modules.Route;
/**
 * Created by smarhas on 2/20/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

