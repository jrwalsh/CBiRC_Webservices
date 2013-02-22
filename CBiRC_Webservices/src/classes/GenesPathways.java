package classes;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.Gene;
import edu.iastate.javacyco.JavacycConnection;
import edu.iastate.javacyco.Pathway;
import edu.iastate.javacyco.PtoolsErrorException;

public class GenesPathways {
	private static String server = "tht.vrac.iastate.edu";
	private static int port = 4444;
	
	/*
	 * This webservice will take in 3 arguments. IDs, Direction, and Organism.
	 * 
	 * ID types allowed are ecocycID for either genes or pathways
	 */
	public static void main(String[] args) {
		if (args.length<3 || args.length>3) {
			System.out.println("Usage: GenesPathways IDs PathwaysOfGenes Organism");
			System.exit(0);
		}
		
		String ids = args[0];
		boolean pathwaysOfGenes = (args[1].equalsIgnoreCase("0")) ? false : true;
		String org = args[2];

		JavacycConnection conn = new JavacycConnection(server,port);
		conn.selectOrganism(org);
		try {
			if (pathwaysOfGenes) {
				System.out.println(getPathwaysOfGenes(ids, conn));
			} else {
				System.out.println(getGenesOfPathways(ids, conn));
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static String getPathwaysOfGenes(String ids, JavacycConnection conn) throws PtoolsErrorException {
		String returnString = "";
		for (String id : ids.split(",")) {
			Gene gene;
			try {
				gene = (Gene)Gene.load(conn, id);
			} catch (ClassCastException e) {
				returnString += "Failed to find gene " + id + ".  Please make sure id is a gene id or that lookup direction is correct.\n";
				continue;
			}
			returnString += id + "\t";
			if (gene != null) {
				boolean firstResult = true;
				for (Frame result : gene.getPathways()) {
					if (!firstResult) returnString += ",";
					returnString += result.getLocalID();
					firstResult = false;
				}
			} else {
				System.out.print("Error: Could not find gene.");
			}
			returnString += "\n";
		}
		return returnString;
	}
	
	private static String getGenesOfPathways(String ids, JavacycConnection conn) throws PtoolsErrorException {
		String returnString = "";
		for (String id : ids.split(",")) {
			Pathway pathway;
			try {
				pathway = (Pathway)Pathway.load(conn, id);
			} catch (ClassCastException e) {
				returnString += "Failed to find pathway " + id + ".  Please make sure id is a pathway id or that lookup direction is correct.\n";
				continue;
			}
			returnString += id + "\t";
			if (pathway != null) {
				boolean firstResult = true;
				for (Frame result : pathway.getGenes()) {
					if (!firstResult) returnString += ",";
					returnString += result.getLocalID();
					firstResult = false;
				}
			} else {
				System.out.print("Error: Could not find gene.");
			}
			returnString += "\n";
		}
		return returnString;
	}
}
