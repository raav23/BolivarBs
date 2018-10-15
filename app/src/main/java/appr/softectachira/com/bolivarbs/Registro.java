package appr.softectachira.com.bolivarbs;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {

    //Instancias de vistas
    private Button btnRegistrar;
    private EditText editText_email,editText_password1,editText_password2,editText_nombre;
    private ProgressBar progressBar;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;

    //Referencia a la base de datos FireBase
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Enlazando vistas
        btnRegistrar=findViewById(R.id.button_registrar);
        editText_email=findViewById(R.id.editText_email_register);
        editText_password1=findViewById(R.id.editText_contraseña1_register);
        editText_password2=findViewById(R.id.editText_contraseña2_register);
        editText_nombre=findViewById(R.id.editText_nombreUsuario_register);
        progressBar=findViewById(R.id.progressBar_register);

        //Inicializando la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Obteniendo la referencia de la base de datos (bolivarbs-64dc1)
        databaseReference=FirebaseDatabase.getInstance().getReference();


        //BOTON REGISTRAR
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });



    }


    public  void registerUser(){
        String correo=editText_email.getText().toString().trim();
        String contraseña1= editText_password1.getText().toString().trim();
        String contraseña2=editText_password2.getText().toString().trim();
        final String nombreUsuario=editText_nombre.getText().toString().trim();

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

        if(TextUtils.isEmpty(contraseña1)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor introduce tu contraseña", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(contraseña1.length()<7){
            //Contraseña muy corta  tiene menos de 7 caracteres
            Toast.makeText(this, "Contraseña debe tener 7 caracteres minimo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(contraseña2)){
            //Contraseña is empty
            Toast.makeText(this, "Por favor confirma tu contraseña", Toast.LENGTH_SHORT).show();
            //Detener la ejecucion de la funcion (registerUser())
            return;
        }

        if(!contraseña1.equals(contraseña2)){
            Toast.makeText(this, "Contraseñas no iguales", Toast.LENGTH_SHORT).show();
            return;
        }

        if(nombreUsuario.length()<5  || nombreUsuario.length()>10){
            Toast.makeText(this, "Nombre debe contener 5 a 10 caracteres", Toast.LENGTH_SHORT).show();
            return;

        }

        //Mostramos una progress bar
        progressBar.setVisibility(View.VISIBLE);


        //Ejecutamos metodo para crear usuario nuevo
        mAuth.createUserWithEmailAndPassword(correo, contraseña1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Registro.this, "Usuario registrado!", Toast.LENGTH_SHORT).show();


                            //Guardamos el nombre elegido de usuario en la base de datos y el dinero con el que empieza
                            FirebaseUser user = mAuth.getCurrentUser();
                            ObjetoInsercionToFirebase objetoInsercionToFirebase=new ObjetoInsercionToFirebase(nombreUsuario,5,0,0);
                            databaseReference.child(user.getUid()).setValue(objetoInsercionToFirebase);

                            //pasamos a SalasJuego.class
                            Intent intent=new Intent(getApplicationContext(),SalasJuego.class);
                            startActivity(intent);


                            //Cerramos la progressBar
                            progressBar.setVisibility(View.INVISIBLE);


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Registro.this, "Fallo registro", Toast.LENGTH_SHORT).show();

                            //Cerramos la progressBar
                            progressBar.setVisibility(View.INVISIBLE);


                        }

                    }
                });






    }

    //Metodo que evalua si un correo es correcto
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



}
