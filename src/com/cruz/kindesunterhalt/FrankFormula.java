package com.cruz.kindesunterhalt;

import java.text.NumberFormat;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author rosborne,acruz
 */
public class FrankFormula {
	
	public static void main(String[] args) throws IOException {

		InputStream input = FrankFormula.class.getClassLoader().getResourceAsStream("config.properties");
		Properties prop = new Properties();
		String propFileName = "config.properties";
		if (input != null) {
			prop.load(input);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}		
		
		//Calculate Salarys and Salary Net Available
		double salary1 = Double.valueOf((String) prop.get("gehaltVater"));
		double salary2 = Double.valueOf((String) prop.get("gehaltMutter"));
		double salaryTotal = (salary1 + salary2);
		int salaryDiff1 = (int) Math.round((salary1 / salaryTotal) * 100.00);
		int salaryDiff2 = (int) Math.round((salary2 / salaryTotal) * 100.00);

		// Calculate the total child allowance
		double totalkindergeld = 0;
		double dtKinderTotalChildAllowance = 0;
		ArrayList<Double> kinder = new ArrayList<Double>();
		String kindesunterhalt[] = prop.get("kindesunterhalt").toString().split(",");
		String kindermehrbedarf[] = prop.get("kindermehrbedarf").toString().split(",");
		String kindergeld[] = prop.get("kindergeld").toString().split(",");

		for (int i = 0; i < kindesunterhalt.length; i++) {
			dtKinderTotalChildAllowance += Double.valueOf(kindesunterhalt[i]) + Double.valueOf(kindermehrbedarf[i]);
			kinder.add(Double.valueOf(kindesunterhalt[i]) + Double.valueOf(kindermehrbedarf[i]));
			totalkindergeld += Double.valueOf(kindergeld[i]);
		}		

		double salary1diffAmount = (dtKinderTotalChildAllowance * salaryDiff1 / 100f);
		double salary2diffAmount = (dtKinderTotalChildAllowance * salaryDiff2 / 100f);

		prt("Nettogehalt Vater ", fmtEU(salary1));
		prt("Nettogehalt Mutter", fmtEU(salary2));
		prt("Gesamtnettogehalt", fmtEU(salaryTotal));
		prt("Gesamtnettogehaltanteil Vater  %", Math.round(salaryDiff1) + "%");
		prt("Gesamtnettogehaltanteil Mutter %", Math.round(salaryDiff2) + "%");

		lineBreak();

		for (int i = 0; i < kinder.size(); i++) {
			prt("Kind-" + (i + 1), fmtEU(kinder.get(i)));
		}
		prt("Kinder Gesamtbedarf", fmtEU(dtKinderTotalChildAllowance));
		lineBreak();

		prt(String.format("Anteil Kindesgesamtbedarf Nettogehalt 1 -> %s Prozent von %s ", salaryDiff1,
				fmtEU(dtKinderTotalChildAllowance)), fmtEU(salary1diffAmount));
		prt(String.format("Anteil Kindesgesamtbedarf Nettogehalt 2 -> %s Prozent von %s ", salaryDiff2,
				fmtEU(dtKinderTotalChildAllowance)), fmtEU(salary2diffAmount));

		lineBreak();
		if (salary1diffAmount - (dtKinderTotalChildAllowance / 2) > 0) {
			prt("Vater zahlt Gesamtbedarf Brutto(" + fmtEU(salary1diffAmount) + "-"
					+ fmtEU((dtKinderTotalChildAllowance / 2)) + ")", fmtEU(salary1diffAmount - (dtKinderTotalChildAllowance / 2)));
			prt("Vater zahlt Gesamtbedarf nach abzug von Kindergeld Netto (-" + fmtEU(totalkindergeld)  + "/" + kinder.size() + ")",
					fmtEU(salary1diffAmount - (dtKinderTotalChildAllowance / 2) - totalkindergeld / kinder.size()));
		} else {

		}

	}

	/**
	 * Format & print to console
	 *
	 * @param t
	 * @param v
	 */
	public static void prt(String t, String v) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < t.length(); i++) {
			char c = t.charAt(i);
			buffer.append(c);
		}
		for (int i = 0; i < 80 - t.length(); i++) {
			char c = '.';
			buffer.append(c);
		}
		buffer.append(":").append(String.format("%1$" + 10 + "s", v));
		System.out.println(buffer.toString());
	}

	/**
	 * Simple new line
	 */
	public static void lineBreak() {
		System.out.println();
	}

	/**
	 * Format given value into EURO (Local) Currency
	 *
	 * @param d
	 * @return
	 */
	public static String fmtEU(double d) {
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		return currencyFormatter.format(d);
	}
}