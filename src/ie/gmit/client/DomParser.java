package ie.gmit.client;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ie.gmit.requests.Access;

import javax.xml.parsers.*;
import java.io.*;

public class DomParser {
	
	private Access data;
	
	public DomParser(Access data) {
		super();
		this.data = data;
	}
	
	public void parser() {
		
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	try {
		DocumentBuilder builder = factory.newDocumentBuilder(); // create builder from factory
		Document doc = builder.parse("src/Client-config.xml"); // the file we will extract data from
		doc.getDocumentElement().normalize(); // normilize the file layout, not really needed here but good practice
		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName()); // check the root of the DOM
		Element root = doc.getDocumentElement(); //Get the root of the DOM Element(s)
		NodeList details = root.getChildNodes(); //extract the details inside
		//	System.out.println(details);
		
		for (int i = 0; i < details.getLength(); i++){ //Loop over the child nodes
			Node nNode = details.item(i); //Get the next child

				//System.out.println("\nCurrent Element :" + nNode.getNodeName()); get current element

				if (nNode.getNodeName().equals("server-details")) {
					/* Check if these are the details we need and if so loop through the DOM until
					 * everything needed to connect to the server is extracted and stored */
					Element eElement = (Element) nNode;

					data.setUsername(eElement.getAttribute("username"));
					data.setServerHost(eElement.getElementsByTagName("serverHost").item(0).getTextContent());
					data.setServerPort(eElement.getElementsByTagName("serverPort").item(0).getTextContent());
					data.setDownloadDir(eElement.getElementsByTagName("download-dir").item(0).getTextContent());
					
				}
			}
		
	
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		}
	
	public Access getData() { 
		return data;
	}

	public void setData(Access data) {
		this.data = data;
	}
	
}
