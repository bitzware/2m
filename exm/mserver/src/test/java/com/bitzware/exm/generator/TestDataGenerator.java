package com.bitzware.exm.generator;

import static com.bitzware.exm.generator.TestDataConsts.givenNames;
import static com.bitzware.exm.generator.TestDataConsts.languages;
import static com.bitzware.exm.generator.TestDataConsts.rooms;
import static com.bitzware.exm.generator.TestDataConsts.surnames;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.bitzware.exm.visitordb.model.ActiveStationStatus;
import com.bitzware.exm.visitordb.model.Event;
import com.bitzware.exm.visitordb.model.EventType;
import com.bitzware.exm.visitordb.model.Room;
import com.bitzware.exm.visitordb.model.Station;
import com.bitzware.exm.visitordb.model.Visitor;


public class TestDataGenerator {

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Set<String> ipAddresses = new HashSet<String>();
	private Set<String> macAddresses = new HashSet<String>();

	private long idGenerator = -1;

	private List<Event> events;
	private List<Room> dbRooms;
	private List<Station> stations;
	private List<Visitor> visitors;

	public long getId() {
		return idGenerator--;
	}

	public void generateRooms() {
		dbRooms = new ArrayList<Room>(rooms.length);

		for (int i = 0;i < rooms.length;i++) {
			Room room = new Room();
			room.setId(getId());
			room.setName(rooms[i].name);
			room.setDescription(rooms[i].description);
			room.setFloor(rooms[i].floor);
			room.setStations(new HashSet<Station>());

			dbRooms.add(room);
		}
	}

	public void generateStations() {
		stations = new ArrayList<Station>(100);

		for (int i = 0;i < rooms.length;i++) {
			for (int j = 0;j < rooms[i].stationNames.length;j++) {
				Station station = new Station();
				station.setId(getId());
				station.setName(rooms[i].stationNames[j]);
				station.setDescription(rooms[i].stationNames[j]);
				station.setRoom(dbRooms.get(i));
				station.setIpAddress(generateIpAddress());
				station.setMacAddress(generateMacAddress());

				long now = new Date().getTime();
				long yesterday = new Date(now - 24 * 60 * 60 * 1000).getTime();
				long reg = RandomUtils.nextLong() % (now - yesterday) + yesterday;

				station.setRegisteredOn(new Date(reg));

				station.setLastHeartbeat(new Date(now - RandomUtils.nextLong() % (60 * 1000)));

				station.setStatus(ActiveStationStatus.values()[RandomUtils.nextInt() % 2]);

				dbRooms.get(i).getStations().add(station);

				stations.add(station);
			}
		}
	}

	public void generateVisitors(Calendar startDate, Calendar endDate, int minVisitors, int maxVisitors) {
		visitors = new LinkedList<Visitor>();

		while (startDate.getTimeInMillis() <= endDate.getTimeInMillis()) {
			if (startDate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& startDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				startDate.set(Calendar.HOUR, 10);
				startDate.set(Calendar.MINUTE, 0);

				int visitorsToday = RandomUtils.nextInt() % (maxVisitors - minVisitors + 1) + minVisitors;

				for (int i = 0;i < visitorsToday;i++) {
					Visitor visitor = generateVisitor();

					long arrivalTime = startDate.getTimeInMillis() + RandomUtils.nextLong() % (8 * 60 *  60 * 1000);
					String rfid = generateRfid();

					visitor.setRfid(rfid);
					visitor.setRfidTimestamp(new Date(arrivalTime));

					visitors.add(visitor);
				}
			}

			startDate.add(Calendar.DAY_OF_MONTH, 1);
		}
	}

	public void generateEvents(int minEvents, int maxEvents, long maxInterval, int expiredEventChance, int invalidEventChance) {
		events = new LinkedList<Event>();

		for (Visitor visitor : visitors) {
			int eventsAmount = RandomUtils.nextInt() % (maxEvents - minEvents + 1) + minEvents;

			long currentTime = visitor.getRfidTimestamp().getTime();
			int lastStation = -1;
			
			for (int i = 0;i < eventsAmount;i++) {
				long interval = maxInterval / eventsAmount;
				long time = currentTime + RandomUtils.nextLong() % interval;
				currentTime = time;
				
				int station;
				if (lastStation < 0) {
					station = RandomUtils.nextInt() % stations.size();
				} else {
					Station currentStation = stations.get(lastStation);
					if (RandomUtils.nextInt() % 100 < 40) {
						// Visitor moves to another station.
						
						if (RandomUtils.nextInt() % 100 < 50) {
							// Visitor moves to another room.
							station = RandomUtils.nextInt() % stations.size();
						} else {
							// Visitors stays in the same room.
							Room room = currentStation.getRoom();
							int stationsCount = room.getStations().size();
							int nextStation = RandomUtils.nextInt() % stationsCount;
							int j = 0;
							station = -1;
							for (Station stationIt : room.getStations()) {
								if (j == nextStation) {
									int k = 0;
									for (Station s : stations) {
										if (s == stationIt) {
											station = k;
											break;
										}
										k++;
									}
									break;
								}
								j++;
							}
						}
					} else {
						station = lastStation;
					}
					
				}
				lastStation = station;
				
				int eventTypeInt = RandomUtils.nextInt() % 5;
				EventType eventType = null;
				switch (eventTypeInt) {
				case 0 :
					eventType = EventType.RFID_ACCEPTED;
					break;
				case 1 :
					eventType = EventType.PIR_CHANGED;
					break;
				case 2 :
					eventType = EventType.PROFILE_CHANGED;
					break;
				case 3 :
					eventType = EventType.PAGE_CHANGED;
					break;
				case 4 :
					eventType = EventType.CUSTOM_DIRECTOR_EVENT;
					break;
				default:
					break;
				}

				Event event = new Event();
				event.setId(getId());
				if (eventType != EventType.PIR_CHANGED) {
					event.setVisitor(visitor);
				}
				event.setEventType(eventType);
				event.setStation(stations.get(station));
				event.setTimestamp(new Date(time));
				
				events.add(event);
			}
			
			if (RandomUtils.nextInt() % 100 < expiredEventChance) {
				Event event = new Event();
				event.setId(getId());
				event.setVisitor(visitor);
				event.setEventType(EventType.RFID_EXPIRED);
				event.setStation(stations.get(RandomUtils.nextInt() % stations.size()));
				event.setTimestamp(new Date(visitor.getRfidTimestamp().getTime() + maxInterval + RandomUtils.nextLong() % (20 * 60 * 1000)));
				
				events.add(event);
			}
			
			if (RandomUtils.nextInt() % 100 < invalidEventChance) {
				Event event = new Event();
				event.setId(getId());
				event.setEventType(EventType.RFID_INVALID);
				event.setStation(stations.get(RandomUtils.nextInt() % stations.size()));
				event.setTimestamp(new Date(visitor.getRfidTimestamp().getTime() + maxInterval + RandomUtils.nextLong() % (20 * 60 * 1000)));
				event.setData(generateRfid());
				
				events.add(event);
			}
		}
	}

	public void writeRooms(Writer w) throws IOException {
		for (Room room : dbRooms) {
			w.write("insert into room(id, version, name, description, floor)\n\tvalues(");
			w.write(String.valueOf(room.getId()));
			w.write(", 0, '");
			w.write(room.getName());
			w.write("', '");
			w.write(room.getDescription());
			w.write("', '");
			w.write(room.getFloor());
			w.write("');\n");
		}
	}

	public void writeStations(Writer w) throws IOException {
		for (Station station : stations) {
			w.write("insert into station(id, version, name, description, ipaddress, lastheartbeat, macaddress, registeredon, status, room_id) values\n\t(");
			w.write(String.valueOf(station.getId()));
			w.write(", 0, '");
			w.write(station.getName());
			w.write("', '");
			w.write(station.getDescription());
			w.write("', '");
			w.write(station.getIpAddress());
			w.write("', '");
			w.write(dateFormat.format(station.getLastHeartbeat()));
			w.write("', '");
			w.write(station.getMacAddress());
			w.write("', '");
			w.write(dateFormat.format(station.getRegisteredOn()));
			w.write("', ");
			w.write(String.valueOf(ArrayUtils.indexOf(ActiveStationStatus.values(), station.getStatus())));
			w.write(", ");
			w.write(String.valueOf(station.getRoom().getId()));
			w.write(");\n");
		}
	}

	public void writeVisitors(Writer w) throws IOException {
		for (Visitor visitor : visitors) {
			w.write("insert into visitor(id, version, age, language, level, name, rfid, rfidTimestamp, zoom) values\n\t(");
			w.write(String.valueOf(visitor.getId()));
			w.write(", 0, '");
			w.write(visitor.getAge());
			w.write("', '");
			w.write(visitor.getLanguage());
			w.write("', '");
			w.write(visitor.getLevel());
			w.write("', '");
			w.write(visitor.getName());
			w.write("', '");
			w.write(visitor.getRfid());
			w.write("', '");
			w.write(dateFormat.format(visitor.getRfidTimestamp()));
			w.write("', '");
			w.write(visitor.getZoom());
			w.write("');\n");
		}
	}
	
	public void writeEvents(Writer w) throws IOException {
		for (Event event : events) {
			w.write("insert into event(id, version, data, eventtype, timestamp, station_id, visitor_id) values\n\t(");
			w.write(String.valueOf(event.getId()));
			w.write(", 0, '");
			w.write(event.getData() != null ? event.getData() : "");
			w.write("', ");
			w.write(String.valueOf(ArrayUtils.indexOf(EventType.values(), event.getEventType())));
			w.write(", '");
			w.write(dateFormat.format(event.getTimestamp()));
			w.write("', ");
			w.write(event.getStation() != null ? String.valueOf(event.getStation().getId()) : "null");
			w.write(", ");
			w.write(event.getVisitor() != null ? String.valueOf(event.getVisitor().getId()) : "null");
			w.write(");\n");
		}
	}

	private String generateIpAddress() {
		String address = null;

		do {
			int[] parts = new int[4];
			parts[0] = 10;
			for (int i = 1;i < 4;i++) {
				parts[i] = RandomUtils.nextInt() % 256;
			}

			address = "" + parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
		} while (ipAddresses.contains(address));

		ipAddresses.add(address);
		return address;
	}

	private String generateMacAddress() {
		final char[] chars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
		};

		String address = null;

		do {
			StringBuilder sb = new StringBuilder(17);

			for (int i = 0;i < 6;i++) {
				if (i > 0) {
					sb.append('-');
				}

				sb.append(chars[RandomUtils.nextInt() % chars.length]);
				sb.append(chars[RandomUtils.nextInt() % chars.length]);
			}
			address = sb.toString();
		} while (macAddresses.contains(address));

		macAddresses.add(address);
		return address;
	}

	private String generateRfid() {
		final char[] chars = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
		};

		final int rfidLength = 10;

		StringBuilder sb = new StringBuilder(rfidLength);
		for (int i = 0;i < rfidLength;i++) {
			sb.append(chars[RandomUtils.nextInt() % chars.length]);
		}

		return sb.toString();
	}

	private Visitor generateVisitor() {
		Visitor visitor = new Visitor();

		int language = RandomUtils.nextInt() % languages.length;
		int givenName = RandomUtils.nextInt() % givenNames[language].length;
		int surname = RandomUtils.nextInt() % surnames[language].length;
		int age = RandomUtils.nextInt() % 100 + 4;
		String level = (RandomUtils.nextInt() % 100 > 70 ? "EXPERT" : "NOVICE");
		String zoom = (RandomUtils.nextInt() % 100 > 95 ? "TRUE" : "FALSE");

		visitor.setId(getId());
		visitor.setLanguage(languages[language]);
		visitor.setName(givenNames[language][givenName] + ' ' + surnames[language][surname]);
		visitor.setAge(String.valueOf(age));
		visitor.setLevel(level);
		visitor.setZoom(zoom);

		return visitor;
	}

	public static void main(String[] args) throws Exception {
		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.YEAR, 2009);
		startDate.set(Calendar.MONTH, 10);
		startDate.set(Calendar.DAY_OF_MONTH, 7);

		Calendar endDate = Calendar.getInstance();

		TestDataGenerator generator = new TestDataGenerator();

		generator.generateRooms();
		generator.generateStations();
		generator.generateVisitors(startDate, endDate, 10, 100);
		generator.generateEvents(10, 40, 2 * 60 * 60 * 1000, 2, 1);

//		StringWriter w = new StringWriter();
		Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../visitordb/sql/dummy-master2.sql"), "UTF-8"));

		generator.writeRooms(w);
		w.write('\n');
		generator.writeStations(w);
		w.write('\n');
		generator.writeVisitors(w);
		w.write('\n');
		generator.writeEvents(w);
		w.write('\n');

		w.close();

//		System.out.println(w.toString());
//		for (String[] part : TestDataConsts.surnames) {
//		System.out.println("Part:");

//		for (String value : part) {
//		System.out.println("\"" + value + "\"");
//		}
//		}
	}

}
