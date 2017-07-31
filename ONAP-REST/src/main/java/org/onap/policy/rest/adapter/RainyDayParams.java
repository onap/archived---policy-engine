package org.onap.policy.rest.adapter;

import java.util.ArrayList;
import java.util.List;

public class RainyDayParams {
	private String serviceType;
	private String vnfType;
	private String bbid;
	private String workstep;
	private ArrayList<Object> treatmentTableChoices;
	private List<String> errorcode;
	private List<String> treatment;

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	/**
	 * @return the vnfType
	 */
	public String getVnfType() {
		return vnfType;
	}
	/**
	 * @param vnfType the vnfType to set
	 */
	public void setVnfType(String vnfType) {
		this.vnfType = vnfType;
	}
	/**
	 * @return the workstep
	 */
	public String getWorkstep() {
		return workstep;
	}
	/**
	 * @param workstep the workstep to set
	 */
	public void setWorkstep(String workstep) {
		this.workstep = workstep;
	}
	/**
	 * @return the bbid
	 */
	public String getBbid() {
		return bbid;
	}
	/**
	 * @param bbid the bbid to set
	 */
	public void setBbid(String bbid) {
		this.bbid = bbid;
	}
	/**
	 * @return the treatmentTableChoices
	 */
	public ArrayList<Object> getTreatmentTableChoices() {
		return treatmentTableChoices;
	}
	/**
	 * @param treatmentTableChoices the treatmentTableChoices to set
	 */
	public void setTreatmentTableChoices(ArrayList<Object> treatmentTableChoices) {
		this.treatmentTableChoices = treatmentTableChoices;
	}
	/**
	 * @return the errorcode
	 */
	public List<String> getErrorcode() {
		return errorcode;
	}
	/**
	 * @param errorcode the errorcode to set
	 */
	public void setErrorcode(List<String> errorcode) {
		this.errorcode = errorcode;
	}
	/**
	 * @return the treatment
	 */
	public List<String> getTreatment() {
		return treatment;
	}
	/**
	 * @param treatment the treatment to set
	 */
	public void setTreatment(List<String> treatment) {
		this.treatment = treatment;
	}
	
}
