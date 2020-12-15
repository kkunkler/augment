TestComponentParallel : UnitTest {
	var mainComp;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		mainComp = ComponentParallel(nil, nil, 4);
	}

	tearDown {
		mainComp.cleanUp;
		mainComp = nil;
		Server.local.quit;
	}

	test_initialization {
		this.assert(mainComp.sumBus.notNil, "sumBus should be set");
		this.assert(mainComp.endMixer.notNil, "endMixer should be set");
		this.assertEquals(mainComp.level, 0);
		this.assertEquals(mainComp.depth, 4);
	}

	test_nilGetInOut {
		this.assertEquals(mainComp.getInput, nil);
		this.assertEquals(mainComp.getOutput, mainComp.sumBus);
	}

	test_nestParallel_numChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = mainComp.addComponent(2, 2, \parallel);

		this.assertEquals(mainComp.numChildren, 2);
	}

	test_nestParallel_checkChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = mainComp.addComponent(2, 2, \parallel);

		this.assert(mainComp.children[0].notNil);
		this.assert(mainComp.children[2].notNil);
		this.assert(mainComp.children[1].isNil);
	}

	test_nestParallel_checkOutputs {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = mainComp.addComponent(2, 2, \parallel);

		this.assertEquals(nested1.getOutput, mainComp.sumBus);
		this.assertEquals(nested2.getOutput, mainComp.sumBus);
	}

	test_nestParallel_componentLevel {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = mainComp.addComponent(2, 2, \parallel);

		this.assertEquals(nested1.level, 1);
		this.assertEquals(nested2.level, 1);
	}

	test_doubleNestParallel_numChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = nested1.addComponent(1, 2, \parallel);

		this.assertEquals(mainComp.numChildren, 1);
		this.assertEquals(nested1.numChildren, 1);
		this.assertEquals(nested2.numChildren, 0);
	}

	test_doubleNestParallel_checkOutputs {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = nested1.addComponent(1, 2, \parallel);

		this.assertEquals(nested1.getOutput, mainComp.sumBus);
		this.assertEquals(nested2.getOutput, mainComp.sumBus);
	}

	test_doubleNestParallel_componentLevels {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = nested1.addComponent(0, 2, \parallel);

		this.assertEquals(nested1.level, 1);
		this.assertEquals(nested2.level, 2);
	}

	test_doubleNestParallel_backtraceIndices {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = nested1.addComponent(0, 2, \parallel);

		this.assertEquals(nested2.backtraceIndices, [0, 0]);
	}

	test_mainComp_backtraceIndices {
		this.assert(mainComp.backtraceIndices.isEmpty);
	}

	test_getNearestChild_basic {
		var nested1, nested2, nearest;

		nested1 = mainComp.addComponent(0, 2, \parallel);
		nested2 = mainComp.addComponent(3, 2, \parallel);

		nearest = mainComp.getNearestChild(1);

		this.assertEquals(nearest, nested1);
	}

	test_getNearestChild_noChildren {
		var nearest;

		nearest = mainComp.getNearestChild(2);

		this.assert(nearest.isNil);
	}

	test_getNearestChild_occupiedIndex {
		var nested, nearest;

		nested = mainComp.addComponent(1, 2, \parallel);

		nearest = mainComp.getNearestChild(1);

		// should not return the occupied index, since this generally represents the component
		// making the request in the first place
		this.assertEquals(nearest, nil);
	}
}

TestComponentSerialSimple : UnitTest {
	var mainComp;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		mainComp = ComponentSerialSimple(nil, nil, 4);
	}

	tearDown {
		mainComp.cleanUp;
		mainComp = nil;
		Server.local.quit;
	}

	test_initialization {
		this.assert(mainComp.sumBus.notNil);
		this.assert(mainComp.endMixer.notNil);
		this.assertEquals(mainComp.level, 0);
		this.assertEquals(mainComp.depth, 4);
		this.assert(mainComp.busses[2].notNil);
	}

	test_nestSerialSimple_numChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialSimple);
		nested2 = mainComp.addComponent(1, 3, \serialSimple);

		this.assertEquals(mainComp.numChildren, 2);
	}

	test_nestSerialSimple_checkChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialSimple);
		nested2 = mainComp.addComponent(1, 3, \serialSimple);

		this.assert(mainComp.children[0].notNil);
		this.assert(mainComp.children[1].notNil);
		this.assert(mainComp.children[2].isNil);
		this.assert(mainComp.children[3].isNil);
	}

	test_nestSerialSimple_checkOutput {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialSimple);
		nested2 = mainComp.addComponent(1, 3, \serialSimple);

		this.assertEquals(nested1.getOutput, mainComp.busses[0]);
		this.assertEquals(nested2.getOutput, mainComp.busses[1]);
	}

	test_nestSerialSimple_checkInput {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialSimple);
		nested2 = mainComp.addComponent(1, 2, \serialSimple);

		this.assertEquals(nested1.getInput, nil);
		this.assertEquals(nested2.getInput, nested1.getOutput);
	}

	test_nestSerialSimple_checkLevel {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialSimple);
		nested2 = mainComp.addComponent(1, 3, \serialSimple);

		this.assertEquals(nested1.level, 1);
		this.assertEquals(nested2.level, 1);
	}

	test_deepNestSerialSimple_numChildren {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialSimple);
		nested2 = nested1.addComponent(0, 2, \serialSimple);
		nested3 = nested1.addComponent(1, 2, \serialSimple);

		this.assertEquals(mainComp.numChildren, 1);
		this.assertEquals(nested1.numChildren, 2);
	}

	test_deepNestSerialSimple_checkChildren {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialSimple);
		nested2 = nested1.addComponent(0, 2, \serialSimple);
		nested3 = nested1.addComponent(1, 2, \serialSimple);

		this.assert(mainComp.children[1].notNil);
		this.assert(nested1.children[0].notNil);
		this.assert(nested1.children[1].notNil);
	}

	test_deepNestSerialSimple_checkOutputs {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialSimple);
		nested2 = nested1.addComponent(0, 2, \serialSimple);
		nested3 = nested1.addComponent(1, 2, \serialSimple);

		this.assertEquals(nested1.getOutput, mainComp.busses[1]);
		this.assertEquals(nested2.getOutput, nested1.busses[0]);
		this.assertEquals(nested3.getOutput, nested1.getOutput);
	}

	test_deepNestSerialSimple_checkInputs {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialSimple);
		nested2 = nested1.addComponent(0, 2, \serialSimple);
		nested3 = nested1.addComponent(1, 2, \serialSimple);

		this.assertEquals(nested1.getInput, mainComp.busses[0]);
		this.assertEquals(nested2.getInput, nested1.getInput);
		this.assertEquals(nested3.getInput, nested1.busses[0]);
	}

	test_deepNestSerialSimple_checkLevels {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialSimple);
		nested2 = nested1.addComponent(0, 2, \serialSimple);
		nested3 = nested1.addComponent(1, 2, \serialSimple);

		this.assertEquals(nested1.level, 1);
		this.assertEquals(nested2.level, 2);
		this.assertEquals(nested3.level, 2);
	}

	test_deepNestSerialSimple_backtraceIndices {
		var nested1, nested2, nested3, indices2, indices3;

		nested1 = mainComp.addComponent(1, 2, \serialSimple);
		nested2 = nested1.addComponent(0, 2, \serialSimple);
		nested3 = nested1.addComponent(1, 2, \serialSimple);

		indices2 = nested2.backtraceIndices;
		indices3 = nested3.backtraceIndices;

		this.assertEquals(indices2, [1,0]);
		this.assertEquals(indices3, [1,1]);
	}

	test_noNesting_backtraceIndices {
		var indices;

		indices = mainComp.backtraceIndices;

		this.assert(indices.isEmpty);
	}

	test_getNearestChild_basic {
		var nested, nearest;

		nested = mainComp.addComponent(0, 2, \serialSimple);
		nearest = mainComp.getNearestChild(1);

		this.assertEquals(nested, nearest);
	}

	test_getNearestChild_noChildren {
		var nearest;

		nearest = mainComp.getNearestChild(2);

		this.assert(nearest.isNil);
	}

	test_getNearestChild_occupiedIndex {
		var nested, nearest;

		nested = mainComp.addComponent(2, 2, \serialSimple);
		nearest = mainComp.getNearestChild(2);

		this.assert(nearest.isNil);
	}
}


TestComponentSerialComplex : UnitTest {
	var mainComp;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
		mainComp = ComponentSerialComplex(nil, nil, 4);
	}

	tearDown {
		mainComp.cleanUp;
		mainComp = nil;
		Server.local.quit;
	}

	test_initialization {
		this.assert(mainComp.sumBus.notNil);
		this.assert(mainComp.endMixer.notNil);
		this.assertEquals(mainComp.level, 0);
		this.assertEquals(mainComp.depth, 4);
		this.assert(mainComp.busses[3].notNil);
		this.assert(mainComp.busses[0].notNil);
		this.assert(mainComp.mixers[3].notNil);
		this.assert(mainComp.mixers[0].notNil);
	}

	test_nestSerialComplex_numChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialComplex);
		nested2 = mainComp.addComponent(1, 3, \serialComplex);

		this.assertEquals(mainComp.numChildren, 2);
	}

	test_nestSerialComplex_checkChildren {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialComplex);
		nested2 = mainComp.addComponent(1, 3, \serialComplex);

		this.assert(mainComp.children[0].notNil);
		this.assert(mainComp.children[1].notNil);
		this.assert(mainComp.children[2].isNil);
	}

	test_nestSerialComplex_checkOutputs {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialComplex);
		nested2 = mainComp.addComponent(1, 3, \serialComplex);

		this.assertEquals(nested1.getOutput, mainComp.busses[0]);
		this.assertEquals(nested2.getOutput, mainComp.busses[1]);
	}

	test_nestSerialComplex_checkInputs {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialComplex);
		nested2 = mainComp.addComponent(1, 3, \serialComplex);

		this.assertEquals(nested1.getInput, nil);
		this.assertEquals(nested2.getInput, mainComp.busses[0]);
	}

	test_nestSerialComplex_checkLevels {
		var nested1, nested2;

		nested1 = mainComp.addComponent(0, 2, \serialComplex);
		nested2 = mainComp.addComponent(1, 3, \serialComplex);

		this.assertEquals(nested1.level, 1);
		this.assertEquals(nested2.level, 1);
	}

	test_deepNestSerialComplex_numChildren {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialComplex);
		nested2 = nested1.addComponent(1, 2, \serialComplex);
		nested3 = nested2.addComponent(0, 2, \serialComplex);

		this.assertEquals(mainComp.numChildren, 1);
		this.assertEquals(nested1.numChildren, 1);
		this.assertEquals(nested2.numChildren, 1);
	}

	test_deepNestSerialComplex_checkOutputs {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialComplex);
		nested2 = nested1.addComponent(1, 2, \serialComplex);
		nested3 = nested2.addComponent(0, 2, \serialComplex);

		this.assertEquals(nested1.getOutput, mainComp.busses[1]);
		this.assertEquals(nested2.getOutput, nested1.busses[1]);
		this.assertEquals(nested3.getOutput, nested2.busses[0]);
	}

	test_deepNestSerialComplex_checkInputs {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialComplex);
		nested2 = nested1.addComponent(1, 2, \serialComplex);
		nested3 = nested2.addComponent(0, 2, \serialComplex);

		this.assertEquals(nested1.getInput, mainComp.busses[0]);
		this.assertEquals(nested2.getInput, nested1.busses[0]);
		this.assertEquals(nested3.getInput, nested2.getInput);
	}

	test_deepNestSerialComplex_checkLevels {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialComplex);
		nested2 = nested1.addComponent(1, 2, \serialComplex);
		nested3 = nested2.addComponent(0, 2, \serialComplex);

		this.assertEquals(nested1.level, 1);
		this.assertEquals(nested2.level, 2);
		this.assertEquals(nested3.level, 3);
	}

	test_deepNestSerialComplex_backtraceIndices {
		var nested1, nested2, nested3;

		nested1 = mainComp.addComponent(1, 2, \serialComplex);
		nested2 = nested1.addComponent(1, 2, \serialComplex);
		nested3 = nested2.addComponent(0, 2, \serialComplex);

		this.assertEquals(nested2.backtraceIndices, [1,1]);
		this.assertEquals(nested3.backtraceIndices, [1,1,0]);
	}

	test_getNearestChild_basic {
		var nested, nearest;

		nested = mainComp.addComponent(2, 2, \serialComplex);
		nearest = mainComp.getNearestChild(3);

		this.assertEquals(nested, nearest);
	}

	test_getNearestChild_noChildren {
		var nearest;

		nearest = mainComp.getNearestChild(2);

		this.assert(nearest.isNil);
	}

	test_getNearestChild_occupiedIndex {
		var nested, nearest;

		nested = mainComp.addComponent(1, 2, \serialComplex);
		nearest = mainComp.getNearestChild(1);

		this.assert(nearest.isNil);
	}
}

TestComponentHost : UnitTest {

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		Server.local.quit;
	}

	test_inParallel_checkInputs {
		var container, nested1, nested2;

		container = ComponentParallel(nil, nil, 2);
		nested1 = container.addComponent(0, nil, \host);
		nested2 = container.addComponent(1, nil, \host);

		this.assertEquals(nested1.getInput, nil);
		this.assertEquals(nested2.getInput, nil);
	}

	test_inParallel_checkOutputs {
		var container, nested1, nested2;

		container = ComponentParallel(nil, nil, 2);
		nested1 = container.addComponent(0, nil, \host);
		nested2 = container.addComponent(1, nil, \host);

		this.assertEquals(nested1.getOutput, container.sumBus);
		this.assertEquals(nested2.getOutput, container.sumBus);
		this.assertEquals(container.getOutput, container.sumBus);
	}

	test_inSerialSimple_checkInputs {
		var container, nested1, nested2;

		container = ComponentSerialSimple(nil, nil, 2);
		nested1 = container.addComponent(0, nil, \host);
		nested2 = container.addComponent(1, nil, \host);

		this.assertEquals(nested1.getInput, nil);
		this.assertEquals(nested2.getInput, container.busses[0]);
	}

	test_inSerialSimple_checkOutputs {
		var container, nested1, nested2;

		container = ComponentSerialSimple(nil, nil, 2);
		nested1 = container.addComponent(0, nil, \host);
		nested2 = container.addComponent(1, nil, \host);

		this.assertEquals(nested1.getOutput, container.busses[0]);
		this.assertEquals(nested2.getOutput, container.sumBus);
		this.assertEquals(container.getOutput, container.sumBus);
	}

	test_inSerialComplex_checkInputs {
		var container, nested1, nested2;

		container = ComponentSerialComplex(nil, nil, 2);
		nested1 = container.addComponent(0, nil, \host);
		nested2 = container.addComponent(1, nil, \host);

		this.assertEquals(nested1.getInput, nil);
		this.assertEquals(nested2.getInput, container.busses[0]);
	}

	test_inSerialComplex_checkOutputs {
		var container, nested1, nested2;

		container = ComponentSerialComplex(nil, nil, 2);
		nested1 = container.addComponent(0, nil, \host);
		nested2 = container.addComponent(1, nil, \host);

		this.assertEquals(nested1.getOutput, container.busses[0]);
		this.assertEquals(nested2.getOutput, container.busses[1]);
		this.assertEquals(container.getOutput, container.sumBus);
	}
}


TestComponentBussing : UnitTest {

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;
	}

	tearDown {
		Server.local.quit;
	}

	test_parallelFirstAndLast_checkInputs {
		var mainComp, first, last;

		mainComp = ComponentParallel(nil, nil, 4);
		first = mainComp.addComponent(0, 2, \serialComplex);
		last = mainComp.addComponent(3, 2, \serialComplex);

		this.assertEquals(first.getInput, nil);
		this.assertEquals(last.getInput, nil);
	}

	test_parallelFirstAndLast_checkOutputs {
		var mainComp, first, last;

		mainComp = ComponentParallel(nil, nil, 4);
		first = mainComp.addComponent(0, 2, \serialComplex);
		last = mainComp.addComponent(3, 2, \serialComplex);

		this.assertEquals(first.getOutput, mainComp.sumBus);
		this.assertEquals(last.getOutput, mainComp.sumBus);
		this.assertEquals(mainComp.getOutput, mainComp.sumBus);
	}

	test_miniMixer_checkChannelInputs {
		var mainComp, channel1, channel2, host1, host2, host3;

		mainComp = ComponentParallel(nil, nil, 4);
		channel1 = mainComp.addComponent(0, 2, \serialComplex);
		channel2 = mainComp.addComponent(1, 2, \serialComplex);
		host1 = channel1.addComponent(0, nil, \host);
		host2 = channel2.addComponent(0, nil, \host);
		host3 = channel2.addComponent(1, nil, \host);

		this.assert(channel1.getInput.isNil);
		this.assert(channel2.getInput.isNil);
	}

	test_miniMixer_checkChannelOutputs {
		var mainComp, channel1, channel2, host1, host2, host3;

		mainComp = ComponentParallel(nil, nil, 4);
		channel1 = mainComp.addComponent(0, 2, \serialComplex);
		channel2 = mainComp.addComponent(1, 2, \serialComplex);
		host1 = channel1.addComponent(0, nil, \host);
		host2 = channel2.addComponent(0, nil, \host);
		host3 = channel2.addComponent(1, nil, \host);

		this.assertEquals(channel1.getOutput, mainComp.sumBus);
		this.assertEquals(channel2.getOutput, mainComp.sumBus);
	}

	test_miniMixer_checkHostInputs {
		var mainComp, channel1, channel2, host1, host2, host3;

		mainComp = ComponentParallel(nil, nil, 4);
		channel1 = mainComp.addComponent(0, 2, \serialComplex);
		channel2 = mainComp.addComponent(1, 2, \serialComplex);
		host1 = channel1.addComponent(0, nil, \host);
		host2 = channel2.addComponent(0, nil, \host);
		host3 = channel2.addComponent(1, nil, \host);

		this.assert(host1.getInput.isNil);
		this.assert(host2.getInput.isNil);
		this.assertEquals(host3.getInput, channel2.busses[0]);
	}

	test_miniMixer_checkHostOutputs {
		var mainComp, channel1, channel2, host1, host2, host3;

		mainComp = ComponentParallel(nil, nil, 4);
		channel1 = mainComp.addComponent(0, 2, \serialComplex);
		channel2 = mainComp.addComponent(1, 2, \serialComplex);
		host1 = channel1.addComponent(0, nil, \host);
		host2 = channel2.addComponent(0, nil, \host);
		host3 = channel2.addComponent(1, nil, \host);

		this.assertEquals(host1.getOutput, channel1.busses[0]);
		this.assertEquals(host2.getOutput, channel2.busses[0]);
		this.assertEquals(host3.getOutput, channel2.busses[1]);
	}
}