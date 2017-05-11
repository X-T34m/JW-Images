package it.xteam.estrattore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.xteam.lmscheduling.mail.GmailSend;

public class Estrattore {

	private static String BASE_URL = "https://wol.jw.org";
	private static String link = "https://wol.jw.org/it/wol/dt/r6/lp-i/";
	public static String PATH_OUTPUT = "";
	public static String EMAIL_TO = "";
	public static String EMAIL_CC = "";

	public static void main(String[] args) {
		Document doc = null;
		try {

			//if(args.length<1){
			//	System.out.println("Parametri mancanti: <path>");
			//	System.exit(1);
			//}
			PATH_OUTPUT = "./images/";
			//EMAIL_TO=args[1];
			//EMAIL_CC=args[2];

			long timestamp = System.currentTimeMillis();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timestamp);
			int anno = cal.get(Calendar.YEAR);
			int mese = cal.get(Calendar.MONTH) + 1;
			int giorno = cal.get(Calendar.DAY_OF_MONTH);
			link = link + anno + "/" + mese + "/" + giorno;
			System.out.println("Link di partenza:" + link);
			doc = Jsoup.connect(link).get();
			Set<LinkFigure> listaFigureLink = new HashSet<LinkFigure>();
			List<LinkMateriale> listalink = estraiUrlMateriale(doc);
			System.out.println("Numero link articoli: " + listalink.size());
			for (LinkMateriale str : listalink) {
				System.out.println(str.getTipo() + " - " + str.getUrl());
				doc = Jsoup.connect(str.getUrl()).get();
				Set<LinkFigure> listaFigure = Estrattore.estraiUrlFigure(str.getTipo(), doc);
				listaFigureLink.addAll(listaFigure);
			}
			List<String> linkProdotti = new ArrayList<String>();
			System.out.println("\n\nNumero immagini recuperate (senza doppioni): " + listaFigureLink.size());
			for (LinkFigure str : listaFigureLink) {
				System.out.println(str.getTipo() + " - " + str.getUrl());
				boolean esiste = checkIfExists(linkProdotti, str.getUrl());
				if (!esiste) {
					File newFileImg = new File(Estrattore.PATH_OUTPUT + str.getTipo() + "/" + System.currentTimeMillis() + ".jpg");
					FileUtils.copyURLToFile(new URL(str.getUrl()), newFileImg);
					linkProdotti.add(str.getUrl());
				}
			}
			System.out.println("Inizio produzione immagine domande di ripasso");
			GeneraDomande.genera(doc);
			System.out.println("Immagine generata");
			System.out.println("Creo 3 file zip");
			String cartelle[] = { "EFFICACI", "TESORI", "TORRE", "VITA" };
			boolean esitoCompressione = false;
			for (String nome : cartelle) {
				esitoCompressione = comprimiCartella("");

				if (!esitoCompressione) {
					GmailSend.sendSingleEmailERROR("Errore in fase di compressione", "Errore nella compressione delle immagini");
					System.exit(1);
				}
			}
			File file = new File(Estrattore.PATH_OUTPUT + ".zip");
			File file2 = new File(Estrattore.PATH_OUTPUT + "output.zip");
			boolean success = file.renameTo(file2);
			System.out.println("File zip generati");
			//System.out.println("Inizio invio email");
			//boolean invioEmail=GmailSend.sendSingleEmailOK();
			//System.out.println("Email inviata: "+invioEmail);
		}
		catch (IOException e) {
			System.out.println("Errore grave...");
			e.printStackTrace();
		}
	}

	public static boolean comprimiCartella(String nomeCartella) {
		ZipOutputStream zos;
		try {
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(Estrattore.PATH_OUTPUT + nomeCartella + ".zip")));

			// cartella da comprimere
			File f = new File(Estrattore.PATH_OUTPUT + nomeCartella);

			File[] listaFiles = f.listFiles();

			for (int i = 0; i < listaFiles.length; i++) {
				File currFile = listaFiles[i];
				if (currFile.getName().contains(".zip")) continue;
				File currZip = new File(Estrattore.PATH_OUTPUT + nomeCartella + ".zip");
				Compress.compress(currFile, "", zos);
			}

			zos.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (false);
		}
		return (true);
	}

	private static boolean checkIfExists(List<String> linkProdotti, String url) {
		for (int i = 0; i < linkProdotti.size(); i++) {
			String daVerificare = linkProdotti.get(i);
			if (url.equalsIgnoreCase(daVerificare)) {
				return (true);
			}
		}
		return false;
	}

	/**
	 * Recupera la lista dei link in cui cercare le figure. Esclusi cantici e puntamenti a libri presentazioni
	 * 
	 * @param doc
	 * @return
	 */
	public static List<LinkMateriale> estraiUrlMateriale(Document doc) {
		List<LinkMateriale> listaUrl = new ArrayList<LinkMateriale>();
		Elements SEZIONE_TESORI = doc.select("#section2");
		Elements SEZIONE_EFFICACI = doc.select("#section3");
		Elements SEZIONE_VITA = doc.select("#section4");

		/////// TESORI
		Element link = SEZIONE_TESORI.select("a").first();// Della sezione tesori recupero solo il primo link, il resto sono versetti
		listaUrl.add(new LinkMateriale("TESORI", BASE_URL + link.attr("href")));

		////// EFFICACI
		Elements linkefficaci = SEZIONE_EFFICACI.select("a");
		for (Element ele : linkefficaci) {
			if (!ele.toString().contains("Cantic") && !ele.toString().contains("finder")) {
				listaUrl.add(new LinkMateriale("EFFICACI", BASE_URL + ele.attr("href")));
			}
		}

		///// VITA
		Elements linkvita = SEZIONE_VITA.select("a");
		for (Element ele : linkvita) {
			if (!ele.toString().contains("Cantic") && !ele.toString().contains("finder")) {
				listaUrl.add(new LinkMateriale("VITA", BASE_URL + ele.attr("href")));
			}
		}

		///// TORRE
		Element linktorre = doc.select("div.groupTOC").first();
		String linktorrestudio = linktorre.select("a").first().attr("href");
		listaUrl.add(new LinkMateriale("TORRE", BASE_URL + linktorrestudio));
		return (listaUrl);
	}// End method...

	public static Set<LinkFigure> estraiUrlFigure(String tipo, Document doc) {
		Set<LinkFigure> listaFigure = new HashSet<LinkFigure>();
		Elements EL_FIGURE = doc.select("figure");
		Elements EL_IMAGE = EL_FIGURE.select("img");

		for (Element elem : EL_IMAGE) {
			String url_image = elem.select("img").attr("src");
			if (url_image.startsWith("http")) {
				listaFigure.add(new LinkFigure(tipo, url_image));
			}
			else {
				listaFigure.add(new LinkFigure(tipo, BASE_URL + url_image));
			}

		}

		return (listaFigure);
	}// End method...

}
