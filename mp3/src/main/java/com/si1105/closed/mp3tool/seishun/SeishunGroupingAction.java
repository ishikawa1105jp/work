package com.si1105.closed.mp3tool.seishun;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.si1105.closed.mp3tool.Mp3Util;

public class SeishunGroupingAction {

	public static void main(String[] args) throws Exception {

		List<String> songList = Files.readAllLines(Paths.get("seishun_uta_nenkan.txt")/* , Charset.forName("MS932") */);
		File[] files = Paths.get("C:\\Users\\信也\\Music\\domestic_barabara").toFile()
				.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".mp3"));
		Arrays.stream(files).forEach(f -> {
			String fileName = f.getName();
			if (!fileName.contains(" - ")) {
				System.out.printf("INVALID FILE NAME: %s%n", f);
				return;
			}
			fileName = fileName.substring(0, fileName.length() - ".mp3".length());
			String artist = fileName.split(" \\- ")[0];
			String song = fileName.split(" \\- ")[1];
			String seishun = songList.stream().filter(e -> e.contains(song) && e.contains(artist))
					.map(e -> e.startsWith("続") ? e.split("\t")[0] + '\t' + e.split("\t")[1]
							: e.split("\t")[0] + '\t' + e.split("\t")[1] + '\t' + e.split("\t")[2])
					.findFirst().orElse(null);
			System.out.printf("%s\t%s%n", seishun, f);
		});

//		List<PrivateSong> songs = Arrays.stream(files).map(SeishunGroupingAction::getPrivateSong)
//				.filter(Objects::nonNull).collect(Collectors.toList());

//		songs.forEach(s -> {
//			System.out.printf("%s\t%s%n", s.songTitle, s.artist);
//			songList.stream().filter(e -> e.contains(s.songTitle) && e.contains(s.artist)).map(e -> e.split("\t")[0])
//					.findFirst().ifPresent(seishun -> s.seishun = seishun);
//			System.out.printf("%s: %s%n", s.seishun, s.file);
//		});
	}

	static PrivateSong getPrivateSong(File file) {
		Song song = Mp3Util.getSong(file);
		return new PrivateSong(song.albumTitle, song.discNo, song.track, song.albumArtist, song.artist, song.songTitle,
				file);
	}

	static class PrivateSong extends Song {

		String seishun;

		final File file;

		public PrivateSong(String albumTitle, String discNo, String track, String albumArtist, String artist,
				String songTitle, File file) {
			super(albumTitle, discNo, track, albumArtist, artist, songTitle);
			this.file = file;
		}
	}
}
