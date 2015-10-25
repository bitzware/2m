package com.bitzware.exm.action.panel;

import org.apache.commons.lang.StringUtils;


import com.bitzware.exm.service.MasterServerManager;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Struts action for the master server configuration panel.
 * 
 * @author finagle
 *
 */
public class MasterConfigAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;

	private MasterServerManager masterServerManager;
	
	private String action;
	
	private final String saveAction = "save";
	
	private String message;
	private String messageType;

	private String name;
	private String shutdownScript;
	private String startupScript;
	private String description;
	private String rfidTimeout;
	private String rfidTimeOfEntry;
	
	public String getShutdownScript() {
		return shutdownScript;
	}

	public void setShutdownScript(String shutdownScript) {
		this.shutdownScript = shutdownScript;
	}

	public String getStartupScript() {
		return startupScript;
	}

	public void setStartupScript(String startupScript) {
		this.startupScript = startupScript;
	}


	public MasterServerManager getMasterServerManager() {
		return masterServerManager;
	}

	public void setMasterServerManager(final MasterServerManager masterServerManager) {
		this.masterServerManager = masterServerManager;
	}

	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(final String messageType) {
		this.messageType = messageType;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getRfidTimeout() {
		return rfidTimeout;
	}

	public void setRfidTimeout(final String rfidTimeout) {
		this.rfidTimeout = rfidTimeout;
	}

	public String getRfidTimeOfEntry() {
		return rfidTimeOfEntry;
	}

	public void setRfidTimeOfEntry(String rfidTimeOfEntry) {
		this.rfidTimeOfEntry = rfidTimeOfEntry;
	}

	@Override
	public String execute() throws Exception {
		if (saveAction.equals(action)) {
			// Saving data entered by the user.
			MasterServerManager.Properties configData = new MasterServerManager.Properties();
			if (readParams(configData)) {
				masterServerManager.saveProperties(
						configData.getName(),
						configData.getDescription(),
						configData.getRfidTimeout(),
						configData.getRfidTimeOfEntry());

				message = getText("text.masterconfig.success");
				messageType = "success";
			}
		} else {
			// Displaying data.
			name = masterServerManager.getName();
			description = masterServerManager.getDescription();
			Integer iRfidTimeout = masterServerManager.getRfidTimeout(); 
			if (iRfidTimeout != null) {
				rfidTimeout = String.valueOf(iRfidTimeout);
			}
			Integer iRfidTimeOfEntry = masterServerManager.getRfidTimeOfEntry();
			if (iRfidTimeOfEntry != null) {
				rfidTimeOfEntry = String.valueOf(iRfidTimeOfEntry);
			}
		}
		return SUCCESS;
	}
	
	/**
	 * Validated the data entered by the user and stores it in the specified object.
	 * 
	 * @param configData this object will be filled with data.
	 * @return true on success, false on validation error.
	 */
	private boolean readParams(final MasterServerManager.Properties configData) {
		configData.setName(name);
		configData.setDescription(description);
		
		if (!StringUtils.isEmpty(rfidTimeout)) {
			try {
				Integer iRfidTimeout = Integer.valueOf(rfidTimeout);
				if (iRfidTimeout < 0) {
					message = getText("text.masterconfig.error.rfidtimeout");
					messageType = "error";
					
					return false;
				} else {
					configData.setRfidTimeout(iRfidTimeout);
				}
			} catch (NumberFormatException e) {
				message = getText("text.masterconfig.error.rfidtimeout");
				messageType = "error";
				
				return false;
			}
		}
		
		if (!StringUtils.isEmpty(rfidTimeOfEntry)) {
			try {
				Integer iRfidTimeOfEntry = Integer.valueOf(rfidTimeOfEntry);
				if (iRfidTimeOfEntry < 0) {
					message = getText("text.masterconfig.error.rfidtimeofentry");
					messageType = "error";
					
					return false;
				} else {
					configData.setRfidTimeOfEntry(iRfidTimeOfEntry);
				}
			} catch (NumberFormatException e) {
				message = getText("text.masterconfig.error.rfidtimeofentry");
				messageType = "error";
				
				return false;
			}
		}
		
		return true;
	}

}
