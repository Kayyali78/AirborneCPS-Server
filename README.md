# AirborneCPS-Server
**A server application to make [Airborne-CPS ](https://github.com/tenbergen/Airborne-CPS) able to communicate within global network.**

You can download the packed jar file from the [release](https://github.com/Kayyali78/AirborneCPS-Server/releases), if you want to see the console output, please run it with terminal.

Tested on windows 10 x64

##Installation

* Maven for building project.
* \*Install [opensky-api](https://github.com/openskynetwork/opensky-api) locally with maven first.\*
* Then use `maven build` for the jar file, and the jar would be self-executable.

## Airborne-CPS dataflow

According to [Airborne-CPS ](https://github.com/tenbergen/Airborne-CPS), the plugin will output udp packets to `255.255.255.255`. Our goal is to collect those information, and relay to other clients.

The updated Airborne-CPS plugin will have the ability to connect to the server in TCP.

* Source = UDP -> TCP
* Source = TCP -> TCP, UDP
* Source = ADSB -> TCP, UDP

The packet will contains the info below.

`payload = n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383`

which is composed of
* MAC
* IP
* latitude
* longtitude
* altitude

The last three postitions are necessary parameters for a [TCAS](https://en.wikipedia.org/wiki/Traffic_collision_avoidance_system) system.

## GUI

The initial version of GUI
![](https://i.imgur.com/Sj3aRaP.png)

The final version of GUI
![image](https://user-images.githubusercontent.com/98616637/167196787-f6bb5207-4af0-4e4f-94c7-3744e7a56bca.png)

Split panel consist of three panel.

Left Panel is where we can selected view, or selected drop file.

Middle Panel is where we can view maps, alone with the plane markers.

Right panel containes a tab with three kinds of sources, ADSB, TCP, UDP. On the downside, there are some parameters according to the selected items.

## Functions

### Map
The AirborneCPS-Server has a map in the middle, which can render the received planes with its *location* and *heading* in purple.
#### Plane ListView
There is a listview on the right of the gui, which contains three kinds of different sources. Each plane is listed in either ( "CALLSIGN" or "IP" ) + ("Speed").
##### Click
If you select a plane which is listed, the map will be reposition to where the plane exist right now, and will render the selected plane is green.

### Starts TCP/UDP server with port/IP
On the left of the map, we can choose the port which the TCP/UDP server maps to, and also we need to choose the IP address the server is going to bind, then click on *network start* to get the server working.


### Opensky-api

```java
OpenSkyApi api = new OpenSkyApi(USERNAME, PASSWORD);
BoundingBox bbox = new BoundingBox(30.8389, 50.8229, -100.9962, -40.5226);
OpenSkyStates os = api.getStates(0,null, bbox);

```

The opensky-api will return the selected region of planes in HashSet.

The plane will given in StateVector, which is a dict.
```java
StateVector@71 "StateVectorStateVector {
    geoAltitude = null,
    longitude = -74.1787,
    latitude = 40.6993,
    velocity = 1.8,
    heading = 205.31,
    verticalRate = null,
    icao24 = 'ab1644',
    callsign = 'UAL588  ',
    onGround = true,
    lastContact = 1.647997447E9,
    lastPositionUpdate = 1.647997446E9,
    originCountry = 'United States',
    squawk = '2226',
    spi = false,
    baroAltitude = null,
    positionSource = ADS_B,
    serials = null
}"
```

#### Scope of the Opensky-ADSB
On the left side of the map contains a box looks like the earth with for parameters, which is the bounded area of the opensky api region. We can update border with *Update Border* button with the border parameters are modified in the box of four.




