package com.bitzware.exm.action.panel;

import java.util.List;

import com.bitzware.exm.service.MasterServerManager;
import com.bitzware.exm.visitordb.model.Station;


/**
 * Struts action for the 'unconfigured stations' panel.
 * 
 * @author finagle
 */
public class UnconfiguredStationsAction extends StationsInfoActionSupport {

//	private final String displayConfigJs1 = "displayStationConfig('";
//	private final String displayConfigJs2 = "')";
	
	private static final long serialVersionUID = 1L;

	private MasterServerManager masterServerManager;
	
	private List<Station> unconfiguredStations;

	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public List<Station> getUnconfiguredStations() {
		return unconfiguredStations;
	}

	public void setUnconfiguredStations(final List<Station> unconfiguredStations) {
		this.unconfiguredStations = unconfiguredStations;
	}

	@Override
	public String execute() throws Exception {
		prepare();
		
		unconfiguredStations = masterServerManager.getUnconfiguredStations();
		
		return SUCCESS;
	}

//	public String generateDisplayConfigJs(String ip) {
//		String ipJs = StringEscapeUtils.escapeJavaScript(ip);
//		StringBuilder sb = new StringBuilder(displayConfigJs1.length() + ipJs.length()
//				+ displayConfigJs2.length());
//		
//		sb.append(displayConfigJs1);
//		sb.append(ipJs);
//		sb.append(displayConfigJs2);
//		
//		return sb.toString();
//	}
}
