package com.csc380.teame.airbornecpsserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapNotInitializedException;
import com.dlsc.gmapsfx.javascript.event.GMapMouseEvent;
import com.dlsc.gmapsfx.javascript.event.UIEventType;
import com.dlsc.gmapsfx.javascript.object.GoogleMap;
import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MVCArray;
import com.dlsc.gmapsfx.javascript.object.MapOptions;
import com.dlsc.gmapsfx.javascript.object.MapTypeIdEnum;
import com.dlsc.gmapsfx.javascript.object.Marker;
import com.dlsc.gmapsfx.javascript.object.MarkerOptions;
import com.dlsc.gmapsfx.shapes.Polyline;
import com.dlsc.gmapsfx.shapes.PolylineOptions;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private TextArea termial;

    @FXML
    private Label callLabel;

    @FXML
    private AnchorPane apane;

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private SplitPane spane;

    @FXML
    private CheckBox f_opensky;

    @FXML
    private CheckBox f_tcp;

    @FXML
    private CheckBox f_udp;

    @FXML
    private TabPane srcTabPane;

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
    private Label speedLabel;

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
    private final Object ADSBlock = new Object();
    private static boolean updateStatus = false;
    protected HashSet<Marker> markers = new HashSet<Marker>();
    protected HashMap<String, Marker> markersmap = new HashMap<String, Marker>();
    private DecimalFormat formatter = new DecimalFormat("###.00000");
    private HashSet<Marker> ADSBMarker = new HashSet<>();
    private HashSet<Marker> TCPMarker = new HashSet<>();
    private HashSet<Marker> UDPMarker = new HashSet<>();
    public static Plane selectedPlaneHistory = null;
    public static final String iconPath = "https://i.imgur.com/23cmzJk.png";
    public static final String PlaneBlue = "https://i.imgur.com/5UoLwPH.png";
    public static final String Plane0 = "https://i.imgur.com/VVbEWFQ.png";
    public static final String Plane22_5 = "https://i.imgur.com/QsY95XU.png";
    public static final String Plane45 = "https://i.imgur.com/cZHwaUF.png";
    public static final String Plane67_5 = "https://i.imgur.com/jMF3BB6.png";
    public static final String Plane90 = "https://i.imgur.com/UHfoEa8.png";
    public static final String Plane112_5 = "https://i.imgur.com/ZfMRkbz.png";
    public static final String Plane135 = "https://i.imgur.com/F4twXWO.png";
    public static final String Plane157_5 = "https://i.imgur.com/XtsYSf3.png";
    public static final String Plane180 = "https://i.imgur.com/SRg3wfq.png";
    public static final String Plane202_5 = "https://i.imgur.com/TB8KGOc.png";
    public static final String Plane225 = "https://i.imgur.com/NNmYKTp.png";
    public static final String Plane247_5 = "https://i.imgur.com/toeMBlj.png";
    public static final String Plane270 = "https://i.imgur.com/L2SMO2o.png";
    public static final String Plane292_5 = "https://i.imgur.com/WA54tCM.png";
    public static final String Plane315 = "https://i.imgur.com/ndQciWG.png";
    public static final String Plane337_5 = "https://i.imgur.com/VPt0FtI.png";
    // PipedOutputStream pout = new PipedOutputStream(this.pipeIn);
    // PrintStream printStream = new PrintStream(new CustomOutputStream(termial));

    @FXML
    public void tcpInjection(ActionEvent event) {
        try {
            SampleClient sclient = new SampleClient();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void udpInjection(ActionEvent event) {
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        UDPHandler udpInjector = new UDPHandler(12345, UDPHandler.mode.slower, "127.0.0.2");
                        udpInjector.fillList();
                        udpInjector.serve();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void r_map(ActionEvent event) {

        // final Stage myDialog = new Stage();
        // myDialog.initModality(Modality.WINDOW_MODAL);
        // VBox dialogVbox = new VBox(20);
        // Scene dialogScene = new Scene(dialogVbox, 300, 200);
        // myDialog.setScene(dialogScene);
        // myDialog.show();
        synchronized (lock) {
            try {
                // googleMapView = new GoogleMapView();
                // googleMapView.setKey("AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
                map = googleMapView.getMap();
            } catch (MapNotInitializedException ex) {
                System.err.println("NOT initialize again");
            }
        }

    }

    @FXML
    void adsbclicked(MouseEvent event) {
        Plane p = ListADSB.getSelectionModel().getSelectedItem();
        updateLabel(p);
        map.removeMarker(markersmap.get(p.mac));
        map.addMarker(toBlueMarker(p));
        selectedPlaneHistory = p;
        Task<ArrayList<Plane>> gethistoryOpen = new Task<ArrayList<Plane>>() {
            @Override
            public ArrayList<Plane> call() throws Exception {
                // do background update here;
                // return new ArrayList<Plane>();
                return controller.getSomePlane(selectedPlaneHistory);
            }
        };
        gethistoryOpen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println(Thread.currentThread().getName() + " finishes the work");
                ArrayList<Plane> ar = gethistoryOpen.getValue();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        plotPlane(gethistoryOpen.getValue());
                    }
                });
            }
        });
        gethistoryOpen.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.err.println(Thread.currentThread().getName() + " fails the task");
            }
        });
        Thread thread_gethistory = new Thread(gethistoryOpen);
        thread_gethistory.setDaemon(true);
        thread_gethistory.start();
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

            long t1 = System.currentTimeMillis();

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
            long t2 = System.currentTimeMillis();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.print(dtf.format(now));
            System.out.println(Thread.currentThread().getName() + " ADSB update took: " + (t2 - t1) + " ms");

            // updateStatus = false;

        }
    }

    public void backgroundupdate() {

//        controller.getOpensky();

        updateStatus = true;

    }

    public void plotPlane(ArrayList<Plane> hp) {
        // LatLong [] arr = null;
        if (hp.size() <= 0)
            return;
        ArrayList<LatLong> ll = new ArrayList<>();
        for (Plane p : hp) {
            ll.add(new LatLong(p.lat, p.lon));
        }
        synchronized (lock) {
            try {
                PolylineOptions line_opt;
                Polyline line;
                line_opt = new PolylineOptions();
                line_opt.path(new MVCArray(ll.toArray()))
                        .clickable(false)
                        .draggable(false)
                        .editable(false)
                        .strokeColor("#ff4500")
                        .strokeWeight(2)
                        .visible(true);

                line = new Polyline(line_opt);
                map.addMapShape(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // add the Plotline which extends Mapshape to map

    }

    public void toMarkers() {

        ADSBMarker = new HashSet<>();
        markersmap = new HashMap<String, Marker>();
        // MarkerOptions options = new MarkerOptions();

        if (p.heading <= (0 + 12.25) || p.heading >= (360 - 12.25)) {
                options.icon(Plane0);
            } else if (p.heading <= (335 + 12.25) && p.heading >= (335 - 12.25)) {
                options.icon(Plane337_5);
            } else if (p.heading <= (310 + 12.25) && p.heading >= (310 - 12.25)) {
                options.icon(Plane315);
            } else if (p.heading <= (285 + 12.25) && p.heading >= (285 - 12.25)) {
                options.icon(Plane292_5);
            } else if (p.heading <= (260 + 12.25) && p.heading >= (260 - 12.25)) {
                options.icon(Plane270);
            } else if (p.heading <= (235 + 12.25) && p.heading >= (235 - 12.25)) {
                options.icon(Plane247_5);
            } else if (p.heading <= (210 + 12.25) && p.heading >= (210 - 12.25)) {
                options.icon(Plane225);
            } else if (p.heading <= (185 + 12.25) && p.heading >= (185 - 12.25)) {
                options.icon(Plane202_5);
            } else if (p.heading <= (160 + 12.25) && p.heading >= (160 - 12.25)) {
                options.icon(Plane180);
            } else if (p.heading <= (135 + 12.25) && p.heading >= (135 - 12.25)) {
                options.icon(Plane157_5);
            } else if (p.heading <= (110 + 12.25) && p.heading >= (110 - 12.25)) {
                options.icon(Plane112_5);
            } else if (p.heading <= (85 + 12.25) && p.heading >= (85 - 12.25)) {
                options.icon(Plane90);
            } else if (p.heading <= (60 + 12.25) && p.heading >= (60 - 12.25)) {
                options.icon(Plane67_5);
            } else if (p.heading <= (35 + 12.25) && p.heading >= (35 - 12.25)) {
                options.icon(Plane45);
            } else if (p.heading <= (10 + 12.25)) {
                options.icon(Plane22_5);
            }
            options.position(new LatLong(p.lat, p.lon));
            Marker marker = new Marker(options);
            ADSBMarker.add(marker);
            markersmap.put(p.mac, marker);
        }

    }

    public void updateMarkers(){ 
        markers.clear();
        markers.addAll(ADSBMarker);
        markers.addAll(UDPMarker);
        markers.addAll(TCPMarker);
        map.clearMarkers();
        map.addMarkers(markers);
    }

    public Marker toMarker(Plane p) {
        MarkerOptionsAlt options = new MarkerOptionsAlt();
        options.rotation((int) p.heading);
        if (p.heading <= (0 + 12.25) || p.heading >= (360 - 12.25)) {
                options.icon(Plane0);
            } else if (p.heading <= (335 + 12.25) && p.heading >= (335 - 12.25)) {
                options.icon(Plane337_5);
            } else if (p.heading <= (310 + 12.25) && p.heading >= (310 - 12.25)) {
                options.icon(Plane315);
            } else if (p.heading <= (285 + 12.25) && p.heading >= (285 - 12.25)) {
                options.icon(Plane292_5);
            } else if (p.heading <= (260 + 12.25) && p.heading >= (260 - 12.25)) {
                options.icon(Plane270);
            } else if (p.heading <= (235 + 12.25) && p.heading >= (235 - 12.25)) {
                options.icon(Plane247_5);
            } else if (p.heading <= (210 + 12.25) && p.heading >= (210 - 12.25)) {
                options.icon(Plane225);
            } else if (p.heading <= (185 + 12.25) && p.heading >= (185 - 12.25)) {
                options.icon(Plane202_5);
            } else if (p.heading <= (160 + 12.25) && p.heading >= (160 - 12.25)) {
                options.icon(Plane180);
            } else if (p.heading <= (135 + 12.25) && p.heading >= (135 - 12.25)) {
                options.icon(Plane157_5);
            } else if (p.heading <= (110 + 12.25) && p.heading >= (110 - 12.25)) {
                options.icon(Plane112_5);
            } else if (p.heading <= (85 + 12.25) && p.heading >= (85 - 12.25)) {
                options.icon(Plane90);
            } else if (p.heading <= (60 + 12.25) && p.heading >= (60 - 12.25)) {
                options.icon(Plane67_5);
            } else if (p.heading <= (35 + 12.25) && p.heading >= (35 - 12.25)) {
                options.icon(Plane45);
            } else if (p.heading <= (10 + 12.25)) {
                options.icon(Plane22_5);
            }
        options.position(new LatLong(p.lat, p.lon));
        return new Marker(options);
    }

    public Marker toBlueMarker(Plane p) {
        MarkerOptionsAlt options = new MarkerOptionsAlt();
        options.rotation("[" + String.valueOf(p.heading) + "]").icon(PlaneBlue).position(new LatLong(p.lat, p.lon));
        return new Marker(options);
    }

    public void updateLabel(Plane p) {

        latitudeLabel.setText(formatter.format(p.lat));
        longitudeLabel.setText(formatter.format(p.lon));
        altitudeLabel.setText(formatter.format(p.alt));
        ICAOLabel.setText(p.mac);
        callLabel.setText(p.ip);
        headingLabel.setText(formatter.format(p.heading));
        map.setCenter(new LatLong(p.lat, p.lon));
        speedLabel.setText(formatter.format(p.speed));
    }

    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            // show alert to user
            e.printStackTrace();

            // do whatever you want with the Exception e
            Platform.runLater(new Runnable() {
                public void run() {
                    termial.appendText(e.getMessage());
                }
            });
        });
        // googleMapView = new
        // GoogleMapView(null,"en-US","AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70",false);
        // googleMapView = new
        // GoogleMapView(null,"AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
        googleMapView.setKey("AIzaSyAHBxuvQP1YzTjvx6Q2z50B0OaVkJROM70");
        googleMapView.addMapInitializedListener(this::configureMap);
        Timeline fiveSecondsWonder = new Timeline(
                new KeyFrame(Duration.seconds(10),
                        new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                // System.out.println("this is called every 5 seconds on UI thread");
                                // long t1 = System.currentTimeMillis();
                                // //update();
                                // long t2 = System.currentTimeMillis();
                                // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                // LocalDateTime now = LocalDateTime.now();
                                // System.out.print(dtf.format(now));
                                // System.out.println(Thread.currentThread().getName() + " ADSB update took: " +
                                // (t2-t1) + " ms" );
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
                        synchronized (ADSBlock) {
                            backgroundupdate();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // update ProgressIndicator on FX thread

            }
        }.start();

        // view_adsb.selectedProperty().addListener(new ChangeListener<Boolean>() {
        //     @Override
        //     public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //         // view_adsb.setSelected(!newValue);

        //         // fire command to javafx thread
        //         synchronized (lock) {
        //             if (newValue) {
        //                 update();
        //             } else if (oldValue) {
        //                 map.clearMarkers();

        //             }
        //             System.out.println(observable);

        //         }
        //     }
        // });
        // view_tcp.selectedProperty().addListener(new ChangeListener<Boolean>() {
        //     @Override
        //     public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //         // view_adsb.setSelected(!newValue);

        //         // fire command to javafx thread
        //         synchronized (lock) {
        //             if (newValue) {
        //                 update();
        //             } else if (oldValue) {
        //                 map.clearMarkers();
        //             }
        //             System.out.println(observable);
        //         }
        //     }
        // });
        // view_udp.selectedProperty().addListener(new ChangeListener<Boolean>() {
        //     @Override
        //     public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //         // view_adsb.setSelected(!newValue);

        //         // fire command to javafx thread
        //         synchronized (lock) {
        //             if (newValue) {
        //                 update();
        //             } else if (oldValue) {
        //                 map.clearMarkers();

        //             }
        //             System.out.println(observable);

        //         }
        //     }
        // });
        Timeline fiveSecondsUDP = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                ArrayList<Plane> udptemp = new ArrayList<>();
                                System.out.println("Getting udplist from controller");
                                udptemp = controller.getUDPList();
                                System.out.println("UDPlist size " + udptemp.size());
                                ObservableList<Plane> observableArrayListUDP = FXCollections
                                        .observableArrayList(udptemp);
                                ListUDP.setItems(observableArrayListUDP);
                                ListUDP.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                                ArrayList<Marker> udpMarker = new ArrayList<>();
                                for (Plane p : udptemp) {
                                    Marker m = toMarker(p);
                                    udpMarker.add(m);
                                    // markers.add(m);
                                    // markersmap.put(p.mac,m);
                                }
                                updateMarkers();
                            }
                        }));
        fiveSecondsUDP.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsUDP.play();

        srcTabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {

                        if (t1.getText().equals("UDP")) {
                            fiveSecondsUDP.play();
                        } else {
                            fiveSecondsUDP.stop();
                        }

                        System.out.println("Tab Selection changed to " + t1.toString());
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

        // showMarker(40.748433, -73.985656,
        // "https://i.imgur.com/m7rKNFx.png");
        // showMarker(40.713, -74.0135,
        // "https://i.imgur.com/m7rKNFx.png");

    }

    public void showMarker(double lat, double lng, String iconPath) {
        MarkerOptions options = new MarkerOptions();
        options.icon(iconPath).position(new LatLong(lat, lng));
        Marker marker = new Marker(options);
        map.addMarker(marker);
    }

    public void updateBbox(ActionEvent actionEvent) {
        update();
    }

    public class CustomOutputStream extends OutputStream {
        private TextArea terminal;

        public CustomOutputStream(TextArea terminal) {
            this.terminal = terminal;
        }

        @Override
        public void write(int b) throws IOException {
            terminal.setText(terminal.getText() + String.valueOf((char) b));
        }
    }
}
