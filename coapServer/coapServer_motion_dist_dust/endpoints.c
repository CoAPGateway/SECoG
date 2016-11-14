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
static int pin_motion = 2; //pin for motion sensor
static int pin_dust = 8; //pin for dust sensor

//pins for distance sensor
static int pin_dist_trig = 12;
static int pin_dist_echo = 13;

void endpoint_setup(void)
{                
    pinMode(pin_motion, INPUT);
    pinMode(pin_dust, INPUT);
    
    //init distance sensor
    pinMode(pin_dist_trig, OUTPUT);
    pinMode(pin_dist_echo, INPUT);

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

static const coap_endpoint_path_t path_well_known_core = {2, {".well-known", "core"}};
static int handle_get_well_known_core(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
    return coap_make_response(scratch, outpkt, (const uint8_t *)rsp, strlen(rsp), id_hi, id_lo, &inpkt->tok, COAP_RSPCODE_CONTENT, COAP_CONTENTTYPE_APPLICATION_LINKFORMAT);
}

//motion sensor******************************************************************************************************************************************
static const coap_endpoint_path_t path_motion = {1, {"motion"}};

static int handle_get_motion(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
  delay(1000);
  int value_motion = digitalRead(pin_motion);
  
  coap_return_result(value_motion, scratch, inpkt, outpkt, id_hi, id_lo);
}
//motion sensor end*********************************************************************************************************************************************

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

//distance sensor******************************************************************************************************************************************
static const coap_endpoint_path_t path_distance = {1, {"distance"}};

static int handle_get_distance(coap_rw_buffer_t *scratch, const coap_packet_t *inpkt, coap_packet_t *outpkt, uint8_t id_hi, uint8_t id_lo)
{
  int value_distance; 
  int duration, distance;
  
  digitalWrite(pin_dist_trig, HIGH);
  delayMicroseconds(1000);
  digitalWrite(pin_dist_trig, LOW);
  duration = pulseIn(pin_dist_echo, HIGH, 1000);
  distance = (duration/2) / 29.1;
  
  if (distance >= 200 || distance <= 0){
    printf("Out of range\n");
  }
  else {
    value_distance = distance;
  }
  
  coap_return_result(value_distance, scratch, inpkt, outpkt, id_hi, id_lo);
}
//distance end*********************************************************************************************************************************************

const coap_endpoint_t endpoints[] =
{
  {COAP_METHOD_GET, handle_get_well_known_core, &path_well_known_core, "ct=40"},
  {COAP_METHOD_GET, handle_get_motion, &path_motion, "ct=0"},
  {COAP_METHOD_GET, handle_get_dust, &path_dust, "ct=0"},
  {COAP_METHOD_GET, handle_get_distance, &path_distance
, "ct=0"},
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

