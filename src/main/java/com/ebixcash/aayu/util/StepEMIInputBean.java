
package com.ebixcash.aayu.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Bean Object For Step EMI Input
 * @author sheetal.parihar
 * @since 1.0
 */
public class StepEMIInputBean
{
	private int		stepmode  	= 0;
	private int 	frm_month 	= 0;
	private int 	to_month  	= 0;
	private int 	stepbasis 	= 0;	// 0:Step by amount,1:Step by %,2:Step by Given Step EMI
	private double	stepby	  	= 0.0d;
	private int 	stepadjust	= 0;
	private int 	stepmodeval = 0;	//Is set to 1 if Step up and -1 if Step Down
	
	@JsonProperty("R1")
    private JsonNode R1;

	public int getStepadjust() {
		return stepadjust;
	}

	public void setStepadjust(int stepadjust) {
		this.stepadjust = stepadjust;
	}

	public int getStepmode() {
		return stepmode;
	}

	public void setStepmode(int stepmode) {
		this.stepmode = stepmode;

		if(stepmode == 0)
			stepmodeval = 1;
		else if(stepmode == 1)
			stepmodeval = -1;
	}

	public int getFrm_month() {
		return frm_month;
	}

	public void setFrm_month(int frm_month) {
		this.frm_month = frm_month;
	}

	public int getTo_month() {
		return to_month;
	}

	public void setTo_month(int to_month) {
		this.to_month = to_month;
	}

	public int getStepbasis() {
		return stepbasis;
	}

	public void setStepbasis(int stepbasis) {
		this.stepbasis = stepbasis;
	}

	public double getStepby() {
		return stepby;
	}

	public void setStepby(double stepby) {
		this.stepby = stepby;
	}

	public int getStepmodeval() {
		return stepmodeval;
	}

}
