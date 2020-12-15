TestResourceManager : UnitTest {
	var manager;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		Server.local.sync;
		manager = ResourceManager.new;
	}

	tearDown {
		manager.clearBuffers;
		manager = nil;
		Server.local.quit;
	}

	test_addBuffers {
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Auditem Cut.wav");
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Langsamer Satz Webern.wav");

		this.assert(manager.buffers.at('Auditem Cut').notNil);
		this.assert(manager.buffers.at('Langsamer Satz Webern').notNil);
	}

	test_getBufferFromPathToken {
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Auditem Cut.wav");
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Langsamer Satz Webern.wav");

		this.assert(manager.getBufferFromPathToken('Auditem Cut').isKindOf(Buffer));
		this.assert(manager.getBufferFromPathToken('Langsamer Satz Webern').isKindOf(Buffer));
	}

	test_getBufferFromFileName {
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Auditem Cut.wav");
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Langsamer Satz Webern.wav");

		this.assert(manager.getBufferFromFileName("Auditem Cut.wav").isKindOf(Buffer));
		this.assert(manager.getBufferFromFileName("Langsamer Satz Webern.wav").isKindOf(Buffer));
	}

	test_getBufferFromPath {
		var buffer1, buffer2;

		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Auditem Cut.wav");
		manager.addBuffer("/Users/kylekunkler/Music/For Supercollider/Langsamer Satz Webern.wav");

		buffer1 = manager.getBufferFromPath("/Users/kylekunkler/Music/For Supercollider/Auditem Cut.wav");
		buffer2 = manager.getBufferFromPath("/Users/kylekunkler/Music/For Supercollider/Langsamer Satz Webern.wav");

		this.assert(buffer1.isKindOf(Buffer));
		this.assert(buffer2.isKindOf(Buffer));
	}

	test_buildCycleRecorder_checkName {
		manager.buildCycleRecorder(\theName, 2.0);

		this.assert(manager.getCycleRecorder(\theName).notNil);
	}

	test_buildCycleRecorder_checkLength {
		var cycleRecorder;

		manager.buildCycleRecorder(\theName, 3.0);
		cycleRecorder = manager.getCycleRecorder(\theName);

		this.assertEquals(cycleRecorder.cycleLength, 3.0);
	}

	test_buildCycleRecorder_badNameError {
		this.assertException({ manager.buildCycleRecorder(nil, 1.0) }, Error);
	}

	test_buildCycleRecorder_badLengthError {
		this.assertException({ manager.buildCycleRecorder(\nameThing, nil) }, Error);
	}

	test_getLiveBuffer_basic {
		var cycleRecorder, setArray;

		manager.buildCycleRecorder(\theName, 1.0);
		manager.buildCycleRecorder(\theOther, 5.0);

		cycleRecorder = manager.getCycleRecorder(\theName);

		setArray = manager.getLiveBuffer(\theName, []);

		this.assertEquals(setArray[1], cycleRecorder.recordBuffer);
	}

	test_getLiveBuffer_arrayAlreadyHasContents {
		var cycleRecorder, setArray;

		manager.buildCycleRecorder(\theName, 1.0);
		manager.buildCycleRecorder(\theOther, 5.0);

		cycleRecorder = manager.getCycleRecorder(\theName);

		setArray = manager.getLiveBuffer(\theName, [\oneThing, 101, \aNumber, 19998]);

		this.assertEquals(setArray[5], cycleRecorder.recordBuffer);
	}

	test_getLiveBuffer_noRecorderError {
		this.assertException({ manager.getLiveBuffer(\blahBlahBlah, []) }, Error);
	}

	test_getLiveBufferCopy_correctType {
		var setArray;

		manager.buildCycleRecorder(\first, 1.0);
		manager.buildCycleRecorder(\theLast, 5.0);

		setArray = manager.getLiveBufferCopy(\first, []);

		this.assert(setArray[1].isKindOf(Buffer));
	}

	test_getLiveBufferCopy_isCopy {
		var cycleRecorder, setArray;

		manager.buildCycleRecorder(\first, 1.0);
		manager.buildCycleRecorder(\theLast, 5.0);

		cycleRecorder = manager.getCycleRecorder(\theLast);

		setArray = manager.getLiveBufferCopy(\theLast, []);

		this.assert(setArray[1] != cycleRecorder.recordBuffer);
	}

	test_getLiveBufferCopy_arrayAlreadyHasContents {
		var cycleRecorder, setArray, bufferLen;

		manager.buildCycleRecorder(\first, 2.0);

		cycleRecorder = manager.getCycleRecorder(\first);

		setArray = manager.getLiveBufferCopy(\first, []);
		bufferLen = setArray[1].numFrames / setArray[1].sampleRate;

		this.assertEquals(cycleRecorder.cycleLength, bufferLen);
	}

	test_getLiveBufferCopy_noRecorderError {
		this.assertException({ manager.getLiveBufferCopy(\first, []) }, Error);
	}
}








