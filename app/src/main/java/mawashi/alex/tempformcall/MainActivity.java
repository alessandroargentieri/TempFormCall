package mawashi.alex.tempformcall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText ContactNameEdit;
    String ContactName;
    Boolean fallen = false;
    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContactNameEdit = (EditText) findViewById(R.id.editText);
        sharedpreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        ContactNameEdit.setText(sharedpreferences.getString("SkypeName",""));

    }

    public void SavePreferences(View v){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("SkypeName", ContactNameEdit.getText().toString());
        //editor.putString(Phone, ph);
        //editor.putString(Email, e);
        editor.commit();
        Toast.makeText(getApplicationContext(),"Nominativo contatto salvato",Toast.LENGTH_LONG).show();
    }


    public void Caduta(View v){
        fallen = true; //L'UTENTE RISULTA CADUTO
        //ACQUISISCE DALL'EDIT IL NOME SKYPE DEL CONTATTO DA CHIAMARE
        ContactName = ContactNameEdit.getText().toString();
        //AVVIA LA PROCEDURA DI CALCOLO DEI 10 SECONDI
        new AsyncCaduta().execute();
    }

    private class AsyncCaduta extends AsyncTask<Void,Void,Void>{
        long istante;
        AlertDialog.Builder miaAlert;
        AlertDialog alert;

        @Override
        protected void onPreExecute(){
            //COMPARE IL DIALOG E INIZIA IL CONTO ALLA ROVESCIA
            miaAlert = new AlertDialog.Builder(MainActivity.this);
            miaAlert.setTitle("Utente Caduto!");
            miaAlert.setMessage("Tra pochi secondi si avvierà la chiamata Skype.");
            miaAlert.setIcon(R.mipmap.ic_launcher);
            miaAlert.setCancelable(false);
            miaAlert.setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(getApplicationContext(),"Chiamata annullata", Toast.LENGTH_LONG).show();
                    fallen = false;
                    dialog.dismiss();
                }
            });

            miaAlert.setNegativeButton("Chiama", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    istante = 0; //così risultano trascorsi all'istante i 10 secondi
                    dialog.dismiss();
                }
            });

            alert = miaAlert.create();
            alert.show();

            istante = System.currentTimeMillis();
        }
        @Override
        protected Void doInBackground(Void...params){
            //CALCOLA 10 SECONDI
            while (System.currentTimeMillis()-istante<10000 && fallen==true){}
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            //CHIUDE IL DIALOG E CHIAMA SE L'UTENTE NON HA PREMUTO RESTORE
            //DISMISS
            if (alert.isShowing())
                alert.dismiss();
            if(fallen==true){
                //SKYPE CALL
                //String user_name = "francesco.pagliara.cetma";
                Intent sky = new Intent("android.intent.action.VIEW");
                sky.setData(Uri.parse("skype:" + ContactName + "?call&video=true"));
                startActivity(sky);
            }
            super.onPostExecute(result);
        }
    }
}
