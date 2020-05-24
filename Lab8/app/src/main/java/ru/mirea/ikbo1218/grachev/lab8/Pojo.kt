package ru.mirea.ikbo1218.grachev.lab8

data class MapDTO(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val start_address: String,
    val end_location: Location,
    val start_location: Location,
    val steps: List<Step>
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val start_address: String,
    val end_location: Location,
    val start_location: Location,
    val polyline: PolyLine,
    val travel_mode: String,
    val maneuver: String
)

data class Location(
    val lat: String,
    val lng: String
)

data class Distance(
    val text: String,
    val value: Double
)

data class Duration(
    val text: String,
    val value: Double
)

data class PolyLine(
    val points: String
)