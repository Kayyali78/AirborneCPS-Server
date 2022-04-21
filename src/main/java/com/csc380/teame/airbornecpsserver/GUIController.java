package com.csc380.teame.airbornecpsserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusData;
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
import javafx.collections.ObservableSet;

public class GUIController implements Initializable {
    private static final Logger logger = LogManager.getLogger(GUIController.class);

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
    private TextField tcpServerPort;

    @FXML
    private TextField tcpTargetPort;

    @FXML
    private TextArea termial;

    @FXML
    private TextField udpPort;

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
    protected HashMap<Plane, Marker> markersmap = new HashMap<Plane, Marker>();
    private DecimalFormat formatter = new DecimalFormat("###.00000");
    // private HashSet<Marker> ADSBMarker = new HashSet<>();
    // private HashSet<Marker> TCPMarker = new HashSet<>();
    // private HashSet<Marker> UDPMarker = new HashSet<>();
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
                        setName("udpInjectorThread");
                        UDPHandler udpInjector = new UDPHandler(12345, UDPHandler.mode.slower, "127.0.0.2");
                        udpInjector.fillList();
                        udpInjector.renewThread();
                        udpInjector.serveWriterThread();
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
    public void resetHandler(ActionEvent event) {
        this.controller.udpHandler.resetSocket(Integer.parseInt(udpPort.getText()));
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
        //mapmarkers.put(
        map.removeMarker(markersmap.get(p));
        map.addMarker(toBlueMarker(p));
        selectedPlaneHistory = p;
        Task<HashSet<Plane>> gethistoryOpen = new Task<HashSet<Plane>>() {
            @Override
            public HashSet<Plane> call() throws Exception {
                // do background update here;
                // return new HashSet<Plane>();
                return controller.getSomePlane(selectedPlaneHistory);
            }
        };
        gethistoryOpen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                logger.info(Thread.currentThread().getName() + " finishes the work");
                HashSet<Plane> ar = gethistoryOpen.getValue();
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

            //udp section
            HashSet<Plane> udptemp = controller.getUDPList();
            logger.info("Getting udplist from controller");
            logger.info("UDPlist size " + udptemp.size());
            ObservableList<Plane> observableListUDP = FXCollections
                    .observableList(new ArrayList<Plane>(udptemp));
            ListUDP.setItems(observableListUDP);
            ListUDP.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            
            //tcp section
            HashSet<Plane> tcptemp = new HashSet<>();
            logger.info("Getting tcplist from controller");
            tcptemp = controller.getTCPList();
            logger.info("TCPlist size " + tcptemp.size());
            ObservableList<Plane> observableHashSetTCP = FXCollections
                    .observableList(new ArrayList<Plane>(tcptemp));
            ListTCP.setItems(observableHashSetTCP);
            ListTCP.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            //adsb section
            HashSet<Plane> adsbtemp = new HashSet<>();
            logger.info("Getting adsblist from controller");
            adsbtemp = controller.getADSBList();
            logger.info("ADSBlist size " + adsbtemp.size());
            ObservableList<Plane> observableHashSetADSB = FXCollections
                    .observableList(new ArrayList<Plane>(adsbtemp));
            ListADSB.setItems(observableHashSetADSB);
            ListADSB.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            
            long t2 = System.currentTimeMillis();
            logger.info("ListView update took: " + (t2 - t1) + " ms");

            // updateStatus = false;

        }
    }

    public void backgroundupdate() {
        controller.getOpensky();
        updateStatus = true;
    }

    public void plotPlane(HashSet<Plane> hp) {
        // LatLong [] arr = null;
        if (hp.size() <= 0)
            return;
        HashSet<LatLong> ll = new HashSet<>();
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
        //ADSBMarker = new HashSet<>();
        // markersmap = new HashMap<Plane, Marker>();
        // markersmap.clear();
        // MarkerOptions options = new MarkerOptions();

        for (Plane p : ListADSB.getItems()) {
            MarkerOptionsAlt options = new MarkerOptionsAlt();
            options.rotation((int) p.heading);
            if (p.heading <= (11.25) || p.heading >= (348.75)) {
                options.icon(Plane0);
            } else if (p.heading <= (348.75) && p.heading >= (326.25)) {
                options.icon(Plane337_5);
            } else if (p.heading <= (326.25) && p.heading >= (303.75)) {
                options.icon(Plane315);
            } else if (p.heading <= (303.75) && p.heading >= (281.25)) {
                options.icon(Plane292_5);
            } else if (p.heading <= (281.25) && p.heading >= (258.75)) {
                options.icon(Plane270);
            } else if (p.heading <= (258.75) && p.heading >= (236.25)) {
                options.icon(Plane247_5);
            } else if (p.heading <= (236.25) && p.heading >= (213.75)) {
                options.icon(Plane225);
            } else if (p.heading <= (213.75) && p.heading >= (191.25)) {
                options.icon(Plane202_5);
            } else if (p.heading <= (191.25) && p.heading >= (168.75)) {
                options.icon(Plane180);
            } else if (p.heading <= (168.75) && p.heading >= (146.25)) {
                options.icon(Plane157_5);
            } else if (p.heading <= (146.25) && p.heading >= (123.75)) {
                options.icon(Plane135);
            }else if (p.heading <= (123.75) && p.heading >= (101.25)){
                options.icon(Plane112_5);
            } else if (p.heading <= (101.25) && p.heading >= (78.75)) {
                options.icon(Plane90);
            } else if (p.heading <= (78.75) && p.heading >= (56.25)) {
                options.icon(Plane67_5);
            } else if (p.heading <= (56.25) && p.heading >= (33.75)) {
                options.icon(Plane45);
            } else if (p.heading <= (33.75) && p.heading >= (11.25)) {
                options.icon(Plane22_5);
            }
            options.position(new LatLong(p.lat, p.lon));
            Marker marker = new Marker(options);
            //ADSBMarker.add(marker);
            markersmap.put(p, marker);
        }

    }

    public void updateMarkers(){ 
        markers.clear();
        // markers.addAll(ADSBMarker);
        // markers.addAll(UDPMarker);
        // markers.addAll(TCPMarker);
        map.clearMarkers();
        map.addMarkers(markersmap.values());
    }

    public Marker toMarker(Plane p) {
        MarkerOptionsAlt options = new MarkerOptionsAlt();
        options.rotation((int) p.heading);
        if (p.heading <= (11.25) || p.heading >= (348.75)) {
            options.icon(Plane0);
        } else if (p.heading <= (348.75) && p.heading >= (326.25)) {
            options.icon(Plane337_5);
        } else if (p.heading <= (326.25) && p.heading >= (303.75)) {
            options.icon(Plane315);
        } else if (p.heading <= (303.75) && p.heading >= (281.25)) {
            options.icon(Plane292_5);
        } else if (p.heading <= (281.25) && p.heading >= (258.75)) {
            options.icon(Plane270);
        } else if (p.heading <= (258.75) && p.heading >= (236.25)) {
            options.icon(Plane247_5);
        } else if (p.heading <= (236.25) && p.heading >= (213.75)) {
            options.icon(Plane225);
        } else if (p.heading <= (213.75) && p.heading >= (191.25)) {
            options.icon(Plane202_5);
        } else if (p.heading <= (191.25) && p.heading >= (168.75)) {
            options.icon(Plane180);
        } else if (p.heading <= (168.75) && p.heading >= (146.25)) {
            options.icon(Plane157_5);
        } else if (p.heading <= (146.25) && p.heading >= (123.75)) {
            options.icon(Plane135);
        } else if (p.heading <= (123.75) && p.heading >= (101.25)) {
            options.icon(Plane112_5);
        } else if (p.heading <= (101.25) && p.heading >= (78.75)) {
            options.icon(Plane90);
        } else if (p.heading <= (78.75) && p.heading >= (56.25)) {
            options.icon(Plane67_5);
        } else if (p.heading <= (56.25) && p.heading >= (33.75)) {
            options.icon(Plane45);
        } else if (p.heading <= (33.75) && p.heading >= (11.25)) {
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


    /**
     * @param
     * Plane p
     * 
     * @TODO
     * 1.setLabel
     * 2.setCenter
     */
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

    public void relayTransmission(){
        HashSet<Plane> toudp = new HashSet<>();
        HashSet<Plane> totcp = new HashSet<>();
        if(f_udp.isSelected()){
            totcp.addAll(controller.getUDPwoFilter());
        }
        if(f_tcp.isSelected()){
            totcp.addAll(controller.getTCPwoFilter());
            toudp.addAll(controller.getTCPwoFilter());
        }
        if(f_opensky.isSelected()){
            totcp.addAll(controller.getADSBList());
            toudp.addAll(controller.getADSBList());
        }
        controller.setUDPList(toudp);
        controller.setTCPList(totcp);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info("GUIController Initializing");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            // show alert to user
            e.printStackTrace();

            // do whatever you want with the Exception e
            Platform.runLater(new Runnable() {
                public void run() {
                    termial.clear();
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
                new KeyFrame(Duration.seconds(2),
                        new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                // logger.info("this is called every 5 seconds on UI thread");
                                // long t1 = System.currentTimeMillis();
                                update();
                                // long t2 = System.currentTimeMillis();
                                // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                // LocalDateTime now = LocalDateTime.now();
                                // System.out.print(dtf.format(now));
                                // logger.info(Thread.currentThread().getName() + " ADSB update took: " +
                                // (t2-t1) + " ms" );
                            }
                        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();

        new Thread() {

            // runnable for that thread
            public void run() {
                setName("GUI Backgroundupdate");
                while (true) {
                    try {
                        // imitating work
                        synchronized (ADSBlock) {
                            backgroundupdate();
                        }
                        relayTransmission();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            logger.log(Level.INFO, "purged TCP {}", String.valueOf(controller.getTCPwoFilter().size()));
                            logger.log(Level.INFO, "purged UDP {}", String.valueOf(controller.getUDPwoFilter().size()));
                            logger.log(Level.INFO, "purged ADSB {}", String.valueOf(controller.getADSBList().size()));
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
        //             logger.info(observable);

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
        //             logger.info(observable);
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
        //             logger.info(observable);

        //         }
        //     }
        // });
        Timeline fiveSecondsUDP = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                HashSet<Plane> udptemp = controller.getUDPwoFilter();
                                HashSet<Marker> udpMarker = new HashSet<>();
                                if (view_udp.isSelected()) {
                                    for (Plane p : udptemp) {
                                        Marker m = toMarker(p);
                                        // udpMarker.add(m);
                                        markersmap.put(p, m);
                                        // markers.add(m);
                                        // markersmap.put(p.mac,m);
                                    }
                                    try {
                                        Plane p = ListUDP.getSelectionModel().getSelectedItem();
                                        if (p != null) {
                                            // markersmap.put(p,toBlueMarker(p));
                                            updateLabel(p);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateMarkers();
                            }
                        }));
        fiveSecondsUDP.setCycleCount(Timeline.INDEFINITE);
        
        Timeline fiveSecondsTCP = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                HashSet<Plane> tcptemp = controller.getTCPwoFilter();
                                HashSet<Marker> tcpMarker = new HashSet<>();
                                if (view_tcp.isSelected()) {
                                    for (Plane p : tcptemp) {
                                        Marker m = toMarker(p);
                                        // udpMarker.add(m);
                                        markersmap.put(p, m);
                                        // markers.add(m);
                                        // markersmap.put(p.mac,m);
                                    }
                                    try {
                                        Plane p = ListTCP.getSelectionModel().getSelectedItem();
                                        if (p != null) {
                                            // markersmap.put(p, toBlueMarker(p));
                                            updateLabel(p);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                updateMarkers();
                            }
                        }));
        fiveSecondsTCP.setCycleCount(Timeline.INDEFINITE);
        Timeline fiveSecondsADSB = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                HashSet<Plane> adsbtemp = controller.getADSBList();
                                HashSet<Marker> adsbMarker = new HashSet<>();
                                if (view_adsb.isSelected()) {
                                    for (Plane p : adsbtemp) {
                                        Marker m = toMarker(p);
                                        // udpMarker.add(m);
                                        markersmap.put(p, m);
                                        // markers.add(m);
                                        // markersmap.put(p.mac,m);
                                    }
                                    try {
                                        Plane p = ListADSB.getSelectionModel().getSelectedItem();
                                        if (p != null) {
                                            // markersmap.put(p, toBlueMarker(p));
                                            updateLabel(p);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                
                                updateMarkers();
                            }
                        }));
        fiveSecondsADSB.setCycleCount(Timeline.INDEFINITE);


        srcTabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {

                        if (t1.getText().equals("ADSB")) {
                            fiveSecondsTCP.stop();
                            fiveSecondsUDP.stop();
                            fiveSecondsADSB.play();
                        } else if(t1.getText().equals("TCP")) {
                            fiveSecondsUDP.stop();
                            fiveSecondsADSB.stop();
                            fiveSecondsTCP.play();
                        } else if(t1.getText().equals("UDP")){
                            fiveSecondsADSB.stop();
                            fiveSecondsTCP.stop();
                            fiveSecondsUDP.play();
                        }

                        logger.info("Tab Selection changed to " + t1.toString());
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

    
}
