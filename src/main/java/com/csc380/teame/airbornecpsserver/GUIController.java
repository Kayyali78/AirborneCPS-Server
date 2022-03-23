package com.csc380.teame.airbornecpsserver;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapNotInitializedException;
import com.dlsc.gmapsfx.javascript.event.GMapMouseEvent;
import com.dlsc.gmapsfx.javascript.event.UIEventType;
import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MapOptions;
import com.dlsc.gmapsfx.javascript.object.MapTypeIdEnum;
import com.dlsc.gmapsfx.javascript.object.Marker;
import com.dlsc.gmapsfx.javascript.object.MarkerOptions;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GUIController implements Initializable {

    @FXML
    private ListView<?> ListADSB;

    @FXML
    private ListView<?> ListTCP;

    @FXML
    private ListView<?> ListUDP;

    @FXML
    private CheckBox f_opensky;

    @FXML
    private CheckBox f_tcp;

    @FXML
    private CheckBox f_udp;

    @FXML
    private GoogleMapView googleMapView;

    @FXML
    private Label latitudeLabel;

    @FXML
    private Label latitudeLabel1;

    @FXML
    private Label longitudeLabel;

    @FXML
    private Label planedetail;

    @FXML
    private Button refreshmap;

    @FXML
    private CheckBox view_adsb;

    @FXML
    private CheckBox view_tcp;

    @FXML
    private CheckBox view_udp;

    @FXML
    private Font x1;

    @FXML
    private Font x11;

    @FXML
    private Color x2;

    @FXML
    private Color x21;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    private GoogleMap map;

    private DecimalFormat formatter = new DecimalFormat("###.00000");
    
    @FXML
    public void r_map(ActionEvent event){
        try{
            googleMapView = new GoogleMapView();
            googleMapView.setKey("AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
            map = googleMapView.getMap();
        }catch(MapNotInitializedException ex){
            System.err.println("NOT initialize again");
        }
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        googleMapView = new GoogleMapView(true);
        googleMapView.setKey("");
        googleMapView.addMapInitializedListener(() -> configureMap());
    }

    protected void configureMap() {
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(40.748433,-73.985656))
                .mapType(MapTypeIdEnum.ROADMAP)
                .zoom(9);
        map = googleMapView.createMap(mapOptions, false);
        
        map.addMouseEventHandler(UIEventType.click, (GMapMouseEvent event) -> {
            LatLong latLong = event.getLatLong();
            latitudeLabel.setText(formatter.format(latLong.getLatitude()));
            longitudeLabel.setText(formatter.format(latLong.getLongitude()));
        });

        showMarker(40.748433, -73.985656,
                "https://i.imgur.com/m7rKNFx.png");
        showMarker(40.713, -74.0135,
                "https://i.imgur.com/m7rKNFx.png");

    }
    
    public void showMarker(double lat, double lng, String iconPath) {
        MarkerOptions options = new MarkerOptions();
        options.icon(iconPath).position(new LatLong(lat, lng));
        Marker marker = new Marker(options);
        map.addMarker(marker);
    }

}
