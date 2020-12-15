TestStructNode : UnitTest {
	var node, component;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		component = ComponentParallel(nil, nil, 4);
		node = StructNode(component, nil, 4, 0);
	}

	tearDown {
		node = nil;
		component = nil;
		Server.local.quit;
	}

	test_addComponent_childExists {
		node.addComponent(0, 2, \parallel);

		this.assert(node.childNodes.at(0).isKindOf(StructNode));
	}

	test_addComponent_checkReferences {
		node.addComponent(0, 2, \parallel);

		this.assertEquals(node.childNodes.at(0).parentNode, node);
		this.assertEquals(node.childNodes.at(0).indexInParent, 0);
	}

	test_addComponent_checkComponentReferences {
		var child, nodeComponent, childComponent;

		node.addComponent(0, 2, \parallel);
		child = node.childNodes.at(0);
		nodeComponent = node.component;
		childComponent = child.component;

		this.assertEquals(nodeComponent.children[0], childComponent);
		this.assertEquals(childComponent.parent, nodeComponent);
	}

	teat_clearComponent {
		var child;

		node.addComponent(0, 2, \parallel);
		child = node.childNodes.at(0);

		child.clearComponent;

		this.assert(node.childNodes.isEmpty);
	}
}


TestComponentStruct : UnitTest {
	var struct;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		struct = ComponentStruct(4);
	}

	tearDown {
		struct.clearStruct;
		struct = nil;
		Server.local.quit;
	}

	test_setSynthHosts_checkConnections {
		var node1, node2;

		struct.addComponent([0], nil, \host);
		struct.addComponent([1], nil, \host);

		node1 = struct.getNode([0]);
		node2 = struct.getNode([1]);

		this.assertEquals(node1.component.getOutput, struct.mainComponent.sumBus);
		this.assertEquals(node2.component.getOutput, struct.mainComponent.sumBus);
	}

	test_setSynthHosts_nodeRelationships {
		var node1, node2;

		struct.addComponent([0], nil, \host);
		struct.addComponent([1], nil, \host);

		node1 = struct.getNode([0]);
		node2 = struct.getNode([1]);

		this.assertEquals(node1.parentNode, struct.rootNode);
		this.assertEquals(node2.parentNode, struct.rootNode);
	}

	test_setSynthHosts_componentRelationships {
		var node1, node2;

		struct.addComponent([0], nil, \host);
		struct.addComponent([1], nil, \host);

		node1 = struct.getNode([0]);
		node2 = struct.getNode([1]);

		this.assertEquals(node1.component.parent, struct.mainComponent);
		this.assertEquals(node2.component.parent, struct.mainComponent);
	}

	test_nestingSerialComplex_channelConnections {
		var channel1, channel2, node1, node2, node3;

		struct.addComponent([0], 2, \serialComplex);
		channel1 = struct.getNode([0]);
		struct.addComponent([1], 2, \serialComplex);
		channel2 = struct.getNode([1]);

		struct.addComponent([0,0], nil, \host);
		node1 = struct.getNode([0,0]);
		struct.addComponent([1,0], nil, \host);
		node2 = struct.getNode([1,0]);
		struct.addComponent([1,1], nil, \host);
		node3 = struct.getNode([1,1]);

		this.assertEquals(channel1.component.getOutput, struct.mainComponent.sumBus);
		this.assertEquals(channel2.component.getOutput, struct.mainComponent.sumBus);
	}

	test_nestingSerialComplex_hostConnections {
		var channel1, channel2, node1, node2, node3;

		struct.addComponent([0], 2, \serialComplex);
		channel1 = struct.getNode([0]);
		struct.addComponent([1], 2, \serialComplex);
		channel2 = struct.getNode([1]);

		struct.addComponent([0,0], nil, \host);
		node1 = struct.getNode([0,0]);
		struct.addComponent([1,0], nil, \host);
		node2 = struct.getNode([1,0]);
		struct.addComponent([1,1], nil, \host);
		node3 = struct.getNode([1,1]);

		this.assertEquals(node1.component.getInput, nil);
		this.assertEquals(node3.component.getInput, node2.component.getOutput);
		this.assertEquals(node3.component.getOutput, channel2.component.busses[1]);
	}

	test_nestingSerialComplex_nodeRelationships {
		var channel1, channel2, node1, node2, node3;

		struct.addComponent([0], 2, \serialComplex);
		channel1 = struct.getNode([0]);
		struct.addComponent([1], 2, \serialComplex);
		channel2 = struct.getNode([1]);

		struct.addComponent([0,0], nil, \host);
		node1 = struct.getNode([0,0]);
		struct.addComponent([1,0], nil, \host);
		node2 = struct.getNode([1,0]);
		struct.addComponent([1,1], nil, \host);
		node3 = struct.getNode([1,1]);

		this.assertEquals(node3.parentNode, channel2);
		this.assertEquals(node1.parentNode, channel1);
		this.assertEquals(channel1.parentNode, struct.rootNode);
	}

	test_nestingSerialComplex_componentRelationships {
		var channel1, channel2, node1, node2, node3;

		struct.addComponent([0], 2, \serialComplex);
		channel1 = struct.getNode([0]);
		struct.addComponent([1], 2, \serialComplex);
		channel2 = struct.getNode([1]);

		struct.addComponent([0,0], nil, \host);
		node1 = struct.getNode([0,0]);
		struct.addComponent([1,0], nil, \host);
		node2 = struct.getNode([1,0]);
		struct.addComponent([1,1], nil, \host);
		node3 = struct.getNode([1,1]);

		this.assertEquals(node1.component.parent, channel1.component);
		this.assertEquals(node2.component.parent, channel2.component);
		this.assertEquals(channel2.component.parent, struct.mainComponent);
	}

	test_clearComponent {
		var channel;

		struct.addComponent([0], 2, \serialComplex);
		struct.addComponent([0,0], nil, \host);
		struct.addComponent([0,1], nil, \host);
		channel = struct.getNode([0]);

		struct.clearComponent([0,1]);

		this.assert(channel.component.getChild(0).notNil);
		this.assert(channel.component.getChild(1).isNil);
	}
}




TestMixerSystem : UnitTest {
	var midiModel, mixerSys;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/testInstruments.scd";

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
	}

	tearDown {
		midiModel = nil;
		mixerSys = nil;
		Server.local.quit;
	}

	test_setInstrumentSynth_instrumentType {
		var node;

		mixerSys.registerInstrument(SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]));
		mixerSys.setInstrument(\tester, 0, 0, nil, nil, nil);
		node = mixerSys.structure.getNode([0,0]);

		this.assert(node.instrument.isKindOf(SynthInstrument));
	}

	test_setInstrumentSynth_instrumentProperties {
		var node, instrument;

		mixerSys.registerInstrument(SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]));
		mixerSys.setInstrument(\tester, 0, 0, nil, nil, nil);
		node = mixerSys.structure.getNode([0,0]);

		instrument = node.instrument;
		this.assertEquals(instrument.name, \tester);
		this.assertEquals(instrument.synthDef, \sine);
	}

	test_setInstrumentPattern_instrumentType {
		var node;

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
		mixerSys.setInstrument(\patternInstTest, 0, 0, nil, nil, nil);
		node = mixerSys.structure.getNode([0,0]);

		this.assert(node.instrument.isKindOf(PatternInstrument));
	}

	test_setInstrumentPattern_instrumentProperties {
		var node;

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
		mixerSys.setInstrument(\patternInstTest, 0, 0, nil, nil, nil);
		node = mixerSys.structure.getNode([0,0]);

		this.assertEquals(node.instrument.name, \patternInstTest);
		this.assert(node.instrument.def.notNil);
	}

	test_setInstrument_alreadySet {
		var node, instrument;

		mixerSys.registerInstrument(SynthInstrument(\firstOne, \sine, [[\freq, 200, 800]]));
		mixerSys.setInstrument(\firstOne, 1, 0, nil, nil, nil);

		mixerSys.registerInstrument(SynthInstrument(\secondOne, \sine, [[\whatever, 1, 9]]));
		mixerSys.setInstrument(\secondOne, 1, 0, nil, nil, nil);

		node = mixerSys.structure.getNode([1,0]);
		instrument = node.instrument;

		this.assertEquals(instrument.name, \secondOne);
		this.assertEquals(instrument.params[0], [\whatever, 1, 9]);
	}

	test_setThenClear {
		var node;

		mixerSys.registerInstrument(SynthInstrument(\tester, \sine, [[\freq, 200, 4000]]));
		mixerSys.setInstrument(\tester, 0, 0, nil, nil, nil);

		mixerSys.clearInstrument(0, 0);
		node = mixerSys.structure.getNode([0,0]);

		this.assert(node.instrument.isNil);
	}
}


