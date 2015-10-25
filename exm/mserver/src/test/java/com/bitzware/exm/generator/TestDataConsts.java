package com.bitzware.exm.generator;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class TestDataConsts {
	
	public static class RoomInfo {
		public String name;
		public String description;
		public String floor;
		
		public String[] stationNames;

		public RoomInfo(String name, String description, String floor,
				String[] stationNames) {
			this.name = name;
			this.description = description;
			this.floor = floor;
			this.stationNames = stationNames;
		}
		
	}
	
	private static RoomInfo r(String name, String description, String floor, String[] stationNames) {
		return new RoomInfo(name, description, floor, stationNames);
	}
	
	public static final RoomInfo[] rooms = new RoomInfo[] {
			r("0/6", "Zelazowa Wola", "0", new String[] { "Interactive Poland Map", "Virtual Book"}),
			r("0/7", "Warsaw - 1830 Uprising", "0", new String[] { "Individual Sound Station 1", "Individual Sound Station 2", "Individual Sound Station 3", "Individual Sound Station 4", "Multimedia Panel With Sound 1", "Multimedia Panel With Sound 2", "Multimedia Panel With Sound 3", "Multimedia Panel Without Sound", "Videowall" }),
			r("0/8", "Micolaj Chopin Drawing Room in Warsaw", "0", new String[] {"Interactive Monitor", "Interactive Sound Station", "Exercise Book", "Interactive Music Score" }),
			r("0/5", "Children Area", "0", new String[] { "Interactive Table 1", "Interactive Table 2", "Sound Station 1", "Sound Station 2", "Sound Station 3" }),
			r("1/5", "Chopin''s Drawing Room in Paris", "1", new String[] { "Interactive Lighting System 1", "Interactive Lighting System 2", "Virtual Book", "Interactive Monitor 253", "Interactive Monitor 254", "Interactive Monitor 247", "Interactive Monitor 248", "Loop Monitor" }),
			r("1/4", "Women", "1", new String[] { "Interactive Monitor (with loudspeaker) 1", "Interactive Monitor (with loudspeaker) 2", "Interactive Monitor (with loudspeaker) 3", "Interactive Monitor (with loudspeaker) 4",  "Interactive Monitor (with headphone) 1", "Interactive Monitor (with headphone) 2" }),
			r("1/6", "Nohant", "1", new String[] { "Interactive Monitor 327", "Interactive Monitor 328", "Interactive Monitor 523", "Interactive Monitor 528", "Interactive Drawers", "Interactive Monitor" }),
			r("1/7", "Interactive Sound Station", "1", new String[] { "Interactive Monitor 1", "Interactive Monitor 2", "Interactive Monitor 3" }),
			r("2/5", "Personality - Europe", "2", new String[] { "Interactive Europe Map", "Interactive Monitor 1", "Interactive Monitor 2", "Interactive Monitor 3", "Interactive Monitor 4", "Interactive Monitor 5", "Interactive Monitor 6", "Interactive Monitor 7", "Interactive Monitor 8", "Interactive Sound Station 1", "Interactive Sound Station 2", "Interactive Sound Station 3", "Interactive Sound Station 4", "Interactive Sound Station 5", "Interactive Sound Station 6", "Interactive Sound Station 7", "Interactive Sound Station 8", "Interactive Sound Station 9", "Interactive Sound Station 10" }),
			r("2/6", "Death", "2", new String[] { "Interactive Lighting System 1", "Interactive Lighting System 2", "Loop Monitor" }),
			r("1/20", "Pianists - Imitators", "-1", new String[] { "Interactive Music Stand", "\"Chopin as Pianist\" interaction", "Virtual Book", "Interactive Sound Station", "Interactive Background Music" }),
			r("1/19", "The Composer", "-1", new String[] { "Interactive System for the Original", "Interactive Music Book 1", "Interactive Music Book 2", "Interactive Music Book 3", "Interactive Music Book 4", "Interactive Music Book 5", "Interactive Music Book 6", "Interactive Music Book 7", "Interactive Music Book 8", "Interactive Music Book 9", "Interactive Music Book 10", "Interactive Music Book 11", "Interactive Music Book 12", "Interactive Music Book 13", "Interactive Music Book 14" }),
			r("1/20a", "Projection Room", "-1", new String[] { "Video Station 1", "Video Station 2" }),
			r("1/19a", "Projection Room", "-1", new String[] { "Video Station 1", "Video Station 2" })
	};
	
	public static final String[] languages = new String[] {
		"pl",
		"ge",
		"ru",
		"en",
		"cz"
	};
	public static final String[][] givenNames = readStrings("/com/bitzware/exm/util/generator/given-names.txt");
	public static final String[][] surnames = readStrings("/com/bitzware/exm/util/generator/surnames.txt");
	
	private static String[][] readStrings(String fileName) {
		try {
			InputStream input = TestDataConsts.class.getResource(fileName).openStream();
			String contents = IOUtils.toString(input, "UTF-8");
			String[] parts = contents.split("\\n");
			
			String[][] result = new String[parts.length][];
			for (int i = 0;i < parts.length;i++) {
				result[i] = parts[i].split(" ");
				
				for (int j = 0;j < result[i].length;j++) {
					result[i][j] = result[i][j].trim();
				}
			}
			
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
