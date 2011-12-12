package classes;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

/**
* The GenePoints class is designed to be a webservice which can provide genomic structures located at a given nucleotide position in a genome.
* This method allows a user to specify what type of structures they are interested in (one, many, or all), what nucleotide positions they are interested in,
* and what organism they are interested in.  The output is printed to stdout, where it can be picked up by a php web-form.  Prints out some header information
* and a tab-delimited list of "hits" representing structures that can be found at the given locations.
*
* @author jrwalsh
*/
public class GenePoints {
	private static String server = "tht.vrac.iastate.edu";
	private static int port = 4444;
	
	/*
	 * This webservice will take in 3 arguments. StructureTypes, GenePoints, Organism.
	 * 
	 * StructureTypes types allowed include AllTypes,All-Genes,Rho-Independent-Terminators
	 * GenePoints is a list of integers representing a nucleotide location on the genome
	 */
	public static void main(String[] args) {
		// Collect arguments
		if (args.length<3 || args.length>3) {
			System.out.println("Usage: GenePoints StructureTypes GenePoints Organism");
			System.exit(0);
		}
		
		ArrayList<String> structureTypes = new ArrayList<String>();
		try {
			for (String type : args[0].split(",")) structureTypes.add(type);
		} catch (Exception e) {
			System.out.println("Failed to read genomic structure types. Must be comma separated with no whitespace.");
			System.exit(1);
		}
		
		String[] pointListArgs = null;
		try {
			pointListArgs = args[1].split(",");
		} catch (Exception e) {
			System.out.println("Failed to read genomic points.");
			System.exit(1);
		}
		int[] pointList = new int[pointListArgs.length];
		
		int i = 0;
		for (String point : pointListArgs) {
			try {
				pointList[i] = Integer.parseInt(point);
				i++;
			} catch (Exception e) {
				System.out.println("Genomic points must be comma separated integers with no whitespace.");
				System.exit(1);
			}
		}
		
		String org = args[2];
		
		// Get genomic structures from organism database
		JavacycConnection conn = new JavacycConnection(server,port);
		conn.selectOrganism(org);
		ArrayList<Frame> genomicStructures = getGenomeStructures(conn, structureTypes);
		
		// Print Headers
		System.out.println("Processing points: " + pointList.length);
		System.out.println("POINT\tCOMMON-NAME\tECOCYC-ID\tLEFT-END-POSITION\tRIGHT-END-POSITION");
		
		// Check each point against the list of genomic structures to see which structure might contain this point
		for (int point : pointList) {
			System.out.print(point);
			boolean pointFound = false;
			for (Frame genomicStructure : genomicStructures) {
				try {
					if (inRange(Integer.parseInt(genomicStructure.getSlotValue("LEFT-END-POSITION")), Integer.parseInt(genomicStructure.getSlotValue("RIGHT-END-POSITION")), point)) {
						System.out.println("\t" + genomicStructure.getCommonName() + "\t" + genomicStructure.getLocalID() + "\t" + genomicStructure.getSlotValue("LEFT-END-POSITION") + "\t" + genomicStructure.getSlotValue("RIGHT-END-POSITION"));
						pointFound = true;
					} else if (Integer.parseInt(genomicStructure.getSlotValue("LEFT-END-POSITION")) >= point && point >= Integer.parseInt(genomicStructure.getSlotValue("RIGHT-END-POSITION"))) {
						System.out.println("\t" + genomicStructure.getCommonName() + "\t" + genomicStructure.getLocalID() + "\t" + genomicStructure.getSlotValue("LEFT-END-POSITION") + "\t" + genomicStructure.getSlotValue("RIGHT-END-POSITION"));
						pointFound = true;
					}
				} catch (Exception e){
					// Ignore elements that do not have integer left and right end positions, since these positions are not known
				}
			}
			if (!pointFound) System.out.println("\t\t\t\t");
		}
	}

	public static ArrayList<Frame> getGenomeStructures(JavacycConnection conn, ArrayList<String> structureTypes) {
 		ArrayList<Frame> genomicStructures = new ArrayList<Frame>();
 		try {
			// Process genomic elements for location on genome
 			if (structureTypes.contains("All-Genes") || structureTypes.contains("AllTypes")) {
	 			ArrayList<Frame> genes = conn.getAllGFPInstances("|All-Genes|");
	 			System.out.println("All-Genes: " + genes.size());
	 			genomicStructures.addAll(genes);
 			}
 			
 			if (structureTypes.contains("Rho-Independent-Terminators") || structureTypes.contains("AllTypes")) {
	 			ArrayList<Frame> terminators = conn.getAllGFPInstances("|Rho-Independent-Terminators|");
				System.out.println("Rho-Independent-Terminators: " + terminators.size());
				genomicStructures.addAll(terminators);
 			}
 			
			// Consider adding more elements which do not have a start/end position
//			ArrayList<Frame> bindingSites = conn.getAllGFPInstances("|DNA-Binding-Sites|");
//			System.out.println("DNA-Binding-Sites: " + bindingSites.size());

		} catch (PtoolsErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

 		return genomicStructures;
 	}
	
	private static boolean inRange(int left, int right, int point) {
		if (left <= point && point <= right) {
			return true;
		} else if (left >= point && point >= right) {
			return true;
		} else {
			return false;
		}
	}
}
