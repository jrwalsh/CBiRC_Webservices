package classes;

import java.util.ArrayList;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

/**
 *
 * @author jrwalsh
 */
public class GetFrame {

	public static void main(String[] args) {
		if(args.length<4) {
			System.out.println("Usage: GetFrame SERVER PORT ORGANISM FRAME [slotName]");
			System.exit(0);
		}
		String server = args[0];
		int port  = Integer.parseInt(args[1]);
		String org = args[2];
		String frameName = args[3];
		String slotName = "";
		boolean useSlotName = false;
		if(args.length>4) {
			slotName = args[4];
			useSlotName = true;
		}

		//TODO if no slot name given, print entire frame in xml
		JavacycConnection conn = new JavacycConnection(server,port);
		conn.selectOrganism(org);
		try {
			if (useSlotName) {
				System.out.println(conn.getSlotValues(frameName, slotName));
			} else {
				System.out.println("In the works");
				//System.out.println(convertToXML(conn, frameName));
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//TODO
	@SuppressWarnings({ "unchecked", "static-access", "unused", "rawtypes" })
	private static String convertToXML(JavacycConnection conn, String frameName) throws PtoolsErrorException {
		String xmlString = "";
		
//		Frame.load(conn, frameName).getXGMML(false, pathways, nodeAtts, GMLids)
		
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            Element root = doc.createElement("root");
            doc.appendChild(root);
			
			ArrayList<String> slots = (ArrayList<String>)conn.getFrameSlots(frameName);
			
			slots = new ArrayList<String>();
			slots.add("COMMON-NAME");
			slots.add("COMMENT");
			for (String slotName : slots) {
				ArrayList slotValues = conn.getSlotValues(frameName, slotName);
				//System.out.println(slotName+" ("+JavacycConnection.countLists(slotValues)+")");
				for(Object slotValue : slotValues)
				{
					if(slotValue instanceof String) {
						Element slot = doc.createElement("slot");
			            slot.setAttribute(slotName, (String)slotValue);
			            root.appendChild(slot);
						
			            //System.out.println("\t"+(String)slotValue);
						ArrayList<String> annots = conn.getAllAnnotLabels(frameName, slotName, (String)slotValue);
						for(String annotName : annots) {
							Element annotation = doc.createElement("annotation");
				            annotation.setAttribute(annotName, conn.getValueAnnot(frameName, slotName, (String)slotValue, annotName));
				            root.appendChild(annotation);
							//System.out.println("\t\t--"+annotName+"\t"+conn.getValueAnnot(frameName, slotName, (String)slotValue, annotName));
						}
					}
					else {
						System.out.println("\t"+conn.ArrayList2LispList(slotValues));
						break;
					}
				}
			}
			
			
			TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            xmlString = sw.toString();
		
		} catch (Exception e) {
            System.out.println(e);
        }
		
		
//		xml += frame.getLocalID() + "\n";
//		xml += frame.getCommonName();
//		frame.print();
		return xmlString;
	}
	
	public String DomXmlExample() {
		// Example from http://www.genedavis.com/library/xml/java_dom_xml_creation.jsp
        try {
            /////////////////////////////
            //Creating an empty XML Document

            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            ////////////////////////
            //Creating the XML tree

            //create the root element and add it to the document
            Element root = doc.createElement("root");
            doc.appendChild(root);

            //create a comment and put it in the root element
            Comment comment = doc.createComment("Just a thought");
            root.appendChild(comment);

            //create child element, add an attribute, and add to root
            Element child = doc.createElement("child");
            child.setAttribute("name", "value");
            root.appendChild(child);

            //add a text element to the child
            Text text = doc.createTextNode("Filler, ... I could have had a foo!");
            child.appendChild(text);

            /////////////////
            //Output the XML

            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();

            //print xml
            System.out.println("Here's the xml:\n\n" + xmlString);

        } catch (Exception e) {
            System.out.println(e);
        }
        
		return null;
    }
}
