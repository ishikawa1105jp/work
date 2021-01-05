package com.si1105.closed.mp3tool.seishun;

import com.si1105.closed.mp3tool.Mp3Util;

public class Song {

	final String albumTitle;
	final String discNo;
	final String track;
	final String albumArtist;
	final String artist;
	final String songTitle;

	public Song(String albumTitle, String discNo, String track, String albumArtist, String artist, String songTitle) {
		super();
		this.albumTitle = albumTitle;
		this.discNo = discNo;
		this.track = track;
		this.albumArtist = albumArtist;
		this.artist = artist;
		this.songTitle = songTitle;
	}

	public Song getSanitized() {
		String albumTitle = Mp3Util.sanitize(this.albumTitle);
		String discNo = Mp3Util.sanitize(this.discNo);
		String track = Mp3Util.sanitize(this.track);
		String albumArtist = Mp3Util.sanitize(this.albumArtist);
		String artist = Mp3Util.sanitize(this.artist);
		String songTitle = Mp3Util.sanitize(this.songTitle);
		return new Song(albumTitle, discNo, track, albumArtist, artist, songTitle);
	}
}
