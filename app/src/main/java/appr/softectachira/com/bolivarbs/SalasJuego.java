package appr.softectachira.com.bolivarbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

public class SalasJuego extends AppCompatActivity {

    //Instancia de vistas
    ImageButton btn1bs,btn5bs,btn7bs,btn15bs;
    Context context=this;
    private TextView textView_nombreUsuario,textView_dineroUsuario,textView_dineroDiferido,
            textView_esperandoJugadores,textView_notasTapapantalla,textView_mas,textView_37,textView_38;
    private ImageView imageView_retirarDinero,imageView_ingresarDinero,
            imageView_tapaPantalla,imageView_imagenTapaPantalla,imageView_help;
    private ProgressBar progressBar;
    private Button btn_cancelar;


    //Estaticas
    public static String PARTIDAS_JUGADAS;
    public static String TOKEN;


    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    //Instancia de DatabaseReference
    private DatabaseReference databaseReference;

    //comprobante pago unico mercadopago
   public String comprobantePago;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salas_juego);


        //Enlazando vistas
        btn5bs = findViewById(R.id.imageButton250bs);
        btn1bs = findViewById(R.id.imageButton100bs);
        btn7bs = findViewById(R.id.imageButton1000bs);
        btn15bs = findViewById(R.id.imageButton5000bs);

        textView_nombreUsuario = findViewById(R.id.textView_nombreUsuario);
        textView_dineroUsuario = findViewById(R.id.textView_dineroUsuario);
        textView_dineroDiferido=findViewById(R.id.textView_dineroDiferido);
        textView_esperandoJugadores = findViewById(R.id.textView_esperandoJugadores);
        textView_notasTapapantalla = findViewById(R.id.textView_notasTapaPantalla);
        textView_mas=findViewById(R.id.textView28);
        textView_37=findViewById(R.id.textView37);
        textView_38=findViewById(R.id.textView38);
        imageView_retirarDinero = findViewById(R.id.imageView_retirarBs);
        imageView_ingresarDinero = findViewById(R.id.imageView_ingresarBs);
        imageView_tapaPantalla = findViewById(R.id.imageView_tapaPantalla);
        imageView_imagenTapaPantalla = findViewById(R.id.imageView_imagenTapaPantalla);
        imageView_help = findViewById(R.id.imageView_ayuda);
        progressBar = findViewById(R.id.progressBar_salas);
        btn_cancelar = findViewById(R.id.button_salas_cancelar);


        //Si NO tenemos conexion a internet pasamos directamente a Login
        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(context, "Sin conexion a Internet", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        //Obtenemos el TOKEN especifico de este dispositivo
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SalasJuego.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                TOKEN = instanceIdResult.getToken();
                Log.e("newToken", TOKEN);


            }
        });

        //Animacion
        final Animation aumento= AnimationUtils.loadAnimation(this,R.anim.aumento);


        //Volvemos clickleables los botones
        btn1bs.setClickable(true);
        btn5bs.setClickable(true);
        btn7bs.setClickable(true);
        btn15bs.setClickable(true);
        imageView_ingresarDinero.setClickable(true);
        imageView_retirarDinero.setClickable(true);


        //Inicializando la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();



        //Obteniendo la referencia de la base de datos (bolivarbs-64dc1)
       databaseReference = FirebaseDatabase.getInstance().getReference();

      //"Actualizador" de nombre y dinero del usuario y partidas jugadas
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nam = dataSnapshot.child("nombre").getValue().toString();
                String mon = dataSnapshot.child("money").getValue().toString();
                PARTIDAS_JUGADAS = dataSnapshot.child("partidasJugadas").getValue().toString();
                String diferido = dataSnapshot.child("money_pendiente").getValue().toString(); //String para usar en salasJuego??


                textView_nombreUsuario.setText(nam);
                textView_dineroUsuario.setText(mon);
                textView_dineroDiferido.setText(diferido);
                FullScreen_Juego.DINERO_USUARIO = mon;

                textView_dineroUsuario.startAnimation(aumento);
                textView_dineroDiferido.startAnimation(aumento);
                textView_mas.startAnimation(aumento);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        //Boton 1 bs
        btn1bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Evaluacion de lo almacenado en  textView_dineroUsuario
                double dineroUsuario = Double.parseDouble(textView_dineroUsuario.getText().toString());

                if (dineroUsuario >= 1) {

                    //Nos conectaremos al servidor de 1 Bs
                    MyService.SERVERPORT = 9000;
                    MyService.ADDRESS="18.216.73.3";
                    FullScreen_Juego.DINERO_EN_JUEGO = "1";


                    //Empieza El Servicio
                    Intent empezarServicio = new Intent(getBaseContext(), MyService.class);
                    startService(empezarServicio);

                    //Mostramos la progress Bar ,el textView, el tapa pantalla ,la imagen y las notas
                    progressBar.setVisibility(View.VISIBLE);
                    textView_esperandoJugadores.setVisibility(View.VISIBLE);
                    String[] notas = {"Tienes 75% de probabilidad de ganar en cada turno",
                            "El porcentaje de perdida disminuye en cada turno",
                            "Pierde solo 1 jugador, los otros 3 ganan !!!",
                            "En este juego la suerte esta literalmente a tu favor",
                            "Puedes retirar tu dinero cuando quieras",
                            "¡No puedes salirte en plena partida!",
                            "¡Puedes ganar dinero en menos de 2 minutos!",
                            "El dinero perdido por un jugador es repartido entre los otros 3",
                            "Pierde el jugador que saque el menor numero",
                            "Ganas simplemente no sacando el menor numero"};
                    int idx = new Random().nextInt(notas.length);
                    textView_notasTapapantalla.setText(notas[idx]);
                    textView_notasTapapantalla.setVisibility(View.VISIBLE);

                    imageView_tapaPantalla.setVisibility(View.VISIBLE);
                    imageView_imagenTapaPantalla.setVisibility(View.VISIBLE);
                    btn_cancelar.setVisibility(View.VISIBLE);

                    //Hacemos NO clickleables los botones detras de la imagen tapaPantalla
                    btn1bs.setClickable(false);
                    btn5bs.setClickable(false);
                    btn7bs.setClickable(false);
                    btn15bs.setClickable(false);
                    imageView_ingresarDinero.setClickable(false);
                    imageView_retirarDinero.setClickable(false);
                    imageView_help.setClickable(false);

                    //Hacemos INVISIBLES elementos
                    imageView_help.setVisibility(View.INVISIBLE);
                    textView_37.setVisibility(View.INVISIBLE);
                    textView_38.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(getBaseContext(), "No tienes suficiente dinero", Toast.LENGTH_SHORT).show();

                }


            }

        });


        //Boton 5 Bs
        btn5bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Evaluacion de lo almacenado en  textView_dineroUsuario
                double dineroUsuario = Double.parseDouble(textView_dineroUsuario.getText().toString());

                if (dineroUsuario >= 5) {

                    //Nos conectaremos al servidor de 5 Bs
                    MyService.SERVERPORT = 9001;
                    MyService.ADDRESS="18.216.73.3";
                    FullScreen_Juego.DINERO_EN_JUEGO = "5";


                    //Empieza El Servicio
                    Intent empezarServicio = new Intent(getBaseContext(), MyService.class);
                    startService(empezarServicio);

                    //Mostramos la progress Bar ,el textView, el tapa pantalla ,la imagen y las notas
                    progressBar.setVisibility(View.VISIBLE);
                    textView_esperandoJugadores.setVisibility(View.VISIBLE);
                    String[] notas = {"Tienes 75% de probabilidad de ganar en cada turno",
                            "El porcentaje de perdida disminuye en cada turno",
                            "Pierde solo 1 jugador, los otros 3 ganan !!!",
                            "En este juego la suerte esta literalmente a tu favor",
                            "Puedes retirar tu dinero cuando quieras",
                            "¡No puedes salirte en plena partida!",
                            "¡Puedes ganar dinero en menos de 2 minutos!",
                            "El dinero perdido por un jugador es repartido entre los otros 3",
                            "Pierde el jugador que saque el menor numero",
                            "Ganas simplemente no sacando el menor numero"};
                    int idx = new Random().nextInt(notas.length);
                    textView_notasTapapantalla.setText(notas[idx]);
                    textView_notasTapapantalla.setVisibility(View.VISIBLE);
                    imageView_tapaPantalla.setVisibility(View.VISIBLE);
                    imageView_imagenTapaPantalla.setVisibility(View.VISIBLE);
                    btn_cancelar.setVisibility(View.VISIBLE);

                    //Hacemos NO clickleables los botones detras de la imagen tapaPantalla
                    btn1bs.setClickable(false);
                    btn5bs.setClickable(false);
                    btn7bs.setClickable(false);
                    btn15bs.setClickable(false);
                    imageView_ingresarDinero.setClickable(false);
                    imageView_retirarDinero.setClickable(false);
                    imageView_help.setClickable(false);

                    //Hacemos INVISIBLES elementos
                    imageView_help.setVisibility(View.INVISIBLE);
                    textView_37.setVisibility(View.INVISIBLE);
                    textView_38.setVisibility(View.INVISIBLE);



                } else {
                    Toast.makeText(getBaseContext(), "No tienes suficiente dinero", Toast.LENGTH_SHORT).show();

                }

            }
        });

        //Boton 7 bs
        btn7bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Evaluacion de lo almacenado en  textView_dineroUsuario
                double dineroUsuario = Double.parseDouble(textView_dineroUsuario.getText().toString());

                if (dineroUsuario >= 7) {

                    //Nos conectaremos al servidor de 1 Bs
                    MyService.SERVERPORT = 9002;
                    MyService.ADDRESS="18.216.73.3";
                    FullScreen_Juego.DINERO_EN_JUEGO = "7";


                    //Empieza El Servicio
                    Intent empezarServicio = new Intent(getBaseContext(), MyService.class);
                    startService(empezarServicio);

                    //Mostramos la progress Bar ,el textView, el tapa pantalla ,la imagen y las notas
                    progressBar.setVisibility(View.VISIBLE);
                    textView_esperandoJugadores.setVisibility(View.VISIBLE);
                    String[] notas = {"Tienes 75% de probabilidad de ganar en cada turno",
                            "El porcentaje de perdida disminuye en cada turno",
                            "Pierde solo 1 jugador, los otros 3 ganan !!!",
                            "En este juego la suerte esta literalmente a tu favor",
                            "Puedes retirar tu dinero cuando quieras",
                            "¡No puedes salirte en plena partida!",
                            "¡Puedes ganar dinero en menos de 2 minutos!",
                            "El dinero perdido por un jugador es repartido entre los otros 3",
                            "Pierde el jugador que saque el menor numero",
                            "Ganas simplemente no sacando el menor numero"};
                    int idx = new Random().nextInt(notas.length);
                    textView_notasTapapantalla.setText(notas[idx]);
                    textView_notasTapapantalla.setVisibility(View.VISIBLE);
                    imageView_tapaPantalla.setVisibility(View.VISIBLE);
                    imageView_imagenTapaPantalla.setVisibility(View.VISIBLE);
                    btn_cancelar.setVisibility(View.VISIBLE);

                    //Hacemos NO clickleables los botones detras de la imagen tapaPantalla
                    btn1bs.setClickable(false);
                    btn5bs.setClickable(false);
                    btn7bs.setClickable(false);
                    btn15bs.setClickable(false);
                    imageView_ingresarDinero.setClickable(false);
                    imageView_retirarDinero.setClickable(false);
                    imageView_help.setClickable(false);

                    //Hacemos INVISIBLES elementos
                    imageView_help.setVisibility(View.INVISIBLE);
                    textView_37.setVisibility(View.INVISIBLE);
                    textView_38.setVisibility(View.INVISIBLE);



                } else {
                    Toast.makeText(getBaseContext(), "No tienes suficiente dinero", Toast.LENGTH_SHORT).show();

                }

            }
        });

        //Boton 15 bs
        btn15bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Evaluacion de lo almacenado en  textView_dineroUsuario
                double dineroUsuario = Double.parseDouble(textView_dineroUsuario.getText().toString());

                if (dineroUsuario >= 15) {

                    //Nos conectaremos al servidor de 15 Bs
                    MyService.SERVERPORT = 9003;
                    MyService.ADDRESS="18.216.73.3";
                    FullScreen_Juego.DINERO_EN_JUEGO = "15";


                    //Empieza El Servicio
                    Intent empezarServicio = new Intent(getBaseContext(), MyService.class);
                    startService(empezarServicio);

                    //Mostramos la progress Bar ,el textView, el tapa pantalla ,la imagen y las notas
                    progressBar.setVisibility(View.VISIBLE);
                    textView_esperandoJugadores.setVisibility(View.VISIBLE);
                    String[] notas = {"Tienes 75% de probabilidad de ganar en cada turno",
                            "El porcentaje de perdida disminuye en cada turno",
                            "Pierde solo 1 jugador, los otros 3 ganan !!!",
                            "Si pierdes en el primer turno no te preocupes puedes recuperarlo y hasta ganar mas",
                            "Si te sales en plena partida pierdes todo el dinero en esa partida",
                            "En este juego la suerte esta literalmente a tu favor",
                            "Puedes retirar tu dinero cuando quieras",
                            "¡No puedes salirte en plena partida!",
                            "¡Puedes ganar dinero en menos de 2 minutos!",
                            "El dinero perdido por un jugador es repartido entre los otros 3",
                            "Pierde el jugador que saque el menor numero",
                            "Ganas simplemente no sacando el menor numero"};
                    int idx = new Random().nextInt(notas.length);
                    textView_notasTapapantalla.setText(notas[idx]);
                    textView_notasTapapantalla.setVisibility(View.VISIBLE);
                    imageView_tapaPantalla.setVisibility(View.VISIBLE);
                    imageView_imagenTapaPantalla.setVisibility(View.VISIBLE);
                    btn_cancelar.setVisibility(View.VISIBLE);

                    //Hacemos NO clickleables los botones detras de la imagen tapaPantalla
                    btn1bs.setClickable(false);
                    btn5bs.setClickable(false);
                    btn7bs.setClickable(false);
                    btn15bs.setClickable(false);
                    imageView_ingresarDinero.setClickable(false);
                    imageView_retirarDinero.setClickable(false);
                    imageView_help.setClickable(false);

                    //Hacemos INVISIBLES elementos
                    imageView_help.setVisibility(View.INVISIBLE);
                    textView_37.setVisibility(View.INVISIBLE);
                    textView_38.setVisibility(View.INVISIBLE);



                } else {
                    Toast.makeText(getBaseContext(), "No tienes suficiente dinero", Toast.LENGTH_SHORT).show();

                }

            }
        });


        //Boton retirar dinero
        imageView_retirarDinero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int parJugadas = Integer.parseInt(PARTIDAS_JUGADAS);

                //Si ya ha jugado mas de 5 partidas
                if (parJugadas >= 5) {
                    Intent intent = new Intent(getBaseContext(), RetirarFondos.class);
                    intent.putExtra("dinero_usuario", textView_dineroUsuario.getText().toString());
                    intent.putExtra("nombre_usuario", textView_nombreUsuario.getText().toString());
                    intent.putExtra("dineroDiferido",textView_dineroDiferido.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "TIENES QUE JUGAR MINIMO 5 PARTIDAS PARA RETIRAR DINERO", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "PARTIDAS JUGADAS:" + parJugadas, Toast.LENGTH_SHORT).show();
                }


            }
        });

        //Boton ingresar fondos
        imageView_ingresarDinero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getBaseContext(), IngresarFondos.class);
                intent.putExtra("dinero_usuario", textView_dineroUsuario.getText().toString());
                intent.putExtra("nombre_usuario", textView_nombreUsuario.getText().toString());
                intent.putExtra("dineroDiferido",textView_dineroDiferido.getText().toString());
                startActivity(intent);

            }
        });

        //Boton ingresar fondos(desde textview dinero usuario)
        textView_dineroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), IngresarFondos.class);
                intent.putExtra("dinero_usuario", textView_dineroUsuario.getText().toString());
                intent.putExtra("nombre_usuario", textView_nombreUsuario.getText().toString());
                intent.putExtra("dineroDiferido",textView_dineroDiferido.getText().toString());
                startActivity(intent);

            }
        });

        //Boton cancelar (Solo visible al dar click en jugar )
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ocultamos la progress Bar y el textView
                progressBar.setVisibility(View.INVISIBLE);
                textView_esperandoJugadores.setVisibility(View.INVISIBLE);
                textView_notasTapapantalla.setVisibility(View.INVISIBLE);
                imageView_tapaPantalla.setVisibility(View.INVISIBLE);
                imageView_imagenTapaPantalla.setVisibility(View.INVISIBLE);
                btn_cancelar.setVisibility(View.INVISIBLE);

                //Volvemos clickleables los botones
                btn1bs.setClickable(true);
                btn5bs.setClickable(true);
                btn7bs.setClickable(true);
                btn15bs.setClickable(true);
                imageView_ingresarDinero.setClickable(true);
                imageView_retirarDinero.setClickable(true);
                imageView_help.setClickable(true);

                //Volvemos Visibles los elementos
                imageView_help.setVisibility(View.VISIBLE);
                textView_37.setVisibility(View.VISIBLE);
                textView_38.setVisibility(View.VISIBLE);


                //cancelamos el servicio
                Intent stopServicio = new Intent(getBaseContext(), MyService.class);
                getBaseContext().stopService(stopServicio);

            }
        });

        imageView_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Help.class);
                startActivity(intent);
            }
        });




        // MANEJADOR DE LA APP CUANDO SE ABRE DESDE LINK
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        String appLinkData = appLinkIntent.getDataString();


        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            Log.d("appLinkData", appLinkData);
            String comprobanteExtraido = appLinkData.substring(76, 86);

            //PERSISTENCIA DE DATOS RECUPERANDO
            SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
            comprobantePago=datos.getString("comp","");
            String money=datos.getString("money","");


            Log.i("II/Sala", "comprobantePago =" +comprobantePago+ "Comprobante extraido="+comprobanteExtraido );
            //AQUI TENEMOS QUE EVALUAR SI EL COMPROBANTE ES IGUAL AL ALMACENADO ENTONCES EN UN COMPROBANTE REPETIDO Y NO SE HACE NADA
            //PERO SI ES UN COMPROBANTE NUEVO ENTONCES SE AGREGAN 10bs AL FIREBASE USUARIO INSTANTANEAMENTE
            if(comprobanteExtraido.equals(comprobantePago)){
                Toast.makeText(context, "Boton de pago ya Utilizado", Toast.LENGTH_SHORT).show();

            }else{


                try {
                    //Sumamos dinero firebase

                    double dineroUsuarioo = Double.parseDouble(money);
                    dineroUsuarioo = dineroUsuarioo + 9.3;
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.CEILING);
                    databaseReference.child(user.getUid()).child("money").setValue(df.format(dineroUsuarioo));

                    //Guardamos este valor en shared preference
                    SharedPreferences.Editor miEditor=datos.edit();
                    miEditor.putString("money", String.valueOf(df.format(dineroUsuarioo)));
                    miEditor.apply();

                }catch (NumberFormatException n){
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }



                //Guardar el comprobante en shared preferences
                comprobantePago=comprobanteExtraido;
                SharedPreferences.Editor miEditor=datos.edit();
                miEditor.putString("comp",comprobantePago);
                miEditor.apply();


            }







        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Registramos el localBroadcastManager que escuchara/filtrara el intent llamado  INTENT_TO_SALAS (Que viene desde MyService.java)
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(mYourBroadcastReceiver,
                new IntentFilter(MyService.INTENT_TO_SALAS));




    }

    @Override
    protected void onResume() {
        super.onResume();
        //PERSISTENCIA DE DATOS
        //Recuperamos los valores de los textViews nombreUsuario y dineroUsuario
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
        String nombre=datos.getString("name","");
        String money=datos.getString("money","");
        String moneyDif=datos.getString("moneyDif","");
        PARTIDAS_JUGADAS=datos.getString("partidas","");
        comprobantePago=datos.getString("comp","");
        Log.i("II/Sala", "comprobantePago linea 534=" +comprobantePago );

        textView_nombreUsuario.setText(nombre);
        textView_dineroUsuario.setText(money);
        textView_dineroDiferido.setText(moneyDif);








    }

    @Override
    protected void onPause() {
        super.onPause();

        //PERSISTENCIA DE DATOS
        //Guardamos los valores de los textViews nombreUsuario y dineroUsuario
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor miEditor=datos.edit();
        miEditor.putString("name",textView_nombreUsuario.getText().toString());
        miEditor.putString("money",textView_dineroUsuario.getText().toString());
        miEditor.putString("partidas",PARTIDAS_JUGADAS);
        miEditor.putString("moneyDif",textView_dineroDiferido.getText().toString());


        miEditor.apply();

    }

    //BroadcastReceiver que escucha el mensage MSG_TO_SALAS que viene del intent INTENT_TO_SALAS  MyService.java
    private final BroadcastReceiver mYourBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            // Get extra data included in the Intent
            String message = intent.getStringExtra(MyService.MSG_TO_SALAS);
            //Si recibimos conexionExitosa desde MyService.java desaperecemos la progress dialog y empezamos fullScreen.java
            if(message=="conexionExitosa") {
               // progressDialog.dismiss();
                Log.i("II/Sala", "Recibido: conexion exitosa  "  );
                //Empieza la actividad FullScreen_Juego
                Intent empezarActividad=new Intent(getBaseContext(),FullScreen_Juego.class);
                startActivity(empezarActividad);

            }
        }
    };

    //Evitar salir de la actividad dandole atras
    @Override
    public boolean onKeyDown (int keyCode,KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK){

            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode,event);

    }
}
