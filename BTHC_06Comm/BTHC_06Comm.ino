   //make sure that the BT module is connected and paired, 
   //and set to the right com port
   
    #include <SoftwareSerial.h>

    SoftwareSerial mySerial(2, 3); // RX, TX
    int a=0;
    char c;
    char d;
    void setup()  
    {
     Serial.begin(9600);
     Serial.println("Goodnight moon!");

     mySerial.begin(9600);
     mySerial.println("Hello, world?");
    }

    void loop() 
    {
     delay(10);
     if (Serial.available()){
      c=Serial.read();
      delay(10);
      mySerial.write(c);
     }
     delay(10);
     if (mySerial.available()){
      d=mySerial.read();
      delay(10);
      Serial.write(d);
     }

    }
