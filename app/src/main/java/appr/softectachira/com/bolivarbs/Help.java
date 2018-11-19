package appr.softectachira.com.bolivarbs;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Help extends AppCompatActivity {

    Button btn;

    //Instancia de FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btn = findViewById(R.id.button_help);

        //Inicializando la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mailto = "mailto:desarrollotecnologicoaraque.s@gmail.com" +
                        "?cc=" + Uri.encode("") +
                        "&subject=" + Uri.encode("Necesito ayuda. Mi correo en el juego es:  "+user.getEmail()) +
                        "&body=" + Uri.encode("");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no email app is available
                }

            }
        });





    }
}
