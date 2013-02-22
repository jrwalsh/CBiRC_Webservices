package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;

/**
*
* @author jrwalsh
*/
@SuppressWarnings({"rawtypes","unchecked"})
public class GeneIDTranslate {
	private static String server = "tht.vrac.iastate.edu";
	private static int port = 4444;
	private static String probeMapFileName ="Probe_to_ecoliGene_map_MG1655.txt"; 
		//"/home/jesse/workspace/CBiRC_Webservices/src/webservices/TranslateGeneID/Probe_to_ecoliGene_map_MG1655.txt";
	
	/*
	 * This webservice will take in 3 arguments. GeneIDs, GeneIDTypeRequested, and Organism.
	 * 
	 * ID types allowed include ecocycID, commonName, bnum, synonym, and probesetId
	 * 
	 * Allows multiple results to be printed, but only selects the first synonym for any single result. Also selects only last probe ID for a 
	 * single result.
	 */
	public static void main(String[] args) {
		if (args.length<3 || args.length>3) {
			System.out.println("Usage: GeneIDTranslate GeneIDs GeneIDTypeRequested Organism");
			System.exit(0);
		}
		
		int idTypeSwitch = 0;
		HashMap<String, String> geneToProbeMap = null;
		HashMap<String, String> probeToGeneMap = null;
		
		String geneIDs = args[0];
		String geneIDTypeRequested = args[1];
		String org = args[2];
		
		if (geneIDTypeRequested.equalsIgnoreCase("ecocycID")) idTypeSwitch = 1;
		else if (geneIDTypeRequested.equalsIgnoreCase("commonName")) idTypeSwitch = 2;
		else if (geneIDTypeRequested.equalsIgnoreCase("bnum")) idTypeSwitch = 3;
		else if (geneIDTypeRequested.equalsIgnoreCase("synonym")) idTypeSwitch = 4;
		else if (geneIDTypeRequested.equalsIgnoreCase("probesetId")) {
			idTypeSwitch = 5;
			HashMap[] maps = loadProbeMap(probeMapFileName);
			geneToProbeMap = maps[0];
			probeToGeneMap = maps[1];
		}
		else {
			System.out.println("Allowed output types: ecocycID, commonName, bnum, synonym, and probesetId");
			System.exit(0);
		}
		
		JavacycConnection conn = new JavacycConnection(server,port);
		conn.selectOrganism(org);
		try {
			for (String geneID : geneIDs.split(",")) {
				ArrayList<Frame> results = conn.search(geneID, "Gene");
				if (results.size() == 0) {
					// Since the search returned no results, check if this gene ID is really a probeset ID.  First load the maps
					if (probeToGeneMap == null) {
						HashMap[] maps = loadProbeMap(probeMapFileName);
						geneToProbeMap = maps[0];
						probeToGeneMap = maps[1];
					}
					
					String ecoCycID = probeToGeneMap.get(geneID);
					if (ecoCycID != null) {
						try {
							results.add(Frame.load(conn, ecoCycID));
						} catch (Exception e) { }
					}
				}
				System.out.print(geneID + "\t");
				boolean firstResult = true;
				for (Frame result : results) {
					if (!firstResult) System.out.print(",");
					switch (idTypeSwitch) {
						case 1: {
							try {
								System.out.print(result.getLocalID());
							} catch (Exception e) { }
						} break;
						case 2: {
							try {
								System.out.print(result.getCommonName());
							} catch (Exception e) { }
						} break;
						case 3: {
							try {
								System.out.print(result.getSlotValue("Accession-1"));
							} catch (Exception e) { }
						} break;
						case 4: {
							try {
								System.out.print(result.getSynonyms().get(0));
							} catch (Exception e) { }
						} break;
						case 5: {
							try {
								System.out.print(geneToProbeMap.get(result.getLocalID()));
							} catch (Exception e) { }
						} break;
					}
					firstResult = false;
				}
				System.out.println("");
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static HashMap[] loadProbeMap(String fileName) {
		HashMap<String, String> geneToProbeMap = new HashMap<String, String>();
		HashMap<String, String> probeToGeneMap = new HashMap<String, String>();
		
		File reactionMapFile = new File(fileName);
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(reactionMapFile));
			String text = null;
			
			while ((text = reader.readLine()) != null) {
				String[] line = text.split("\t");
				geneToProbeMap.put(line[3], line[0]);
				probeToGeneMap.put(line[0], line[3]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return new HashMap[] {geneToProbeMap, probeToGeneMap};
	}
}
