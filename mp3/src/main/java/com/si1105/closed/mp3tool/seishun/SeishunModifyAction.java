package com.si1105.closed.mp3tool.seishun;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class SeishunModifyAction {

	public static void main(String[] args) throws Exception {

		List<String> lines = Files.readAllLines(Paths.get("seishun_mapping.txt"));

		for (String aLine : lines) {
			int i;
			if (aLine.startsWith("青")) {
				i = 2;
			}
			if (aLine.startsWith("続")) {
				i = 1;
			}
			else {
				continue;
			}

			String[] split = aLine.split("\t");
			String seishun = split[0];
			String trackNo = split[i++];
			String artistName;
			String songName;
			{
				String[] fileName = split[i].split(" \\- ");
				songName = fileName[0];
				artistName = fileName[1];
			}
			File file = Paths.get(aLine.split("\t")[i]).toFile();

			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			if (tag == null) {
				continue;
			}
			tag.setField(FieldKey.ALBUM, seishun);
			tag.setField(FieldKey.TRACK, trackNo);

		}
	}
}
