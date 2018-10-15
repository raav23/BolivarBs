package appr.softectachira.com.bolivarbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;


public class FullScreen_Juego extends AppCompatActivity {

    //  Encargado de recibir "Transmisiones"
    BroadcastReceiver receiver,receiver2,receiver3;

    //Encargado de enviar "Transmisiones"
    LocalBroadcastManager localBroadcastManager;

    //Variables GLobales Estaticas
    public static String DINERO_EN_JUEGO;
    public  static  String DINERO_USUARIO;
    public static String SOY_JUGADOR;

    private TextView visorp1,visorp2,visorp3,visorp4,
                     visorMoneyP1,visorMoneyP2,visorMoneyP3,visorMoneyP4,visorFijoBs,
                     textView_nombre_p2,textView_nombre_p3,textView_nombre_p4,
                     textView_contador_p1,textView_contador_p2,textView_contador_p3,textView_contador_p4;

    private TextView textView_ronda;
    private int contadorTurno=1;

    private Button btnLanzar;

    private ImageView imageView_gif_load,
                      imageView_gif_confeti;

    private MediaPlayer soundFondo,soundFondo2;

    //Variable para manejar de forma dinamica el dinero de usuario en la partida
    double dineroUsuario;

    //Variables que utilizara el Intent que se transmitira hacia MyService.java
    static final public String INTENT_TO_SERVICE = "INTENT_TO_SERVICE";
    static final public String NUMEROALEATORIO= "NUMEROALEATORIO";

    //Variables para controlar los visoresMoney_p2,p3 y p4
    boolean p2_money_recibido=false;
    boolean p3_money_recibido=false;
    boolean p4_money_recibido=false;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //Instancia de DatabaseReference
    private DatabaseReference databaseReference;


    //Controlador para el handler
    Boolean miTurno=false;

    //Controlador para el Toast que esta en OnStop
    Boolean sacadoPorInactividad=false;

    //DecimalFormat
    DecimalFormat df;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen__juego);


        //Que player dentro de la sala soy?
        Log.i("II/FullScreen_Juego>", " SOY PLAYER: "+ SOY_JUGADOR);
        final int soyJugador=Integer.parseInt(SOY_JUGADOR)+1;
        Toast.makeText(this, "ERES EL JUGADOR : "+soyJugador, Toast.LENGTH_SHORT).show();

        //Inicializando la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Obteniendo la referencia de la base de datos (bolivarbs-64dc1)
        databaseReference= FirebaseDatabase.getInstance().getReference();

        //Se le descuenta el dineroEnJuego al jugador para que pueda unirse a la sala
        try {
            //Obtenemos y transformamos a double el dinero con el que cuenta e usuario en firebase
            double dineroUsuario = Double.parseDouble(DINERO_USUARIO);
            //Obtenemos y transformamos a double el dinero de la PARTIDA
            double dineroEnJuego=Double.parseDouble(DINERO_EN_JUEGO);
            //Al dinero de FIREBASE le restamos el DINERO EN JUEGO
            dineroUsuario = dineroUsuario - dineroEnJuego;
            databaseReference.child(user.getUid()).child("money").setValue(dineroUsuario); //Referencia>Hijo> money:dinero

            Toast.makeText(getBaseContext(), "-"+DINERO_EN_JUEGO+" Bs USADOS", Toast.LENGTH_SHORT).show();
        }catch (Exception dineroUsuario){
            Log.i("II/FullScreen_Juego>", " NO SE LE DESCONTARON BS AL JUGADOR: "+ SOY_JUGADOR);

        }


        //Animacion
        final Animation aumento= AnimationUtils.loadAnimation(this,R.anim.aumento);
        final Animation aumento2= AnimationUtils.loadAnimation(this,R.anim.aumento2);
        final Animation sacudir= AnimationUtils.loadAnimation(this,R.anim.sacudir);
        final Animation flash= AnimationUtils.loadAnimation(this,R.anim.flashinfinito);



        //Sonidos
        final MediaPlayer soundDados=MediaPlayer.create(this,R.raw.dados);
        final MediaPlayer soundGanamosDinero=MediaPlayer.create(this, R.raw.ganamosdinero);
        final MediaPlayer otrosLanzan=MediaPlayer.create(this, R.raw.otrosplayersledan);
        final MediaPlayer soundPerdimosDinero=MediaPlayer.create(this,R.raw.perdemosdinero);



        //Obtenemos la instancia donde trabajara nuestro Enviador De Broadcast Locales
        localBroadcastManager = LocalBroadcastManager.getInstance(this);


        //Enlazando textViews y button
        visorp1 = findViewById(R.id.visor_player1);
        visorp2 = findViewById(R.id.visor_player2);
        visorp3 = findViewById(R.id.visor_player3);
        visorp4 = findViewById(R.id.visor_player4);
        btnLanzar = findViewById(R.id.Button_lanzar);
        visorMoneyP1 = findViewById(R.id.visor_money_p1);
        visorMoneyP2 = findViewById(R.id.visor_money_p2);
        visorMoneyP3 = findViewById(R.id.visor_money_p3);
        visorMoneyP4 = findViewById(R.id.visor_money_p4);
        visorFijoBs = findViewById(R.id.visor_fijo_bs);
        textView_nombre_p2 = findViewById(R.id.textView_nombre_p2);
        textView_nombre_p3 = findViewById(R.id.textView_nombre_p3);
        textView_nombre_p4 = findViewById(R.id.textView_nombre_p4);
        textView_contador_p2 = findViewById(R.id.textView_contador_p2);
        textView_contador_p3 = findViewById(R.id.textView_contador_p3);
        textView_contador_p4 = findViewById(R.id.textView_contador_p4);
        textView_contador_p1 = findViewById(R.id.textView_contador_p1);
        textView_ronda=findViewById(R.id.textView_ronda);


        //Enlazando imageView
        imageView_gif_load = findViewById(R.id.imageView_carga_gif);
        imageView_gif_confeti = findViewById(R.id.imageView_confeti_gif);
        imageView_gif_load.setVisibility(View.INVISIBLE);
        imageView_gif_confeti.setVisibility(View.INVISIBLE);

        //Asignando el gif en el imageView Cargando
        Glide.with(FullScreen_Juego.this)
                .asGif()
                .load(R.drawable.load)
                .into(imageView_gif_load);
        //Asignando el gif en imageView Confeti
        Glide.with(FullScreen_Juego.this)
                .asGif()
                .load(R.drawable.confeti)
                .into(imageView_gif_confeti);


        //Custom Toast
        //Creating the LayoutInflater instance
        LayoutInflater li = getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        final View layout_ganamos = li.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast));
        final View layout_pierde = li.inflate(R.layout.custom_toast_pierde,
                (ViewGroup) findViewById(R.id.custom_toast_pierde));
        final View layout_empate = li.inflate(R.layout.custom_toast_empate,
                (ViewGroup) findViewById(R.id.custom_toast_empate));

        //Enviando a los visores de dinero el dinero en juego inicial(100,250,1000)
        visorMoneyP1.setText(DINERO_EN_JUEGO);
        visorMoneyP2.setText(DINERO_EN_JUEGO);
        visorMoneyP3.setText(DINERO_EN_JUEGO);
        visorMoneyP4.setText(DINERO_EN_JUEGO);

        //Enviando a los textView_nombres los nombres aleatorios
        final String[] names = getResources().getStringArray(R.array.nombres);

        for(int i=0;i<=2;i++) {
            int randomIndex = new Random().nextInt(names.length); //Generamos un NUMERO aleatorio por cada iteracion del bucle for
            String randomName = names[randomIndex]; //El NUMERO generado sera un NOMBRE especifico del array

                switch (i){
                    case 0:
                        textView_nombre_p2.setText(randomName);
                        break;
                    case 1:
                        if (randomName.equals(textView_nombre_p2.getText().toString())) {

                            while (textView_nombre_p2.getText().equals(randomName)) {
                                randomIndex = new Random().nextInt(names.length);
                                randomName = names[randomIndex];

                            }
                        } else {
                            textView_nombre_p3.setText(randomName);
                        }


                        break;
                    case 2:
                        if (randomName.equals(textView_nombre_p3.getText().toString())
                                || randomName.equals(textView_nombre_p2.getText().toString())) {

                            while (randomName.equals(textView_nombre_p3.getText().toString())
                                    || randomName.equals(textView_nombre_p2.getText().toString())) {
                                randomIndex = new Random().nextInt(names.length);
                                randomName = names[randomIndex];

                            }
                        } else {
                            textView_nombre_p4.setText(randomName);
                        }

                        break;
                }




            }



        //Lo que se ejecuta al darle al Boton de lanzar
        btnLanzar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Random altr=new Random();
                    String numSacado=Integer.toString(altr.nextInt(13-1)+1);
                    //Enviamos al visorp1 el numero sacado
                    visorp1.setText(numSacado);

                    //Ejecutamos animacion en visorp1
                    visorp1.startAnimation(aumento2);

                    //Enviamos al servicio el Numero Sacado
                    sendResult(numSacado);

                    //Ejecutamos un sonido
                    soundDados.start();

                    //Ocultamos los dados
                    btnLanzar.setVisibility(View.INVISIBLE);

                    //Mostramos el GIF
                    imageView_gif_load.setVisibility(View.VISIBLE);

                    //miTurno =falso
                    miTurno=false;
                    Log.i("II/FullScreen_Juego>", " MITURNO=FALSE ");







                    //¿Que jugador soy? ¿Que debo hacer?
                    switch (SOY_JUGADOR){
                        case "0":
                            //V1 Invisible
                            textView_contador_p1.setVisibility(View.INVISIBLE);
                            //V2 VIsible
                            textView_contador_p2.setVisibility(View.VISIBLE);
                            //Contador p2
                            new CountDownTimer(15000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    textView_contador_p2.setText("TIEMPO: "+millisUntilFinished/1000 );
                                }
                                public void onFinish() {
                                        textView_contador_p2.setText("PIERDE");
                                }
                            }.start();
                            break;

                        case "1":
                            //V1 Invisible
                            textView_contador_p1.setVisibility(View.INVISIBLE);
                            //V3 Visible
                            textView_contador_p3.setVisibility(View.VISIBLE);
                            //Contador p3
                            new CountDownTimer(15000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                    textView_contador_p3.setText("TIEMPO: "+millisUntilFinished/1000 );
                                }

                                public void onFinish() {
                                    textView_contador_p3.setText("PIERDE");
                                }
                            }.start();
                            break;

                        case "2":
                            //V1 Invisible
                            textView_contador_p1.setVisibility(View.INVISIBLE);
                            //V4 Visible
                            textView_contador_p4.setVisibility(View.VISIBLE);
                            //Contador
                            new CountDownTimer(15000, 1000) {

                                public void onTick(long millisUntilFinished) {

                                    textView_contador_p4.setText("TIEMPO: "+millisUntilFinished/1000 );
                                }

                                public void onFinish() {
                                    textView_contador_p4.setText("PIERDE");
                                }
                            }.start();
                            break;

                        case "3":
                            //V1 Invisible
                            textView_contador_p1.setVisibility(View.INVISIBLE);

                            //Despues de 7 segundos muestro contador v2 y reseteo a 0 todos los visores
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    visorp1.setText("0");
                                    visorp2.setText("0");
                                    visorp3.setText("0");
                                    visorp4.setText("0");

                                    contadorTurno++;
                                    textView_ronda.setText("RONDA: "+contadorTurno);

                                    //V2 Visible
                                    textView_contador_p2.setVisibility(View.VISIBLE);
                                    new CountDownTimer(15000, 1000) {

                                        public void onTick(long millisUntilFinished) {

                                            textView_contador_p2.setText("TIEMPO: "+millisUntilFinished/1000 );
                                        }

                                        public void onFinish() {
                                            textView_contador_p2.setText("PIERDE");
                                        }
                                    }.start();
                                }
                            }, 7000);

                            break;



                    }




                }
            });

        //BroadcastReceiver que escucha el mensage MSG_TO_FULLSCREEN que viene del intent INTENT_TO_FULLSCREEN Que viene desde MyService.java
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {

                    String s = intent.getStringExtra(MyService.MSG_TO_FULLSCREEN);
                Log.i("II/FullScreen_Juego>", " BroadcastReceiver Recibido: "+s);


                //Acciones a ejecutar cuando recibamos un Broadcast/Transmision del intent INTENT_TO_FULLSCREEN
                    //SI recibimo "TURNO" mostramos  los dados, si no pues mostramos lo recibido en visor
                    switch(s) {
                        case "TURNO":
                            Log.i("II/FullScreen_Juego>", " RECIBIMOS TURNO, BOTON VISIBLE ");
                            btnLanzar.setVisibility(View.VISIBLE);
                            imageView_gif_load.setVisibility(View.INVISIBLE);

                            //Mi Turno=true
                            miTurno=true;
                            Log.i("II/FullScreen_Juego>", " MITURNO=TRUE");



                            //Al transcurrir 15s sin lanzar dados se cierra la actividad


                            Handler handler=new Handler();
                            final Runnable runnable=new Runnable() {
                                @Override
                                public void run() {


                                    if(miTurno && btnLanzar.getVisibility()==View.VISIBLE) {

                                        Log.i("II/FullScreen/Handler>", "TRANSCURRIERON 15s DE INACTIVIDAD");
                                        //Pasamos a salas
                                        Intent regresarASalas=new Intent(getBaseContext(),SalasJuego.class);
                                        startActivity(regresarASalas);
                                        //Cerramos servicio
                                        Intent intent1=new Intent(getBaseContext(),MyService.class);
                                        stopService(intent1);

                                        //Avisamos al Toast en onStop que fuimos sacados por inactividad
                                        sacadoPorInactividad=true;

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Sumamos otra partida jugada
                                                int pj= Integer.parseInt(SalasJuego.PARTIDAS_JUGADAS);
                                                pj=pj+1;
                                                Log.i("II/FullScreen_Juego>", " Into Handler pj= "+pj);

                                                //Pasamos el dato de partida jugada a la Base de Datos
                                                databaseReference.child(user.getUid()).child("partidasJugadas").setValue(pj);
                                            }
                                        }, 2000);





                                    }else{
                                        Log.i("II/FullScreen/Handler>", "Se cumplieron 15s pero no era mi turno ");
                                    }
                                }
                            };

                            handler.postDelayed(runnable,15000);










                            break;
                        case "EMP":

                            //Mostramos el Custom Toast EMPATE
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setView(layout_empate);//setting the view of custom toast layout
                            toast.show();

                            break;

                        case "closed":

                            //Paramos la musica
                            soundDados.stop();

                            //Detenemos el servicio
                            Intent pararJuego =new Intent(getBaseContext(),MyService.class);
                            stopService(pararJuego);

                            //Pasamos a salas
                            Intent regresarASalas=new Intent(getBaseContext(),SalasJuego.class);
                            startActivity(regresarASalas);

                            //SUMAMOS EL DINERO CON EL QUE TERMINO EL JUEGO EL PLAYER 1
                            try {

                                 df = new DecimalFormat("#.##");
                                df.setRoundingMode(RoundingMode.CEILING);

                                dineroUsuario = Double.parseDouble(DINERO_USUARIO.replace(",","."));
                                Log.i("II/FullScreen_Juego>", "1.dineroUsuario "+dineroUsuario);

                                dineroUsuario = dineroUsuario + Double.parseDouble(visorMoneyP1.getText().toString().replace(",","."));
                                Log.i("II/FullScreen_Juego>", "2.dineroUsuario (sin decimalFormat) "+dineroUsuario);

                                df.format(dineroUsuario);
                                Log.i("II/FullScreen_Juego>", "3.dineroUsuario (con decimalFormat) "+df.format(dineroUsuario));






                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Sumamos otra partida jugada
                                        try {
                                            int pj = Integer.parseInt(SalasJuego.PARTIDAS_JUGADAS);
                                            pj = pj + 1;
                                            Log.i("II/FullScreen_Juego>", " Partidas Jugadas aumenta a = " + pj);

                                            //Pasamos los datos a la Base de Datos

                                            databaseReference.child(user.getUid()).child("money").setValue(df.format(dineroUsuario)); //Referencia>Hijo> money:dinero
                                            databaseReference.child(user.getUid()).child("partidasJugadas").setValue(pj);
                                            Log.i("II/FullScreen_Juego>", " SUMAMOS A MI DINERO: " + visorMoneyP1.getText().toString().trim());

                                        } catch (NumberFormatException n) {
                                            Log.i("II/FullScreen_Juego>", "Error NumberFormatException 1" + n);


                                        }

                                    }
                                }, 2000);

                            }catch (NumberFormatException n){
                                Log.i("II/FullScreen_Juego>", "Error NumberFormatException 2" + n);

                            }
                            break;



                        default:

                            //Sonido
                            otrosLanzan.start();

                            //VISOR 2
                            if(visorp2.getText().equals("0") ) {
                                //Mostramos el numero recibido (s) en el VISOR 2
                                visorp2.setText(s);

                                //Ocultar contador de V2
                                textView_contador_p2.setVisibility(View.INVISIBLE);

                                //¿Que jugador soy? ¿Que debo hacer?
                                switch (SOY_JUGADOR){
                                    case "0":
                                        //Contador V3 Visible
                                        textView_contador_p3.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V3
                                        new CountDownTimer(15000, 1000) {
                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p3.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }
                                            public void onFinish() {

                                                textView_contador_p3.setText("PIERDE");
                                            }
                                        }.start();
                                        break;

                                    case "1":
                                        //Contador V1 Visible
                                        textView_contador_p1.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V1
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p1.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p1.setText("PIERDE");
                                            }
                                        }.start();
                                        break;

                                    case "2":
                                        //Contador V3 Visible
                                        textView_contador_p3.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V3
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p3.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p3.setText("PIERDE");
                                            }
                                        }.start();
                                        break;

                                    case "3":
                                        //Contador V3 Visible
                                        textView_contador_p3.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V3
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p3.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p3.setText("PIERDE");
                                            }
                                        }.start();


                                }



                            //VISOR 3
                            }else if(visorp3.getText().equals("0")){
                                //Mostramos el numero recibido (s) en el VISOR 3
                                visorp3.setText(s);

                                //Ocultar  Contador de V3
                                textView_contador_p3.setVisibility(View.INVISIBLE);

                                //Evaluamos que Contador vamos a mostrar
                                switch (SOY_JUGADOR){
                                    case "0":
                                        //Contador V4 Visible
                                        textView_contador_p4.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V4
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p4.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p4.setText("PIERDE");
                                            }
                                        }.start();
                                        break;

                                    case "1":
                                        //Contador V4 Visible
                                        textView_contador_p4.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V4
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p4.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p4.setText("PIERDE");
                                            }
                                        }.start();
                                        break;

                                    case "2":
                                        //Contador V1 Visible
                                        textView_contador_p1.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V1
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p1.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p1.setText("PIERDE");
                                            }
                                        }.start();
                                        break;

                                    case "3":
                                        //Contador V4 Visible
                                        textView_contador_p4.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V4
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p4.setText("TIEMPO: "+millisUntilFinished/1000 );
                                            }

                                            public void onFinish() {
                                                textView_contador_p4.setText("PIERDE");
                                            }
                                        }.start();


                                }


                             //VISOR 4
                            }else if(visorp4.getText().equals("0")) {
                                visorp4.setText(s);

                                //Ocultar contador de V4
                                textView_contador_p4.setVisibility(View.INVISIBLE);

                                //Evaluamos que Contador vamos a mostrar
                                switch (SOY_JUGADOR) {
                                    case "0":
                                        //Despues de 7 segundos muestro mi contador
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Contador V1 Visible
                                                textView_contador_p1.setVisibility(View.VISIBLE);
                                                //Pasar Segundos restantes a Contador V1
                                                new CountDownTimer(15000, 1000) {

                                                    public void onTick(long millisUntilFinished) {

                                                        textView_contador_p1.setText("TIEMPO: " + millisUntilFinished / 1000);
                                                    }

                                                    public void onFinish() {
                                                        textView_contador_p1.setText("PIERDE");
                                                    }
                                                }.start();
                                            }
                                        }, 7000);

                                        break;

                                    case "1":
                                        //Despues de 7 segundos muestro contador v2
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Contador V2 Visible
                                                textView_contador_p2.setVisibility(View.VISIBLE);
                                                //Pasar Segundos restantes a Contador V2
                                                new CountDownTimer(15000, 1000) {

                                                    public void onTick(long millisUntilFinished) {

                                                        textView_contador_p2.setText("TIEMPO: " + millisUntilFinished / 1000);
                                                    }

                                                    public void onFinish() {
                                                        textView_contador_p2.setText("PIERDE");
                                                    }
                                                }.start();
                                            }
                                        }, 7000);

                                        break;

                                    case "2":
                                        //Despues de 7 segundos muestro contador v2
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Contador V2 Visible
                                                textView_contador_p2.setVisibility(View.VISIBLE);
                                                //Pasar Segundos restantes a Contador V2
                                                new CountDownTimer(15000, 1000) {

                                                    public void onTick(long millisUntilFinished) {

                                                        textView_contador_p2.setText("TIEMPO: " + millisUntilFinished / 1000);
                                                    }

                                                    public void onFinish() {
                                                        textView_contador_p2.setText("PIERDE");
                                                    }
                                                }.start();
                                            }
                                        }, 7000);


                                        break;

                                    case "3":

                                        //Contador V1 Visible
                                        textView_contador_p1.setVisibility(View.VISIBLE);
                                        //Pasar Segundos restantes a Contador V1
                                        new CountDownTimer(15000, 1000) {

                                            public void onTick(long millisUntilFinished) {

                                                textView_contador_p1.setText("TIEMPO: " + millisUntilFinished / 1000);
                                            }

                                            public void onFinish() {
                                                textView_contador_p1.setText("PIERDE");
                                            }
                                        }.start();


                                }


                                // Si somos jugadores 1,2 o 3  y llega numero a V4 Reseteamos a 0 todos los visores
                                if (SOY_JUGADOR.equals("0") || SOY_JUGADOR.equals("1")||SOY_JUGADOR.equals("2")) {
                                    new CountDownTimer(7000, 1000) {

                                        public void onTick(long millisUntilFinished) {

                                        }

                                        public void onFinish() {
                                            visorp1.setText("0");
                                            visorp2.setText("0");
                                            visorp3.setText("0");
                                            visorp4.setText("0");

                                            contadorTurno++;
                                            textView_ronda.setText("RONDA: "+contadorTurno);
                                        }
                                    }.start();

                                }
                            }

                            break;
                    }
            }
        };

        //BroadcastReceiver que escucha el mensage REFRESH_MONEY_PLAYER que viene del intent INTENT_TO_FULLSCREEN_2 Que viene desde MyService.java)
        receiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //Guardamos el string que viene desde myservice.java (el dinero actualizado del player1
                String refresh = intent.getStringExtra(MyService.REFRESH_MONEY_PLAYER);
                Log.i("II/FullScreen_Juego>", " receiver2 > REFRESH : "+refresh);



                //Si lo que recibimos es mayor que lo que habia entonces GANAMOS dinero
                if(Float.parseFloat(refresh) > Float.parseFloat(visorMoneyP1.getText().toString())
                        ){
                    //Ejecutamos sonido
                    soundGanamosDinero.start();
                    //Ejecutar animacion
                    visorMoneyP1.startAnimation(aumento);
                    //Mostramos el Custom Toast Felicitaciones!
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setView(layout_ganamos);//setting the view of custom toast layout
                    toast.show();


                    //Monstramos imagview/gif confeti
                    imageView_gif_confeti.setVisibility(View.VISIBLE);
                    //Despues de 6 segundos ocultamos Confeti
                    new CountDownTimer(6000, 1000) {
                        public void onFinish() {
                            // When timer is finished
                            // Execute your code here

                            imageView_gif_confeti.setVisibility(View.INVISIBLE);
                        }

                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                        }
                    }.start();


                    //Si lo que recibimos es MENOR que lo que habia entonces PERDIMOS dinero
                } else if (Float.parseFloat(refresh) < Float.parseFloat(visorMoneyP1.getText().toString())
                        ) {

                    //Ejecutamos sonido
                    soundPerdimosDinero.start();
                    //Ejecutamos animacion
                    visorMoneyP1.startAnimation(sacudir);

                    //Mostramos el Custom Toast Perdimos :(
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setView(layout_pierde);//setting the view of custom toast layout
                    toast.show();
                }


                visorMoneyP1.setText(refresh);




            }
        };


        // BroadcastReceiver 3 que escucha el mensaje  REFRESH_MONEY_OTHER_PLAYERS que viene desde el intent  INTENT_TO_FULLSCREEN_3
        receiver3=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String numActualizado=intent.getStringExtra(MyService.REFRESH_MONEY_OTHER_PLAYERS);

                if(p2_money_recibido==false){
                    visorMoneyP2.setText(numActualizado);
                    p2_money_recibido=true;

                    //Ejecutar animacion
                    visorMoneyP2.setAnimation(aumento);



                    }else if(p3_money_recibido==false){
                        visorMoneyP3.setText(numActualizado);
                         p3_money_recibido=true;

                        //Ejecutar animacion
                        visorMoneyP3.setAnimation(aumento);


                         }else if(p4_money_recibido==false){
                            visorMoneyP4.setText(numActualizado);

                          //Ejecutar animacion
                          visorMoneyP4.setAnimation(aumento);

                            p2_money_recibido=false;
                            p3_money_recibido=false;
                        }

            }
        };


    }


    @Override
    protected void onStart() {
        super.onStart();

        //Filtrando los intents que vengan desde MyService y que sean "INTENT_TO_FULLSCREEN"
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(MyService.INTENT_TO_FULLSCREEN));
        // Con esto filtramos intents que vengan desde MyService y que sean "INTENT_TO_FULLSCREEN_2"
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver2), new IntentFilter(MyService.INTENT_TO_FULLSCREEN_2));
        ////Filtrando los intents que vengan desde MyService y que sean "INTENT_TO_FULLSCREEN_3"
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver3),new IntentFilter(MyService.INTENT_TO_FULLSCREEN_3));



        //initializing the sounds
        soundFondo = MediaPlayer.create(this, R.raw.fondo);
        soundFondo2 = MediaPlayer.create(this, R.raw.fondo2);

        //generate random number
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(2) + 1;



        //picking the right sound to play
        switch (randomInt){
            case 1: soundFondo.start();

                break;
            case 2: soundFondo2.start();
                break;

        }



    }


    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver2);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver3);

        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

            if(!sacadoPorInactividad){
                Toast.makeText(this, "DINERO GANADO: "+visorMoneyP1.getText().toString().trim(), Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "SACADO DEL JUEGO POR INACTIVIDAD", Toast.LENGTH_SHORT).show();
            }



            soundFondo.stop();
            soundFondo2.stop();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();

    }






    @Override
    protected void onDestroy() {

        super.onDestroy();
        soundFondo.stop();
        soundFondo2.stop();
    }

    //Metodo que enviara el Broadcast a MyService
    public void sendResult(String message) {
        Intent intent = new Intent(INTENT_TO_SERVICE);
        if(message != null)
            intent.putExtra(NUMEROALEATORIO, message);
        //Transmitir el intent dado a TODOS los BroadcastReceivers.
        localBroadcastManager.sendBroadcast(intent);
    }

    //Evitar salir de la actividad dandole atras
    @Override
    public boolean onKeyDown (int keyCode,KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    //VOLVER LA ACTIVIDAD FULSCREEN DE INMERSION
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }




}


