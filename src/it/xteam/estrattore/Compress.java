package it.xteam.estrattore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress {

	public static void compress(File f, String path, ZipOutputStream zos) throws IOException {

		boolean isDirectory = f.isDirectory();
		//le directory terminano con "/", altrimenti sono file
		final String nextPath = path + f.getName() + (isDirectory ? "/" : "");

		ZipEntry zipEntry = new ZipEntry(nextPath);
		zos.putNextEntry(zipEntry);

		if (isDirectory) {
			File[] child = f.listFiles();
			//ricorsione per ogni figlio
			for (int i = 0; i < child.length; i++)
				compress(child[i], nextPath, zos);

		}
		else if (f.isFile()) {
			FileInputStream fis = new FileInputStream(f);
			byte[] readBuffer = new byte[4096];
			int bytesIn = 0;

			//insert del file nell'ultima zipEntry dello ZipOutputStream 
			while ((bytesIn = fis.read(readBuffer)) != -1)
				zos.write(readBuffer, 0, bytesIn);

			fis.close();
		}
	}
}