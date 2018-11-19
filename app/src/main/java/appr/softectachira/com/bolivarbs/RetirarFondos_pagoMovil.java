package appr.softectachira.com.bolivarbs;

import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RetirarFondos_pagoMovil extends AppCompatActivity {

    TextView textView_nombreUsuario,textView_dineroUsuario,textView_dineroDiferido;
    Spinner spinner;
    Button button;
    EditText editText_phone,editText_cedula,editText_montoretiro;
    double doubleAretirar;
    String correo="desarrollotecnologicoaraque2@gmail.com";
    String contraseña="#pinky23";
    Session session;
    ProgressBar progressBar;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //Instancia de DatabaseReference
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirar_fondos_pago_movil);

        textView_dineroUsuario=findViewById(R.id.textView_dineroUsuario_retiroFondos_pagomovil);
        textView_nombreUsuario=findViewById(R.id.textView_nombreUsuario_retiro_pagomovil);
        textView_dineroDiferido=findViewById(R.id.textView_dineroDiferido_pagomovil);
        spinner=findViewById(R.id.spinner_tipo2);
        button=findViewById(R.id.button_retirarpagomovil);
        editText_phone=findViewById(R.id.editText_numerotelefonopagomovil);
        editText_cedula=findViewById(R.id.editText_cedula2);
        editText_montoretiro=findViewById(R.id.editText_montoretiropagomovil);
        progressBar=findViewById(R.id.progressBar_retirarPagomovil);

        Bundle recupera=getIntent().getExtras();
        textView_dineroUsuario.setText(recupera.getString("dinero_usuario"));
        textView_nombreUsuario.setText(recupera.getString("nombre_usuario"));
        textView_dineroDiferido.setText(recupera.getString("dineroDiferido"));

        String [] tipo={"Venezuela","Mercantil","Provincial","Bancaribe","Exterior",
                            "BOD","Caroní","Banesco","Sofitasa","BFC","100% Banco","DelSur","Banco del Tesoro",
                                "Bancrecer","Banplus","Bicentenario","BNC"};
        ArrayAdapter adapter=new ArrayAdapter(this,R.layout.spinner,tipo);
        spinner.setAdapter(adapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               retirarFondos();
            }
        });
    }


    public  void retirarFondos(){
        String telefono=editText_phone.getText().toString().trim();
        String cedula=editText_cedula.getText().toString().trim();
        String banco = spinner.getSelectedItem().toString();
        String montoRetirar=editText_montoretiro.getText().toString().trim();


        //Evaluamos campos vacios
        if(TextUtils.isEmpty(telefono)){
            //correo is empty
            Toast.makeText(this, "Por favor introduce un numero de movil", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(telefono.length()< 11){
            //Contraseña muy corta  tiene menos de 7 caracteres
            Toast.makeText(this, "Numero de movil muy corto", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(banco)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor selecciona un banco", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(TextUtils.isEmpty(cedula)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor introduce el numero de cedula del titular", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }



        if(cedula.length()< 8){
            //Contraseña muy corta  tiene menos de 7 caracteres
            Toast.makeText(this, "Numero de cuenta debe ser de 20 digitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(montoRetirar)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor introduce el monto de dinero a retirar", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }




        //Aqui evaluamos si se tiene suficiente dinero para retirar

        double dineroDisponible= Double.parseDouble(textView_dineroUsuario.getText().toString());
        doubleAretirar=Double.parseDouble(editText_montoretiro.getText().toString());

        if(dineroDisponible<doubleAretirar){
            Toast.makeText(RetirarFondos_pagoMovil.this, "MONTO A RETIRAR EXCEDE TU DINERO DISPONIBLE", Toast.LENGTH_SHORT).show();

        }else if(doubleAretirar<10){
            Toast.makeText(RetirarFondos_pagoMovil.this, "El MONTO MINIMO DE RETIRO ES DE 10 Bs ", Toast.LENGTH_SHORT).show();


        }else{


            //Inicializando la instancia de FirebaseAuth
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            //Obteniendo la referencia de la base de datos (bolivarbs-64dc1)
            databaseReference= FirebaseDatabase.getInstance().getReference();



            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Properties properties=new Properties();
            properties.put("mail.smtp.host","smtp.googlemail.com");
            properties.put("mail.smtp.socketFactory.port","465");
            properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth","true");
            properties.put("mail.smtp.port","465");


            try {
                session = Session.getDefaultInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(correo,contraseña);
                    }
                });


                if(session!=null){

                    final javax.mail.Message message=new MimeMessage(session);
                    message.setFrom(new InternetAddress(correo));
                    message.setSubject("PAGOMOVIL POR REALIZAR");
                    message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(correo));
                    message.setContent("<p>CELULAR: <b>"+editText_phone.getText().toString()+"</b></p>"
                            + "<p>C.I: <b>"+editText_cedula.getText().toString()+"</b></p>"
                            + "<p>MONTO A RETIRAR:  <b>"+editText_montoretiro.getText().toString()+"</b></p>"
                            + "<p></p>"
                            + "<p>EMAIL DEL JUGADOR: "+user.getEmail()+"</p>"
                            + "<p>UID DEL JUGADOR: "+user.getUid()+"</p>"
                            + "<p>TOKEN: "+SalasJuego.TOKEN+"</p>"
                            ,"text/html; charsert=utf-8");



                    //Mostramos la progressDIalog
                    progressBar.setVisibility(View.VISIBLE);


                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Transport.send(message);
                                progressBar.setVisibility(View.INVISIBLE);
                                //Restamos del dinero de la base de datos
                                double dineroUsuario = Double.parseDouble(textView_dineroUsuario.getText().toString());
                                dineroUsuario = dineroUsuario - doubleAretirar;
                                databaseReference.child(user.getUid()).child("money").setValue(dineroUsuario); //Referencia>Hijo> money:dinero

                                Toast.makeText(getBaseContext(), "EL DINERO TE SERA DEPOSITADO EN BREVE", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getBaseContext(),SalasJuego.class);
                                startActivity(intent);

                            } catch (MessagingException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RetirarFondos_pagoMovil.this, "ERROR: DATOS NO ENVIADOS", Toast.LENGTH_SHORT).show();

                            }

                        }
                    },3000);




                }

            }catch (Exception e){
                e.printStackTrace();
            }







        }










    }
}
