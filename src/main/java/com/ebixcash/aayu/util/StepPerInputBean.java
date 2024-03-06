package com.ebixcash.aayu.util;

public class StepPerInputBean {
	int		frm_inst  = 0;
	int 	to_inst  = 0;
	double 	P_R   = 0.0d;
	double 	P_PER  = 0.0d;
	int 	P_F   = 0;
	int     P_T_IN=0;
	public int getP_T_IN() {
		return P_T_IN;
	}
	public void setP_T_IN(int p_t_in) {
		P_T_IN = p_t_in;
	}
	public int getFrm_inst() {
		return frm_inst;
	}
	public void setFrm_inst(int frm_inst) {
		this.frm_inst = frm_inst;
	}
	public int getTo_inst() {
		return to_inst;
	}
	public void setTo_inst(int to_inst) {
		this.to_inst = to_inst;
	}
	public double getP_R() {
		return P_R;
	}
	public void setP_R(double p_r) {
		P_R = p_r;
	}
	public double getP_PER() {
		return P_PER;
	}
	public void setP_PER(double p_per) {
		P_PER = p_per;
	}
	public int getP_F() {
		return P_F;
	}
	public void setP_F(int p_f) {
		P_F = p_f;
	}
	
}
