package classes;

import java.util.ArrayList;
import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.PtoolsErrorException;

/**
* The AnnotationsByRange class is designed to be a webservice which can provide genomic structures located either within or overlapping a given range on 
* the genome. This method allows a user to specify what type of structures they are interested in (one, many, or all), what nucleotide ranges they are interested
* in, and what organism they are interested in.  The output is printed to stdout, where it can be picked up by a php web-form.  Prints out some header information
* and a tab-delimited list of "hits" representing structures that can be found at the given ranges.
*
* @author jrwalsh
*/
public class AnnotationsByRange {
	private static String server = "tht.vrac.iastate.edu";
	private static int port = 4444;
	
	/*
	 * This webservice will take in 3 arguments. StructureTypes, Ranges, Organism.
	 * 
	 * StructureTypes types allowed include AllTypes,All-Genes,Rho-Independent-Terminators
	 * Ranges is a list of integers representing a nucleotide locations on the genome, given in comma separated pairs, semi-colon separated ranges.
	 */
	public static void main(String[] args) {
		// Collect arguments
		if (args.length<3 || args.length>3) {
			System.out.println("Usage: AnnotationsByRange StructureTypes Ranges Organism");
			System.exit(0);
		}
		
		ArrayList<String> structureTypes = new ArrayList<String>();
		try {
			for (String type : args[0].split(",")) structureTypes.add(type);
		} catch (Exception e) {
			System.out.println("Failed to read genomic structure types. Must be comma separated with no whitespace.");
			System.exit(1);
		}
		
		String[] rangePairs = null;
		try {
			rangePairs = args[1].split(";");
		} catch (Exception e) {
			System.out.println("Failed to read genomic ranges.");
			System.exit(1);
		}
		
		ArrayList<GenomicRange> genomicRanges = new ArrayList<GenomicRange>();
		for (String rangePair : rangePairs) {
			try {
				String[] pair =  rangePair.split(",");
				genomicRanges.add(new GenomicRange(Integer.parseInt(pair[0]), Integer.parseInt(pair[1])));
			} catch (Exception e) {
				System.out.println("Failed to parse range pairs. Genomic points must be comma separated integers with no whitespace.");
				System.exit(1);
			}
		}
		
		String org = args[2];
		
		// Get genomic structures from organism database
		JavacycConnection conn = new JavacycConnection(server,port);
		conn.selectOrganism(org);
		ArrayList<Frame> genomicStructures = getGenomeStructures(conn, structureTypes);
		ArrayList<GenomicRange> structureRanges = new ArrayList<GenomicRange>();
		for (Frame genomicStructure : genomicStructures) {
			try {
				int start = Integer.parseInt(genomicStructure.getSlotValue("LEFT-END-POSITION"));
				int end = Integer.parseInt(genomicStructure.getSlotValue("RIGHT-END-POSITION"));
				if (start <= end) structureRanges.add(new GenomicRange(start, end, genomicStructure));
				else structureRanges.add(new GenomicRange(end, start));
			} catch (Exception e){
				// Ignore elements that do not have integer left and right end positions, since these positions are not known
			}
		}
		
		// Print Headers
		System.out.println("Processing ranges: " + genomicRanges.size());
		System.out.println("RANGE-START\tRANGE-END\tCOMMON-NAME\tECOCYC-ID\tLEFT-END-POSITION\tRIGHT-END-POSITION");
		
		// Check each range against the list of genomic structures to see which structure might overlap this range
		for (GenomicRange genomicRange : genomicRanges) {
			System.out.print(genomicRange.getStart() + "\t" + genomicRange.getEnd());
			boolean matchFound = false;
			for (GenomicRange structureRange : structureRanges) {
				if (genomicRange.overLap(structureRange)) {
					try {
						if (matchFound) System.out.print("\t");
						System.out.println("\t" + structureRange.getFrame().getCommonName() + "\t" + structureRange.getFrame().getLocalID() + "\t" + structureRange.getFrame().getSlotValue("LEFT-END-POSITION") + "\t" + structureRange.getFrame().getSlotValue("RIGHT-END-POSITION"));
					} catch (PtoolsErrorException e) {
						e.printStackTrace();
					}
					matchFound = true;
				}
			}
			if (!matchFound) System.out.println("\t\t\t\t\t");
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
}
