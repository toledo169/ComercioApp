package com.example.oaxacacomercio.Detalles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.oaxacacomercio.Adapter.DetallesVendedorAdapter;
import com.example.oaxacacomercio.Adapter.VendedorAdapter;
import com.example.oaxacacomercio.MainActivity;
import com.example.oaxacacomercio.Modelos.User;
import com.example.oaxacacomercio.Modelos.Vendedor;
import com.example.oaxacacomercio.R;
import com.example.oaxacacomercio.Ventanas;
import com.example.oaxacacomercio.ui.gallery.GalleryFragment;
import com.example.oaxacacomercio.ui.home.HomeFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetallesorganizacionActivity extends AppCompatActivity {
    RecyclerView recyclerViewDetalles;
    ArrayList<Vendedor> listavendedoresdetalles;
    ArrayList<Vendedor> listauxiliar;
    ArrayList<Double> lat = new ArrayList<>();
    ArrayList<Double> log = new ArrayList<>();
    AlertDialog mDialog;
    JsonRequest jsonObjectRequest;
    RequestQueue request;
    private LinearLayoutManager layoutManager;
    //TextView tvclave,tvnombre;
   // private EditText serchvo;
    int claved;
    DetallesVendedorAdapter adapter;
    SweetAlertDialog sweetAlertDialog;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallesorganizacion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        Toolbar toolbar = findViewById(R.id.toolbardo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String name = getIntent().getExtras().getString("nombre_organizacion");
        getSupportActionBar().setTitle(name);
        // String dirigente=getIntent().getExtras().getString("nombre_dirigente");
        claved = getIntent().getExtras().getInt("id_organizacion");
        listavendedoresdetalles = new ArrayList<>();
        listauxiliar = new ArrayList<>();
        recyclerViewDetalles = (RecyclerView) findViewById(R.id.idRecyclerdetallesvendedor);
     //   serchvo = (EditText) findViewById(R.id.buscarvendedororga);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewDetalles.setLayoutManager(layoutManager);
        recyclerViewDetalles.setHasFixedSize(true);

        adapter = new DetallesVendedorAdapter(listavendedoresdetalles, this);
        recyclerViewDetalles.setAdapter(adapter);
        request = Volley.newRequestQueue(this);
        ejecutarservicio();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ejecutarservicio() {
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();
        mDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!DetallesorganizacionActivity.this.isFinishing() && mDialog != null) {
                    mDialog.dismiss();
                }
            }
        }, 3000);
        String url = "http://192.168.10.233/api/Usuario/deta/" + claved;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Vendedor vendedor = null;
                JSONArray json = response.optJSONArray("permisos");
                try {
                    for (int i = 0; i < json.length(); i++) {
                        vendedor = new Vendedor(context);
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);

                        vendedor.setId(jsonObject.optInt("id_vendedor"));
                        vendedor.setNombre(jsonObject.optString("name"));
                        vendedor.setApellido_paterno(jsonObject.optString("apellido_paterno"));
                        vendedor.setApellido_materno(jsonObject.optString("apellido_materno"));
                        vendedor.setNomorganizacion(jsonObject.optString("nombre_organizacion"));
                        vendedor.setActividad(jsonObject.optString("tipo_actividad"));
                        vendedor.setGiro(jsonObject.optString("giro"));
                        vendedor.setNomzona(jsonObject.optString("nombre"));
                        vendedor.setLatitud(jsonObject.optDouble("latitud"));
                        vendedor.setLongitud(jsonObject.optDouble("longitud"));
                        listavendedoresdetalles.add(vendedor);
                        listauxiliar.add(vendedor);
                    }
                    mDialog.hide();
                    recyclerViewDetalles.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    final User user = new User(DetallesorganizacionActivity.this);
                    //    Toast.makeText(this,"no se ha podido establecer conexion"+" "+response,Toast.LENGTH_LONG).show();
                    sweetAlertDialog = new SweetAlertDialog(DetallesorganizacionActivity.this, SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitleText("Lo sentimos");
                    sweetAlertDialog.setContentText("En este momento no se puede realizar su petición");
                    sweetAlertDialog.setContentTextSize(15);
                    sweetAlertDialog.setCancelable(false);
                    sweetAlertDialog.setConfirmText("Volver a intentarlo");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent intent = new Intent(DetallesorganizacionActivity.this, Ventanas.class);
                            intent.putExtra(GalleryFragment.numexpediente, user.getAdminsecre());
                            intent.putExtra(GalleryFragment.correoe, user.getCorreoelectronico());
                            intent.putExtra(HomeFragment.apellido_paternos, user.getApellido_paterno());
                            intent.putExtra(HomeFragment.apellido_maternos, user.getApellido_materno());
                            intent.putExtra(HomeFragment.nombres, user.getNombre());
                            intent.putExtra(HomeFragment.correo, user.getCorreoelectronico());
                            intent.putExtra(HomeFragment.cargo, user.getCargo());
                            intent.putExtra(HomeFragment.municipio, user.getMunicipio());
                            //intent.putExtra(HomeFragment.fotoperfil, user.getImage());
                            startActivity(intent);
                            finish();
                        }
                    });
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                    sweetAlertDialog.show();
                    mDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                final User user = new User(DetallesorganizacionActivity.this);
                sweetAlertDialog = new SweetAlertDialog(DetallesorganizacionActivity.this, SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Lo sentimos");
                sweetAlertDialog.setContentText("En este momento no se puede realizar su petición");
                sweetAlertDialog.setContentTextSize(15);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setConfirmText("Volver a intentarlo");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(DetallesorganizacionActivity.this, Ventanas.class);
                        intent.putExtra(GalleryFragment.numexpediente, user.getAdminsecre());
                        intent.putExtra(GalleryFragment.correoe, user.getCorreoelectronico());
                        intent.putExtra(HomeFragment.apellido_paternos, user.getApellido_paterno());
                        intent.putExtra(HomeFragment.apellido_maternos, user.getApellido_materno());
                        intent.putExtra(HomeFragment.nombres, user.getNombre());
                        intent.putExtra(HomeFragment.correo, user.getCorreoelectronico());
                        intent.putExtra(HomeFragment.cargo, user.getCargo());
                        intent.putExtra(HomeFragment.municipio, user.getMunicipio());
                       // intent.putExtra(HomeFragment.fotoperfil, user.getImage());
                        startActivity(intent);
                        finish();
                    }
                });
                sweetAlertDialog.setCanceledOnTouchOutside(false);
                sweetAlertDialog.show();
                mDialog.hide();
            }
        });
        request.add(jsonObjectRequest);
    }

    public void buscador(String texto) {
        listavendedoresdetalles.clear();
        for (int i = 0; i < listauxiliar.size(); i++) {
            if (listauxiliar.get(i).getNombrev().toLowerCase().contains(texto.toLowerCase()) || listauxiliar.get(i).getApellido_paterno().toLowerCase().contains(texto.toLowerCase())) {
                listavendedoresdetalles.add(listauxiliar.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem item= menu.findItem(R.id.search_vendedoraction);
        androidx.appcompat.widget.SearchView searchView=(androidx.appcompat.widget.SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscador("" + newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}

