package com.si1105.closed.mp3tool.seishun;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Copy {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get("seishun_mapping.txt"), Charset.forName("MS932"));
		for (String aLine : lines) {
			int i;
			if (aLine.startsWith("青")) {
				i = 3;
			}
			if (aLine.startsWith("続")) {
				i = 2;
			}
			else {
				continue;
			}
			String fileName = aLine.split("\t")[i];
			Path src = Paths.get(fileName);
			FileUtils.copyFileToDirectory(src.toFile(), Paths.get("C:\\Users\\信也\\Music\\seishun_temp").toFile());
		}
	}
}
