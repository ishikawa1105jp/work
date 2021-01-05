package com.si1105.closed.mp3tool;

import java.io.File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.si1105.closed.mp3tool.seishun.Song;

public class Mp3Util {

	static char[][] replaceChars = { { '\\', '￥' }, { '/', '／' }, { ':', '：' }, { '*', '＊' }, { '?', '？' },
			{ '"', '”' }, { '<', '＜' }, { '>', '＞' }, { '|', '｜' }, };

	private Mp3Util() {
		;
	}

	public static Song getSong(File file) {

		try {
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();
			if (tag == null) {
				return null;
			}

			String artistName = tag.getFirst(FieldKey.ARTIST);
			String albumTitle = tag.getFirst(FieldKey.ALBUM);
			String discNo = tag.getFirst(FieldKey.DISC_NO);
			String albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
			String trackNo = tag.getFirst(FieldKey.TRACK);
			String songTitle = tag.getFirst(FieldKey.TITLE);

			return new Song(albumTitle, discNo, trackNo, albumArtist, artistName, songTitle);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String sanitize(String str) {
		if (str == null) {
			return null;
		}
		String result = str;
		for (char[] replaceChar : Mp3Util.replaceChars) {
			result = result.replace(replaceChar[0], replaceChar[1]);
		}
		return result;
	}

}
