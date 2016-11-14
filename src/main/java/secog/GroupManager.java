package secog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class GroupManager {
	
	public void createGroup(ArrayList<String> coapURLArray, String coapLocation, String coapResourceType) throws IOException{
		try{
			OutputStream out = new FileOutputStream("D:/Workspace_J2EE/SECoG/Data/Group/" + coapLocation + coapResourceType + ".txt");
			PrintStream printStream = new PrintStream(out);
			
            for(int i=0; i<coapURLArray.size(); i++){
            	if(i==coapURLArray.size()-1){
            		printStream.print(coapURLArray.get(i));
            	}else{
            		printStream.print(coapURLArray.get(i) + "\n");
            	}
            }
            
            out.close();
            System.out.println("Create group done! " + coapLocation + coapResourceType + ".txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isGroupExisting(String coapLocation, String coapResourceType){
		File file = new File("D:/Workspace_J2EE/SECoG/Data/Group/" + coapLocation + coapResourceType + ".txt");
		
		return file.exists();
	}
	
	public ArrayList<String> getCoapURLsFromGroup(String coapLocation, String coapResourceType){
		ArrayList<String> coapURLArray = new ArrayList<String>();

		//get coap URLs of a group
		File file = new File("D:/Workspace_J2EE/SECoG/Data/Group/" + coapLocation + coapResourceType + ".txt");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       coapURLArray.add(line);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return coapURLArray;
	}
	
	public ArrayList<Float> getObservationResults(String coapLocation, 
			String coapResourceType, String numberOfReplies, String timeout, String retransmission){
		ArrayList<Float> observationResultArray = new ArrayList<Float>();
		ArrayList<String> coapURLArray = new ArrayList<String>();

		//get coap URLs of a group
		coapURLArray = getCoapURLsFromGroup(coapLocation, coapResourceType);
		
		//Process in linear way 
		//long startLinear = System.currentTimeMillis();
		
		//Process number of replies
		int nor = 0;
		if(numberOfReplies.equals("")){
			nor = coapURLArray.size();
		}else{
			nor = Math.min(coapURLArray.size(), Integer.parseInt(numberOfReplies));
		}
		
		//System.out.println("The number of group members is: " + coapURLArray.size());
		//System.out.println("User requested nor is: " + nor);
		
		//Process timeout
		long totalWaitingTime = 0;
		if(timeout.equals("")){
			totalWaitingTime = Integer.MAX_VALUE;
		}else{
			totalWaitingTime = Integer.parseInt(timeout) * 1000;
		}
		
		CoapClient coapClient = new CoapClient();;
		CoapResponse coapResponse = null;
		
		int accumulatedWaitingTime = 0;
		for(int i=0; i<nor; i++){
			try {
				long startWaiting = System.currentTimeMillis();
				
				try {
					coapClient.setURI(coapURLArray.get(i));
					coapResponse = coapClient.get();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				//re-transmission
				if(retransmission.equals("yes")){
					while(coapResponse==null){
						//Thread.sleep(1);
						coapResponse = coapClient.get();
					}
				}
				
		    	//if the observation result of dust is 0, re-send request
		    	if(coapResponse!=null && Float.parseFloat(coapResponse.getResponseText()) == 0){
		    		coapResponse = coapClient.get();
		    	}
		    	
		    	//Error report
		    	if(coapResponse==null){
		    		System.out.println("Fail to get coap response from " + coapURLArray.get(i));
		    	}else if(Float.parseFloat(coapResponse.getResponseText()) == (float)-1){
		    		System.out.println("Get coap response but fail to get sensor data from " + coapURLArray.get(i));
		    	}
		    	
		    	if(coapResponse!=null && Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
			    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
			    	//System.out.println("The number of received response is: " + (observationResultArray.size()) );
			    	System.out.println("The observation is: " + coapURLArray.get(i) + " || "+ coapResponse.getResponseText());
		    	}
		    	
		    	long endWaiting = System.currentTimeMillis();
		    	
		    	if(timeout!=null){
			    	accumulatedWaitingTime += endWaiting - startWaiting;
		    	}
		    	
		    	if(accumulatedWaitingTime>totalWaitingTime){
		    		i=nor;
		    	}
		    	
		    	//System.out.println("Current accumulated waiting time is: " + accumulatedWaitingTime + " and total waiting time is: " + totalWaitingTime);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    	
		}
		
		//long endLinear = System.currentTimeMillis();

		//System.out.println( "Execution time of get observation results(linear) : " + ( endLinear - startLinear )/1000.0 );
		
		//if not all the results received, set the observation result null
		if(observationResultArray.size()<nor){
			observationResultArray.clear();
		}
		
		return observationResultArray;
	}
	
	public ArrayList<Float> getObservationResultsParallelly(String coapLocation, 
			String coapResourceType, String numberOfReplies, String timeout, String retransmission){
		ArrayList<Float> observationResultArray = new ArrayList<Float>();
		ArrayList<String> coapURLArray = new ArrayList<String>();

		//get coap URLs of a group
		coapURLArray = getCoapURLsFromGroup(coapLocation, coapResourceType);
		
		//Process in parallel way
		long startParallel = System.currentTimeMillis();
		
		//Process number of replies
		int nor = 0;
		if(numberOfReplies.equals("")){
			nor = coapURLArray.size();
		}else{
			nor = Integer.parseInt(numberOfReplies);
		}
		
		//System.out.println("The number of group members is: " + coapURLArray.size());
		//System.out.println("User requested nor is: " + nor);
		
		//Process timeout
		long totalWaitingTime;
		if(timeout.equals("")){
			totalWaitingTime = Integer.MAX_VALUE;
		}else{
			totalWaitingTime = Integer.parseInt(timeout) * 1000;
		}
		
		Thread0 thread0 = new Thread0(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread1 thread1 = new Thread1(observationResultArray, coapURLArray, nor, timeout, retransmission);
		/*Thread2 thread2 = new Thread2(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread3 thread3 = new Thread3(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread4 thread4 = new Thread4(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread5 thread5 = new Thread5(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread6 thread6 = new Thread6(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread7 thread7 = new Thread7(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread8 thread8 = new Thread8(observationResultArray, coapURLArray, nor, timeout, retransmission);
		Thread9 thread9 = new Thread9(observationResultArray, coapURLArray, nor, timeout, retransmission);*/
		
		thread0.start();
		thread1.start();
		/*thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();
		thread6.start();
		thread7.start();
		thread8.start();
		thread9.start();*/
		
		long startWaitingTime = System.currentTimeMillis();
		long endWaitingTime = 0;
		
		while(observationResultArray.size()<nor){
			try {
				endWaitingTime = System.currentTimeMillis();
				if(endWaitingTime - startWaitingTime > totalWaitingTime){
					break;
				}
				
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(observationResultArray.size() + " < " + coapURLArray.size());
		}
		
		long endParallel = System.currentTimeMillis();

		System.out.println( "Execution time of get observation results(parallel) : " + ( endParallel - startParallel )/1000.0 );
		
		return observationResultArray;
	}
	
	//Thread definitions
	//Thread 0
	class Thread0 extends Thread{
		ArrayList<Float> observationResultArray;
		ArrayList<String> coapURLArray;
		String retransmission;
		
		public Thread0(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
			this.observationResultArray = observationResultArray;
			this.coapURLArray = coapURLArray;
			this.retransmission = retransmission;
		}
		
		public void run(){
			for(int i=0; i<coapURLArray.size(); i+=10){
				try {
					CoapClient coapClient = new CoapClient(coapURLArray.get(i));
					CoapResponse coapResponse = coapClient.get();

					//re-transmission
					if(retransmission.equals("yes")){
						while(coapResponse==null){
							//Thread.sleep(1);
							System.out.println("Thread0: fail to get coap response from " + coapURLArray.get(i));
							coapResponse = coapClient.get();
						}
					}
					
					//if the observation result of dust is 0, re-send request
			    	if(coapResponse!=null && Float.parseFloat(coapResponse.getResponseText()) == 0){
			    		coapResponse = coapClient.get();
			    	}
			    	
			    	//Error report
			    	if(Float.parseFloat(coapResponse.getResponseText()) == (float)-1){
			    		System.out.println("Get coap response but fail to get sensor data from " + coapURLArray.get(i));
			    	}
			    	
			    	if(coapResponse!=null && Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
				    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	System.out.println("Thread0: number of received response is: " + (observationResultArray.size()) );
				    	System.out.println("Thread0: observation is: " + coapURLArray.get(i) + " || "+ coapResponse.getResponseText());
			    	}
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
			}
			
		}
	}
	
	//Thread 1
	class Thread1 extends Thread{
		ArrayList<Float> observationResultArray;
		ArrayList<String> coapURLArray;
		String retransmission;
		
		public Thread1(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
			this.observationResultArray = observationResultArray;
			this.coapURLArray = coapURLArray;
			this.retransmission = retransmission;
		}
		
		public void run(){
			for(int i=1; i<coapURLArray.size(); i+=10){
				try {
					CoapClient coapClient = new CoapClient(coapURLArray.get(i));
					CoapResponse coapResponse = coapClient.get();

					//re-transmission
					if(retransmission.equals("yes")){
						while(coapResponse==null){
							//Thread.sleep(1);
							System.out.println("Thread0: fail to get coap response from " + coapURLArray.get(i));
							coapResponse = coapClient.get();
						}
					}
					
					//if the observation result of dust is 0, re-send request
			    	if(coapResponse!=null && Float.parseFloat(coapResponse.getResponseText()) == 0){
			    		coapResponse = coapClient.get();
			    	}
			    	
			    	//Error report
			    	if(Float.parseFloat(coapResponse.getResponseText()) == (float)-1){
			    		System.out.println("Get coap response but fail to get sensor data from " + coapURLArray.get(i));
			    	}
			    	
			    	if(coapResponse!=null && Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
				    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	System.out.println("Thread1: number of received response is: " + (observationResultArray.size()) );
				    	System.out.println("Thread1: observation is: " + coapURLArray.get(i) + " || "+ coapResponse.getResponseText());
			    	}
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		    	
			}
		}
	}
	
	//Thread 2
		class Thread2 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread2(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=2; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 3
		class Thread3 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread3(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=3; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 4
		class Thread4 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread4(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=4; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
										    	
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 5
		class Thread5 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread5(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=5; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
										    	
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 6
		class Thread6 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread6(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=6; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
										    	
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 7
		class Thread7 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread7(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=7; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
										    	
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 8
		class Thread8 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread8(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=8; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
										    	
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
		
		//Thread 9
		class Thread9 extends Thread{
			ArrayList<Float> observationResultArray = new ArrayList<Float>();
			ArrayList<String> coapURLArray = new ArrayList<String>();
			
			public Thread9(ArrayList<Float> observationResultArray, ArrayList<String> coapURLArray, int nor, String timeout, String retransmission) {
				this.observationResultArray = observationResultArray;
				this.coapURLArray = coapURLArray;
			}
			
			public void run(){
				for(int i=9; i<coapURLArray.size(); i+=10){
					try {
						CoapClient coapClient = new CoapClient(coapURLArray.get(i));
						CoapResponse coapResponse = coapClient.get();

						while(coapResponse==null){
							Thread.sleep(1);
							coapResponse = coapClient.get();
						}
						
						if(Float.parseFloat(coapResponse.getResponseText()) != (float)-1){
					    	observationResultArray.add(Float.parseFloat(coapResponse.getResponseText()));
				    	}
										    	
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
				}
			}
		}
}
