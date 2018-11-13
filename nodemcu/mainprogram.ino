//Checking to ensure you can connect ESP-12E to a router
     
    #include <ESP8266WiFi.h>
    #include <ESP8266HTTPClient.h>
     
    const char* ssid     = "SSID";
    const char* password = "password";     

    int wifiStatus;
     
    void setup() {
      
      Serial.begin(115200);\
      delay(200);
      
     
     
      // We start by connecting to a WiFi network
     
      Serial.println();
      Serial.println();
      Serial.print("Your are connecting to;");
      Serial.println(ssid);
      
      WiFi.begin(ssid, password);
      
      while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.print(".");
      }
     
 }   
     
void loop() {
      wifiStatus = WiFi.status();

      if(wifiStatus == WL_CONNECTED){
         Serial.println("");
         Serial.println("Your ESP is connected!");  
         Serial.println("Your IP address is: ");
         Serial.println(WiFi.localIP());
         HTTPClient http;  //Declare an object of class HTTPClient
 
         http.begin("http://localhost/dashboard/");  //Specify request destination
         int httpCode = http.GET();                                                                  //Send the request
         Serial.print("HTTP Response : ");
         Serial.println(httpCode);
         if (httpCode > 0) { //Check the returning code
       
           String payload = http.getString();   //Get the request response payload
           Serial.println(payload);             //Print the response payload
       
         }
         
         http.end();   //Close connection  
      }
      else{
        Serial.println("");
        Serial.println("WiFi not connected");
      }
      WiFi.disconnect();
      while(1);

}
