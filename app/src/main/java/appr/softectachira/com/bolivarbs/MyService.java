package appr.softectachira.com.bolivarbs;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;


/**
 * Created by CASA on 13/05/2017.
 */

public class MyService extends Service {
    //Declaracion de Objetos GLobales

    private Socket socket;
    private Boolean listos4Jugadores=false,
                    partidaViva=true,
                    miTurno=false,
                    clickDados=false;



    public  static String POPONUMBER;




    //Ayudante para  enviar transmisiones de intents a objetos locales dentro de su proceso
    LocalBroadcastManager localBroadcastManager;
    //Recibidor de "Transmisiones"
    BroadcastReceiver receiverInService;
    //PUERTO
    //private static final int SERVERPORT = 9000;
      public static int SERVERPORT;
    //HOST
     public static  String ADDRESS;
    //private static final String ADDRESS = "52.15.238.106";
    //private static final String ADDRESS = "192.168.1.37";

    //Variables del intent que se transmitira a SalasJuego.java
    static final public String INTENT_TO_SALAS = "INTENT_TO_SALAS";
    static final public String MSG_TO_SALAS="MSG_TO_SALAS";
    //Variables del intent que se transmitira a FullScreen_Juego.java
    static final public String INTENT_TO_FULLSCREEN = "TOFULLSCREEN";
    static final public String MSG_TO_FULLSCREEN = "MSG_TO_FULLSCREEN";
    //Variables de otro intent que se transmitira tambien a FullScreen_juego.java
    static final public String INTENT_TO_FULLSCREEN_2 = "TOFULLSCREEN2";
    static final public String REFRESH_MONEY_PLAYER = "REFRESH";
    //Variables de otro intent que se transmitira tambien a FullScreen_juego.java
    static final public String INTENT_TO_FULLSCREEN_3 = "TOFULLSCREEN3";
    static final public String REFRESH_MONEY_OTHER_PLAYERS = "REFRESH_OTHER";



    @Override
    public void onCreate() {
        super.onCreate();
                try {
                    //Empezamos el Hilo
                    HiloConector mihilo = new HiloConector();
                    mihilo.start();
                }catch (Exception e){

                }

        //Creamos el BroadcastReceiver encargado de RECIBIR "Transmisiones"
        receiverInService = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Guardamos el contenido del Broadcast que recibimos desde   FullScreen_juego
                String numeroAleatorio = intent.getStringExtra(FullScreen_Juego.NUMEROALEATORIO);
                Log.i("II/MyService>", " intent.getStringEztra.NUMEROALEATORIO: "+intent.getStringExtra(FullScreen_Juego.NUMEROALEATORIO));

                //Los dados fueron presionados
                clickDados=true;



            }
        };

        //Obtenemos la instancia donde trabajara nuestro LocalBroadcastManager
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        //Es necesario registrar el BroadcastReceiver quien filtrara intents que vengan desde FullScrenn_Juego con parametro"RESULTADO“
        LocalBroadcastManager.getInstance(this).registerReceiver((receiverInService),
                new IntentFilter(FullScreen_Juego.INTENT_TO_SERVICE)
        );


    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("I/SERVICIO >","onStartCommand");

        return super.START_NOT_STICKY;

    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverInService);

       //Cuando de cierra el servicio , cerramos el socket.
       try {
           if(socket!=null) {
               socket.close();
               Log.i("II/MyService", "socket cerrado ");
           }else {
               Toast.makeText(this, "No se pudo comunicar con el servidor", Toast.LENGTH_SHORT).show();
           }

       } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    //Hilo conector
    class HiloConector extends Thread {


        @Override
        public void run() {
            super.run();

            try {
                //Se conecta al servidor
                InetAddress serverAddr = InetAddress.getByName(ADDRESS);
                socket = new Socket(serverAddr, SERVERPORT);
                Log.i("II/TCP Client", "Connected to server");



                //Creamos la Entrada
                final InputStream inputFromServer = socket.getInputStream();
                //Creamos la salida.
                PrintStream outputToServer = new PrintStream(socket.getOutputStream());



                while(!listos4Jugadores){
                    //RECIBIMOS DESDE EL SERVER

                    byte[] lenBytes = new byte[256];
                    inputFromServer.read(lenBytes, 0, 256);
                    String received = new String(lenBytes, "UTF-8").trim();
                    Log.i("II/Hilo", "RECIBIDO EN !listos4Jugadores: " + received);


                    //Si ya hay 4 jugadores efectivamente en una sala el servidor envia "OK"
                    if(received.equals("OK")){
                        Log.i("II/Hilo", "Recibido OK? : " + received);
                        listos4Jugadores=true;
                        sendResult("conexionExitosa");//Enviamos a Salas.java

                    }


                    switch (received){
                        case "0":
                            Log.i("II/Hilo >", "Que jugaodor soy " + received);
                            FullScreen_Juego.SOY_JUGADOR="0";
                            break;
                        case "1":
                            Log.i("II/Hilo >", "Que jugaodor soy " + received);
                            FullScreen_Juego.SOY_JUGADOR="1";
                            break;
                        case "2":
                            Log.i("II/Hilo >", "Que jugaodor soy " + received);
                            FullScreen_Juego.SOY_JUGADOR="2";
                            break;
                        case "3":
                            Log.i("II/Hilo >", "Que jugaodor soy " + received);
                            FullScreen_Juego.SOY_JUGADOR="3";
                            break;
                    }

                }

                while(partidaViva) {

                    //MIENTRAS NO SEA MI TURNO
                    while (!miTurno ) {

                        //RECIBIMOS DESDE EL SERVER
                        byte[] lenBytes = new byte[256];
                        inputFromServer.read(lenBytes, 0, 256);
                        String received = new String(lenBytes, "UTF-8").trim();
                        Log.i("II/Hilo >", "Recibido en: miTurno=false > " + received);

                        //SI RECIBIMOS "LOSE"
                        if(received.equals("LOSE")){
                                    Log.i("II/Hilo", "Received LOSE?   :" + received);
                                    received="";
                                    boolean esperandoNum=true;
                                        //Mientras no recibamos el monto del dinero que nos quedo en el server no seguimos
                                        while(esperandoNum){

                                            //RECIBIMOS DESDE EL SERVER
                                            lenBytes = new byte[256];
                                            inputFromServer.read(lenBytes, 0, 256);
                                            received = new String(lenBytes, "UTF-8").trim();
                                            Log.i("II/Hilo >", "Recibido en: esperandoNumLOSE==true > " + received);

                                                if(received.length()>0 ){
                                                    refreshMoney(received);//*

                                                    esperandoNum=false;
                                                    received="";
                                                }
                                        }

                         //SI RECIBIMOS "WIN"
                        }else if(received.equals("WIN")){
                            Log.i("II/Hilo", "Received WIN?   :" + received);
                            received="";
                            boolean esperandoNum=true;
                            //Mientras no recibamos el monto del dinero que nos quedo en el server no seguimos
                            while(esperandoNum){

                                //RECIBIMOS DESDE EL SERVER
                                lenBytes = new byte[256];
                                inputFromServer.read(lenBytes, 0, 256);
                                received = new String(lenBytes, "UTF-8").trim();
                                Log.i("II/Hilo >", "Recibido en: esperandoNumWIN==true > " + received);

                                if(received.length()>0 ){
                                    refreshMoney(received);//*
                                    esperandoNum=false;
                                    received="";
                                }
                            }

                        }else if(received.equals("EMP")){
                            Log.i("II/Hilo", "EMPATE  :" );
                            sendResult("EMP");



                            //SI RECIBIMOS "TURNO" ENTONCES ES NUESTRO TURNO Y LE INFORMAMOS A FULLSCREEN.JAVA
                        } else if (received.equals("TURNO")) {
                            Log.i("II/Hilo", "Received TURNO?  :" + received);
                            sendResult("TURNO");
                            miTurno = true;

                            //SI RECIBIMOS CLOSED  YA HAY QUE CERRAR LA ACTIVIDAD Y LE INFORMAMOS A FULLSCREEN.JAVA
                        } else if (received.equals("closed")) {
                            Log.i("II/Hilo", "Received closed?  :" + received);
                            sendResult("closed");


                            //SI RECIBIMOS 1 o 2 DIGITOS (RECIBIMOS NUMEROS 1-12) o RECIBIMOS UN NUMERO MAYOR A 0 Y MENOR A 13
                        } else if(received.length()>0 && received.length()<3 && Integer.parseInt(received)>=1 && Integer.parseInt(received)<=12)  {


                                Log.i("II/Hilo", "RECIBIDO UN NUMERO:  :" + received+ " length: "+received.length());
                                //Enviamos a FullScreen (Lo numeros que sacaron los demas jugadores)
                                sendResult(received);

                                //SI DECIMAL
                            }else if (esDecimal(received)){
                            //esDecimal(received)|| received.length()>=2 && received.length()<=4 &&Integer.parseInt(received)>12
                                 Log.i("II/Hilo", "esDecimal?:  :" +esDecimal(received));


                                refreshMoneyOtherPlayers(received);



                            //SI NO RECIBIMOS NADA DE LO ANTERIOR
                            }else {
                            Log.i("II/Hilo","LO RECIBIDO NO CUMPLE NINGUN PARAMETRO, RECIBIDO: "+received);

                            Boolean recibimos_closed,
                                    recibimos_turno,
                                    recibimos_win;

                            //boolean= la cadena que recibimos contiene el texto? :
                            recibimos_closed = received.contains("closed");
                            recibimos_turno = received.contains("TURNO");
                            recibimos_win = received.contains("WIN");



                            //RECIBIMOS TURNO + ALGO
                            if (recibimos_turno) {
                                sendResult("TURNO");
                            }

                            //SI RECIBIMOS  WIN + ALGO

                            if (recibimos_win) {
                                //Si recibimos 7 caracteres: WIN6.79
                                if (received.length() == 7) {

                                    String dineroFiltrado = received.substring(3, 7);
                                    refreshMoney(dineroFiltrado);
                                }
                            }


                            //RECIBIMOS WIN + ALGO + CLOSED
                            if(recibimos_win && recibimos_closed) {
                                        //Si recibimos 11 caracteres pegados :WIN50closed
                                        if (received.length() == 11) {
                                            //Filtramos el dinero que ganamos:  desde la posicion 3 hasta la posicion 8
                                            String dineroFiltrado = received.substring(3, 5);
                                            //Lo enviamos a fullScreen
                                            refreshMoney(dineroFiltrado);
                                            sendResult("closed");

                                            //Si recibimos 13 caracteres pegados :WIN7.22closed
                                        } else if (received.length() == 13) {
                                            //Filtramos el dinero que ganamos:  desde la posicion 3 hasta la posicion 8
                                            String dineroFiltrado = received.substring(3, 7);
                                            //Lo enviamos a fullScreen
                                            refreshMoney(dineroFiltrado);
                                            sendResult("closed");

                                            //Si recibimos 14 caracteres pegados :WIN15.22closed
                                        }else if (received.length() == 14) {
                                                //Filtramos el dinero que ganamos:  desde la posicion 3 hasta la posicion 8
                                                String dineroFiltrado = received.substring(3, 8);
                                                //Lo enviamos a fullScreen
                                                refreshMoney(dineroFiltrado);
                                                sendResult("closed");

                                            //Si recibimos 15 caracteres pegados :WIN123.33closed
                                        } else if (received.length() == 15) {
                                            //Filtramos el dinero que ganamos:  desde la posicion 3 hasta la posicion 9
                                            String dineroFiltrado = received.substring(3, 9);
                                            //Lo enviamos a fullScreen
                                            refreshMoney(dineroFiltrado);
                                            sendResult("closed");

                                            //Si recibimos 16 caracteres pegados :WIN3333.33closed
                                        } else if (received.length() == 16) {
                                            //Filtramos el dinero que ganamos:  desde la posicion 3 hasta la posicion 9
                                            String dineroFiltrado = received.substring(3, 10);
                                            //Lo enviamos a fullScreen
                                            refreshMoney(dineroFiltrado);
                                            sendResult("closed");

                                        }

                                //RECIBIMOS ALGO + CLOSED
                                }else if(recibimos_closed){
                                sendResult("closed");

                                }




                        }



                    }


                    //MIENTRAS SEA MI TURNO:
                    while (miTurno) {


                            //Ya fueron presionados los dados?
                            if (clickDados) {


                                outputToServer.print(POPONUMBER);
                                Log.i("II/Hilo>", "Enviamos al server POPONUMBER: " + POPONUMBER);
                                miTurno = false;
                                clickDados = false;
                                Log.i("II/Hilo>", "Termina nuestro turno");


                            }




                    }

                }



                } catch (UnknownHostException ex) {
                Log.e("E/TCP Client", "" + ex.getMessage());

                 } catch (IOException ex) {
                Log.e("E/TCP Client", " " + ex.getMessage());

            }


        }
    }

    //Metodo que enviara el Broadcast
    public void sendResult(String message) {
        //Transmision/Broadcast hacia SalasJuego
        if(message =="conexionExitosa"){
                Intent intent = new Intent(INTENT_TO_SALAS);
                intent.putExtra(MSG_TO_SALAS, message);
                localBroadcastManager.sendBroadcast(intent);

        }else {
            //Transmision/Broadcast hacia FULLSCREEN_JUEGO.JAVA
            Intent intent = new Intent(INTENT_TO_FULLSCREEN);
            intent.putExtra(MSG_TO_FULLSCREEN, message);
            localBroadcastManager.sendBroadcast(intent);
        }


    }



    //Metodo que enviara otro broadcast encargado de actualizar le dinero del Jugador
    public void refreshMoney(String message){
        Intent intent = new Intent(INTENT_TO_FULLSCREEN_2);
        intent.putExtra(REFRESH_MONEY_PLAYER, message);
        localBroadcastManager.sendBroadcast(intent);
    }

    //Metodo que enviara otro  broadcast encargado de actualizar el dinero de los demas jugadores
    public void refreshMoneyOtherPlayers(String message){
        Intent intent=new Intent(INTENT_TO_FULLSCREEN_3);
        intent.putExtra(REFRESH_MONEY_OTHER_PLAYERS,message);
        localBroadcastManager.sendBroadcast(intent);
    }


    //Metodo para evaluar si un numero es decimal
    //Devuelve true si la cadena que llega tiene la sintaxis de un decimal
    public boolean esDecimal(String cad)
    {
        boolean hayPunto=false;
        StringBuffer parteEntera = new StringBuffer();
        StringBuffer parteDecimal = new StringBuffer();
        int i=0, posicionDelPunto;

        for( i=0;i<cad.length(); i++ )
            if ( cad.charAt(i) == '.')                          //Detectar si hay un punto decimal en la cadena
                hayPunto=true;
        if(hayPunto)                                            //Si hay punto guardar la posicion donde se encuentra el carater punto
            posicionDelPunto=cad.indexOf('.');                  //(si la cadena tiene varios puntos, detecta donde esta el primero).
        else
            return false;                                       //Si no hay punto; no es decimal

        if( posicionDelPunto == cad.length()-1 || posicionDelPunto== 0)    //Si el punto esta al final o al principio no es un decimal
            return false;

        for( i=0;i<posicionDelPunto; i++ )
            parteEntera.append(cad.charAt(i)) ;                 //Guardar la parte entera en una variable

        for(i = 0; i<parteEntera.length(); i++)
            if( ! Character.isDigit(parteEntera.charAt(i)) )    //Si alguno de los caracteres de la parte entera no son digitos no es decimal
                return false;

        for( i=posicionDelPunto+1;i<cad.length(); i++ )
            parteDecimal.append(cad.charAt(i));                 //Guardar la parte decimal en una variable

        for(i = 0; i<parteDecimal.length(); i++)
            if( ! Character.isDigit(parteDecimal.charAt(i)) )   //Si alguno de los caracteres de la parte decimal no es un digito no es decimal
                return false;                                   //Incluye el caso en el que la cadena tenga dos o mas puntos

        return true;                                            //Si paso todas las pruebas anteriores, la cadena es un Numero decimal
    }






}
