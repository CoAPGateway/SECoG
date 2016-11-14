#include <Dhcp.h>
#include <Dns.h>
#include <Ethernet2.h>
#include <EthernetClient.h>
#include <EthernetServer.h>
#include <EthernetUdp2.h>
#include <Twitter.h>
#include <util.h>

/*
* WARNING - UDP_TX_PACKET_MAX_SIZE is hardcoded by Arduino to 24 bytes
* This limits the size of possible outbound UDP packets
*/

#include <SPI.h>
#include <stdint.h>
#include "coap.h"

#define PORT 5683
static uint8_t mac[] = {0x00, 0xAA, 0xBB, 0xCC, 0xDE, 0x02};
static uint8_t ip[] = {165, 132, 120, 227};
static uint8_t myDns[] = {165, 132, 10, 21};
static uint8_t gateway[] = {165, 132, 120, 1};
static uint8_t subnet[] = {255, 255, 252, 0};

EthernetClient client;
EthernetUDP udp;
uint8_t packetbuf[256];
static uint8_t scratch_raw[32];
static coap_rw_buffer_t scratch_buf = {scratch_raw, sizeof(scratch_raw)};

void setup()
{
    Serial.begin(9600);
    while (!Serial) 
    {
        ; // wait for serial port to connect. Needed for Leonardo only
    }

    // start the Ethernet connection:
    Ethernet.begin(mac, ip, myDns, gateway, subnet);
    
    Serial.print("My IP address: ");
    Serial.println(Ethernet.localIP());
    
    udp.begin(PORT);

    coap_setup();
    endpoint_setup();
}

void udp_send(const uint8_t *buf, int buflen)
{
    udp.beginPacket(udp.remoteIP(), udp.remotePort());
    while(buflen--)
        udp.write(*buf++);
    udp.endPacket();
}

void loop()
{
    int sz;
    int rc;
    coap_packet_t pkt;
    int i;
    
    if ((sz = udp.parsePacket()) > 0)
    {
        udp.read(packetbuf, sizeof(packetbuf));

        for (i=0;i<sz;i++)
        {
            Serial.print(packetbuf[i], HEX);
            Serial.print(" ");
        }
        Serial.println("");

        if (0 != (rc = coap_parse(&pkt, packetbuf, sz)))
        {
            Serial.print("Bad packet rc=");
            Serial.println(rc, DEC);
        }
        else
        {
            size_t rsplen = sizeof(packetbuf);
            coap_packet_t rsppkt;
            coap_handle_req(&scratch_buf, &pkt, &rsppkt);

            //memset(packetbuf, 0, UDP_TX_PACKET_MAX_SIZE);
            if (0 != (rc = coap_build(packetbuf, &rsplen, &rsppkt)))
            {
                Serial.print("coap_build failed rc=");
                Serial.println(rc, DEC);
            }
            else
            {
                udp_send(packetbuf, rsplen);
            }
        }
    }
}

