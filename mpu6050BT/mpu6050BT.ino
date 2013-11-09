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

    int16_t ax, ay, az;
    int16_t gx, gy, gz;

    SoftwareSerial mySerial(3, 4); // RX, TX
    int a=0;
    char c;
    char d;
    int motorpin = 9;
    void setup()  
    {
     Wire.begin();

     accelgyro.initialize();
     mySerial.begin(9600);
     mySerial.println("Hello, world?");
     pinMode(motorpin, OUTPUT);
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


      axg = ax/16384.00;
      ayg = ay/16384.00;
      azg = az/16384.00;
      gxd = gx/ 131.00;
      if (abs(gxd) < 2){gxd = 0;}
      gyd = gy/ 131.00;
      if (abs(gyd) < 2){gyd = 0;}
      gzd = gz/ 131.00;
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
        
        //digitalWrite(motorpin, vibrate);
        //vibrate = !vibrate;
        //delay(100);
    }
