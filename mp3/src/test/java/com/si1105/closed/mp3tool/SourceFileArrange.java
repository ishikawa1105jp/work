package com.si1105.closed.mp3tool;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class SourceFileArrange {

	@Test
	public void execute() throws Exception {

		List<String> lines = Files.readAllLines(Paths.get("seishun_uta_nenkan_src.txt"));

		final String albumTitlePrefix = "青春歌年鑑";
		final String discNoPrefix = "ディスク";
		String albumTitle = "";
		String discNo = "";
		String replaced = "";
		for (String aLine : lines) {
			if (aLine.length() == 0) {
				continue;
			}
			if (aLine.startsWith(albumTitlePrefix)) {
				albumTitle = aLine;
			}
			else if (aLine.startsWith(discNoPrefix)) {
				discNo = aLine;
			}
			else {
				replaced = aLine.replaceAll("\\. ", "\t").replaceAll("\\(", "\t").replaceAll("\\)", "");
				System.out.printf("%s\t%s\t%s%n", albumTitle, discNo, replaced);
			}
		}

		lines = Files.readAllLines(Paths.get("seishun_uta_nenkan_src2.txt"));

		final String albumTitlePrefixZoku = "続・青春歌年鑑";
		albumTitle = "";
		String song = "";
		String artist = "";
		for (String aLine : lines) {
			if (aLine.length() == 0) {
				continue;
			}
			if (aLine.startsWith(albumTitlePrefixZoku)) {
				albumTitle = aLine;
			}
			else {
				replaced = aLine.substring(0, aLine.length() - 1).replaceAll("\\. ", "\t");
				song = replaced.substring(0, replaced.lastIndexOf('('));
				artist = replaced.substring(replaced.lastIndexOf('(') + 1);
				System.out.printf("%s\t%s\t%s%n", albumTitle, song, artist);
			}
		}
	}
}
