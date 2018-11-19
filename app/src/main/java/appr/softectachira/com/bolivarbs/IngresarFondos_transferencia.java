package appr.softectachira.com.bolivarbs;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

public class IngresarFondos_transferencia extends AppCompatActivity {

    private TextView textView_banco,textView_cuenta,textView_beneficiario,textView_numeroci,textView_tipoCuenta,textView_correo,
                textView_copiar1,textView_copiar2,textView_copiar3;
    private ImageView imageView_banesco,imageView_provincial,imageView_bicentenario,imageView_mercantil,
            imageView_venezuela,imageView_reciboSubido;
    private Button btn_subirComprobante,btn_enviarComprobante;
    private ProgressBar progressBar;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    String correo="desarrollotecnologicoaraque3@gmail.com";
    String contraseña="#pinky23";
    String dineroDiferido;//<<<<< Entra a esta actividad en string
    double dineroDiferidoDouble; //>>>>> Es convertido en Double
     String monto_transferencia;
    Session session;
    String filename ;
    private static final int PICK_IMAGE = 1;
    private final int  MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=11211;
    StringBuilder sb;



    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //Instancia de DatabaseReference
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_fondos_transferencia);

        textView_banco=findViewById(R.id.textView_nombreBanco);
        textView_cuenta=findViewById(R.id.textView_numeroCuenta);
        textView_beneficiario=findViewById(R.id.textView_beneficiario);
        textView_numeroci=findViewById(R.id.textView_numeroCI);
        textView_tipoCuenta=findViewById(R.id.textView_tipoCuenta);
        textView_correo=findViewById(R.id.textView_correo);
        textView_copiar1=findViewById(R.id.textView_copiar1);
        textView_copiar2=findViewById(R.id.textView_copiar2);
        textView_copiar3=findViewById(R.id.textView_copiar3);
        imageView_banesco=findViewById(R.id.imageView_banesco);
        imageView_provincial=findViewById(R.id.imageView_provincial);
        imageView_bicentenario=findViewById(R.id.imageView_bicentenario);
        imageView_mercantil=findViewById(R.id.imageView_mercantil);
        imageView_venezuela=findViewById(R.id.imageView_venezuela);
        imageView_reciboSubido=findViewById(R.id.imageView6);
        btn_subirComprobante=findViewById(R.id.button_subirReciboTransferencia);
        btn_enviarComprobante=findViewById(R.id.button_enviarreciboTransferencia);
        progressBar=findViewById(R.id.progressBar_ingresoTransf);




        try {
            //Recuperando nombre y dinero de usuario
            Bundle recupera=getIntent().getExtras();
            dineroDiferido=recupera.getString("dineroDiferido");
            dineroDiferidoDouble = Double.parseDouble(dineroDiferido);
        }catch (NumberFormatException ex){
            Log.i("II/","ERROR FORMATO EXCEPCION: "+ex);
        }





        //ACCIONES IMAGENES BANCOS
        imageView_banesco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_banco.setText("Banesco");
                textView_cuenta.setText("01340946330001319759");
                textView_beneficiario.setText("RAYMOND ARAQUE");
                textView_numeroci.setText("23545920");
                textView_tipoCuenta.setText("Corriente");
                textView_correo.setText("desarrollotecnologicoaraque3@gmail.com");

                imageView_banesco.setBackgroundColor(Color.GRAY);
                imageView_provincial.setBackgroundColor(Color.WHITE);
                imageView_bicentenario.setBackgroundColor(Color.WHITE);
                imageView_mercantil.setBackgroundColor(Color.WHITE);
                imageView_venezuela.setBackgroundColor(Color.WHITE);

            }
        });

        imageView_provincial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_banco.setText("Provincial");
                textView_cuenta.setText("01080362420100038438");
                textView_beneficiario.setText("RICARDO ARAQUE");
                textView_numeroci.setText("9209203");
                textView_tipoCuenta.setText("Corriente");
                textView_correo.setText("desarrollotecnologicoaraque3@gmail.com");

                imageView_banesco.setBackgroundColor(Color.WHITE);
                imageView_provincial.setBackgroundColor(Color.GRAY);
                imageView_bicentenario.setBackgroundColor(Color.WHITE);
                imageView_mercantil.setBackgroundColor(Color.WHITE);
                imageView_venezuela.setBackgroundColor(Color.WHITE);
            }
        });

        imageView_bicentenario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_banco.setText("Bicentenario");
                textView_cuenta.setText("01750001580071027307");
                textView_beneficiario.setText("RAQUEL VIVAS");
                textView_numeroci.setText("11498287");
                textView_tipoCuenta.setText("Corriente");
                textView_correo.setText("desarrollotecnologicoaraque3@gmail.com");

                imageView_banesco.setBackgroundColor(Color.WHITE);
                imageView_provincial.setBackgroundColor(Color.WHITE);
                imageView_bicentenario.setBackgroundColor(Color.GRAY);
                imageView_mercantil.setBackgroundColor(Color.WHITE);
                imageView_venezuela.setBackgroundColor(Color.WHITE);
            }
        });

        imageView_mercantil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_banco.setText("Mercantil");
                textView_cuenta.setText("01050093170093398700");
                textView_beneficiario.setText("RICARDO ARAQUE");
                textView_numeroci.setText("9209203");
                textView_tipoCuenta.setText("Ahorro");
                textView_correo.setText("desarrollotecnologicoaraque3@gmail.com");

                imageView_banesco.setBackgroundColor(Color.WHITE);
                imageView_provincial.setBackgroundColor(Color.WHITE);
                imageView_bicentenario.setBackgroundColor(Color.WHITE);
                imageView_mercantil.setBackgroundColor(Color.GRAY);
                imageView_venezuela.setBackgroundColor(Color.WHITE);
            }
        });

        imageView_venezuela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_banco.setText("Venezuela");
                textView_cuenta.setText("01020129200000356291");
                textView_beneficiario.setText("RAYMOND ARAQUE");
                textView_numeroci.setText("23545920");
                textView_tipoCuenta.setText("Corriente");
                textView_correo.setText("desarrollotecnologicoaraque3@gmail.com");

                imageView_banesco.setBackgroundColor(Color.WHITE);
                imageView_provincial.setBackgroundColor(Color.WHITE);
                imageView_bicentenario.setBackgroundColor(Color.WHITE);
                imageView_mercantil.setBackgroundColor(Color.WHITE);
                imageView_venezuela.setBackgroundColor(Color.GRAY);
            }
        });







        //ACIONES TEXTVIEW COPIAR

        textView_copiar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text;
                text = textView_cuenta.getText().toString();
                copiarPortapapeles(text);

            }
        });

        textView_copiar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text;
                text = textView_beneficiario.getText().toString();
                copiarPortapapeles(text);

            }
        });

        textView_copiar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text;
                text = textView_numeroci.getText().toString();
                copiarPortapapeles(text);

            }
        });


        //ACCIONES BOTONES SUBIR Y ENVIAR RECIBO
        btn_subirComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isReadStoragePermissionGranted();


            }//FINAL ONCLIK
        });


        btn_enviarComprobante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    session =Session.getDefaultInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(correo,contraseña);
                        }
                    });


                    if(session!=null){

                        final javax.mail.Message message=new MimeMessage(session);
                        message.setFrom(new InternetAddress(correo));
                        message.setSubject("TRANSFERENCIA");
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
                        btn_enviarComprobante.setBackgroundColor(Color.GRAY);
                        progressBar.setVisibility(View.VISIBLE);


                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Transport.send(message);
                                    progressBar.setVisibility(View.INVISIBLE);

                                    //Sumamos dinero a firebase (dineroDiferido)
                                    dineroDiferidoDouble =dineroDiferidoDouble+ Double.parseDouble(monto_transferencia);
                                    databaseReference.child(user.getUid()).child("money_pendiente").setValue(dineroDiferidoDouble); //Referencia>Hijo> money:dinero

                                    Toast.makeText(IngresarFondos_transferencia.this, "ENVIO EXITOSO", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(IngresarFondos_transferencia.this, "EN BREVE SE VERA REFLEJADO TU DINERO", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(getBaseContext(),SalasJuego.class);
                                    startActivity(intent);

                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(IngresarFondos_transferencia.this, "FALLO EL ENVIO", Toast.LENGTH_SHORT).show();
                                    btn_enviarComprobante.setBackgroundColor(Color.RED);
                                }

                            }
                        },3000);


                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }//FINAL ONCREATE<<<<<<

//METODOS





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            btn_subirComprobante.setBackgroundColor(Color.GRAY);

            imageView_reciboSubido.setVisibility(View.VISIBLE);
            final  Uri imageuri = data.getData();
            imageView_reciboSubido.setImageURI(imageuri);
            Log.i("URI",""+imageuri);
            String realPath = RealPathUtil.getRealPathFromURI_API19(this, imageuri);
            Log.i("RealPath>",realPath);
            filename=realPath;

            getTextFromImageView(filename);



        }



    }


    public void getTextFromImageView(String path){
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

            showAddItemDialog(IngresarFondos_transferencia.this);


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
                                btn_enviarComprobante.setVisibility(View.VISIBLE);
                                final Animation aumento= AnimationUtils.loadAnimation(IngresarFondos_transferencia.this,R.anim.aumento);
                                btn_enviarComprobante.startAnimation(aumento);


                            }else{
                                dialogFragment2(IngresarFondos_transferencia.this);
                            }


                        }else if(string.contains(".00")){
                            Log.i("II","contiene .00: "+string.contains(".00"));
                            String abuscar=monto_transferencia+".00";


                            if(!monto_transferencia.equals("") && string.contains(abuscar)){
                              //  Toast.makeText(getBaseContext(), "Monto introducido coincide con la imagen", Toast.LENGTH_SHORT).show();
                                btn_enviarComprobante.setVisibility(View.VISIBLE);
                                final Animation aumento= AnimationUtils.loadAnimation(IngresarFondos_transferencia.this,R.anim.aumento);
                                btn_enviarComprobante.startAnimation(aumento);


                            }else{
                                dialogFragment2(IngresarFondos_transferencia.this);

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


    public void copiarPortapapeles(String texto){

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        myClip = ClipData.newPlainText("text", texto);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(getApplicationContext(), "Copiado",Toast.LENGTH_SHORT).show();

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


    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione el capture"), PICK_IMAGE);
                btn_enviarComprobante.setVisibility(View.INVISIBLE);

                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("KO","Permission is granted1");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione el capture"), PICK_IMAGE);
            btn_enviarComprobante.setVisibility(View.INVISIBLE);
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 2:
                Log.d("II/", "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("II/","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    //downloadPdfFile();
                }else{

                }
                break;

        }
    }



}
