TestSynthPreProc : UnitTest {

	setUp { }

	tearDown { }

	test_liveBufferShort_isSet {
		var options;

		SynthDef(\tester, {
			arg thing, liveBufferShort, last;
		}).add;

		options = SynthPreProc.processForOptions(\tester);

		this.assert(options[0] == \liveBufferShort);
	}

	test_liveBufferShort_numOptions {
		var options;

		SynthDef(\tester, {
			arg thing, liveBufferShort, last;
		}).add;

		options = SynthPreProc.processForOptions(\tester);

		this.assertEquals(options.size, 1);
	}

	test_liveBufferLong_isSet {
		var options;

		SynthDef(\tester, {
			arg one, two, liveBufferLong, lastThing;
		}).add;

		options = SynthPreProc.processForOptions(\tester);

		this.assert(options[0] == \liveBufferLong);
	}

	test_liveBufferLong_numOptions {
		var options;

		SynthDef(\tester, {
			arg one, two, liveBufferLong, lastThing;
		}).add;

		options = SynthPreProc.processForOptions(\tester);

		this.assertEquals(options.size, 1);
	}

	test_multipleArgs_isSet {
		var options;

		SynthDef(\tester, {
			arg one, two, liveBufferShort, liveBufferLongCopy;
		}).add;

		options = SynthPreProc.processForOptions(\tester);

		this.assert(options[0] == \liveBufferShort);
		this.assert(options[1] == \liveBufferLongCopy);
	}

	test_multipleArgs_numOptions {
		var options;

		SynthDef(\tester, {
			arg one, two, liveBufferShort, liveBufferLongCopy;
		}).add;

		options = SynthPreProc.processForOptions(\tester);

		this.assertEquals(options.size, 2);
	}
}



TestPatternPreProc : UnitTest {

	setUp { }

	tearDown { }

	test_liveBufferShort_isSet {
		var options;

		Pbindef(\testing,
			\instrument, \nada,
			\thing, "stuff",
			\liveBufferShort, nil,
			\last, 8
		);

		options = PatternPreProc.processForOptions(\testing);

		this.assertEquals(options[0], \liveBufferShort);
	}

	test_liveBufferShort_numOptions {
		var options;

		Pbindef(\testing,
			\instrument, \nada,
			\thing, "stuff",
			\liveBufferShort, nil,
			\last, 8
		);

		options = PatternPreProc.processForOptions(\testing);

		this.assertEquals(options.size, 1);
	}

	test_liveBufferLong_isSet {
		var options;

		Pbindef(\testing,
			\instrument, \nada,
			\thing, "stuff",
			\liveBufferLong, nil,
			\last, 8
		);

		options = PatternPreProc.processForOptions(\testing);

		this.assertEquals(options[0], \liveBufferLong);
	}

	test_liveBufferLong_numOptions {
		var options;

		Pbindef(\testing,
			\instrument, \nada,
			\thing, "stuff",
			\liveBufferLong, nil,
			\last, 8
		);

		options = PatternPreProc.processForOptions(\testing);

		this.assertEquals(options.size, 1);
	}

	test_multipleArgs_isSet {
		var options;

		Pbindef(\testing,
			\instrument, \whatever,
			\thing, 'yep',
			\number, 9,
			\liveBufferLong, nil,
			\otherNum, 110,
			\liveBufferShortCopy, nil
		);

		options = PatternPreProc.processForOptions(\testing);

		this.assertEquals(options[0], \liveBufferLong);
		this.assertEquals(options[1], \liveBufferShortCopy);
	}

	test_multipleArgs_numOptions {
		var options;

		Pbindef(\testing,
			\instrument, \whatever,
			\thing, 'yep',
			\number, 9,
			\liveBufferLong, nil,
			\otherNum, 110,
			\liveBufferShortCopy, nil
		);

		options = PatternPreProc.processForOptions(\testing);

		this.assertEquals(options.size, 2);
	}
}