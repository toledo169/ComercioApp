package com.example.oaxacacomercio.ui.send;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.oaxacacomercio.Adapter.ZonaAdapter;
import com.example.oaxacacomercio.DetallesZonaActivity;
import com.example.oaxacacomercio.Helper.MySwipeHelper;
import com.example.oaxacacomercio.Helper.MybuttonClickListener;
import com.example.oaxacacomercio.MapaActivity;
import com.example.oaxacacomercio.R;
import com.example.oaxacacomercio.Modelos.Zona;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {

    private SendViewModel sendViewModel;
    RecyclerView recyclerViewzonas;
    ArrayList<Zona>listazona;
    ProgressDialog progress;
    JsonRequest jsonObjectRequest;
    RequestQueue request;
    private ArrayList<Double> lat= new ArrayList<>();
    private ArrayList<Double> lon= new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private String idZona;
    private String opcion="zonas";
  //  private GridLayoutManager layoutManager;
    ZonaAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
      //  final TextView textView = root.findViewById(R.id.text_send);
     //   sendViewModel.getText().observe(this, new Observer<String>() {
       //     @Override
       //     public void onChanged(@Nullable String s) {
         //       textView.setText(s);
       //     }
       // });

        listazona=new ArrayList<>();
        recyclerViewzonas=(RecyclerView)root.findViewById(R.id.idRecyclerzona);
        layoutManager= new LinearLayoutManager(getActivity());
     //   layoutManager= new GridLayoutManager(getActivity(),2);
        recyclerViewzonas.setLayoutManager(layoutManager);
        recyclerViewzonas.setHasFixedSize(true);

        adapter=new ZonaAdapter(listazona,getContext());
        recyclerViewzonas.setAdapter(adapter);
        request= Volley.newRequestQueue(getContext());
        MySwipeHelper swipeHelper= new MySwipeHelper(getContext(),recyclerViewzonas,200) {
            @Override
            public void instanciateMyButton(final RecyclerView.ViewHolder viewHolder, List<Mybutton> buffer) {
                buffer.add(new Mybutton(getContext(),
                        "Detalles",
                        40,
                        0,
                        Color.parseColor("#5d2442"),
                        new MybuttonClickListener(){
                            @Override
                            public void onClick(int pos) {
                                Intent intent=new Intent(getContext(), DetallesZonaActivity.class);
                                intent.putExtra("id_zona",listazona.get(viewHolder.getAdapterPosition()).getId());
                                intent.putExtra("nombre",listazona.get(viewHolder.getAdapterPosition()).getNombre());
                                getContext().startActivity(intent);
                                Toast.makeText(getContext(),"Detalles", Toast.LENGTH_SHORT).show();
                            }
                        }
                ));
                buffer.add(new Mybutton(getContext(),
                        "Ver\n mapa",
                        40,
                        0,
                        Color.parseColor("#b34766"),
                        new MybuttonClickListener(){
                            @Override
                            public void onClick(int pos) {
                                if(listazona.get(viewHolder.getAdapterPosition()).getNombre().equals("PERMITIDA")){
                                    Toast.makeText(getContext(),"PERMITIDA",Toast.LENGTH_SHORT).show();
                                    idZona="1";
                                    lat.add(17.060081);
                                    lat.add(17.059917);
                                    lat.add(17.058932);
                                    lat.add(17.059107);
                                    lon.add(-96.729958);
                                    lon.add(-96.728998);
                                    lon.add(-96.729207);
                                    lon.add(-96.730125);
                                    Intent i= new Intent(getContext(), MapaActivity.class);
                                    i.putExtra("lat",lat);
                                    i.putExtra("lon",lon);
                                    i.putExtra("idZona",idZona);
                                    i.putExtra("zonas",opcion);
                                    startActivity(i);
                                }else if(listazona.get(viewHolder.getAdapterPosition()).getNombre().equals("RESTRINGIDA")){
                                    Toast.makeText(getContext(),"RESTRINGIDA",Toast.LENGTH_SHORT).show();
                                }

                                //Toast.makeText(getContext(),"Eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }
                ));
            }
        };
        cargarwebservice();
        return root;
    }

    private void cargarwebservice() {
        progress=new ProgressDialog(getContext());
        progress.setMessage("Consultando...");
        progress.show();
        String url="http://192.168.0.11/api/Usuario/listarzona/";
        jsonObjectRequest= new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
        progress.hide();

    }

    @Override
    public void onResponse(JSONObject response) {
        Zona zona=null;
        JSONArray json=response.optJSONArray("zonas");
        try {
            for (int i=0;i<json.length();i++)
            {
            zona= new Zona();
                JSONObject jsonObject = null;
                jsonObject=json.getJSONObject(i);
                zona.setNombre(jsonObject.optString("nombre"));
                zona.setId(jsonObject.optInt("id_zona"));
                listazona.add(zona);
            }
            progress.hide();
            recyclerViewzonas.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"no se ha podido establecer conexion"+" "+response,Toast.LENGTH_LONG).show();
            progress.hide();
        }
    }
}