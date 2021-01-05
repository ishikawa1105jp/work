package com.si1105.closed.mp3tool;

import org.junit.Test;

import com.si1105.utils.Utils;

public class Mp3FileRenameActionExecutor {

	@Test
	public void run() throws Exception {
		String[] args = Utils.asArray( //
				"C:\\Users\\信也\\Music\\Media Go\\Black Sabbath" //
				, "C:\\Users\\信也\\Music\\temp" //
				, Mp3FileRenameAction.FileNameGeneratorStore.ARTIST_ALUBUM);

		Mp3FileRenameAction.main(args);
	}
}
