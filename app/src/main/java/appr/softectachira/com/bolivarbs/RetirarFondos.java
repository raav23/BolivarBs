package appr.softectachira.com.bolivarbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RetirarFondos extends AppCompatActivity {

    TextView textView_nombreUsuario,textView_dineroUsuario,textView_dineroDiferido;
    Button btn_transf,btn_pagomovil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirar_fondos);

        //Enlazando vistas
        textView_dineroUsuario=findViewById(R.id.textView_dineroUsuario_retiroFondos);
        textView_nombreUsuario=findViewById(R.id.textView_nombreUsuario_retiroFondos2);
        textView_dineroDiferido=findViewById(R.id.textView_dineroDiferido3);
        btn_transf=findViewById(R.id.button_transferencia);
        btn_pagomovil=findViewById(R.id.button_pagoMovil);


        Bundle recupera=getIntent().getExtras();
        textView_dineroUsuario.setText(recupera.getString("dinero_usuario"));
        textView_nombreUsuario.setText(recupera.getString("nombre_usuario"));
        textView_dineroDiferido.setText(recupera.getString("dineroDiferido"));

        //Acciones de los btns
        //Transferencia
        btn_transf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RetirarFondos.this);

                builder.setCancelable(true);
                builder.setTitle("Importante");
                builder.setMessage("Por ahora solo las transferencias hacia los siguientes bancos se haran efectivas de manera inmediata  : BANESCO,  VENEZUELA, PROVINCIAL, MERCANTIL, 100% BANCO. Para los demas bancos el dinero se hara efectivo el siguiente dia habil despues de haber recibido los datos para la transferencia");

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent=new Intent(getApplicationContext(),RetirarFondos_transferencia.class);
                        intent.putExtra("dinero_usuario",textView_dineroUsuario.getText().toString());
                        intent.putExtra("nombre_usuario",textView_nombreUsuario.getText().toString());
                        intent.putExtra("dineroDiferido",textView_dineroDiferido.getText().toString());
                        startActivity(intent);

                    }
                });
                builder.show();

            }
        });

        //Pago Movil
        btn_pagomovil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
