package appr.softectachira.com.bolivarbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import appr.softectachira.com.bolivarbs.slide1.SliderActivity;


public class MainActivity extends AppCompatActivity {
   //Instancias de Vistas
    Button btn_login;
    EditText editText_email,editText_contraseña;
    TextView textView_creaCuenta,textView_olvidecontra;
    private ProgressBar progressBar;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Enlazando Vistas
        btn_login=findViewById(R.id.buttonMainActivity);
        editText_email=findViewById(R.id.editText_email);
        editText_contraseña=findViewById(R.id.editText_contraseña);
        textView_creaCuenta=findViewById(R.id.textViewLogin_creaCuenta) ;
        textView_olvidecontra=findViewById(R.id.textView34_olvidecontra);
        progressBar=findViewById(R.id.progressBar_login);


        //Inicializando la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


        //Si el usuario tiene la sesión activa se abre directamente "SalasJuego"  al menos de que No tenga internet
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                //Si no hay conexion la aplicacion no pasa de login
                if(!isConnected){
                    Toast.makeText(getApplicationContext(), "Sin conexion a Internet", Toast.LENGTH_SHORT).show();
                //Pero si existe conexion a internet pasamos a salasJuego
                }else{

                    Intent intent=new Intent(getApplicationContext(),SalasJuego.class);
                    startActivity(intent);
                }



        }else{
            //Recuperamos los valores desde SliderActivity
            SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(this);
            SliderActivity.executed_firstime_slideractivity= datos.getBoolean("execute_first",false);

            if(!SliderActivity.executed_firstime_slideractivity) {

                Intent intent = new Intent(getBaseContext(), SliderActivity.class);
                startActivity(intent);
            }


        }


        //Boton Login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               userLogin();




            }
        });

        //Accion del textView
        textView_creaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Registro.class);
                startActivity(intent);
            }
        });


        //Accion del textView olvide contraseña
        textView_olvidecontra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog(MainActivity.this);
            }
        });

    }






    //Metodo que contiene la logica para poder "logearnos"
    private void userLogin() {
        String correo=editText_email.getText().toString().trim();
        String contraseña=editText_contraseña.getText().toString().trim();

        if(TextUtils.isEmpty(correo)){
            //correo is empty
            Toast.makeText(this, "Por favor introduce un correo", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(!isValidEmail(correo)){
            //Correo no cumple con la condiciones de un correo
            Toast.makeText(this, "Correo no Valido", Toast.LENGTH_SHORT).show();
            return;

        }

        if(TextUtils.isEmpty(contraseña)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor introduce tu contraseña", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        //Mostramos una progress bar
        progressBar.setVisibility(View.VISIBLE);


        //Ejecutamos
        mAuth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent=new Intent(getApplicationContext(),SalasJuego.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            //Dejar limpios campos de correo y contraseña
                            editText_email.setText("");
                            editText_contraseña.setText("");
                            //Ocultar progressBar
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        // ...
                    }
                });

    }

    //Metodo que evalua si un correo es correcto
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    //Evitar salir de la actividad dandole atras
    @Override
    public boolean onKeyDown (int keyCode,KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }




    private void showAddItemDialog(Context context) {
        final EditText taskEditText = new EditText(context);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Ingresa tu correo")
                .setMessage("Te enviaremos los pasos para cambiar tu contraseña")
                .setView(taskEditText)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      String correo= taskEditText.getText().toString();

                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        auth.sendPasswordResetEmail(correo)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getBaseContext(), "Correo Enviado", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(getBaseContext(), "Correo no Existe", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}
