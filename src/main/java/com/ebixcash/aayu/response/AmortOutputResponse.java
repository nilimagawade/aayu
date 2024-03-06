package com.ebixcash.aayu.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO Object For Amortization Response Output
 * @author prabhanshu.sharma
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class AmortOutputResponse {

	@JsonProperty(value = "NO")
	private String installment;
	@JsonProperty(value = "CYLDT")
	private String cycleDate;
	@JsonProperty(value = "EMI")
	private double roundemi;
	@JsonProperty(value = "PRIEMI")
	private double roundprincipal;
	@JsonProperty(value = "INTEMI")
	private double roundinterest;
	@JsonProperty(value = "OPBAL")
	private double roundopen;
	@JsonProperty(value = "CLSBAL")
	private double roundclose;
	@JsonProperty(value = "RESTBAL")
	private double roundrestopenbal;
	
	@JsonProperty(value = "SKPINT")
	private double skipPartialInt;
	@JsonProperty(value = "SKPINTONINT")
	private double skipPartialIntonInt;
	@JsonProperty(value = "SKPINTOPEN")
	private double skipPartialIntOpen;
	@JsonProperty(value = "SKPINTCLS")
	private double skipPartialIntClose;
	@JsonProperty(value = "INTBEFPARTSKIP")
	private double skipIntBeforePartial;

	private HashMap component = null;
	private HashMap comp = null;
	@JsonProperty(value = "ADJEMI")
	private double  adj_installment;
	@JsonProperty(value = "ACCI")
	private double  accruedinterest;

	@JsonProperty(value = "EOMDT")
	private String endofMonthDate;
	
	@JsonProperty(value = "I_ACCI")
	private double intaccruedinterest;
	@JsonProperty(value = "I_OPBAL")
	private double intopeningBalance;
	@JsonProperty(value = "I_INTEMI")
	private double intInterestEMI;
	@JsonProperty(value = "I_PRIEMI")
	private double intprincipalEMI;
	@JsonProperty(value = "I_CLSBAL")
	private double intclosingBalance;
	@JsonProperty(value = "I_FIN")
	private double financialFee;
	@JsonProperty(value = "I_DEP")
	private double depreciationFee;
	
	public AmortOutputResponse(){}

	public String getCycleDate() {
		return cycleDate;
	}

	public void setCycleDate(String cycleDate) {
		this.cycleDate = cycleDate;
	}

	public double getRoundemi() {
		return roundemi;
	}

	public void setRoundemi(double roundemi) {
		this.roundemi = roundemi;
	}

	public double getRoundprincipal() {
		return roundprincipal;
	}

	public void setRoundprincipal(double roundprincipal) {
		this.roundprincipal = roundprincipal;
	}

	public double getRoundinterest() {
		return roundinterest;
	}

	public void setRoundinterest(double roundinterest) {
		this.roundinterest = roundinterest;
	}

	public double getRoundopen() {
		return roundopen;
	}

	public void setRoundopen(double roundopen) {
		this.roundopen = roundopen;
	}

	public double getRoundclose() {
		return roundclose;
	}

	public void setRoundclose(double roundclose) {
		this.roundclose = roundclose;
	}

	public double getRoundrestopenbal() {
		return roundrestopenbal;
	}

	public void setRoundrestopenbal(double roundrestopenbal) {
		this.roundrestopenbal = roundrestopenbal;
	}

	public double getSkipPartialInt() {
		return skipPartialInt;
	}

	public void setSkipPartialInt(double skipPartialInt) {
		this.skipPartialInt = skipPartialInt;
	}

	public double getSkipPartialIntonInt() {
		return skipPartialIntonInt;
	}

	public void setSkipPartialIntonInt(double skipPartialIntonInt) {
		this.skipPartialIntonInt = skipPartialIntonInt;
	}

	public double getSkipPartialIntOpen() {
		return skipPartialIntOpen;
	}

	public void setSkipPartialIntOpen(double skipPartialIntOpen) {
		this.skipPartialIntOpen = skipPartialIntOpen;
	}

	public double getSkipPartialIntClose() {
		return skipPartialIntClose;
	}

	public void setSkipPartialIntClose(double skipPartialIntClose) {
		this.skipPartialIntClose = skipPartialIntClose;
	}

	public double getSkipIntBeforePartial() {
		return skipIntBeforePartial;
	}

	public void setSkipIntBeforePartial(double skipIntBeforePartial) {
		this.skipIntBeforePartial = skipIntBeforePartial;
	}

	public String getInstallment() {
		return installment;
	}

	public void setInstallment(String installment) {
		this.installment = installment;
	}

	public HashMap getComponent() {
		return component;
	}

	public void setComponent(HashMap component) {
		this.component = component;
	}

	public HashMap getComp() {
		return comp;
	}

	public void setComp(HashMap comp) {
		this.comp = comp;
	}

	public double getAdj_installment() {
		return adj_installment;
	}

	public void setAdj_installment(double adj_installment) {
		this.adj_installment = adj_installment;
	}

	public double getAccruedinterest() {
		return accruedinterest;
	}

	public void setAccruedinterest(double accruedinterest) {
		this.accruedinterest = accruedinterest;
	}

	public String getEndofMonthDate() {
		return endofMonthDate;
	}

	public void setEndofMonthDate(String endofMonthDate) {
		this.endofMonthDate = endofMonthDate;
	}

	public double getIntaccruedinterest() {
		return intaccruedinterest;
	}

	public void setIntaccruedinterest(double intaccruedinterest) {
		this.intaccruedinterest = intaccruedinterest;
	}

	public double getIntopeningBalance() {
		return intopeningBalance;
	}

	public void setIntopeningBalance(double intopeningBalance) {
		this.intopeningBalance = intopeningBalance;
	}

	public double getIntInterestEMI() {
		return intInterestEMI;
	}

	public void setIntInterestEMI(double intInterestEMI) {
		this.intInterestEMI = intInterestEMI;
	}

	public double getIntprincipalEMI() {
		return intprincipalEMI;
	}

	public void setIntprincipalEMI(double intprincipalEMI) {
		this.intprincipalEMI = intprincipalEMI;
	}

	public double getIntclosingBalance() {
		return intclosingBalance;
	}

	public void setIntclosingBalance(double intclosingBalance) {
		this.intclosingBalance = intclosingBalance;
	}

	public double getFinancialFee() {
		return financialFee;
	}

	public void setFinancialFee(double financialFee) {
		this.financialFee = financialFee;
	}

	public double getDepreciationFee() {
		return depreciationFee;
	}

	public void setDepreciationFee(double depreciationFee) {
		this.depreciationFee = depreciationFee;
	}

	@Override
	public String toString() {
		return "AmortOutputResponse [cycleDate=" + cycleDate + ", roundemi=" + roundemi + ", roundprincipal="
				+ roundprincipal + ", roundinterest=" + roundinterest + ", roundopen=" + roundopen + ", roundclose="
				+ roundclose + ", roundrestopenbal=" + roundrestopenbal + ", skipPartialInt=" + skipPartialInt
				+ ", skipPartialIntonInt=" + skipPartialIntonInt + ", skipPartialIntOpen=" + skipPartialIntOpen
				+ ", skipPartialIntClose=" + skipPartialIntClose + ", skipIntBeforePartial=" + skipIntBeforePartial
				+ ", installment=" + installment + ", component=" + component + ", comp=" + comp + ", adj_installment="
				+ adj_installment + ", accruedinterest=" + accruedinterest + ", endofMonthDate=" + endofMonthDate
				+ ", intaccruedinterest=" + intaccruedinterest + ", intopeningBalance=" + intopeningBalance
				+ ", intInterestEMI=" + intInterestEMI + ", intprincipalEMI=" + intprincipalEMI + ", intclosingBalance="
				+ intclosingBalance + ", financialFee=" + financialFee + ", depreciationFee=" + depreciationFee + "]";
	}
	
	public Map<String, Object> toResponse() {
        Map<String, Object> resultMap = new HashMap<>();

        if (installment != null) {
            resultMap.put("NO", installment);
        }
        if (cycleDate != null) {
            resultMap.put("CYLDT", cycleDate);
        }
        if (roundemi != 0.0) {
            resultMap.put("EMI", roundemi);
        }
        if (roundprincipal != 0.0) {
            resultMap.put("PRIEMI", roundprincipal);
        }
        if (roundinterest != 0.0) {
            resultMap.put("INTEMI", roundinterest);
        }
        if (roundopen != 0.0) {
            resultMap.put("OPBAL", roundopen);
        }
        if (roundclose != 0.0) {
            resultMap.put("CLSBAL", roundclose);
        }
        if (roundrestopenbal != 0.0) {
            resultMap.put("RESTBAL", roundrestopenbal);
        }
        if (skipPartialInt != 0.0) {
            resultMap.put("SKPINT", skipPartialInt);
        }
        if (skipPartialIntonInt != 0.0) {
            resultMap.put("SKPINTONINT", skipPartialIntonInt);
        }
        if (skipPartialIntOpen != 0.0) {
            resultMap.put("SKPINTOPEN", skipPartialIntOpen);
        }
        if (skipPartialIntClose != 0.0) {
            resultMap.put("SKPINTCLS", skipPartialIntClose);
        }
        if (skipIntBeforePartial != 0.0) {
            resultMap.put("INTBEFPARTSKIP", skipIntBeforePartial);
        }
        if (adj_installment != 0.0) {
            resultMap.put("ADJEMI", adj_installment);
        }
        if (accruedinterest != 0.0) {
            resultMap.put("ACCI", accruedinterest);
        }
        if (endofMonthDate != null) {
            resultMap.put("EOMDT", endofMonthDate);
        }
        if (intaccruedinterest != 0.0) {
            resultMap.put("I_ACCI", intaccruedinterest);
        }
        if (intopeningBalance != 0.0) {
            resultMap.put("I_OPBAL", intopeningBalance);
        }
        if (intInterestEMI != 0.0) {
            resultMap.put("I_INTEMI", intInterestEMI);
        }
        if (intprincipalEMI != 0.0) {
            resultMap.put("I_PRIEMI", intprincipalEMI);
        }
        if (intclosingBalance != 0.0) {
            resultMap.put("I_CLSBAL", intclosingBalance);
        }
        if (financialFee != 0.0) {
            resultMap.put("I_FIN", financialFee);
        }
        if (depreciationFee != 0.0) {
            resultMap.put("I_DEP", depreciationFee);
        }

        return resultMap;
    }

}
