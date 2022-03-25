package com.csc380.teame.airbornecpsserver;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GUIController implements Initializable {

    @FXML
    private Label ICAOLabel;

    @FXML
    private ListView<Plane> ListADSB;

    @FXML
    private ListView<Plane> ListTCP;

    @FXML
    private ListView<Plane> ListUDP;

    @FXML
    private Label altitudeLabel;

    @FXML
    private Label callLabel;

    @FXML
    private CheckBox f_opensky;

    @FXML
    private CheckBox f_tcp;

    @FXML
    private CheckBox f_udp;

    @FXML
    private GoogleMapView googleMapView;

    @FXML
    private Label headingLabel;

    @FXML
    private Label latitudeLabel;

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

    private GoogleMap map;

    ServerController controller = new ServerController();
    private final Object lock = new Object();
    private static boolean updateStatus = false;
    protected HashSet<Marker> markers;
    private DecimalFormat formatter = new DecimalFormat("###.00000");
    private HashSet<Marker> ADSBMarker = new HashSet<>();
    private HashSet<Marker> TCPMarker = new HashSet<>();
    private HashSet<Marker> UDPMarker = new HashSet<>();

    @FXML
    public void r_map(ActionEvent event) {
        try {
            googleMapView = new GoogleMapView();
            googleMapView.setKey("AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
            map = googleMapView.getMap();
        } catch (MapNotInitializedException ex) {
            System.err.println("NOT initialize again");
        }

    }

    @FXML
    void adsbclicked(MouseEvent event) {
        Plane p = ListADSB.getSelectionModel().getSelectedItem();
        updateLabel(p);
    }

    @FXML
    void tcpclicked(MouseEvent event) {
        Plane p = ListTCP.getSelectionModel().getSelectedItem();
        updateLabel(p);
    }

    @FXML
    void udpclicked(MouseEvent event) {
        Plane p = ListUDP.getSelectionModel().getSelectedItem();
        updateLabel(p);
    }


    public void update() {
        synchronized (lock) {
            if (updateStatus == true) {
                ObservableList<Plane> observableArrayListUDP = FXCollections
                        .observableArrayList(controller.getUDPList());
                ListUDP.setItems(observableArrayListUDP);
                ListUDP.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                ObservableList<Plane> observableArrayListTCP = FXCollections
                        .observableArrayList(controller.getTCPList());
                ListTCP.setItems(observableArrayListTCP);
                ListTCP.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                ObservableList<Plane> observableArrayListADSB = FXCollections
                        .observableArrayList(controller.getADSBList());
                ListADSB.setItems(observableArrayListADSB);
                ListADSB.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                toMarkers();
                map.clearMarkers();
                map.addMarkers(markers);
                updateStatus = false;

            }
        }
    }

    public void backgroundupdate() {

        controller.getOpensky();

        updateStatus = true;

    }

    public void toMarkers() {

        markers = new HashSet<>();
        //MarkerOptions options = new MarkerOptions();
        String iconPath = "https://i.imgur.com/23cmzJk.png";
        for (Plane p : ListADSB.getItems()) {
            MarkerOptionsAlt options = new MarkerOptionsAlt();
            options.rotation((int)p.heading);
            options.icon(iconPath).position(new LatLong(p.lat, p.lon));
            Marker marker = new Marker(options);
            markers.add(marker);
        }

    }

    public void updateLabel(Plane p) {

        latitudeLabel.setText(formatter.format(p.lat));
        longitudeLabel.setText(formatter.format(p.lon));
        altitudeLabel.setText(formatter.format(p.alt));
        ICAOLabel.setText(p.mac);
        callLabel.setText(p.ip);
        headingLabel.setText(formatter.format(p.heading));
        map.setCenter(new LatLong(p.lat, p.lon));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // googleMapView = new GoogleMapView();
        googleMapView.setKey("AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
        googleMapView.addMapInitializedListener(() -> configureMap());
        Timeline fiveSecondsWonder = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                // System.out.println("this is called every 5 seconds on UI thread");
                                update();
                            }
                        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

        new Thread() {

            // runnable for that thread
            public void run() {
                while (true) {

                    try {
                        // imitating work
                        backgroundupdate();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // update ProgressIndicator on FX thread

            }
        }.start();

        view_adsb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                //view_adsb.setSelected(!newValue);

                //fire command to javafx thread
                synchronized (lock){
                    map.clearMarkers();
                }
            }
        });
    }

    protected void configureMap() {
        MapOptions mapOptions = new MapOptions();
        // googleMapView.setKey("AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
        mapOptions.center(new LatLong(40.748433, -73.985656))
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
