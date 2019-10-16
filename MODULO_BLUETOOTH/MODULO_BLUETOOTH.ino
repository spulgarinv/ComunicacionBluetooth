#define pulsador 34
#define pulsador2 36
#define pulsador3 35
String info = "";
String dato = "";
String datoaux = "";
void setup()
{
  pinMode(pulsador, INPUT);
  pinMode(pulsador2, INPUT);
  pinMode(pulsador3, INPUT);
  Serial1.begin(9600);
  Serial.begin(9600);
}

void loop()
{
  if (Serial1.available())
  {
    dato = Serial1.readStringUntil('\n');
    if (dato == "ERROR CADENA INCORRECTA") {
      Serial.println("Error cadena incorrecta");
      dato = "0";
    } else {
      datoaux = dato;
      Serial.println("Cadena correcta");
    }
    Serial.print("Cantidad recibida: ");
    Serial.println(dato);
  }
  if (digitalRead(pulsador) == HIGH ) {
    info = "7";
    Serial1.println("Andres, Garcia, Conmutacion, " + info + ",5, Hola mundo " + '#');
    Serial.println("Andres, Garcia, Conmutacion," + info + ",5, Hola mundo " + '#');
    while (digitalRead(pulsador) == 1);
  }

  if (digitalRead(pulsador3) == HIGH ) {
    info = "concejal";
    Serial1.println("Edwin, Giraldo,mejor , " + info + ",70, de Granada " + '#');
    Serial.println("Edwin, Giraldo,mejor , " + info + ",70, de Granada " + '#');
    while (digitalRead(pulsador3) == 1);
  }

  if (digitalRead(pulsador2) == HIGH) {
    info = "7";
    Serial1.println("Andres, Conmutacion," + info + ", Hola mundo " + '#');
    Serial.println("Andres, Conmutacion," + info + ", Hola mundo " + '#');
    while (digitalRead(pulsador2) == 1);
  }
}
