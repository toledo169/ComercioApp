package com.example.oaxacacomercio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class MapaActivity extends AppCompatActivity {
    private MapView mapView;
    private static final List<List<Point>> POINTS = new ArrayList<>();
    private static final List<Point> OUTER_POINTS = new ArrayList<>();
    static {
        OUTER_POINTS.add(Point.fromLngLat(17.060081, -96.729958));
        OUTER_POINTS.add(Point.fromLngLat(17.059917, -96.728998));
        OUTER_POINTS.add(Point.fromLngLat(17.058932, -96.729207));
        OUTER_POINTS.add(Point.fromLngLat(17.059107, -96.730125));
        POINTS.add(OUTER_POINTS);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoidG9sZWRvMTYiLCJhIjoiY2s4eGR3aHl5MHg5ajNucGsxMHN6YWg0MyJ9.EcFmUIJJCWb47aJAFHddRw");
        //String latitud=getIntent().getExtras().getString("latitud");
       // String longitu=getIntent().getExtras().getString("longitud");
       // String nombrev=getIntent().getExtras().getString("name");
        setContentView(R.layout.activity_mapa);
        mapView =(MapView)findViewById(R.id.mapamaps);
        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);

        mapView.getMapAsync(new OnMapReadyCallback() {
         /*   @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addMarker(new MarkerOptions().position
                        (new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitu))).title(nombrev));
            }*/
            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    style.addSource(new GeoJsonSource("source-id", Polygon.fromLngLats(POINTS)));
                    style.addLayerBelow(new FillLayer("layer-id", "source-id").withProperties(
                            fillColor(Color.parseColor("#3bb2d0"))), "settlement-label"
                    );
                }
            });

        });
    }

}
