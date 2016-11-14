package californium;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

/**
 * CoAP Client Test!
 *
 */
public class CfClientTest 
{
    public static void main( String[] args ) throws InterruptedException
    {
    	CoapClient client1 = new CoapClient("coap://165.132.120.220:5683/dust");
    	CoapResponse coapresponse1 = null;
		
		for(int i=0; i<5; i++){
			System.out.println("The result of process " + (i+1) + " is: ");
			coapresponse1 = client1.get();
			if(coapresponse1 == null){
				System.out.println("Fail to get observation value!");
			}else{
				System.out.println(coapresponse1.getResponseText());
			}
		}
		
    	CoapClient client2 = new CoapClient("coap://165.132.120.222:5683/dust");
    	CoapResponse coapresponse2 = null;

		for(int i=5; i<10; i++){
			System.out.println("The result of process " + (i+1) + " is: ");
			coapresponse2 = client2.get();
			if(coapresponse2 == null){
				System.out.println("Fail to get observation value!");
			}else{
				System.out.println(coapresponse2.getResponseText());
			}		
		}
    }
}
