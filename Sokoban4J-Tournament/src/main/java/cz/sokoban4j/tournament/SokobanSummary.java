package cz.sokoban4j.tournament;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.utils.maps.LazyMap;
import cz.sokoban4j.simulation.SokobanResult;
import cz.sokoban4j.simulation.SokobanResult.SokobanResultType;
import cz.sokoban4j.tournament.utils.CSV;
import cz.sokoban4j.tournament.utils.CSV.CSVRow;

public class SokobanSummary {

	public static class SolvedLevel {
		
		public String id;
		
		public double solutionTimeMillis;
		
		public int solutionSteps;
		
		public SolvedLevel(String key) {
			this.id = key;
		}
		
	}
	
	public static class AgentSummary {
		
		public String id;
		
		public Map<String, SolvedLevel> solved = new LazyMap<String, SolvedLevel>() {
			@Override
			protected SolvedLevel create(String key) {
				return new SolvedLevel(key);
			}
		};
				
		public AgentSummary(String key) {
			this.id = key;
		}
		
		public double getTotalTimeMillis() {
			double result = 0;
			for (SolvedLevel solvedLevel : solved.values()) {
				result += solvedLevel.solutionTimeMillis;
			}
			return result;
		}
		
		public int getTotalSteps() {
			int result = 0;
			for (SolvedLevel solvedLevel : solved.values()) {
				result += solvedLevel.solutionSteps;
			}
			return result;
		}
		
	}
	
	private File file;
	
	private Map<String, AgentSummary> agents = new LazyMap<String, AgentSummary>() {
		@Override
		protected AgentSummary create(String key) {
			return new AgentSummary(key);
		}
	};

	public SokobanSummary(File file) {
		this.file = file;
	}
	
	public void summarize(File outputTo) {
		try {
			CSV csv = new CSV(file, ";", true);
			
			for (CSVRow row : csv.rows) {
				if (!row.getString("result").toLowerCase().equals(SokobanResultType.VICTORY.name().toLowerCase())) continue;				
				AgentSummary agent = agents.get(row.getString("id"));
				SolvedLevel level = agent.solved.get(row.getString("levelFile") + " / " + row.getString("levelNumber"));
				level.solutionSteps = row.getInt("steps");
				level.solutionTimeMillis = row.getInt("playTimeMillis");
			}
			
			output(outputTo);
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to read CSV from: " + file.getAbsolutePath());
		}
	}

	private void output(File resultFile) {
		System.out.println("Outputting summary into: " + resultFile.getAbsolutePath());
		FileOutputStream output = null;		
		boolean header = !resultFile.exists();
		try {
			output = new FileOutputStream(resultFile, false);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to open file: " + resultFile.getAbsolutePath(), e);
		}
		try {
			PrintWriter writer = new PrintWriter(output);
		
			if (header) {
				writer.println("id;levelsSolved;totalSteps;totalSolutionTimeMillis");
			}
			
			List<AgentSummary> agentList = new ArrayList<AgentSummary>(agents.values()); 
			Collections.sort(agentList, new Comparator<AgentSummary>() {
				@Override
				public int compare(AgentSummary o1, AgentSummary o2) {
					return o2.solved.size() - o1.solved.size();
				}				
			});
			
			for (AgentSummary summary : agentList) {
				writer.println(summary.id + ";" + summary.solved.size() + ";" + summary.getTotalSteps() + ";" + summary.getTotalTimeMillis());
			}
			
			writer.flush();
			writer.close();			
		} finally {
			try {
				output.close();
			} catch (IOException e) {
			}
		}		
	}
	
}
