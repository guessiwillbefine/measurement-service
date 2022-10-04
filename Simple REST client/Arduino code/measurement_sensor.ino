#include <WiFi.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>
#include <DHT.h>

class Sensor {
private:
  int id;
public:
  DHT *dht;

public:  //constructor
  Sensor(int id, int port) {
    id = id;
    dht = new DHT(port, DHT11); //DHT is an API we use to get measurements
  }
public:
  int getId() {
    return id;
  }
public:
  float measurements() {
    float value = this->dht->readTemperature();
    return value;
  }
};

Sensor sensor(1, 12); //Sensor object

void setup() {

  sensor.dht->begin();  //start measureSensor (-> means that we use pointer object)

  Serial.begin(115200);
  WiFi.begin("TP-LINK_5012", "0660869152");  //wifi connection

  int i = 0;
  while (WiFi.status() != WL_CONNECTED) {  // Wait for the Wi-Fi to connect
    delay(500);
    Serial.print(++i);
    Serial.print(' ');
  }
}

void loop() {
  HTTPClient http;

  http.begin("http://localhost:8080/api/save");    //link to api via http
  http.addHeader("Content-Type", "application/json");  //we say that we will send json file with our request

  StaticJsonDocument<200> doc;  //creating json for POST method
  doc["id"] = 1;                // key and value of our json
  doc["value"] = sensor.measurements();
  String requestBody;
  serializeJson(doc, requestBody);                // serialization of json
  int httpResponseCode = http.POST(requestBody);  //POST-method

  if (httpResponseCode > 0) {            //if code > 0 (any, 200, 400, 404 etc.)
    String response = http.getString();  //so we have some answer and we can print our response
    Serial.println(httpResponseCode);
    Serial.println(response);
  }
  delay(2000);
}