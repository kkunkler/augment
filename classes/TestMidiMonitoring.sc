TestMidiHandlerCC : UnitTest {
	var buttonModule, continuousModule;

	setUp {
		buttonModule = MidiButtonModule(\tester1, 4, 0, 1888).initializeElements([10,11,12,13]);
		buttonModule.setMappedDepth(4);
		continuousModule = MidiContinuousModule(\tester2, 4, 0, 1888).initializeElements([20,21,22,23]);
		continuousModule.setMappedDepth(4);

	}

	tearDown {
		buttonModule = nil;
		continuousModule = nil;
	}

	test_valNoPassThru {
		MIDIIn.doControlAction(1888, 0, 10, 110);

		this.assertEquals(buttonModule.getVal(10), 110);
	}

	test_valPassThruInRange {
		MIDIIn.doControlAction(1888, 0, 21, 6);

		this.assertEquals(continuousModule.getVal(21), 6);
	}

	test_valPassThruOutOfRange {
		MIDIIn.doControlAction(1888, 0, 21, 100);

		this.assertEquals(continuousModule.getVal(21), 0);
	}
}


TestMidiHandlerNoteOn : UnitTest {
	var module;

	setUp {
		module = MidiPadModule(\tester, 4, 0, 1999).initializeElements([40,41,42,43]);
		module.setMappedDepth(4);
	}

	tearDown {
		module = nil;
	}

	test_valSet {
		MIDIIn.doNoteOnAction(1999, 0, 41, 122);

		this.assertEquals(module.getVal(41), 122);
	}
}