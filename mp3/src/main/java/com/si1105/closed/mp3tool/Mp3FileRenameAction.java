package com.si1105.closed.mp3tool;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * 
 * @author shinya@si1105.com
 *
 */
public class Mp3FileRenameAction {

	static char[][] replaceChars = { { '\\', '￥' }, { '/', '／' }, { ':', '：' }, { '*', '＊' }, { '?', '？' },
			{ '"', '”' }, { '<', '＜' }, { '>', '＞' }, { '|', '｜' }, };

	/** Constant "ARTIST_ALUBUM". Use for execute method nameMethod parameter. */
	public static final String ARTIST_ALUBUM = FileNameGeneratorStore.ARTIST_ALUBUM.name();

	/** Constant "OMNIBAS". Use for execute method nameMethod parameter. */
	public static final String OMNIBAS = FileNameGeneratorStore.OMNIBAS.name();

	/**
	 * The only constructor.
	 */
	public Mp3FileRenameAction() {
	}

	/**
	 * Execute action.
	 * 
	 * @param srcDir     The directory which includes songs to rename.
	 * @param destDir    The directory where move renamed file to.
	 * @param nameMethod
	 */
	public void execute(String srcDir, String destDir, String nameMethod) {

		Path srcPath = Paths.get(srcDir);

		if (!Files.exists(srcPath)) {
			printUsage();
			System.exit(1);
		}

		// ディレクトリーごとに曲ファイルをまとめる
		Map<Path, List<Path>> grouped;
		{
			List<Path> files = new ArrayList<>();
			try {
				Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						if (file.toFile().getName().toLowerCase().endsWith(".mp3")) {
							files.add(file);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			grouped = files.stream().collect(Collectors.groupingBy(f -> f.toFile().getParentFile().toPath()));
		}

		FileNameGenerator nameGen = Enum.valueOf(FileNameGeneratorStore.class, nameMethod);

		if (nameGen == null) {
			printUsage();
			System.exit(1);
		}

		Path destPath = Paths.get(destDir);
		grouped.entrySet().forEach(entry -> {
			List<Path> files = entry.getValue();
			files.forEach(file -> {
				try {
					AudioFile f = AudioFileIO.read(file.toFile());
					Tag tag = f.getTag();
					if (tag == null) {
						return;
					}

					String artistName = tag.getFirst(FieldKey.ARTIST);
					if (artistName == null || artistName.length() == 0) {
						return;
					}
					artistName = sanitize(artistName);

					String albumTitle = tag.getFirst(FieldKey.ALBUM);
					if (albumTitle == null || albumTitle.length() == 0) {
						return;
					}
					albumTitle = sanitize(albumTitle);

					String trackNo = tag.getFirst(FieldKey.TRACK);
					if (trackNo == null || trackNo.length() == 0) {
						return;
					}
					trackNo = sanitize(trackNo);

					String songTitle = tag.getFirst(FieldKey.TITLE);
					if (songTitle == null || songTitle.length() == 0) {
						return;
					}
					songTitle = sanitize(songTitle);
					String[] outInformation = nameGen.execute(artistName, albumTitle, Integer.parseInt(trackNo),
							songTitle, files.size());

					Path newDir = destPath.resolve(outInformation[0]);
					if (!Files.exists(newDir)) {
						Files.createDirectories(newDir);
					}

					Path newFile = newDir.resolve(outInformation[1]);
					Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		});
	}

	/**
	 * 
	 * @param srcDir
	 * @param nameMethod
	 */
	public void execute(String srcDir, String nameMethod) {
		execute(srcDir, Paths.get(srcDir).getParent().toString(), nameMethod);
	}

	/**
	 * print usage.
	 */
	void printUsage() {
		System.out.println("Usage:");
		System.out.println(" arg 1: The directory which includes songs to rename.");
		System.out.println(" arg 2: The directory where move renamed file to."
				+ "If you ommit this arg, renamed files are written in the arg 1 directory.");
		System.out.println(" arg 3(2 if you ommit args2): Rename method like below.");
		Arrays.stream(FileNameGeneratorStore.values()).map(e -> "  " + e.usage()).forEach(System.out::println);
	}

	/**
	 * File name generator enum.
	 * 
	 * @author shinya@si1105.com
	 * @version 1.0.0
	 * @since 2020/12/13
	 */
	public enum FileNameGeneratorStore implements FileNameGenerator {

		/**
		 * Generate file name like "artistName - albumTitle - trackNo - songTitle.mp3"
		 */
		ARTIST_ALUBUM() {
			@Override
			public String[] execute(String artistName, String albumTile, int trackNo, String songTitle,
					int numOfSongs) {
				String format = String.format("%%s - %%s - %%0%dd - %%s.mp3", getDigit(numOfSongs));
				String albumDir = String.format("%s - %s", artistName, albumTile);
				String fileName = String.format(format, artistName, albumTile, trackNo, songTitle);
				return new String[] { albumDir, fileName };
			}

			@Override
			String usage() {
				return this.name() + ": artistName - albumTitle - trackNo - songTitle.mp3";
			}
		},

		/**
		 * Generate file name like "albumTitle - trackNo - artistName - songTitle.mp3"
		 */
		OMNIBAS() {

			@Override
			public String[] execute(String artistName, String albumTile, int trackNo, String songTitle,
					int numOfSongs) {
				String format = String.format("%%s - %%0%dd - %%s - %%s.mp3", getDigit(numOfSongs));
				String albumDir = albumTile;
				String fileName = String.format(format, albumTile, trackNo, artistName, songTitle);
				return new String[] { albumDir, fileName };
			}

			@Override
			String usage() {
				return this.name() + ": albumTitle - trackNo - artistName - songTitle.mp3";
			}
		}

		;

		int getDigit(int numOfSongs) {
			return String.valueOf(numOfSongs).length();
		}

		String usage() {
			return null;
		}
	}

	private interface FileNameGenerator {

		/**
		 * 引数からファイル名を生成して返す。
		 * 
		 * @param artistName アーティスト名
		 * @param albumTile  アルバムタイトル
		 * @param trackNo    曲番号
		 * @param songTitle  曲タイトル
		 * @param digit      アルバム曲数
		 * @return 引数からファイル名を生成して返す。
		 */
		String[] execute(String artistName, String albumTile, int trackNo, String songTitle, int numOfSongs);
	}

	/**
	 * 文字列内のWindowsのファイル名禁止文字を全角に変換して返す。
	 * 
	 * @param str 文字列
	 * @return 文字列内のWindowsのファイル名禁止文字を全角に変換して返す。
	 */
	String sanitize(String str) {
		String result = str;
		for (char[] replaceChar : replaceChars) {
			result = result.replace(replaceChar[0], replaceChar[1]);
		}
		return result;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Mp3FileRenameAction instance = new Mp3FileRenameAction();

		if (args.length == 1) {
			if ("-h".equals(args[0])) {
				instance.printUsage();
			}
		}
		if (args.length == 2) {
			instance.execute(args[0], args[1]);
		}
		else if (args.length > 2) {
			instance.execute(args[0], args[1], args[2]);
		}
		else {
			instance.printUsage();
			System.exit(1);
		}
	}
}
