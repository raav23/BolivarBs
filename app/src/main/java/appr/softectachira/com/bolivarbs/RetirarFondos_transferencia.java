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

public class RetirarFondos_transferencia extends AppCompatActivity {

    Spinner spinner;
    Button btn;
    String correo="desarrollotecnologicoaraque2@gmail.com";
    String contraseña="#pinky23";
    Session session;
    TextView textView_nombreUsuario,textView_dineroUsuario, textView_dineroDiferido;
    EditText editText_nombreApellido,editText_numeroci,editText_numeroCuenta,editText_montoaretirar;
    ProgressBar progressBar;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //Instancia de DatabaseReference
    private DatabaseReference databaseReference;

    //Variable del monto a ser retirado
    double montoAretirar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirar_fondos_transferencia);



        //Enlazando vistas
        textView_dineroUsuario=findViewById(R.id.textView_dineroUsuario_retiroFondos2);
        textView_nombreUsuario=findViewById(R.id.textView_nombreUsuario_retiroFondos2);
        textView_dineroDiferido=findViewById(R.id.textView_dineroDiferido4);

        editText_montoaretirar=findViewById(R.id.editText_montoaretirar);
        editText_nombreApellido=findViewById(R.id.editText_nombreyapellido);
        editText_numeroci=findViewById(R.id.editText_numeroci);
        editText_numeroCuenta=findViewById(R.id.editText_numcuenta);
        btn=findViewById(R.id.button_retirarportransf);
        spinner=findViewById(R.id.spinner_tipo);
        progressBar=findViewById(R.id.progressBar_retirarTransferencia);

    


        Bundle recupera=getIntent().getExtras();
        textView_dineroUsuario.setText(recupera.getString("dinero_usuario"));
        textView_nombreUsuario.setText(recupera.getString("nombre_usuario"));
        textView_dineroDiferido.setText(recupera.getString("dineroDiferido"));







        String [] tipo={"V","J","E"};
        ArrayAdapter adapter=new ArrayAdapter(this,R.layout.spinner,tipo);
        spinner.setAdapter(adapter);

        //Boton retirar por transf
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               retirarFondos();

            }
        });





    }


    public  void retirarFondos(){
        String nombreTItular=editText_nombreApellido.getText().toString().trim();
        String tipoDeCuenta = spinner.getSelectedItem().toString();
        String numeroCi=editText_numeroci.getText().toString().trim();
        String numeroCuenta=editText_numeroCuenta.getText().toString().trim();
        String montoRetirar=editText_montoaretirar.getText().toString().trim();


        //Evaluamos campos vacios
        if(TextUtils.isEmpty(nombreTItular)){
            //correo is empty
            Toast.makeText(this, "Por favor introduce el nombre del titular de la cuenta", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(nombreTItular.length()< 9){
            //Contraseña muy corta  tiene menos de 7 caracteres
            Toast.makeText(this, "Nombre del titular muy corto!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(numeroCi)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor introduce el numero de cedula o rif del titular", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(TextUtils.isEmpty(numeroCuenta)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor introduce el numero de cuenta", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(numeroCuenta.length()< 20){
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
         montoAretirar=Double.parseDouble(editText_montoaretirar.getText().toString());

            if(dineroDisponible<montoAretirar ){
            Toast.makeText(RetirarFondos_transferencia.this, "MONTO A RETIRAR EXCEDE TU DINERO DISPONIBLE", Toast.LENGTH_SHORT).show();

            }else if(montoAretirar<10){
            Toast.makeText(RetirarFondos_transferencia.this, "El MONTO MINIMO DE RETIRO ES DE 10 Bs ", Toast.LENGTH_SHORT).show();


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
                        message.setSubject("TRANSFERENCIA POR REALIZAR");
                        message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(correo));
                        message.setContent("<p>TITULAR DE LA CUENTA: <b>"+editText_nombreApellido.getText().toString()+"</b></p>"
                                            + "<p>TIPO Y NUMERO: <b>"+tipoDeCuenta+"-"+editText_numeroci.getText().toString()+"</b></p>"
                                            + "<p>NUMERO DE LA CUENTA: <b>"+editText_numeroCuenta.getText().toString()+"</b></p>"
                                            + "<p>MONTO A RETIRAR:  <b>"+editText_montoaretirar.getText().toString()+"</b></p>"
                                            + "<p></p>"
                                            + "<p>EMAIL DEL JUGADOR: "+user.getEmail()+"</p>","text/html; charsert=utf-8");



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
                                    dineroUsuario = dineroUsuario - montoAretirar;
                                    databaseReference.child(user.getUid()).child("money").setValue(dineroUsuario); //Referencia>Hijo> money:dinero

                                    Toast.makeText(getBaseContext(), "EL DINERO TE SERA DEPOSITADO EN MENOS DE 12 HORAS", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getBaseContext(),SalasJuego.class);
                                    startActivity(intent);

                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(RetirarFondos_transferencia.this, "ERROR: DATOS NO ENVIADOS", Toast.LENGTH_SHORT).show();

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
