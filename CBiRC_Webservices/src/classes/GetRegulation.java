package classes;

import java.util.ArrayList;

import edu.iastate.javacyco.Frame;
import edu.iastate.javacyco.JavacycConnection;

/**
 *
 * @author jrwalsh
 */
public class GetRegulation {

	public static void main(String[] args) {
		if(args.length<3 || args.length>4) {
			System.out.println("Usage: GetFrame SERVER PORT ORGANISM FRAME");
			System.exit(0);
		}
		String server = args[0];
		int port  = Integer.parseInt(args[1]);
		String org = args[2];
		String frameName = args[3];

		JavacycConnection conn = new JavacycConnection(server,port);
		conn.selectOrganism(org);
		try {
			ArrayList<Frame> regs = conn.getAllGFPInstances("|Regulation|");
			for (Frame reg : regs) {
				if (reg.hasSlot("REGULATED-ENTITY") && reg.getSlotValue("REGULATED-ENTITY") != null && reg.hasSlot("REGULATOR") && reg.getSlotValue("REGULATOR") != null) {
					if (reg.getSlotValue("REGULATED-ENTITY").equals(frameName)) {
						System.out.println("Regulated by gene: " + reg.getSlotValue("REGULATOR"));
					}
					if (reg.getSlotValue("REGULATOR").equals(frameName)) {
						System.out.println("This gene regulates: " + reg.getSlotValue("REGULATED-ENTITY"));
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
