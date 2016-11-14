#include <stdbool.h>
#include <string.h>
#include "coap.h"

//dust
unsigned long duration;
unsigned long starttime;
unsigned long sampletime_ms = 30000;   //sampe 30s ;
unsigned long lowpulseoccupancy = 0;
float ratio = 0;
float concentration = 0;
//dust

const uint16_t rsplen = 2500;
static char rsp[2500] = "";
void build_rsp(void);

#ifdef ARDUINO
#include "Arduino.h"

static int pin_dust = 8; //pin for dust sensor
static int pin_dht = 9; //pin for dht sensor

void endpoint_setup(void)
{                
    pinMode(pin_dust, INPUT);
    //init DHT
    pinMode(pin_dht,OUTPUT);
    digitalWrite(pin_dht,HIGH);
    
    starttime = millis();//get the current time;
    build_rsp();
}

#else
#include <stdio.h>
void endpoint_setup(void)
{
    build_rsp();
}
#endif

//function definitions
void coap_return_result(int value, coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo){
    char charArray[10];
    itoa(value, charArray, 10);

    size_t len = strlen(charArray)/sizeof(char);
  
    int i = len-1;
  
    while (i>0){
      coap_make_response(scratch, outpkt, (const uint8_t *)&charArray[i], len, id_hi, id_lo, &inpkt->tok, COAP_RSPCODE_CONTENT, COAP_CONTENTTYPE_TEXT_PLAIN);
      i--;
    }
  
    return coap_make_response(scratch, outpkt, (const uint8_t *)&charArray[0], len, id_hi, id_lo, &inpkt->tok, COAP_RSPCODE_CONTENT, COAP_CONTENTTYPE_TEXT_PLAIN);
}

//DHT sensor functions
byte bGlobalErr;
byte dht_dat[5];

byte read_dht_dat(){
  byte i = 0;
  byte result=0;
  for(i=0; i< 8; i++)
  {
  while(digitalRead(pin_dht)==LOW);
  delayMicroseconds(30);
  if (digitalRead(pin_dht)==HIGH)
  result |= (1<<(7-i));
  while (digitalRead(pin_dht)==HIGH);
  }
  return result;
}

void ReadDHT(){
  bGlobalErr=0;
  byte dht_in;
  byte i;
  digitalWrite(pin_dht,LOW);
  delay(20);
  digitalWrite(pin_dht,HIGH);
  delayMicroseconds(40);
  pinMode(pin_dht,INPUT);
  //delayMicroseconds(40);
  dht_in=digitalRead(pin_dht);
  if(dht_in){
    bGlobalErr=1;
    return;
  }
  delayMicroseconds(80);
  dht_in=digitalRead(pin_dht);
  if(!dht_in){
    bGlobalErr=2;
    return;
  }
  delayMicroseconds(80);
  for (i=0; i<5; i++)
    dht_dat[i] = read_dht_dat();
  pinMode(pin_dht,OUTPUT);
  digitalWrite(pin_dht,HIGH);
  byte dht_check_sum = dht_dat[0]+dht_dat[1]+dht_dat[2]+dht_dat[3];
  if(dht_dat[4]!= dht_check_sum){
    bGlobalErr=3;
  }
}
//DHT sensor functions end

static const coap_endpoint_path_t path_well_known_core = {2, {".well-known", "core"}};
static int handle_get_well_known_core(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
    return coap_make_response(scratch, outpkt, (const uint8_t *)rsp, strlen(rsp), id_hi, id_lo, &inpkt->tok, COAP_RSPCODE_CONTENT, COAP_CONTENTTYPE_APPLICATION_LINKFORMAT);
}

//humidity sensor******************************************************************************************************************************************
static const coap_endpoint_path_t path_humidity = {1, {"humidity"}};

static int handle_get_humidity(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
  ReadDHT();
  
  int value_humidity = -1;
  
  if (bGlobalErr==0){
    value_humidity = dht_dat[0];
  }
  else{
    printf("DHT error\n");
  }
  
  coap_return_result(value_humidity, scratch, inpkt, outpkt, id_hi, id_lo);
}
//humidity end*********************************************************************************************************************************************

//temperature sensor******************************************************************************************************************************************
static const coap_endpoint_path_t path_temperature = {1, {"temperature"}};

static int handle_get_temperature(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
  ReadDHT();
  
  int value_temperature = -1;
  
  if (bGlobalErr==0){
    value_temperature = dht_dat[2];
  }
  else{
    printf("DHT error\n");
  }
  
  coap_return_result(value_temperature, scratch, inpkt, outpkt, id_hi, id_lo);
}
//temperature end*********************************************************************************************************************************************

//light sensor******************************************************************************************************************************************
static const coap_endpoint_path_t path_light = {1, {"light"}};

static int handle_get_light(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
  int value_light; 
  #ifdef ARDUINO
    value_light = analogRead(A0);
  #else
    printf("Bring light value\n");
  #endif
  
    coap_return_result(value_light, scratch, inpkt, outpkt, id_hi, id_lo);
}
//light end*********************************************************************************************************************************************

//dust sensor*******************************************************************************************************************************************
static const coap_endpoint_path_t path_dust = {1, {"dust"}};

static int handle_get_dust(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
  int value_dust; 
  #ifdef ARDUINO
    duration = pulseIn(pin_dust, LOW, 1);
    lowpulseoccupancy = lowpulseoccupancy + duration;
    //       value_dust = analogRead(soundpin);
    if ((millis() - starttime) > sampletime_ms) //if the sampel time == 30s
    {
      ratio = lowpulseoccupancy / (sampletime_ms * 10.0); // Integer percentage 0=>100
      value_dust = 1.1 * pow(ratio, 3) - 3.8 * pow(ratio, 2) + 520 * ratio + 0.62; // using spec sheet curve
      lowpulseoccupancy = 0;
      starttime = millis();
    }
  #else
        printf("Bring Dust value\n");
  #endif
  
  coap_return_result(value_dust, scratch, inpkt, outpkt, id_hi, id_lo);
}
//dust end*******************************************************************************************************************************************

const coap_endpoint_t endpoints[] =
{
  {COAP_METHOD_GET, handle_get_well_known_core, &path_well_known_core, "ct=40"},
  {COAP_METHOD_GET, handle_get_light, &path_light, "ct=0"},
  {COAP_METHOD_GET, handle_get_dust, &path_dust, "ct=0"},
  {COAP_METHOD_GET, handle_get_temperature, &path_temperature, "ct=0"},
  {COAP_METHOD_GET, handle_get_humidity, &path_humidity, "ct=0"},
  {(coap_method_t)0, NULL, NULL, NULL, NULL, NULL}
};

void build_rsp(void)
{
  uint16_t len = rsplen;
  const coap_endpoint_t *ep = endpoints;
  int i;

  len--; // Null-terminated string

  while(NULL != ep->handler)
  {
    if (NULL == ep->core_attr) {
      ep++;
      continue;
    }

    if (0 < strlen(rsp)) {
      strncat(rsp, ",", len);
      len--;
    }

    strncat(rsp, "<", len);
    len--;

    for (i = 0; i < ep->path->count; i++) {
      strncat(rsp, "/", len);
      len--;

      strncat(rsp, ep->path->elems[i], len);
      len -= strlen(ep->path->elems[i]);
    }

    strncat(rsp, ">;", len);
    len -= 2;

    strncat(rsp, ep->core_attr, len);
    len -= strlen(ep->core_attr);

    ep++;
  }
}

