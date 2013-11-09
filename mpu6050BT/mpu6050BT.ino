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
    
    bool vibrate = true;
    void loop() 
    {
      accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);

      // these methods (and a few others) are also available
      //accelgyro.getAcceleration(&ax, &ay, &az);
      //accelgyro.getRotation(&gx, &gy, &gz);

        // display tab-separated accel/gyro x/y/z values
        mySerial.print("a/g:\t");
        mySerial.print(ax); mySerial.print("\t");
        mySerial.print(ay); mySerial.print("\t");
        mySerial.print(az); mySerial.print("\t");
        mySerial.print(gx); mySerial.print("\t");
        mySerial.print(gy); mySerial.print("\t");
        mySerial.println(gz);
        
        //digitalWrite(motorpin, vibrate);
        //vibrate = !vibrate;
        //delay(100);
    }
