package com.example.controlbt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //1)
    Button IdIncrementar, IdRestar ,IdAceptar;
    TextView IdBufferIn, IdCantidad, IdNombre, IdApellido, IdMateria, Id, Mensaje, IdHome, IdStop,IdExit;
    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;

    private static int cantidad = '\0';
    String texto= "cesar";
    //-------------------------------------------

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //2)
        //Enlaza los controles con sus respectivas vistas
        IdIncrementar = (Button) findViewById(R.id.idIncrementar);
        IdRestar = (Button) findViewById(R.id.idRestar);
        IdAceptar = (Button) findViewById(R.id.idAceptar);
        IdHome = (Button) findViewById(R.id.home);
        IdStop = (Button) findViewById(R.id.stop);
        IdExit = (Button) findViewById(R.id.exit);
        IdCantidad = (TextView) findViewById(R.id.idCantidad);
        IdNombre = (TextView) findViewById(R.id.nombre);
        IdApellido = (TextView) findViewById(R.id.apellido);
        IdMateria = (TextView) findViewById(R.id.materia);
        Id = (TextView) findViewById(R.id.numero);
        Mensaje = (TextView) findViewById(R.id.texto);
        //IdBufferIn = (TextView) findViewById(R.id.IdBufferIn);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                        String[] cadena = dataInPrint.split(",");
                        if(cadena.length == 6) {
                            IdNombre.setText(cadena[0]);//<-<- PARTE A MODIFICAR >->->
                            IdApellido.setText(cadena[1]);
                            IdMateria.setText(cadena[2]);
                            Id.setText(cadena[3]);
                            String num = cadena[4];
                            IdCantidad.setText(num);
                            cantidad = Integer.parseInt(num);
                            Mensaje.setText(cadena[5]);


                        }else{
                           String e = "ERROR CADENA INCORRECTA";
                            MyConexionBT.write(e);
                            IdNombre.setText("");//<-<- PARTE A MODIFICAR >->->
                            IdApellido.setText("");
                            IdMateria.setText("");
                            Id.setText("");
                            IdCantidad.setText("");
                            Mensaje.setText("");
                        }
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte
        // el evento de Click

        IdIncrementar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(cantidad >= 0&& cantidad != '\0'){
                    cantidad++;
                    IdCantidad.setText(Integer.toString(cantidad));
                }

                //MyConexionBT.write("1");
            }
        });

        IdRestar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cantidad > 0 && cantidad != '\0'){
                    cantidad--;
                    IdCantidad.setText(Integer.toString(cantidad));
                }
                //MyConexionBT.write("0");
            }
        });

        IdStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("*");
            }
        });

        IdHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("$");
            }
        });

        IdExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        IdAceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(cantidad != '\0'){
                    MyConexionBT.write(Integer.toString(cantidad));
                    IdNombre.setText("");//<-<- PARTE A MODIFICAR >->->
                    IdApellido.setText("");
                    IdMateria.setText("");
                    Id.setText("");
                    Mensaje.setText("");
                    IdCantidad.setText("");
                    cantidad = '\0';

                }

                /*if (btSocket!=null){
                    try {

                        btSocket.close();
                        }
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                    ;}

                }*/
               // finish();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}