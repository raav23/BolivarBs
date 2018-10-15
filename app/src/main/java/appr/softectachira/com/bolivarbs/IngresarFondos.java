package appr.softectachira.com.bolivarbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;



public class IngresarFondos extends AppCompatActivity {

    //Instancias de vistas
    TextView textView_nombreUsuario,textView_dineroUsuario,textView_dineroDiferido;
    Button button_salir,button_transferencia,button_pagomovil,button_mercadopago;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_fondos);

        //Enlazando vistas
        textView_dineroUsuario=findViewById(R.id.textView_dineroUsuario_ingresoFondos);
        textView_nombreUsuario=findViewById(R.id.textView_nombreUsuario_ingresoFondos);
        textView_dineroDiferido=findViewById(R.id.textView_dineroDiferido2);
        button_salir=findViewById(R.id.button_salir);
        button_pagomovil=findViewById(R.id.button_pagoMovil_ingresar);
        button_mercadopago=findViewById(R.id.button_mp);
        button_transferencia=findViewById(R.id.button_transferencia);

        //Recuperando nombre y dinero de usuario
        Bundle recupera=getIntent().getExtras();
        textView_dineroUsuario.setText(recupera.getString("dinero_usuario"));
        textView_nombreUsuario.setText(recupera.getString("nombre_usuario"));
        textView_dineroDiferido.setText(recupera.getString("dineroDiferido"));


        //Si NO tenemos conexion a internet pasamos directamente a Login
        ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Toast.makeText(getBaseContext(), "Sin conexion a Internet", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);
        }



        //Inicializando la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Boton de salir
        button_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        //Boton pagoMovil
        button_pagomovil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(),IngresarFondos_pagoMovil.class);
                startActivity(intent);
            }
        });

        //Boton transfrenecia
        button_transferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getBaseContext(),IngresarFondos_transferencia.class);
                intent.putExtra("dinero_usuario", textView_dineroUsuario.getText().toString());
                intent.putExtra("nombre_usuario", textView_nombreUsuario.getText().toString());
                intent.putExtra("dineroDiferido",textView_dineroDiferido.getText().toString());
                startActivity(intent);
            }
        });

        //Boton MercadoPago
         button_mercadopago.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 AlertDialog.Builder builder = new AlertDialog.Builder(IngresarFondos.this);

                 builder.setCancelable(true);
                 builder.setTitle("Importante");
                 builder.setMessage("El servicio de MercadoPago tiene un costo del 7% por lo tanto al pagar  10Bs se te acreditara a tu cuenta  9,3 Bs");

                 builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         dialogInterface.cancel();
                     }
                 });

                 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {

                         String url = "https://www.mercadopago.com/mlv/checkout/start?pref_id=177326770-a89c9e54-40cf-407d-9cdc-3e81534b6c1d";
                         Intent intent = new Intent(Intent.ACTION_VIEW);
                         intent.setData(Uri.parse(url));
                         startActivity(intent);

                     }
                 });
                 builder.show();


             }
         });
    }
}
