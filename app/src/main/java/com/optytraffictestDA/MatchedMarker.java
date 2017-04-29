package com.optytraffictestDA;

/**
 * Created by smarhas on 4/9/2017.
 */

public class MatchedMarker {

    public String markerId;
    public String ts;

    public MatchedMarker(String markerId, String ts){
        this.markerId = markerId;
        this.ts = ts;
    }

    public String toString(){
        String s = "markerId " + this.markerId + " ts " + this.ts;
        return s;
    }
}
