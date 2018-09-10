package com.example.erison.mapateste4;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment do mapa
 */
public class MapsLayout extends Fragment implements OnMapReadyCallback {

    static GoogleMap mMap;
    MapView mapView;
    View view;
    private static Dialog filterDialog;
    private static Data mApp = Data.getInstance();
    private static ArrayList<Marker> dataFilter = new ArrayList<>();
    static GoogleMap.InfoWindowAdapter styledInfoWindow;
    static Location myLocationStatic;
    private static Context ctx;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_layout, container, false);

        ctx = getContext();
        //######### filer part ########

        filterDialog = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        //diz qual o layou do dalog, no caso é o filter_dialog
        filterDialog.setContentView(R.layout.filter_dialog);
        filterDialog.findViewById(R.id.volta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.hide();
            }
        });
        filterDialog.findViewById(R.id.confirma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarData();
                filterDialog.hide();
            }
        });



        //chama o servidor
        DatabaseManager.FillData(getActivity(), myRoundPosition());
        myLocationStatic = myPosition();


        view.findViewById(R.id.MapOptions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.show();
            }
        });

       /* view.findViewById(R.id.MapSearchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearch(view);
            }
        });*/

        view.findViewById(R.id.MapChangeTypeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeType(view);
            }
        });


        view.findViewById(R.id.AddNewMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationManager manager = (LocationManager) ctx.getSystemService( Context.LOCATION_SERVICE );
                if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                    Toast.makeText(getContext(),"Você não pode adicionar marcadores estando com o GPS offline", Toast.LENGTH_SHORT).show();return;}
                if(!DatabaseManager.isOnline(getContext())){
                    Toast.makeText(getContext(),"Você não pode adicionar marcadores estando offline", Toast.LENGTH_SHORT).show();return;}
                final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                View myBottomSheet = getActivity().getLayoutInflater().inflate(R.layout.botton_sheet_for_marker_type_select, null);
                dialog.setContentView(myBottomSheet);
                BottomSheetBehavior myBottomBehavior = BottomSheetBehavior.from((View) myBottomSheet.getParent());
                myBottomBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics()));
                dialog.show();


                myBottomSheet.findViewById(R.id.AddLixo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location l = myRoundPosition();
                        String nome = "Lixo em "+ adress(l);
                        l.setLatitude(l.getLatitude() + 0/10000D);
                        l.setLongitude(l.getLongitude() + 0/10000D);
                        Marker m = new Marker(1, "0", l.getLatitude(), l.getLongitude(), nome);
                        addMarkerInMap(m, Data.getInstance().getData().size(), true);
                        DatabaseManager.PutMarker(m, getContext());
                        GoToLocationZoom(l.getLatitude(),l.getLongitude(),13);
                        dialog.hide();
                    }
                });


                myBottomSheet.findViewById(R.id.AddMosquito).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location l = myRoundPosition();
                        String nome = "Dengue em "+ adress(l);
                        l.setLatitude(l.getLatitude() + 1/10000D);
                        l.setLongitude(l.getLongitude() + 1/10000D);
                        Marker m = new Marker(1, "1", l.getLatitude(), l.getLongitude(), nome);
                        addMarkerInMap(m, Data.getInstance().getData().size(), true);
                        DatabaseManager.PutMarker(m, getContext());
                        GoToLocationZoom(l.getLatitude(),l.getLongitude(),13);
                        dialog.hide();
                    }
                });


                myBottomSheet.findViewById(R.id.AddCrime).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location l = myRoundPosition();
                        String nome = "Crime em "+ adress(l);
                        l.setLatitude(l.getLatitude() + 2/10000D);
                        l.setLongitude(l.getLongitude() + 2/10000D);
                        Marker m = new Marker(1, "2", l.getLatitude(), l.getLongitude(), nome);
                        addMarkerInMap(m, Data.getInstance().getData().size(), true);
                        DatabaseManager.PutMarker(m, getContext());
                        GoToLocationZoom(l.getLatitude(),l.getLongitude(),13);
                        dialog.hide();
                    }
                });

                myBottomSheet.findViewById(R.id.AddTransito).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location l = myRoundPosition();
                        String nome = "Trânsito em "+ adress(l);
                        l.setLatitude(l.getLatitude() + 3/10000D);
                        l.setLongitude(l.getLongitude() + 3/10000D);
                        Marker m = new Marker(1, "3", l.getLatitude(), l.getLongitude(), nome);
                        addMarkerInMap(m, Data.getInstance().getData().size(), true);
                        DatabaseManager.PutMarker(m, getContext());
                        GoToLocationZoom(l.getLatitude(),l.getLongitude(),13);
                        dialog.hide();
                    }
                });

                myBottomSheet.findViewById(R.id.AddIncendio).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location l = myRoundPosition();
                        String nome = "Incêndio em "+ adress(l);
                        l.setLatitude(l.getLatitude() + 4/10000D);
                        l.setLongitude(l.getLongitude() + 4/10000D);
                        Marker m = new Marker(1, "4", l.getLatitude(), l.getLongitude(), nome);
                        addMarkerInMap(m, Data.getInstance().getData().size(), true);
                        DatabaseManager.PutMarker(m, getContext());
                        GoToLocationZoom(l.getLatitude(),l.getLongitude(),13);
                        dialog.hide();
                    }
                });
                myBottomSheet.findViewById(R.id.AddProblemaEstrutural).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location l = myRoundPosition();
                        String nome = "Problemas Estruturais em "+ adress(l);
                        l.setLatitude(l.getLatitude() + 5/10000D);
                        l.setLongitude(l.getLongitude() + 5/10000D);
                        Marker m = new Marker(1, "5", l.getLatitude(), l.getLongitude(), nome);
                        addMarkerInMap(m, Data.getInstance().getData().size(), true);
                        DatabaseManager.PutMarker(m, getContext());
                        GoToLocationZoom(l.getLatitude(),l.getLongitude(),13);
                        dialog.hide();
                    }
                });
            }
        });

        return view;
    }

    /**
     * Pega o nome do endereço a partir da latitude o longitude
     * @param l
     * @return
     */
    public String adress(Location l){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), new Locale("pt", "BR"));

        try {
            addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            return address;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Função para qnd mudar o zom no mapa
     * @param zoom
     */
    public void OnZoomChanged(float zoom){
        dataFilter.clear();

        for(int i=0, size=mApp.getData().size(); i<size; i++ ){
            Marker m = mApp.getData().get(i);
            if((m.upVotes)/(21.5f-zoom)>1) {
                dataFilter.add(m);
            }
        }
        fillMap();
    }

    /**
     * Filtra os marcadores a partir das opções escolhidas pelo usuario
     */
    public static void filtrarData(){
        dataFilter.clear();
        ArrayList<String> filtros = new ArrayList<>();
        CheckBox c = (CheckBox) filterDialog.findViewById(R.id.lixo);
        if(c.isChecked()){
            filtros.add("0");
        }
        c = (CheckBox) filterDialog.findViewById(R.id.dengue);
        if(c.isChecked()){
            filtros.add("1");
        }
        c = (CheckBox) filterDialog.findViewById(R.id.crime);
        if(c.isChecked()){
            filtros.add("2");
        }
        c = (CheckBox) filterDialog.findViewById(R.id.transito);
        if(c.isChecked()){
            filtros.add("3");
        }
        c = (CheckBox) filterDialog.findViewById(R.id.incendio);
        if(c.isChecked()){
            filtros.add("4");
        }
        c = (CheckBox) filterDialog.findViewById(R.id.problemaEstrutural);
        if(c.isChecked()){
            filtros.add("5");
        }
        for(int i=0, size=mApp.getData().size(); i<size; i++ ){
            Marker m = mApp.getData().get(i);
            if(filtros.contains(m.type)) {
                dataFilter.add(m);
            }
        }

        fillMap();
    }

    @Override
    public void onViewCreated(View mview, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mview, savedInstanceState);
/*
        mMapaView = ((SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map));
        mMapaView.getMapAsync(this);*/

        mapView = (MapView) getActivity().findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    /**
     * Prenche o mapa com marcadores
     */
    public static void fillMap(){

        mMap.clear();
        Marker m;
        mMap.setInfoWindowAdapter(styledInfoWindow);
        for(int i=0, size = dataFilter.size(); i<size; i++){
            m = dataFilter.get(i);
            addMarkerInMap(m, i,true);
        }
    }

    /**
     * Adicona um marcador no mapa
     * @param m
     * @param posi
     * @param inData
     */
    private static void addMarkerInMap(Marker m, int posi, boolean inData){
        if(inData){mApp.getData().add(m);dataFilter.add(m);}
        MarkerOptions mOptions = new MarkerOptions()
                .title(String.valueOf(posi))
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(Integer.valueOf(m.type),120,120)))
                .position(new LatLng(m.lat,m.lng))
                .snippet(m.subDescription);
        mMap.addMarker(mOptions);

    }

    public static Bitmap resizeMapIcons(int posi,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(ctx.getResources(),
                R.drawable.marcador0lixo+posi);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

   /* private void addMarkerInMapResize(Marker m, int posi, boolean inData){
        if(inData){mApp.getData().add(m);dataFilter.add(m);}
        MarkerOptions mOptions = new MarkerOptions()
                .title(String.valueOf(posi))
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(m,60,60)))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador0lixo + Integer.valueOf(m.type)))
                .position(new LatLng(m.lat,m.lng))
                .snippet(m.subDescription);
        mMap.addMarker(mOptions);

    }*/

    /**
     * Função para após ter acesso ao servidor de mapas do google
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mMap = googleMap;

        if(mMap!=null){
            styledInfoWindow = new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {

                    Marker actual = dataFilter.get(Integer.parseInt(marker.getTitle()));

                    View itemView = getActivity().getLayoutInflater().inflate(R.layout.marker_layout, null);

                    ImageView icon = (ImageView) itemView.findViewById(R.id.CardImage);
                    TextView description = (TextView) itemView.findViewById(R.id.CardDescription1);
                    TextView subDescription = (TextView) itemView.findViewById(R.id.CardSubDescription);
                    ImageView cobreImagem =  (ImageView) itemView.findViewById(R.id.CobreTela);
                    TextView upVotes = (TextView) itemView.findViewById(R.id.UpVotes);
                    TextView downVotes = (TextView) itemView.findViewById(R.id.DownVotes);
                    TextView shares = (TextView) itemView.findViewById(R.id.Shares);
                    TextView comments = (TextView) itemView.findViewById(R.id.Coments);
                    ImageView top =  (ImageView) itemView.findViewById(R.id.iconTop);
                    ImageView down =  (ImageView) itemView.findViewById(R.id.iconDown);
                    ImageView iconShare =  (ImageView) itemView.findViewById(R.id.iconShare);
                    ImageView iconComment =  (ImageView) itemView.findViewById(R.id.iconComment);

                    icon.setImageResource(R.drawable.icone0lixo+Integer.valueOf(actual.type));
                    cobreImagem.setImageResource(R.drawable.area);
                    description.setText(actual.description);
                    subDescription.setText(actual.subDescription);

                    upVotes.setText(String.valueOf(actual.upVotes));
                    downVotes.setText(String.valueOf(actual.downVotes));
                    comments.setText(String.valueOf(actual.comments));
                    shares.setText(String.valueOf(actual.shares));

                    top.setImageResource(R.drawable.up);
                    down.setImageResource(R.drawable.down);
                    iconShare.setImageResource(R.drawable.share);
                    iconComment.setImageResource(R.drawable.commentprocessing);

                    return itemView;
                }
            };
        }

        filtrarData();

        GoToLocationZoom(-3.741625, -38.564147, 12);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        myLocation(getActivity().findViewById(R.id.map));
    }

    /**
     * Vai para um determinado local no mapa
     * @param lat
     * @param lng
     * @param zoom
     */
    public static void GoToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
    }

    /**
     * Pega a lista de locais a partir de um nome
     * @param locationName
     * @param ctx
     */
    static void getSearchPlaceList(String locationName, Context ctx) {
        List<Address> addressList = null;
        ArrayList<SearchItem> result = new ArrayList<>();
        if (locationName != null && !locationName.equals("")) {
            Geocoder geocoder = new Geocoder(ctx);
            try {
                addressList = geocoder.getFromLocationName(locationName, 5);
                if(addressList.size()==0){return;}
                Address a = addressList.get(0);
                for (int i=0; i<a.getMaxAddressLineIndex(); i++){
                    result.add(new SearchItem(1,
                            String.valueOf(a.getAddressLine(i))+" - "+String.valueOf(a.getLocality()),
                            String.valueOf(a.getCountryName())+" - "+String.valueOf(a.getAdminArea()),
                            Double.parseDouble(String.valueOf(a.getLatitude())),
                            Double.parseDouble(String.valueOf(a.getLongitude()))));
                }
                MainActivity.FillSearch(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeType(View view) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Aredonda a posição atual do usuario
     * @return
     */
    public Location myRoundPosition(){
        Location location = myPosition();
        double val = (Math.round(location.getLatitude()*1000))/1000D;
        location.setLatitude(val);
        location.setLongitude((Math.round(location.getLongitude()*1000))/1000D);
        return location;
    }

    /**
     * Usa o gps para saber a posição atual do usuario
     * @return
     */
    public Location myPosition(){
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    "Manifest.permission.ACCESS_COARSE_LOCATION",
                    "Manifest.permission.ACCESS_FINE_LOCATION"
            }, 1);
        } else {

            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            final android.location.LocationListener locationListener;

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                System.err.println("É não tão ligados não");
            }

            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (location == null) {

                System.out.println("Ta nula essa merda");
                locationListener = new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(final Location location) {

                        // getting location of user
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        //do something with Lat and Lng
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                System.out.println("Hmmmm");

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 500, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 500, locationListener);
                    location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

                if (locationManager == null) {System.err.println("pqp tá o manager ta null tbm");}
                if(location==null){
                    System.err.println("pqp tá null dnv");
                }else{return location;}
            }else {
                System.out.println("Deu");
                return location;
            }
        }

        Location location = new Location("dummyprovider");
        location.setLatitude(0);
        location.setLongitude(0);
        return location;
    }

    /**
     * Vai para a localização do usuário
     * @param view
     */
    public void myLocation(View view) {

        Location location = myRoundPosition();

        try {
            GoToLocationZoom(location.getLatitude(), location.getLongitude(), 14);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
