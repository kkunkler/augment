TestInstrumentDictionary : UnitTest {
	var dictionary;

	setUp {
		dictionary = InstrumentDictionary.new;
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		dictionary = nil;
		Server.local.quit;
	}

	test_registerInstrument {
		var inst;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]);
		dictionary.registerInstrument(inst);

		this.assert(dictionary.getInstrument(\tester).notNil);
	}

	test_registerInstrumentFailure {
		this.assert(dictionary.getInstrument(\notThere).isNil);
	}

	test_makeInstrumentVariant {
		var inst;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]);
		dictionary.registerInstrument(inst);

		dictionary.makeInstrumentVariant(\tester, \testerV2, [\freq, 1000]);
		this.assert(dictionary.getInstrument(\tester).notNil);
		this.assert(dictionary.getInstrument(\testerV2).notNil);
		this.assertEquals(dictionary.getInstrument(\testerV2).defaults, [\freq, 1000]);
	}
}


TestSynthInstrumentBinding : UnitTest {
	var binding;

	setUp {
		binding = SynthInstrumentBinding.new;
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		binding = nil;
		Server.local.quit;
	}

	test_setBuffers {
		var buffers;

		buffers = [
			Buffer.new(Server.local, Server.local.sampleRate * 2, 2),
			Buffer.new(Server.local, Server.local.sampleRate * 1, 2)
		];
		Server.local.sync;
		binding.setBuffers(buffers);

		this.assertEquals(binding.buffers.size, 2);
	}

	test_bindBuffersEmptySetArray_checkSize {
		var buffers, setArray;

		buffers = [
			Buffer.new(Server.local, Server.local.sampleRate * 2, 2),
			Buffer.new(Server.local, Server.local.sampleRate * 1, 2)
		];
		Server.local.sync;
		binding.setBuffers(buffers);

		setArray = binding.bindBuffers([]);

		this.assertEquals(setArray.size, 4);
	}

	test_bindBuffersEmptySetArray_checkBufferLengths {
		var buffers, setArray;

		buffers = [
			Buffer.new(Server.local, Server.local.sampleRate * 2, 2),
			Buffer.new(Server.local, Server.local.sampleRate * 1, 2)
		];
		Server.local.sync;
		binding.setBuffers(buffers);

		setArray = binding.bindBuffers([]);

		this.assertEquals(setArray[1].numFrames, buffers[0].numFrames);
		this.assertEquals(setArray[3].numFrames, buffers[1].numFrames);
	}

	test_bindBuffersBasicSetArray_checkSize {
		var buffers, setArray;

		buffers = [
			Buffer.new(Server.local, Server.local.sampleRate * 2, 2),
			Buffer.new(Server.local, Server.local.sampleRate * 1, 2)
		];
		Server.local.sync;
		binding.setBuffers(buffers);

		setArray = binding.bindBuffers([\yay, 109, \another, 1888]);

		this.assertEquals(setArray.size, 8);
	}

	test_bindBuffersBasicSetArray_checkBufferLengths {
		var buffers, setArray;

		buffers = [
			Buffer.new(Server.local, Server.local.sampleRate * 2, 2),
			Buffer.new(Server.local, Server.local.sampleRate * 1, 2)
		];
		Server.local.sync;
		binding.setBuffers(buffers);

		setArray = binding.bindBuffers([\yay, 109, \another, 1888]);

		this.assertEquals(setArray[5].numFrames, buffers[0].numFrames);
		this.assertEquals(setArray[7].numFrames, buffers[1].numFrames);
	}

	test_bindOptionsGetBuffers_checkSize {
		var resourceManager, setArray;

		binding.setOptions([\liveBufferShort, \liveBufferLong]);
		resourceManager = ResourceManager.new;

		setArray = binding.bindOptions([], resourceManager);
		Server.local.sync;

		this.assertEquals(setArray.size, 4);
	}

	test_bindOptionsGetBuffers_checkBuffers {
		var resourceManager, setArray;

		binding.setOptions([\liveBufferShort, \liveBufferLong]);
		resourceManager = ResourceManager.new;

		setArray = binding.bindOptions([], resourceManager);
		Server.local.sync;

		this.assert(setArray[1].isKindOf(Buffer));
		this.assert(setArray[3].isKindOf(Buffer));
	}

	test_bindOptionsGetBufferCopy_checkSize {
		var resourceManager, setArray;

		binding.setOptions([\liveBufferShortCopy, \liveBufferLongCopy]);
		resourceManager = ResourceManager.new;

		setArray = binding.bindOptions([\thing, 198], resourceManager);
		Server.local.sync;

		this.assertEquals(setArray.size, 6);
	}

	test_bindOptionsGetBufferCopy_checkBuffers {
		var resourceManager, setArray;

		binding.setOptions([\liveBufferShortCopy, \liveBufferLongCopy]);
		resourceManager = ResourceManager.new;

		setArray = binding.bindOptions([\thing, 198], resourceManager);
		Server.local.sync;

		this.assert(setArray[3].isKindOf(Buffer));
		this.assert(setArray[5].isKindOf(Buffer));
	}
}

TestPatternInstrumentBinding : UnitTest {
	var binding;

	setUp {
		binding = PatternInstrumentBinding.new;
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		binding = nil;
		Server.local.quit;
	}

	test_bindBuffers_bufferIdentity {
		var buffers, arguments;

		buffers = [
			Buffer.new(Server.local, Server.local.sampleRate * 2, 2),
			Buffer.new(Server.local, Server.local.sampleRate * 1, 2)
		];
		Server.local.sync;
		binding.setBuffers(buffers);
		Pbindef(\tester);

		binding.bindBuffers(\tester);

		arguments = Pbindef(\tester).repositoryArgs;

		this.assertEquals(arguments[2], buffers[0]);
		this.assertEquals(arguments[4], buffers[1]);
	}

	test_bindOptionsGetBuffer {
		var resourceManager, arguments;

		binding.setOptions([\liveBufferShort, \liveBufferLong]);
		resourceManager = ResourceManager.new;
		Pbindef(\tester);

		binding.bindOptions(\tester, resourceManager);
		Server.local.sync;
		arguments = Pbindef(\tester).repositoryArgs;

		this.assert(arguments[2].isKindOf(Buffer));
		this.assert(arguments[4].isKindOf(Buffer));
	}

	test_bindOptionsGetBufferCopy {
		var resourceManager, arguments;

		binding.setOptions([\liveBufferShortCopy, \liveBufferLongCopy]);
		resourceManager = ResourceManager.new;
		Pbindef(\tester);

		binding.bindOptions(\tester, resourceManager);
		Server.local.sync;
		arguments = Pbindef(\tester).repositoryArgs;

		this.assert(arguments[2].isKindOf(Buffer));
		this.assert(arguments[4].isKindOf(Buffer));
	}
}


TestSynthInstrument : UnitTest {

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		Server.local.quit;
	}

	test_createInstrument {
		var inst;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]);
		this.assertEquals(inst.name, \tester);
		this.assertEquals(inst.synthDef, \sine);
		this.assert(inst.params.notNil);
		this.assert(inst.defaults.isEmpty);
		this.assertEquals(inst.freeSelf, false);
		this.assertEquals(inst.componentDepth, nil);
		this.assertEquals(inst.componentType, \synthHost);
	}

	test_makeParamCommands_checkSize {
		var inst, commands;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands.size, 2);
	}

	test_makeParamCommands_checkNames {
		var inst, commands;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands[0].paramName, \freq);
		this.assertEquals(commands[1].paramName, \whatever);
	}

	test_makeParamCommands_checkLow {
		var inst, commands;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands[0].low, 200);
		this.assertEquals(commands[1].low, 1);
	}

	test_makeParamCommands_checkHigh {
		var inst, commands;

		inst = SynthInstrument(\tester, \sine, [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands[0].high, 4000);
		this.assertEquals(commands[1].high, 9);
	}
}


TestPatternInstrument : UnitTest {

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		Server.local.quit;
	}

	test_makeParamCommands_checkSize {
		var inst, commands;

		inst = PatternInstrument(\tester, Pbindef(\test), [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands.size, 2);
	}

	test_makeParamCommands_checkNames {
		var inst, commands;

		inst = PatternInstrument(\tester, Pbindef(\test), [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands[0].paramName, \freq);
		this.assertEquals(commands[1].paramName, \whatever);
	}

	test_makeParamCommands_checkLow {
		var inst, commands;

		inst = PatternInstrument(\tester, Pbindef(\test), [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands[0].low, 200);
		this.assertEquals(commands[1].low, 1);
	}

	test_makeParamCommands_checkHigh {
		var inst, commands;

		inst = PatternInstrument(\tester, Pbindef(\test), [[\freq, 200, 4000], [\whatever, 1, 9]]);

		commands = inst.makeParamCommands;

		this.assertEquals(commands[0].high, 4000);
		this.assertEquals(commands[1].high, 9);
	}
}


TestSetInstrument : UnitTest {
	var midiModel, mixerSys;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;

		midiModel = MidiModel(2);
		midiModel.addContinuousModule(\instAmpCtrl, 8, 0, -1758150572, [0,1,2,3,4,5,6,7]);
		midiModel.addContinuousModule(\instPanCtrl, 8, 0, -1758150572, [16,17,18,19,20,21,22,23]);
		midiModel.addButtonModule(\instBuildCtrl, 8, 0, -1758150572, [64,65,66,67,68,69,70,71]);
		midiModel.addButtonModule(\instDestroyCtrl, 8, 0, -1758150572, [48,49,50,51,52,53,54,55]);
		midiModel.addButtonModule(\channelFocusCtrl, 8, 0, -1758150572, [32,33,34,35,36,37,38,39]);
		midiModel.addContinuousModule(\instSendCtrl, 8, 0, -631467386, [12,13,14,15,16,17,18,19]);
		midiModel.addContinuousModule(\instParamCtrl, 8, 0, -631467386, [22,23,24,25,26,27,28,29]);
		midiModel.addContinuousModule(\fxAmpCtrl, 8, 1, -631467386, [12,13,14,15,16,17,18,19]);
		midiModel.addButtonModule(\fxBuildCtrl, 8, 1, -631467386, [32,33,34,35,36,37,38,39]);
		midiModel.addContinuousModule(\fxSendCtrl, 8, 2, -631467386, [12,13,14,15,16,17,18,19]);
		midiModel.addContinuousModule(\fxParamCtrl, 8, 1, -631467386, [22,23,24,25,26,27,28,29]);

		mixerSys = MixerSystem(8, 2, midiModel);
		mixerSys.defineLayerMidi(0,
			\instAmpCtrl, \instPanCtrl, \instBuildCtrl, \instDestroyCtrl,
			\channelFocusCtrl, \instSendCtrl
		);
		mixerSys.defineLayerMidi(1,
			\fxAmpCtrl, \instPanCtrl, \fxBuildCtrl, \fxBuildCtrl,
			\channelFocusCtrl, \fxSendCtrl
		);
		mixerSys.defineAssignableMidi(0, \instParamCtrl);
		mixerSys.defineAssignableMidi(1, \fxParamCtrl);

		mixerSys.registerInstrument(SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]));
		mixerSys.registerInstrument(PatternInstrument(\patternInstTest,
			{
				arg event;

				Pbindef(event.at(\name),
					\instrument, \sine2,
					\dur, Pfunc( {event.at(\param2)} ),
					\freq, Pfunc( {event.at(\param1)} ),
					\amp, 1.0,
					\atk, Pfunc( {event.at(\param3)} ),
					\rel, Pfunc( {event.at(\param4)} )
				);
			},
			[[\param1, 200, 5000], [\param2, 0.1, 2.0], [\param3, 0.001, 2.0], [\param4, 0.001, 2.0]]
		));
	}

	tearDown {
		midiModel = nil;
		mixerSys = nil;
		Server.local.quit;
	}

	test_blockMade {
		mixerSys.setInstrument(\tester, 0, 0, nil, nil, nil);

		this.assert(mixerSys.getBlock(0, 0).notNil);
	}

	test_instrumentSetInStructure {
		var structure, instrument1, instrument2;

		structure = mixerSys.structure;
		mixerSys.setInstrument(\tester, 0, 0, nil, nil, nil);
		mixerSys.setInstrument(\patternInstTest, 1, 0, nil, nil, nil);
		instrument1 = structure.getInstrument([0,0]);
		instrument2 = structure.getInstrument([1,0]);
		this.assert((instrument1 === mixerSys.instrumentDictionary.getInstrument(\tester)).not);
		this.assert((instrument2 === mixerSys.instrumentDictionary.getInstrument(\patternInstTest)).not);
		this.assertEquals(instrument1.name, \tester);
		this.assertEquals(instrument2.name, \patternInstTest);
	}

	test_instrumentBindingSet {
		var instrument1, instrument2, binding1, binding2;

		mixerSys.setInstrument(\tester, 0, 0, nil, 0.5, 0.2);
		mixerSys.setInstrument(\patternInstTest, 1, 0, nil, 0.1, 0.05);
		instrument1 = mixerSys.structure.getInstrument([0,0]);
		instrument2 = mixerSys.structure.getInstrument([1,0]);
		binding1 = instrument1.binding;
		binding2 = instrument2.binding;

		this.assert(binding1.isKindOf(SynthInstrumentBinding));
		this.assert(binding1.buffers.isEmpty);
		this.assertEquals(binding1.mainAmp, 0.5);
		this.assertEquals(binding1.sendAmp, 0.2);

		this.assert(binding2.isKindOf(PatternInstrumentBinding));
		this.assert(binding2.buffers.isEmpty);
		this.assertEquals(binding2.mainAmp, 0.1);
		this.assertEquals(binding2.sendAmp, 0.05);
	}

	test_hostComponentSet {
		var instrument1, instrument2, binding1, binding2;

		mixerSys.setInstrument(\tester, 0, 0, nil, 0.5, 0.2);
		mixerSys.setInstrument(\patternInstTest, 1, 0, nil, 0.1, 0.025);
		instrument1 = mixerSys.structure.getInstrument([0,0]);
		instrument2 = mixerSys.structure.getInstrument([1,0]);
		binding1 = instrument1.binding;
		binding2 = instrument2.binding;

		this.assertEquals(binding1.hostComponent, mixerSys.structure.getNode([0,0]).component);
		this.assertEquals(binding2.hostComponent, mixerSys.structure.getNode([1,0]).component);
	}

	test_setBuildCommand {
		var commands1, commands2;

		mixerSys.setInstrument(\tester, 0, 0, nil, 0.5, 0.2);
		mixerSys.setInstrument(\patternInstTest, 1, 0, nil, 0.4, 0.3);
		commands1 = mixerSys.mappings[0].getCommands(\build, 0);
		commands2 = mixerSys.mappings[0].getCommands(\build, 1);
		this.assertEquals(commands1[0].channelNum, 0);
		this.assertEquals(commands1[0].layerNum, 0);
		this.assertEquals(commands2[0].channelNum, 1);
		this.assertEquals(commands2[0].layerNum, 0);
	}

	test_setBuildCommandAgain {
		var commands1, commands2;

		mixerSys.setInstrument(\tester, 2, 1, nil, 0.1, 0.1);
		mixerSys.setInstrument(\patternInstTest, 0, 1, nil, 0.8, 0.9);
		commands1 = mixerSys.mappings[1].getCommands(\build, 2);
		commands2 = mixerSys.mappings[1].getCommands(\build, 0);

		this.assertEquals(commands1[0].channelNum, 2);
		this.assertEquals(commands1[0].layerNum, 1);
		this.assertEquals(commands2[0].channelNum, 0);
		this.assertEquals(commands2[0].layerNum, 1);
	}
}


TestBuildInstrument : UnitTest {
	var midiModel, mixerSys;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;

		midiModel = MidiModel(2);
		midiModel.addContinuousModule(\instAmpCtrl, 8, 0, -1758150572, [0,1,2,3,4,5,6,7]);
		midiModel.addContinuousModule(\instPanCtrl, 8, 0, -1758150572, [16,17,18,19,20,21,22,23]);
		midiModel.addButtonModule(\instBuildCtrl, 8, 0, -1758150572, [64,65,66,67,68,69,70,71]);
		midiModel.addButtonModule(\instDestroyCtrl, 8, 0, -1758150572, [48,49,50,51,52,53,54,55]);
		midiModel.addButtonModule(\channelFocusCtrl, 8, 0, -1758150572, [32,33,34,35,36,37,38,39]);
		midiModel.addContinuousModule(\instSendCtrl, 8, 0, -631467386, [12,13,14,15,16,17,18,19]);
		midiModel.addContinuousModule(\instParamCtrl, 8, 0, -631467386, [22,23,24,25,26,27,28,29]);
		midiModel.addContinuousModule(\fxAmpCtrl, 8, 1, -631467386, [12,13,14,15,16,17,18,19]);
		midiModel.addButtonModule(\fxBuildCtrl, 8, 1, -631467386, [32,33,34,35,36,37,38,39]);
		midiModel.addContinuousModule(\fxSendCtrl, 8, 2, -631467386, [12,13,14,15,16,17,18,19]);
		midiModel.addContinuousModule(\fxParamCtrl, 8, 1, -631467386, [22,23,24,25,26,27,28,29]);

		mixerSys = MixerSystem(8, 2, midiModel);
		mixerSys.defineLayerMidi(0,
			\instAmpCtrl, \instPanCtrl, \instBuildCtrl, \instDestroyCtrl,
			\channelFocusCtrl, \instSendCtrl
		);
		mixerSys.defineLayerMidi(1,
			\fxAmpCtrl, \instPanCtrl, \fxBuildCtrl, \fxBuildCtrl,
			\channelFocusCtrl, \fxSendCtrl
		);
		mixerSys.defineAssignableMidi(0, \instParamCtrl);
		mixerSys.defineAssignableMidi(1, \fxParamCtrl);

		mixerSys.registerInstrument(SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]));
		mixerSys.registerInstrument(PatternInstrument(\patternInstTest,
			{
				arg event;

				Pbindef(event.at(\name),
					\instrument, \sine2,
					\dur, Pfunc( {event.at(\param2)} ),
					\freq, Pfunc( {event.at(\param1)} ),
					\amp, 1.0,
					\atk, Pfunc( {event.at(\param3)} ),
					\rel, Pfunc( {event.at(\param4)} )
				);
			},
			[[\param1, 200, 5000], [\param2, 0.1, 2.0], [\param3, 0.001, 2.0], [\param4, 0.001, 2.0]]
		));
	}

	tearDown {
		midiModel = nil;
		mixerSys = nil;
		Server.local.quit;
	}

	test_SynthInstrumentBuild {
		var node;

		mixerSys.setInstrument(\tester, 0, 0, nil, 0, 0);
		mixerSys.buildInstrument(0, 0);
		node = mixerSys.structure.getNode([0,0]);
		this.wait(node.component.synth.notNil, maxTime: 1.0);
		this.wait(node.checkActive, maxTime: 0.5);

		node.instrument.destroy;
	}

	test_SynthInstrumentBuildCommands {
		var ampCommands, sendCommands, focusCommands;

		mixerSys.setInstrument(\tester, 1, 0, nil, 0, 0);
		mixerSys.buildInstrument(1, 0);
		ampCommands = mixerSys.mappings[0].getCommands(\amp, 1);
		sendCommands = mixerSys.mappings[0].getCommands(\send, 1);
		focusCommands = mixerSys.mappings[0].getCommands(\focus, 1);

		this.assertEquals(ampCommands[0].index, 0);
		this.assertEquals(sendCommands[0].index, 0);
		this.assertEquals(focusCommands[0].channelNum, 1);
		this.assertEquals(focusCommands[0].layerNum, 0);

		mixerSys.structure.getNode([1,0]).instrument.destroy;
	}

	test_SynthInstrumentBuildAssignables {
		var element1Commands;

		mixerSys.setInstrument(\tester, 1, 0, nil, 0, 0);
		mixerSys.buildInstrument(1, 0);
		element1Commands = mixerSys.mappings[0].getAllModuleCommands(\ctrls);

		this.assertEquals(element1Commands[0][0].paramName, \freq);
		this.assertEquals(element1Commands[0][0].low, 200);
		this.assertEquals(element1Commands[0][0].high, 4000);

		mixerSys.structure.getNode([1,0]).instrument.destroy;
	}

	test_PatternInstrumentBuild {
		var node;

		mixerSys.setInstrument(\patternInstTest, 0, 0, nil, 0, 0);
		mixerSys.buildInstrument(0, 0);
		node = mixerSys.structure.getNode([0,0]);
		this.wait(node.component.patternDef.notNil, 1.0);
		this.assertEquals(node.component.patternName, \patternInstTest);

		node.instrument.destroy;
	}

	test_PatternInstrumentBuildCommands {
		var ampCommands, sendCommands, focusCommands;

		mixerSys.setInstrument(\patternInstTest, 1, 0, nil, 0, 0);
		mixerSys.buildInstrument(1, 0);
		ampCommands = mixerSys.mappings[0].getCommands(\amp, 1);
		sendCommands = mixerSys.mappings[0].getCommands(\send, 1);
		focusCommands = mixerSys.mappings[0].getCommands(\focus, 1);

		this.assertEquals(ampCommands[0].index, 0);
		this.assertEquals(sendCommands[0].index, 0);
		this.assertEquals(focusCommands[0].channelNum, 1);
		this.assertEquals(focusCommands[0].layerNum, 0);

		mixerSys.structure.getNode([1,0]).instrument.destroy;
	}

	test_PatternInstrumentBuildAssignables {
		var commands;

		mixerSys.setInstrument(\patternInstTest, 1, 0, nil, 0, 0);
		mixerSys.buildInstrument(1, 0);
		commands = mixerSys.mappings[0].getAllModuleCommands(\ctrls);

		this.assertEquals(commands[0][0].paramName, \param1);
		this.assertEquals(commands[0][0].low, 200);
		this.assertEquals(commands[0][0].high, 5000);

		this.assertEquals(commands[3][0].paramName, \param4);
		this.assertEquals(commands[3][0].low, 0.001);
		this.assertEquals(commands[3][0].high, 2.0);

		this.wait(Pbindef(\patternInstTest).isPlaying, maxTime: 4);

		mixerSys.structure.getNode([1,0]).instrument.destroy;
	}
}




