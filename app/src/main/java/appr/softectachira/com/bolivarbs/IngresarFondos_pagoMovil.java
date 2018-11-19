package appr.softectachira.com.bolivarbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class IngresarFondos_pagoMovil extends AppCompatActivity {

    private Button btnSubirRecibo,btnSENDemail;
    private ImageView imageView;
    private ProgressBar progressBar;
    String correo="desarrollotecnologicoaraque3@gmail.com";
    String contraseña="#pinky23";
    Session session;
    String filename ;
    public static final int PICK_IMAGE = 1;
    StringBuilder sb;
    String monto_transferencia;
    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_fondos_pago_movil);

        btnSubirRecibo=findViewById(R.id.button_pagomovilIngresar);
        btnSENDemail=findViewById(R.id.button_enviarrecibopagomovil);
        imageView=findViewById(R.id.imageView3);
        progressBar=findViewById(R.id.progressBar);


       //Boton elegir recibo
        btnSubirRecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //cargarImagen();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione el capture"), PICK_IMAGE);

                btnSENDemail.setVisibility(View.INVISIBLE);
            }
        });


        //Boton enviar recibo
        btnSENDemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Inicializando la instancia de FirebaseAuth
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();




                StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Properties properties=new Properties();
                properties.put("mail.smtp.host","smtp.googlemail.com");
                properties.put("mail.smtp.socketFactory.port","465");
                properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.auth","true");
                properties.put("mail.smtp.port","465");





                try {
                    session =Session.getDefaultInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(correo,contraseña);
                        }
                    });


                    if(session!=null){

                        final javax.mail.Message message=new MimeMessage(session);
                        message.setFrom(new InternetAddress(correo));
                        message.setSubject("PAGOMOVIL");
                        message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse("desarrollotecnologicoaraque3@gmail.com"));
                        //message.setContent("MENSAJE","text/html; charsert=utf-8");

                        //NUEVO
                        BodyPart messageBodyPart = new MimeBodyPart();
                        messageBodyPart.setText("UID DEL JUGADOR: "+user.getUid()+"    "+"EMAIL DEL JUGADOR: "+user.getEmail()+"     "+"TOKEN: "+SalasJuego.TOKEN);
                        Multipart multipart = new MimeMultipart();
                        multipart.addBodyPart(messageBodyPart);
                        messageBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(filename);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(filename);
                        multipart.addBodyPart(messageBodyPart);
                        message.setContent(multipart);

                        //Cambiamos color del boton y abrimos progressBar
                        btnSENDemail.setBackgroundColor(Color.GRAY);
                        progressBar.setVisibility(View.VISIBLE);


                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Transport.send(message);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(IngresarFondos_pagoMovil.this, "ENVIO EXITOSO", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(IngresarFondos_pagoMovil.this, "EN BREVE SE VERA REFLEJADO TU DINERO", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getBaseContext(),SalasJuego.class);
                                    startActivity(intent);

                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(IngresarFondos_pagoMovil.this, "FALLO EL ENVIO", Toast.LENGTH_SHORT).show();
                                    btnSENDemail.setBackgroundColor(Color.RED);
                                }

                            }
                        },3000);


                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            //set the selected image to image variable



            btnSubirRecibo.setBackgroundColor(Color.GRAY);

            imageView.setVisibility(View.VISIBLE);
            final  Uri imageuri = data.getData();
            imageView.setImageURI(imageuri);
            Log.i("URI",""+imageuri);
            String realPath = RealPathUtil.getRealPathFromURI_API19(this, imageuri);
            Log.i("RealPath>",realPath);
            filename=realPath;

           getTextFromImageView(filename);




        }

    }

    private void getTextFromImageView(String path) {

        Bitmap bitmap= BitmapFactory.decodeFile(path);
        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(this, "Reconocimiento de imagen no operacional", Toast.LENGTH_SHORT).show();
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this,"bajo Almacenamiento ", Toast.LENGTH_LONG).show();

            }
        }else{
            Frame frame=new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items=textRecognizer.detect(frame);


            sb=new StringBuilder();

            for(int i=0;i<items.size();++i){
                TextBlock myItem=items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");

            }

            showAddItemDialog(IngresarFondos_pagoMovil.this);


            Log.i("II",sb.toString());


        }
    }

    private void showAddItemDialog(Context context) {
        final EditText taskEditText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Monto de transferencia")
                .setMessage("Debe coincidir con el comprobante de pago")
                .setView(taskEditText)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monto_transferencia = String.valueOf(taskEditText.getText());


                        String string=sb.toString();

                        if(string.contains(",00")){
                            Log.i("II","contiene ,00: "+string.contains(",00"));


                            String abuscar=monto_transferencia+",00";

                            if(!monto_transferencia.equals("") && string.contains(abuscar)){
                                // Toast.makeText(getBaseContext(), "Monto introducido coincide con la imagen", Toast.LENGTH_SHORT).show();
                                btnSENDemail.setVisibility(View.VISIBLE);
                                final Animation aumento= AnimationUtils.loadAnimation(IngresarFondos_pagoMovil.this,R.anim.aumento);
                                btnSENDemail.startAnimation(aumento);


                            }else{
                                dialogFragment2(IngresarFondos_pagoMovil.this);
                            }


                        }else if(string.contains(".00")){
                            Log.i("II","contiene .00: "+string.contains(".00"));
                            String abuscar=monto_transferencia+".00";


                            if(!monto_transferencia.equals("") && string.contains(abuscar)){
                                //  Toast.makeText(getBaseContext(), "Monto introducido coincide con la imagen", Toast.LENGTH_SHORT).show();
                                btnSENDemail.setVisibility(View.VISIBLE);
                                final Animation aumento= AnimationUtils.loadAnimation(IngresarFondos_pagoMovil.this,R.anim.aumento);
                                btnSENDemail.startAnimation(aumento);


                            }else{
                                dialogFragment2(IngresarFondos_pagoMovil.this);

                            }



                        }else{
                            Toast.makeText(getBaseContext(), "Imagen no valida o borrosa", Toast.LENGTH_SHORT).show();

                        }


                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    private void dialogFragment2(Context context) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage("El monto introducido no corresponde con la imagen del comprobante de pago. Intenta:\n" +
                        "1. Introduce el monto exactamente como esta en la imagen pero sin incluir decimales. ( Ejemplo 1.000,00 seria 1.000)  \n" +
                        "2. Sube una imagen del comprobante que se vea mejor \n")
                .setPositiveButton("ok", null)
                .create();
        dialog.show();
    }




}
