package com.example.pm1e2grupo5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm1e2grupo5.Modelo.Contactos;
import com.example.pm1e2grupo5.Modelo.RestApiMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactosAdapter extends ArrayAdapter<Contactos> {

    private ArrayList<Contactos> contactosList;
    private LayoutInflater inflater;
    private Context context;

    public ContactosAdapter(Context context, ArrayList<Contactos> contactosList) {
        super(context, 0, contactosList);
        this.context = context;
        this.contactosList = contactosList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contacto, parent, false);
            holder = new ViewHolder();
            holder.idTextView = convertView.findViewById(R.id.idTextView);
            holder.nombreTextView = convertView.findViewById(R.id.nombreTextView);
            holder.telefonoTextView = convertView.findViewById(R.id.telefonoTextView);
            holder.latitudTextView = convertView.findViewById(R.id.latitudTextView);
            holder.longitudTextView = convertView.findViewById(R.id.longitudTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Contactos contacto = contactosList.get(position);
        holder.idTextView.setText("ID: " + contacto.getContactoId());
        holder.nombreTextView.setText(contacto.getNombre());
        holder.telefonoTextView.setText(String.valueOf(contacto.getTelefono()));
        holder.latitudTextView.setText(contacto.getLatitud());
        holder.longitudTextView.setText(contacto.getLongitud());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpciones(contacto);
            }
        });

        return convertView;
    }

    private void mostrarOpciones(final Contactos contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seleccione una acción")
                .setItems(new CharSequence[]{"Eliminar", "Actualizar"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                eliminarContacto(contacto);
                                break;
                            case 1:
                                actualizarContacto(contacto);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private void eliminarContacto(final Contactos contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de que desea eliminar este contacto?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("contactoId", contacto.getContactoId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                RestApiMethods.EndpointDeleteContacto,
                                jsonBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String mensaje = response.getString("messaje");
                                            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();

                                            contactosList.remove(contacto);
                                            notifyDataSetChanged();
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Error al eliminar el contacto", Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }
                        });

                        requestQueue.add(request);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }




    private void actualizarContacto(final Contactos contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar contacto");


        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_contact, null);


        final EditText nombreEditText = viewInflated.findViewById(R.id.editTextNombre);
        final EditText telefonoEditText = viewInflated.findViewById(R.id.editTextTelefono);
        final EditText latitudEditText = viewInflated.findViewById(R.id.editTextLatitud);
        final EditText longitudEditText = viewInflated.findViewById(R.id.editTextLongitud);


        nombreEditText.setText(contacto.getNombre());
        telefonoEditText.setText(String.valueOf(contacto.getTelefono()));
        latitudEditText.setText(contacto.getLatitud());
        longitudEditText.setText(contacto.getLongitud());

        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevoNombre = nombreEditText.getText().toString().trim();
                String nuevoTelefono = telefonoEditText.getText().toString().trim();
                String nuevaLatitud = latitudEditText.getText().toString().trim();
                String nuevaLongitud = longitudEditText.getText().toString().trim();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("contactoId", contacto.getContactoId());
                    jsonBody.put("nombre", nuevoNombre);
                    jsonBody.put("telefono", nuevoTelefono);
                    jsonBody.put("latitud", nuevaLatitud);
                    jsonBody.put("longitud", nuevaLongitud);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RequestQueue requestQueue = Volley.newRequestQueue(context);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        RestApiMethods.EndpointUpdateContacto,
                        jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String mensaje = response.getString("messaje");
                                    Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();

                                    contacto.setNombre(nuevoNombre);
                                    contacto.setTelefono(Integer.parseInt(nuevoTelefono));
                                    contacto.setLatitud(nuevaLatitud);
                                    contacto.setLongitud(nuevaLongitud);
                                    notifyDataSetChanged();
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Error al actualizar el contacto", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                });

                requestQueue.add(request);
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }


    static class ViewHolder {
        TextView idTextView;
        TextView nombreTextView;
        TextView telefonoTextView;
        TextView latitudTextView;
        TextView longitudTextView;
    }
}
