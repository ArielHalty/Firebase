package com.ahalty.firebase01;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = null;
    private Button btn_registrar;
    private EditText nombre, correo, contrasena;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    String display_Name = null;
    String profile_image_url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre = (EditText) findViewById(R.id.nombre);
        correo = (EditText) findViewById(R.id.correo);
        contrasena = (EditText) findViewById(R.id.contrasena);
        btn_registrar = (Button) findViewById(R.id.btn_registro);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Log.d(TAG, "onCreate: " + user.getDisplayName());
            if (user.getDisplayName() != null){
                display_Name.setText(user.getDisplayName());
                display_Name.setSelection(user.getDisplayName().length());
            }

        }

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreUsuario = nombre.getText().toString().trim();
                String correoUsuario = correo.getText().toString().trim();
                String contrasenaUsuario = contrasena.getText().toString().trim();

                if (nombreUsuario.isEmpty() && correoUsuario.isEmpty() && contrasenaUsuario.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Complete los datos", Toast.LENGTH_SHORT).show();

                }else{
                     registrarUsuario(nombreUsuario, correoUsuario, contrasenaUsuario);
                }
            }
        });
    }

    private void registrarUsuario(String nombreUsuario, String correoUsuario, String contrasenaUsuario) {
        mAuth.createUserWithEmailAndPassword(correoUsuario, contrasenaUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = mAuth.getCurrentUser().getUid();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", nombreUsuario);
                map.put("correo", correoUsuario);
                map.put("contrasena", contrasenaUsuario);

                mFirestore.collection("user").document(id).set(map).addOnSuccesListener(new OnSuccessListener<>() {
                    @Override
                    public void onSuccess(Object o) {
                        finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        Toast.makeText(MainActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateUser (View view){

        view.setEnabled(false);
        display_Name = display_Name.getText().toString();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(display_Name).build();

        firebaseUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                view.setEnabled(true);
                Toast.makeText(MainActivity.this, "Succesfully updated profile", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.setEnabled(true);
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }
}