   //make sure that the BT module is connected and paired, 
   //and set to the right com port
   #include "I2Cdev.h"
  #include "MPU6050.h"

// Arduino Wire library is required if I2Cdev I2CDEV_ARDUINO_WIRE implementation
// is used in I2Cdev.h
    #include "Wire.h"
    #include <SoftwareSerial.h>
    
    MPU6050 accelgyro;
//MPU6050 accelgyro(0x69); // <-- use for AD0 high
    bool band1 = false;
    int16_t ax, ay, az;
    int16_t gx, gy, gz;

    
    int a=0;
    char c;
    char d;
 
    int motorpin;
    int l1;
    int l2;
    int l3;
    int l4;
    int button;
    int tx;
    int rx;
    int adiv;
    int gdiv;
    
    SoftwareSerial  mySerial(3, 4);

    void setup()  
    {
      
      if (band1)
    {
      motorpin = 8;
      l1 = 0;
      l2 = 1;
      l3 = 2;
      l4 = 16;
      button = A1;
    
    }
    else
    {
       motorpin = 9;
       l1 = 1;
       l2 = 2;
       l3 = 7;
       l4 = 8;
       button = 0;
     
    }
      
     // RX, TX
     Wire.begin();

     accelgyro.initialize();
     accelgyro.setFullScaleAccelRange(2);
     accelgyro.setFullScaleGyroRange(2);	

     mySerial.begin(9600);
     mySerial.println("Hello, world?");
     pinMode(motorpin, OUTPUT);
     pinMode(l1, OUTPUT);
     pinMode(l2, OUTPUT);
     pinMode(l3, OUTPUT);
     pinMode(l4, OUTPUT);
     //pinMode(button,INPUT);
    }
    
    float axg;
    float ayg;
    float azg;
    float gxd;
    float gyd;
    float gzd;
    
    bool vibrate = true;
    void loop() 
    {
      accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);


      axg = ax/4096.00;
      ayg = ay/4096.00;
      azg = az/4096.00;
      gxd = gx/ 32.80;
      if (abs(gxd) < 2){gxd = 0;}
      gyd = gy/ 32.80;
      if (abs(gyd) < 2){gyd = 0;}
      gzd = gz/ 32.80;
      if (abs(gzd) < 2){gzd = 0;}
      // these methods (and a few others) are also available
      //accelgyro.getAcceleration(&ax, &ay, &az);
      //accelgyro.getRotation(&gx, &gy, &gz);
  
        // display tab-separated accel/gyro x/y/z values
       // mySerial.print("a/g:\t");
        mySerial.print("A");
        mySerial.print(axg); mySerial.print(",");
        mySerial.print(ayg); mySerial.print(",");
        mySerial.print(azg); mySerial.print(",");
        mySerial.print("G");
        mySerial.print(gxd); mySerial.print(",");
        mySerial.print(gyd); mySerial.print(",");
        mySerial.print(gzd);
        
        //if (band1){vibrate = (analogRead(button) > 500);}
       // else{vibrate = digitalRead(button);}
        //digitalWrite(l1, !vibrate);
        //digitalWrite(l2, vibrate);
        //digitalWrite(l3, !vibrate);
        //digitalWrite(l4, vibrate);
        //digitalWrite(motorpin, vibrate);
        //vibrate = !vibrate;
         char d;
         if (mySerial.available())
         {
           //digitalWrite(l1,true);
           d=mySerial.read();
           switch(d){
           case 'v':
             Vibrate(300);
             break;
           case '1':
           lightled(1,true);
           break;
           case '2': 
           lightled(1,false);
           break;
           case '3':
           lightled(2,true);
           break;
           case '4': 
           lightled(2,false);
           break;
           case '5':
           lightled(3,true);
           break;
           case '6': 
           lightled(3,false);
           break;
           case '7':
           lightled(4,true);
           break;
           case '8': 
           lightled(4,false);
           break;
           default:
           break;
           }
           
           
         }
        
        delay(20);
        
    }
    
    void Vibrate(int time)
    {
      digitalWrite(motorpin, true);
      delay(time);
      digitalWrite(motorpin, false);
    }
    
    void lightled(int light, bool on)
    {
      switch (light)
      {
        case 1:
        digitalWrite(l1, on);
        break;
        case 2:
        digitalWrite(l2, on);
        break;
        case 3:
        digitalWrite(l3, on);
        break;
        case 4:
        digitalWrite(l4, on);
        break;
        default:
        break;
      }
    }
