package it.xteam.estrattore;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GeneraDomande {

	private static int altezza = 1000;
	private static int larghezza = 1770;

	public static void genera(Document doc) {
		List<String> listadomande = new ArrayList<String>();
		Element linktorre = doc.select("aside").first();
		Element titoloBoxDomande = linktorre.select("strong").first();
		System.out.println("-->" + titoloBoxDomande.text());
		Elements domande = linktorre.select("p");
		for (Element singola : domande) {
			System.out.println(singola.text());
			listadomande.add(singola.text());
		}
		generaImmagine3(titoloBoxDomande.text(), listadomande);
	}

	public static void generaImmagine3(String titolo, List<String> listadomande) {
		String titoloRiquadro = titolo;
		BufferedImage bufferedImage = new BufferedImage(larghezza, altezza, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bufferedImage.getGraphics();
		graphics.setColor(Color.ORANGE);
		graphics.fillRect(0, 0, 1770, 100);

		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("Verdana", Font.BOLD, 54));
		graphics.drawString(titoloRiquadro, 30, 70);

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 101, 1770, 948);

		graphics.setColor(Color.BLACK);
		int spaziodisponibileVerticale = 924;
		int numeroDomande = listadomande.size();
		int grandezzaChar = 50;
		if (listadomande.size() < 4) {
			grandezzaChar = 65;
		}

		int numeroCaratteriMaxPerRiga = calcolaMaxCaratteri(graphics, grandezzaChar);
		// numeroCaratteriMaxPerRiga=70;
		System.out.println("numeroCaratteriMaxPerRiga: " + numeroCaratteriMaxPerRiga);

		int ydipartenza = 100;
		int nuovay = ydipartenza;

		int indice = 1;
		int totaleRighe = 0;
		for (String domanda : listadomande) {

			List<String> domandaSpezzata = spezzaDomanda(domanda, numeroCaratteriMaxPerRiga);
			totaleRighe = totaleRighe + domandaSpezzata.size();

		}
		System.out.println("TOTALE RIGHE: " + totaleRighe);
		totaleRighe = totaleRighe + listadomande.size(); // Calcola uno spazio
														// tra una e l'altra

		int mediaperdomanda = spaziodisponibileVerticale / totaleRighe;

		for (String domanda : listadomande) {

			List<String> domandaSpezzata = spezzaDomanda(domanda, numeroCaratteriMaxPerRiga);
			graphics.setFont(new Font("Courier", Font.BOLD, grandezzaChar));
			int larghezzastringa = graphics.getFontMetrics().stringWidth(domanda);
			// while (larghezzastringa>(larghezza-20)){
			// graphics.setFont(new Font("Verdana", Font.BOLD, nuovoChar--));
			// larghezzastringa=graphics.getFontMetrics().stringWidth(domanda);
			// }
			// System.out.println(graphics.getFontMetrics().stringWidth(domanda));
			for (String riga : domandaSpezzata) {

				graphics.drawString(riga, 10, (mediaperdomanda * indice) + ydipartenza);
				indice++;
			}
			indice++;

		}

		try {
			ImageIO.write(bufferedImage, "jpg", new File(Estrattore.PATH_OUTPUT + "TORRE/COME_RISPONDERESTE.jpg"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Image Created");
	}

	private static List<String> spezzaDomanda(String domanda, int numeroCaratteriMaxPerRiga) {
		List<String> output = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(domanda, " ");
		if (domanda.length() <= numeroCaratteriMaxPerRiga) {
			output.add(domanda);
			return (output);
		}

		int numeroRighe = (int) Math.ceil(domanda.length() / numeroCaratteriMaxPerRiga);
		numeroRighe++;
		System.out.println(numeroRighe);

		System.out.println(domanda.length());

		System.out.println("Numero di righe necessarie: " + numeroRighe);
		boolean troppo = false;
		String rigasingola = "";
		for (int i = 0; i < numeroRighe; i++) {

			String prossimaparola = "";

			while (tokenizer.hasMoreTokens()) {
				prossimaparola = tokenizer.nextToken() + " ";
				if ((rigasingola.length() + prossimaparola.length()) > numeroCaratteriMaxPerRiga) {
					troppo = true;
					break;
				}
				else {
					rigasingola = rigasingola + prossimaparola;
				}

			}
			output.add(rigasingola);
			System.out.println("AGGIUNTO RIGA: " + rigasingola);
			if (!troppo) {
				rigasingola = "";
			}
			else {
				rigasingola = prossimaparola;
			}

		}
		return (output);
	}

	private static int calcolaMaxCaratteri(Graphics graphics, int grandezzaChar) {
		int larghezzastringa = 0;

		String domanda = "";
		while (larghezzastringa < larghezza) {
			domanda = domanda + "a";
			graphics.setFont(new Font("Courier", Font.BOLD, grandezzaChar));
			larghezzastringa = graphics.getFontMetrics().stringWidth(domanda);
			System.out.println(domanda.length() + " - " + larghezzastringa + " - " + larghezza);
		}
		return domanda.length();
	}

}
