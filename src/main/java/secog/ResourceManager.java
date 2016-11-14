package secog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class ResourceManager extends Object {
	static final String ontURI = "http://icl.yonsei.ac.kr/ontologies/psm#";
	
	public OntClass getOntClass(ArrayList<OntClass> carray, ArrayList<String> sarray, String s)
	{
		return carray.get(getOntNum(sarray, s));
	}
	
	public OntProperty getOntPro(ArrayList<OntProperty> carray, ArrayList<String> sarray, String s)
	{
		return carray.get(getOntNum(sarray, s));
	}
	
	//해당 array에서 s가 몇 번째 array에 존재하는지 출력함.
	public int getOntNum(ArrayList<String> arry, String s)
	{
		int result;
		
		for(result = 0; result < arry.size(); result++)
		{
			if(arry.get(result).equals(s))
				break;
		}
		
		return result;
	}
	
	public void createResource(ArrayList<OntProperty> PSMop, ArrayList<String> PSMopn, 
		String coapURI, String coapLocation, String coapResourceType, String rdfURI, String rdfFileName){
		String PSMURI = "http://icl.yonsei.ac.kr/ontologies/psm#";
		
		OntModel resourceModel = ModelFactory.createOntologyModel();
		resourceModel.setNsPrefix("psm", PSMURI);
		
		resourceModel.createOntResource(PSMURI + "Resource/" + rdfURI)
		.addProperty(getOntPro(PSMop, PSMopn, "hasRType"), coapResourceType)
	    .addProperty(getOntPro(PSMop, PSMopn, "isIdentifiedBy"), coapURI)
	    .addProperty(getOntPro(PSMop, PSMopn, "hasLocalRange"), coapLocation);
		
		//Resource instance를 rdf파일로 저장
		try {
			OutputStream out = new FileOutputStream("D:/Workspace_J2EE/SECoG/Data/Resource/" + rdfFileName + ".rdf");
			resourceModel.write(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("create resource: " + rdfFileName + ".rdf done!");
		
		resourceModel.close();
	}
	
	public void loadTDB(String directoryPath, Dataset ds, Model model){
   	 	/*for(int i = 1; i <= num; i++){
			String resourceSensor = "D:/Workspace_J2EE/SECoG/Data/Resource/" + i + ".rdf";
			
			FileManager.get().readModel(model, resourceSensor);
   	 	}*/
   	 	
	   	ArrayList<String> fileNames = new ArrayList<String>();
	
	   	File[] files = new File(directoryPath).listFiles();
	   	//If this pathname does not denote a directory, then listFiles() returns null. 
	
	   	for (File file : files) {
	   	    if (file.isFile()) {
	   	    	fileNames.add(directoryPath + file.getName());
	   	    }
	   	}
	   	
	   	for(int i=0; i<fileNames.size(); i++){
	   		//System.out.println(fileNames.get(i));
	   		
	   		FileManager.get().readModel(model, fileNames.get(i));
	   	}

        model.commit();
	}
	
	public ArrayList<String> searchResource(String coapLocation, String coapResourceType, Dataset ds){
		ArrayList<String> coapURLArray = new ArrayList<String>();
		
		//set sparql query
		String sparqlQueryString = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
        		+ " PREFIX psm:<http://icl.yonsei.ac.kr/ontologies/psm#>"
        		+ " SELECT ?identifier"
        		+ " WHERE{ ?resource psm:isIdentifiedBy ?identifier."
        		+ " ?resource psm:hasRType ?coapResourceType."
        		+ " ?resource psm:hasLocalRange ?coapLocation."
        		+ " FILTER ("
        		+ "?coapResourceType = \"" + coapResourceType + "\" &&"
        		+ " ?coapLocation = \"" + coapLocation + "\")}";
        
		//System.out.println(sparqlQueryString);
        
        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ds);
        ResultSet rs = qexec.execSelect();
        
        while(rs.hasNext()){
        	coapURLArray.add(rs.next().get("identifier"
        			+ "").toString());
        }
        
        qexec.close();
        
        return coapURLArray;
	}
	
	public ArrayList<String> manageResource (String directoryPath, String coapLocation, String coapResourceType) {	
		OntModel PSM = ModelFactory.createOntologyModel();
		File in = new File("D:/Workspace_J2EE/SECoG/PSM.owl");
		String PSMURI = "http://icl.yonsei.ac.kr/ontologies/psm#";
		  
		try {
			PSM.read(new FileInputStream(in), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		ArrayList<OntClass> PSMoc = new ArrayList<OntClass>(); // Ontology Class
		ArrayList<OntProperty> PSMop = new ArrayList<OntProperty>(); // Ontology Property
		ArrayList<String> PSMocn = new ArrayList<String>(); // Ontology Class Name
		ArrayList<String> PSMopn = new ArrayList<String>(); // Ontology Property Name
		  
		//create Class array
		for(Iterator<OntClass> clses = PSM.listClasses(); clses.hasNext();)
		{
			OntClass cls = clses.next();
			  
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());	    		  
				String ontUri = "http://icl.yonsei.ac.kr/ontologies/psm#" + name.substring(1);
				  
				PSMocn.add(name.substring(1));
				  
				OntClass ont = PSM.getOntClass(ontUri);
				PSMoc.add(ont);
			}
		}
		  
		//create Property array
		for (Iterator<OntProperty> clses = PSM.listAllOntProperties(); clses.hasNext();){
			OntProperty cls = clses.next(); 
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());
				String ontURI = PSMURI + name.substring(1);
				  
				PSMopn.add(name.substring(1));
				  
				OntProperty ont = PSM.getOntProperty(ontURI);
				PSMop.add(ont);
			}
		}
		  
		//initialize TDB
		Dataset ds = TDBFactory.createDataset("D:/Workspace_J2EE/SECoG/TDB");
	 	Model model = ds.getDefaultModel();
	 	model.removeAll();
	 	
	 	//load TDB
		loadTDB(directoryPath, ds, model);
		
		//search resource
		ArrayList<String> coapURLArray = new ArrayList<String>();
		
		//String coapLocation = "campus/towerbuilding/floor5/529";
		//String coapResourceType = "dust";
		coapURLArray = searchResource(coapLocation, coapResourceType, ds);
		
		/*for(int i=0; i<coapURLArray.size(); i++){
			System.out.println(coapURLArray.get(i));
		}*/
		
		model.close();
		ds.close();
		
		return coapURLArray;
	}

	public ArrayList<String> searchSensor (String geoLocation, String featureOfInterest, Dataset ds){
		ArrayList<String> resultArray = new ArrayList<String>();
		
		//set sparql query
		String sparqlQueryString = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
        		+ " PREFIX psm:<http://icl.yonsei.ac.kr/ontologies/psm#>"
        		+ " SELECT ?identifier ?sensingPeriod ?coverage"
        		+ " WHERE{?resource psm:isIdentifiedBy ?identifier."
        		+ " ?resource psm:hasRType ?resourceType."
        		+ " ?resource psm:hasLocalRange ?location."
        		+ " ?resource psm:hasRCoverage ?coverage."
        		+ " ?resource psm:hasSensingPeriod ?sensingPeriod."
        		+ " FILTER ("
        		+ "?resourceType = \"" + featureOfInterest + "\" &&"
        		+ " ?location = \"" + geoLocation + "\")}";
        
		//System.out.println(sparqlQueryString);
        
        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, ds);
        ResultSet rs = qexec.execSelect();
        
        while(rs.hasNext()){
        	QuerySolution binding = rs.nextSolution();
        	
        	resultArray.add(binding.get("identifier").toString());
        	resultArray.add(binding.get("sensingPeriod").toString());
        	resultArray.add(binding.get("coverage").toString());
        }
        
        qexec.close();
        
        return resultArray;
	}
	
	public ArrayList<String> manageSensor (String directoryPath, String geoLocation, String featureOfInterest) {	
		OntModel PSM = ModelFactory.createOntologyModel();
		File in = new File("D:/Workspace_J2EE/SECoG/PSM2.owl");
		String PSMURI = "http://icl.yonsei.ac.kr/ontologies/psm#";
		  
		try {
			PSM.read(new FileInputStream(in), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		ArrayList<OntClass> PSMoc = new ArrayList<OntClass>(); // Ontology Class
		ArrayList<OntProperty> PSMop = new ArrayList<OntProperty>(); // Ontology Property
		ArrayList<String> PSMocn = new ArrayList<String>(); // Ontology Class Name
		ArrayList<String> PSMopn = new ArrayList<String>(); // Ontology Property Name
		  
		//create Class array
		for(Iterator<OntClass> clses = PSM.listClasses(); clses.hasNext();)
		{
			OntClass cls = clses.next();
			  
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());	    		  
				String ontUri = "http://icl.yonsei.ac.kr/ontologies/psm#" + name.substring(1);
				  
				PSMocn.add(name.substring(1));
				  
				OntClass ont = PSM.getOntClass(ontUri);
				PSMoc.add(ont);
			}
		}
		  
		//create Property array
		for (Iterator<OntProperty> clses = PSM.listAllOntProperties(); clses.hasNext();){
			OntProperty cls = clses.next(); 
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());
				String ontURI = PSMURI + name.substring(1);
				  
				PSMopn.add(name.substring(1));
				  
				OntProperty ont = PSM.getOntProperty(ontURI);
				PSMop.add(ont);
			}
		}
		  
		//initialize TDB
		Dataset ds = TDBFactory.createDataset("D:/Workspace_J2EE/SECoG/TDB");
   	 	Model model = ds.getDefaultModel();
   	 	model.removeAll();
   	 	
   	 	//load TDB
		loadTDB(directoryPath, ds, model);
		
		//search resource
		ArrayList<String> resultArray = new ArrayList<String>();
		
		resultArray = searchSensor(geoLocation, featureOfInterest, ds);
		
		for(int i=0; i<resultArray.size(); i++){
			System.out.println(resultArray.get(i));
		}
		
		model.close();
		ds.close();
		
		return resultArray;
	}
	
	public void registerSensor(String alias, String URL, String featureOfInterest, String geoLocation, String sensingPeriod, String coverage){
		OntModel PSM = ModelFactory.createOntologyModel();
		File in = new File("D:/Workspace_J2EE/SECoG/PSM2.owl");
		String PSMURI = "http://icl.yonsei.ac.kr/ontologies/psm#";
		  
		try {
			PSM.read(new FileInputStream(in), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		ArrayList<OntClass> PSMoc = new ArrayList<OntClass>(); // Ontology Class
		ArrayList<OntProperty> PSMop = new ArrayList<OntProperty>(); // Ontology Property
		ArrayList<String> PSMocn = new ArrayList<String>(); // Ontology Class Name
		ArrayList<String> PSMopn = new ArrayList<String>(); // Ontology Property Name
		  
		//create Class array
		for(Iterator<OntClass> clses = PSM.listClasses(); clses.hasNext();)
		{
			OntClass cls = clses.next();
			  
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());	    		  
				String ontUri = "http://icl.yonsei.ac.kr/ontologies/psm#" + name.substring(1);
				  
				PSMocn.add(name.substring(1));
				  
				OntClass ont = PSM.getOntClass(ontUri);
				PSMoc.add(ont);
			}
		}
		  
		//create Property array
		for (Iterator<OntProperty> clses = PSM.listAllOntProperties(); clses.hasNext();){
			OntProperty cls = clses.next(); 
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());
				String ontURI = PSMURI + name.substring(1);
				  
				PSMopn.add(name.substring(1));
				  
				OntProperty ont = PSM.getOntProperty(ontURI);
				PSMop.add(ont);
			}
		}
		
		OntModel resourceModel = ModelFactory.createOntologyModel();
		resourceModel.setNsPrefix("psm", PSMURI);
		
		resourceModel.createOntResource(PSMURI + "Resource/" + alias)
		.addProperty(getOntPro(PSMop, PSMopn, "isIdentifiedBy"), URL)
		.addProperty(getOntPro(PSMop, PSMopn, "hasRType"), featureOfInterest)
	    .addProperty(getOntPro(PSMop, PSMopn, "hasLocalRange"), geoLocation)
	    .addProperty(getOntPro(PSMop, PSMopn, "hasSensingPeriod"), sensingPeriod)
	    .addProperty(getOntPro(PSMop, PSMopn, "hasRCoverage"), coverage);
		
		//Resource instance를 rdf파일로 저장
		try {
			OutputStream out = new FileOutputStream("D:/Workspace_J2EE/SECoG/Data/Sensor/" + alias + ".rdf");
			resourceModel.write(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("create resource: " + alias + ".rdf done!");
		
		resourceModel.close();
	}

	public void registerComplexSensor(String location, String resourceType, ArrayList<String> composingSensors){
		OntModel PSM = ModelFactory.createOntologyModel();
		File in = new File("D:/Workspace_J2EE/SECoG/PSM2.owl");
		String PSMURI = "http://icl.yonsei.ac.kr/ontologies/psm#";
		  
		try {
			PSM.read(new FileInputStream(in), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		ArrayList<OntClass> PSMoc = new ArrayList<OntClass>(); // Ontology Class
		ArrayList<OntProperty> PSMop = new ArrayList<OntProperty>(); // Ontology Property
		ArrayList<String> PSMocn = new ArrayList<String>(); // Ontology Class Name
		ArrayList<String> PSMopn = new ArrayList<String>(); // Ontology Property Name
		  
		//create Class array
		for(Iterator<OntClass> clses = PSM.listClasses(); clses.hasNext();)
		{
			OntClass cls = clses.next();
			  
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());	    		  
				String ontUri = "http://icl.yonsei.ac.kr/ontologies/psm#" + name.substring(1);
				  
				PSMocn.add(name.substring(1));
				  
				OntClass ont = PSM.getOntClass(ontUri);
				PSMoc.add(ont);
			}
		}
		  
		//create Property array
		for (Iterator<OntProperty> clses = PSM.listAllOntProperties(); clses.hasNext();){
			OntProperty cls = clses.next(); 
			if(!cls.isAnon()){
				String name = cls.getModel().getGraph().getPrefixMapping().shortForm(cls.getURI());
				String ontURI = PSMURI + name.substring(1);
				  
				PSMopn.add(name.substring(1));
				  
				OntProperty ont = PSM.getOntProperty(ontURI);
				PSMop.add(ont);
			}
		}
		
		OntModel resourceModel = ModelFactory.createOntologyModel();
		resourceModel.setNsPrefix("psm", PSMURI);
		
		resourceModel.createOntResource(PSMURI + "Resource/" + location + resourceType)
		.addProperty(getOntPro(PSMop, PSMopn, "hasRType"), resourceType)
	    .addProperty(getOntPro(PSMop, PSMopn, "isIdentifiedBy"), location + resourceType)
	    .addProperty(getOntPro(PSMop, PSMopn, "hasLocalRange"), location);
		
		
		//System.out.println(composingSensors.size());
		for(int i=0; i<composingSensors.size(); i++){
			System.out.println(composingSensors.get(i));
			
			resourceModel.createOntResource(PSMURI + "Resource/" + location + resourceType)
			.addProperty(getOntPro(PSMop, PSMopn, "isComposedOf"), composingSensors.get(i));
		}
		
		//Resource instance를 rdf파일로 저장
		try {
			OutputStream out = new FileOutputStream("D:/Workspace_J2EE/SECoG/Data/Sensor/" + location + resourceType + ".rdf");
			resourceModel.write(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("create resource: " + location + resourceType + ".rdf done!");
		
		resourceModel.close();
	}
}
