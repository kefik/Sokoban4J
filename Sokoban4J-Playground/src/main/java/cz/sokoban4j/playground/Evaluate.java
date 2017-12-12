package cz.sokoban4j.playground;

import com.martiansoftware.jsap.JSAPException;

import cz.sokoban4j.simulation.agent.IAgent;
import cz.sokoban4j.tournament.SokobanTournamentConsole;

public class Evaluate {

	public static String[] LEVELS = new String[] { 
			"../Sokoban4J/levels/Easy/;all",
			"../Sokoban4J/levels/sokobano.de/A.K.K._Informatika.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Andre Bernier.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Andre_Bernier.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Aymeric_du_Peloux_1_Minicosmos.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Aymeric_du_Peloux_2_Microcosmos.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Aymeric_du_Peloux_3_Nabokosmos.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Aymeric_du_Peloux_4_Picokosmos.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Aymeric_du_Peloux_5_Cosmopoly.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Blazz.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Blazz2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Brian_Damgaard_YASGen.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Brian_Kent_Aenigma.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Bruno_Druille.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Christian_Eggermont.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Cosmonotes.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Crazy_Monk_-_Disciple.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Mas_Microban_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Microban_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_III_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_II_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_IV_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_VII_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_VI_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_V_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DWS_Sasquatch_arr.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Microban.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Microban_II.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Microban_III.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_I.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_II.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_III.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_IV.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_V.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_VI.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_VII.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_X.sok;all", 
			"../Sokoban4J/levels/sokobano.de/DavidWSkinner_Sasquatch_XI.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_Bagatelle.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_Bagatelle2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_Cantrip.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_Cantrip2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_Maelstrom.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_dh1.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_dh2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_Holland_dh5.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_W_Skinner_-_Microban_IV.sok;all", 
			"../Sokoban4J/levels/sokobano.de/David_W_Skinner_-_Sasquatch_XII.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Demons_&_Diamonds.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Flatland.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Frantisek_Pokorny.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_2001.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_2002.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_Comet.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_Remodel_Club.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_Special.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_Star.sok;all", 
			"../Sokoban4J/levels/sokobano.de/GRIGoRusha_Sun.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Haikemono.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Jacques_Duthen_Kids.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Jacques_Duthen_Sokogen.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Jean-Pierre_Kent.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kaufmann_Cubes_&_Tubes.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Keijo_Sopuli.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kenya_Maruyama.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kenya_Maruyama_[prototypes].sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_1.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_10.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_11.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_12.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_13.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_14.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_15.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_16.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_17.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_18.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_19.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_20.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_21.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_3.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_4.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_5.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_6.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_7.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_8.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Kevin_B_Reilly_9.sok;all", 
			"../Sokoban4J/levels/sokobano.de/LOMA.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Lee_J_Haywood_SokEvo.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Lee_J_Haywood_SokHard.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Marcus_Palstra.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Myriocosmos.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Original_51-90_remodeled.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Premysl_Zika.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Primus_gradus_ad_Olympo.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Pufiban.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sasquatch_IX.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sasquatch_VIII.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Serg_Belyaev_1.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Serg_Belyaev_2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Serg_Belyaev_3.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Serg_Belyaev_4.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Serg_Belyaev_5.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Serg_Belyaev_6.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Small_chessboards.sok;all", 
			"../Sokoban4J/levels/sokobano.de/SokoMind.sok;all", 
			"../Sokoban4J/levels/sokobano.de/SokoStation.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad,_Sharpen.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_1.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_10.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_11.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_12.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_13.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_14.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_15.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_16.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_17.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_18.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_19.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_2.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_20.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_3.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_4.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_5.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_6.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_7.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_8.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Sven_Egevad_9.sok;all", 
			"../Sokoban4J/levels/sokobano.de/TianLang.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Warehouse_Volume_I.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Yoshio_Murase_autogenerated.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Yoshio_Murase_handmade.sok;all", 
			"../Sokoban4J/levels/sokobano.de/Zbigniew_Kornas.sok;all", 
	};
	
	private static String getAllLevels() {
		StringBuffer result = new StringBuffer();
		for (String level : LEVELS) {
			result.append(";");
			result.append(level);
		}
		result.delete(0, 1);
		return result.toString();
	}

	
	private static void evaluateLevels(Class<IAgent> agentClass, boolean visualize) {
		String levels = getAllLevels();
		
		String ps = System.getProperty("path.separator");
		if (ps == null) {
			String os = System.getProperty("os.name");			
			if (os == null) {
				ps = ";";
			} else {
				os = os.toLowerCase();
				if (os.indexOf("linux") >= 0 || os.indexOf("mac") >= 0) {
					ps = ":";
				} else {
					ps = ";";
				}
			}
		}
		
		try {
			SokobanTournamentConsole.main(
				new String[] {
					"-l", levels,
					"-r", "results/results-" + System.currentTimeMillis() + ".csv",
					"-t", "" + (60*1000),
					"-a", agentClass.getName(),
					"-v", "" + visualize, 
					"-i", agentClass.getSimpleName(),
					"-j", "-cp ./target/classes"+ps+"../Sokoban4J-Tournament/target/classes"+ps+"../Sokoban4J/target/classes"+ps+"../Sokoban4J-Agents/target/classes"+ps+"../Sokoban4J-Tournament/libs/jsap-2.1.jar"+ps+"../Sokoban4J-Tournament/libs/process-execution-3.7.0.jar"+ps+"../Sokoban4J-Tournament/libs/xstream-1.3.1.jar"
				}
			);
		} catch (JSAPException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	
	private static void evaluateTournament(Class<IAgent> agentClass, boolean visualize) {
		evaluateLevels(agentClass, visualize);
	}
	
	public static void main(String[] args) {
		Class agentClass = DFSAgent.class;
		
		boolean visualize = true;
		
		evaluateTournament((Class<IAgent>)agentClass, visualize);
	}
	
}
