package com.csc380.teame.airbornecpsserver;
import com.dlsc.gmapsfx.javascript.object.MarkerOptions;
import com.dlsc.gmapsfx.javascript.JavascriptObject;

public class MarkerOptionsAlt extends MarkerOptions {
    public MarkerOptionsAlt(){
        super();
    }
    public MarkerOptionsAlt rotation(int degree){
        setProperty("rotation",degree);
        return this;
    }
}
