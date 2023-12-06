 /* 
Made by shreyansh shukla
*/



#include <SoftwareSerial.h>


// Define a flag to control where to print the data
bool printToBluetooth = false;

// Define the RX and TX pins for the Bluetooth module
#define BT_RX A0
#define BT_TX A1

// Define the RX and TX pins for the MCU
#define MCU_RX 2
#define MCU_TX 3



// Create an instance of the SoftwareSerial library for the Bluetooth module
SoftwareSerial bluetooth(BT_RX, BT_TX);

// Create an instance of the SoftwareSerial library for the MCU
SoftwareSerial mcu(MCU_RX, MCU_TX);

// Define a counter for the number of lines read and a timeout for data transfer
int lineCount = 0;
unsigned long previousMillis = 0;
const long interval = 2000; // Timeout interval in milliseconds

//1 Mux control pins
int m1s0 = 6;
int m1s1 = 7;
int m1s2 = 8;
int m1s3 = 9;

int m2s0 = 10;
int m2s1 =11;
int m2s2 = 12;
int m2s3 = 13;

//Mux in "SIG" pin this is going to ad5940 eval board
//int m1SIG_pin = 0;
//int m2SIG_pin = 0;

//make enable pin HIGH

int m1en=22;
int m2en=23;

void setup(){
  pinMode(m1s0, OUTPUT); 
  pinMode(m1s1, OUTPUT); 
  pinMode(m1s2, OUTPUT); 
  pinMode(m1s3, OUTPUT); 

  digitalWrite(m1s0, LOW);
  digitalWrite(m1s1, LOW);
  digitalWrite(m1s2, LOW);
  digitalWrite(m1s3, LOW);

  pinMode(m2s0, OUTPUT); 
  pinMode(m2s1, OUTPUT); 
  pinMode(m2s2, OUTPUT); 
  pinMode(m2s3, OUTPUT); 

  digitalWrite(m2s0, LOW);
  digitalWrite(m2s1, LOW);
  digitalWrite(m2s2, LOW);
  digitalWrite(m2s3, LOW);

  // Set the motor enable pins as outputs
  pinMode(m1en, OUTPUT);
  pinMode(m2en, OUTPUT);

  // Make the motor enable pins HIGH
  digitalWrite(m1en, HIGH);
  digitalWrite(m2en, HIGH);

    // Begin serial communication with the Arduino Serial Monitor at 9600 baud
  Serial.begin(9600);

  // Begin serial communication with the MCU at 230400 baud
  mcu.begin(230400);

  // Begin serial communication with the Bluetooth module at 9600 baud
  bluetooth.begin(9600);
}





float readMux(int m1channel, int m2channel){
  int m1controlPin[] = {m1s0, m1s1, m1s2, m1s3};
  int m2controlPin[] = {m2s0, m2s1, m2s2, m2s3};

  int muxChannel[16][4]={
    {0,0,0,0}, //channel 0
    {1,0,0,0}, //channel 1
    {0,1,0,0}, //channel 2
    {1,1,0,0}, //channel 3
    {0,0,1,0}, //channel 4
    {1,0,1,0}, //channel 5
    {0,1,1,0}, //channel 6
    {1,1,1,0}, //channel 7
    {0,0,0,1}, //channel 8
    {1,0,0,1}, //channel 9
    {0,1,0,1}, //channel 10
    {1,1,0,1}, //channel 11
    {0,0,1,1}, //channel 12
    {1,0,1,1}, //channel 13
    {0,1,1,1}, //channel 14
    {1,1,1,1}  //channel 15
  };

  //loop through the 4 sig
  for(int i = 0; i < 4; i ++){
    digitalWrite(m1controlPin[i], muxChannel[m1channel][i]);
    digitalWrite(m2controlPin[i], muxChannel[m2channel][i]);
  }

  //read the value at the SIG pin
  //int val = analogRead(m1SIG_pin);

  //return the value
 // float voltage = (val * 5.0) / 1024.0;
  //return voltage;
}

void checkForDataTransfer() {
  Serial.write("entered reading mode");
  // Check if data is available to read from the MCU
  if (mcu.available()) {
    // Read the incoming byte
    Serial.write("trying to read");
    char incomingByte = mcu.read();

    // Print the incoming byte to the terminal or transmit it via Bluetooth, depending on the flag
    if (printToBluetooth) {
      bluetooth.print(incomingByte);
    } else {
      Serial.print(incomingByte);
    }
  }
  Serial.write("done reading mode");
}

void readData(float* f, int* n1, int* n2, int* n3) {
  // Read a float and three integers from either the Serial Monitor or a Bluetooth device, depending on printToBluetooth
  if (printToBluetooth) {

    bluetooth.println("enter float(frequency),int(number of points),int,int(2 integer to select mux setting (0-5))");
    while (bluetooth.available() < 16) { delay(100); } // Wait for 16 bytes: 4 bytes for the float and 3x4 bytes for the integers
    *f = bluetooth.parseFloat();
    *n1 = bluetooth.parseInt();
    *n2 = bluetooth.parseInt();
    *n3 = bluetooth.parseInt();
  } else {
    Serial.println("enter float(frequency),int(number of points),int,int(2 integer to select mux setting (0-5))");
    while (Serial.available() < 16) { delay(100); }
    *f = Serial.parseFloat();
    *n1 = Serial.parseInt();
    *n2 = Serial.parseInt();
    *n3 = Serial.parseInt();
  }
}

void printData(float f, int n1, int n2, int n3) {
  // Print the read values to either the Serial Monitor or a Bluetooth device, depending on printToBluetooth
  if (printToBluetooth) {
    bluetooth.print("Read values: ");
    bluetooth.print(f);
    bluetooth.print(", ");
    bluetooth.print(n1);
    bluetooth.print(", ");
    bluetooth.print(n2);
    bluetooth.print(", ");
    bluetooth.println(n3);
  } else {
    Serial.print("Read values: ");
    Serial.print(f);
    Serial.print(", ");
    Serial.print(n1);
    Serial.print(", ");
    Serial.print(n2);
    Serial.print(", ");
    Serial.println(n3);
  }
}

  void checkForDataTransfer2() {
  // Check if data is available to read from the MCU
  int i=1;
  Serial.println("data reading mode");
  do{
    Serial.println("trying to read data");
  if (mcu.available()) {
    // Read the incoming byte
    char incomingByte = mcu.read();

    // Print the incoming byte to the terminal or transmit it via Bluetooth, depending on the flag
    if (printToBluetooth) {
      bluetooth.print(incomingByte);
    } else {
      Serial.print(incomingByte);
    }

    // If a newline character is read, increment the line count
    if (incomingByte == '\n') {
      lineCount++;
    }

    // If 55 lines have been read, return to the main loop
    if (lineCount >= 55) {
      i=-1;
      return;
    }

    // Reset the timeout timer
    previousMillis = millis();
  } else {
    // If no data has been received for a certain amount of time, return to the main loop
    unsigned long currentMillis = millis();
    if (currentMillis - previousMillis >= interval) {
      i=-1;
      return;
    }
  }
  }while(i>0);

  
  }

void loop(){

//add code to use uart with adicup33029(uart mode) with ad5940boiz eval board attached
int i=0,j=0;


float f;
  int n1, n2, n3;


  
 readData(&f, &n1, &n2, &n3);
 Serial.println(" printing read data");
 printData(f, n1, n2, n3);

  
//sending values to adicup3029
i=n2;j=n3;

//write 0-5 on channel denoted by i,j here for selecting electrode
readMux(i,j);


checkForDataTransfer2();


  /*Loop through and read all 16 values
  for(int i = 0; i < 16; i ++){
    Serial.print("Value at channel ");
    Serial.print(i);
    Serial.print("is : ");
    Serial.println();
    delay(1000);
  }*/

}