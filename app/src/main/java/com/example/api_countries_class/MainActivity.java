package com.example.api_countries_class;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String URL_API = "https://restcountries.com/v3.1/all";

    private Spinner spinner;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);

        // Hacer la solicitud a la API y llenar el spinner
        requestDataAndFillSpinner();

        // Configurar el temporizador para cerrar la aplicación después de 60 segundos
        countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // No se requiere ninguna acción en cada tick del temporizador
            }

            @Override
            public void onFinish() {
                if (spinner.getAdapter() == null || spinner.getAdapter().getCount() == 0) {
                    // Si el spinner no se llenó con datos, se cierra la aplicación
                    Toast.makeText(MainActivity.this, "La carga de datos desde la API ha tardado demasiado. La aplicación se cerrará.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.start();
    }

    private void requestDataAndFillSpinner() {
        spinner.setVisibility(View.GONE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Crear la solicitud GET a la API
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_API, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<String> countryNames = new ArrayList<>();

                            // Obtener el nombre común de cada país de la respuesta JSON
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject countryObject = response.getJSONObject(i);
                                JSONObject nameObject = countryObject.getJSONObject("name");
                                String countryName = nameObject.getString("common");
                                countryNames.add(countryName);
                            }

                            // Crear el adaptador para el spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    MainActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    countryNames
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Asignar el adaptador al spinner
                            spinner.setAdapter(adapter);

                            // Ocultar la barra de progreso
                            progressBar.setVisibility(View.GONE);
                            spinner.setVisibility(View.VISIBLE);

                            // Cancelar el temporizador ya que la solicitud se completó con éxito
                            cancelCountDownTimer();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error de la solicitud
                        error.printStackTrace();

                        // Cancelar el temporizador ya que se produjo un error en la solicitud
                        cancelCountDownTimer();
                    }
                });

        // Agregar la solicitud a la cola de solicitudes
        requestQueue.add(jsonArrayRequest);
    }

    private void cancelCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}