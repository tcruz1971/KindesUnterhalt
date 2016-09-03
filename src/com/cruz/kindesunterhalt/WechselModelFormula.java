package com.cruz.kindesunterhalt;

import java.text.NumberFormat;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * WechselModelFormula
 *
 * @author rosborne
 */
public class WechselModelFormula {

    public static void main(String[] args) throws IOException {
    	
		InputStream input = FrankFormula.class.getClassLoader().getResourceAsStream("config.properties");
		Properties prop = new Properties();
		String propFileName = "config.properties";
		if (input != null) {
			prop.load(input);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}		    	
		
        // Gehalt
        double gehaltvater = Double.valueOf((String) prop.get("gehaltVater"));
        double gehaltmutter = Double.valueOf((String) prop.get("gehaltMutter"));
        double gesetzlicher_selbstbehalt_vater = Double.valueOf((String) prop.get("selbstbehalt_vater"));
        double gesetzlicher_selbstbehalt_mutter = Double.valueOf((String) prop.get("selbstbehalt_mutter"));
        double totalGehalt = (gehaltvater + gehaltmutter);
        double gehaltvaterNetto = gehaltvater - gesetzlicher_selbstbehalt_vater;
        double gehaltmutterNetto = gehaltmutter - gesetzlicher_selbstbehalt_mutter;
        double totalGehaltNetto = gehaltvaterNetto + gehaltmutterNetto;
        // Kinder
        ArrayList<Double> totalkinderunterhalt = new ArrayList<Double>();
		String kindesunterhalt[] = prop.get("kindesunterhalt").toString().split(",");
		String kindermehrbedarf[] = prop.get("kindermehrbedarf").toString().split(",");    
		String kindergeld[] = prop.get("kindergeld").toString().split(",");
		String kinder[] = prop.get("kinder").toString().split(",");
        double total_kindergeld = 0;        
        double totalKinderKost = 0;
        double totalKinderZimmer = 0;
		for (int i = 0; i < kindesunterhalt.length; i++) {
			totalKinderKost += Double.valueOf(kindesunterhalt[i]) + Double.valueOf(kindermehrbedarf[i]);
			totalKinderZimmer += Double.valueOf(kindermehrbedarf[i]);
			totalkinderunterhalt.add(Double.valueOf(kindesunterhalt[i]) + Double.valueOf(kindermehrbedarf[i]));
			total_kindergeld += Double.valueOf(kindergeld[i]);
		}		        


        // Store gehalt kinderunterhalt
        double gehaltvaterKinderUnterhalt = Math.round(
                gehaltvaterNetto / totalGehaltNetto * totalKinderKost);
        double gehaltmutterKinderUnterhalt = Math.round(
                gehaltmutterNetto / totalGehaltNetto * totalKinderKost);

        // Store the 50% gehalt kinderunterhalt 
        double gehaltvaterKinderunterhalt50 = gehaltvaterKinderUnterhalt / 2;
        double gehaltmutterKinderunterhalt50 = gehaltmutterKinderUnterhalt / 2;

        // Final calculation
        double gehaltvaterResult = gehaltvaterKinderunterhalt50 - gehaltmutterKinderunterhalt50
                - (total_kindergeld / 2);
        double gehaltmutterResult = gehaltvaterKinderunterhalt50 - gehaltmutterKinderunterhalt50
                - (total_kindergeld / 2);

        prt("Gehalt Vater Monatlich",
                fmtEU(gehaltvater));
        prt("Gehalt Mutter Monatlich",
                fmtEU(gehaltmutter));
        prt("Gesamtgehalt Monatlich",
                fmtEU(totalGehalt));
        prt("Gehalt Vater  % vom Gesamtgehalt Monatlich",
                Math.round((gehaltvater / totalGehalt) * 100) + "%");
        prt("Gehalt Mutter % vom Gesamtgehalt Monatlich",
                Math.round((gehaltmutter / totalGehalt) * 100) + "%");

        lineBreak();

        prt("Total Kindergeld Monatlich",
                fmtEU(total_kindergeld));
        
        lineBreak();
        
        for (int i = 0; i < kindermehrbedarf.length; i++) {
            prt(String.format("%s Monatlicher Mehrbedarf", kinder[i]),
                    fmtEU(Double.valueOf(kindermehrbedarf[i])));
        }
        
        lineBreak();

        prt("Total Monatlicher Kindermehrbedarf",
                fmtEU(totalKinderZimmer));

        for (int i = 0; i < kindesunterhalt.length; i++) {
            prt(String.format("%s Monatlicher Unterhalt nach Duesseldorfer Tabelle", kinder[i]),
                    fmtEU(Double.valueOf(kindesunterhalt[i])));
        }

        prt("Total Monatlicher Kindesunterhalt (Inkl. Monatlicher Kindermehrbedarf)",
                fmtEU(totalKinderKost));

        lineBreak();

        prt("Gesetzlicher Selbstbehalt Vater",
                fmtEU(gesetzlicher_selbstbehalt_vater));

        prt(String.format("Gehalt Vater  Netto (%s-%s)",
                fmtEU(gehaltvater),
                fmtEU(gesetzlicher_selbstbehalt_vater)),
                fmtEU(gehaltvaterNetto));
        
        prt("Gesetzlicher Selbstbehalt Mutter",
                fmtEU(gesetzlicher_selbstbehalt_mutter));        

        prt(String.format("Gehalt Mutter Netto (%s-%s)",
                fmtEU(gehaltmutter),
                fmtEU(gesetzlicher_selbstbehalt_mutter)),
                fmtEU(gehaltmutterNetto));

        prt(String.format(
                "Summe Gehaelter Netto (%s+%s)",
                fmtEU(gehaltvaterNetto),
                fmtEU(gehaltmutterNetto)),
                fmtEU(totalGehaltNetto));

        lineBreak();
        prt(String.format("Vater  Kinderunterhalt (%s/%s)*%s",
                fmtEU(gehaltvaterNetto),
                fmtEU(totalGehaltNetto),
                fmtEU(totalKinderKost)),
                fmtEU(gehaltvaterKinderUnterhalt));

        prt(String.format("Mutter Kinderunterhalt (%s/%s)*%s",
                fmtEU(gehaltmutterNetto),
                fmtEU(totalGehaltNetto),
                fmtEU(totalKinderKost)),
                fmtEU(gehaltmutterKinderUnterhalt));

        lineBreak();

        prt(String.format("Vater  Kinderunterhalt 50%% Wechselmodel (%s/2)",
                fmtEU(gehaltvaterKinderUnterhalt)),
                fmtEU(gehaltvaterKinderunterhalt50));
        prt(String.format("Mutter Kinderunterhalt 50%% Wechselmodel (%s/2)",
                fmtEU(gehaltmutterKinderUnterhalt)),
                fmtEU(gehaltmutterKinderunterhalt50));

        lineBreak();

        if (gehaltvaterKinderUnterhalt - (totalKinderKost / 2) > 0 && gehaltvaterKinderUnterhalt > gehaltmutterKinderUnterhalt) {
            prt(String.format("Vater zahlt Monatlich Differenz (%s-%s)-(%s/2)",
                    fmtEU(gehaltvaterKinderunterhalt50),
                    fmtEU(gehaltmutterKinderunterhalt50),
                    fmtEU(total_kindergeld)),
                    fmtEU(gehaltvaterResult));
        } else {
            prt(String.format("Mutter zahlt Monatlich Differenz (%s-%s)-(%s/2)",
                    fmtEU(gehaltmutterKinderunterhalt50),
                    fmtEU(gehaltvaterKinderunterhalt50),
                    fmtEU(total_kindergeld)),
                    fmtEU(gehaltmutterResult));
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