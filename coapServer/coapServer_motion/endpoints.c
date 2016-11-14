#include <stdbool.h>
#include <string.h>
#include "coap.h"

const uint16_t rsplen = 2500;
static char rsp[2500] = "";
void build_rsp(void);

#ifdef ARDUINO
#include "Arduino.h"
static int pin_motion = 2; //pin for motion sensor

void endpoint_setup(void)
{                
    pinMode(pin_motion, INPUT);
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

const coap_endpoint_t endpoints[] =
{
  {COAP_METHOD_GET, handle_get_well_known_core, &path_well_known_core, "ct=40"},
  {COAP_METHOD_GET, handle_get_motion, &path_motion, "ct=0"},
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

