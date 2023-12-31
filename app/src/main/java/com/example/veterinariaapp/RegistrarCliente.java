package com.example.veterinariaapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RegistrarCliente extends AppCompatActivity {
    EditText etNombres, etApellidos, etDni, etClaveAcceso;
    String nombres, apellidos, dni, genero, claveacceso, tipoUsuario = "S";
    RadioGroup rdGroupGenero;
    Button btRegistrarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cliente);
        loadUI();

        if(Utils.getRol().equals("D")){
            tipoUsuario = "D";
        }

        btRegistrarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarFieldsRequired();
            }
        });

    }

    private void validarFieldsRequired(){
        nombres = etNombres.getText().toString().trim();
        apellidos = etApellidos.getText().toString().trim();
        dni = etDni.getText().toString().trim();
        claveacceso = etClaveAcceso.getText().toString().trim();

        int idSelectedRadio = rdGroupGenero.getCheckedRadioButtonId();
        RadioButton radioSelected= findViewById(idSelectedRadio);
        genero = radioSelected.getText().toString();

        if(nombres.isEmpty()) {
            etNombres.setError("Ingrese nombres");
            etNombres.requestFocus();
        }else if(apellidos.isEmpty()){
            etApellidos.setError("Ingrese apellidos");
            etApellidos.requestFocus();
        }else if(dni.isEmpty()){
            etDni.setError("Ingrese dni");
            etDni.requestFocus();
        }else if(genero.isEmpty()){
            Utils.showToast(this, "Seleccione genero");
        } else if (claveacceso.isEmpty()) {
            etClaveAcceso.setError("Ingrese clave de acceso");
            etClaveAcceso.requestFocus();
        } else{
            btRegistrarCliente.setEnabled(false);
            registrarCliente();
        }

    }


    private void registrarCliente(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Utils.URL_CLIENTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       btRegistrarCliente.setEnabled(true);
                       Utils.showToast(getApplicationContext(), "Cliente registrado correctamente");
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONObject data = res.getJSONObject("data");
                            String idCliente = data.getString("LAST_INSERT_ID()");
                            if(tipoUsuario.equals("D")) {
                            }
                            resetUI();

                        }catch (Exception e){
                            Log.e("Error", e.getMessage());
                        }

                    }
                },
        new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.showToast(RegistrarCliente.this, "Error al registrar cliente");
                    }
                }

        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombres", nombres);
                parametros.put("apellidos", apellidos);
                parametros.put("dni", dni);
                parametros.put("genero", genero.substring(0,1));
                parametros.put("operacion", "registrarCliente");
                parametros.put("claveacceso", claveacceso);
                parametros.put("tipousuario", tipoUsuario);

                return parametros;
            }
        };

        RequestQueue requestQueue  = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void resetUI(){
        etNombres.setText("");
        etApellidos.setText("");
        etDni.setText("");
        etClaveAcceso.setText("");
        rdGroupGenero.check(R.id.rdMasculino);
        etApellidos.requestFocus();
    }
    private void loadUI(){
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etDni = findViewById(R.id.etDni);
        etClaveAcceso = findViewById(R.id.etClaveacceso);
        rdGroupGenero = findViewById(R.id.rdGroupGenero);
        btRegistrarCliente = findViewById(R.id.btRegistrarCliente);
    }
}