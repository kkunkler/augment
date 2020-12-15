CommandMockUp {
	var <val=10;

	*new {
		^super.new;
	}

	execute { |newVal|
		val = newVal;
	}
}





TestMidiElement : UnitTest {
	var element;

	setUp {
		element = MidiElement.new;
	}

	tearDown {
		element = nil;
	}

	test_setVal_basic {
		element.setCurrentVal(56);

		this.assertEquals(element.currentVal, 56);
	}

	test_setVal_negativeError {
		this.assertException({ element.setCurrentVal(-10) }, Error);
	}

	test_setVal_nilError {
		this.assertException({ element.setCurrentVal(nil) }, Error);
	}

	test_addCommand_nilError {
		this.assertException({ element.addCommand(nil) }, Error);
	}

	test_executeCommand_basic {
		var command;

		command = CommandMockUp.new;
		element.addCommand(command);
		element.setCurrentVal(109);

		this.assertEquals(command.val, 109);
	}

	test_executeMultipleCommands_checkFirst {
		var command1, command2, command3;

		command1 = CommandMockUp.new;
		command2 = CommandMockUp.new;
		command3 = CommandMockUp.new;
		element.addCommand(command1);
		element.addCommand(command2);
		element.addCommand(command3);

		element.setCurrentVal(112);

		this.assertEquals(command1.val, 112);
	}

	test_executeMultipleCommands_checkLast {
		var command1, command2, command3;

		command1 = CommandMockUp.new;
		command2 = CommandMockUp.new;
		command3 = CommandMockUp.new;
		element.addCommand(command1);
		element.addCommand(command2);
		element.addCommand(command3);

		element.setCurrentVal(112);

		this.assertEquals(command3.val, 112);
	}

	test_clearCommands_basic {
		var command1, command2;

		command1 = CommandMockUp.new;
		command2 = CommandMockUp.new;
		element.addCommand(command1);
		element.addCommand(command2);

		element.clearCommands;

		this.assert(element.commands.isEmpty);
	}

	test_clearCommands_empty {
		element.clearCommands;
		this.assert(element.commands.isEmpty);
	}

	test_hasCommand_empty {
		element.clearCommands;
		this.assert(element.hasCommand.not);
	}

	test_hasCommand_notEmpty {
		var command;

		command = CommandMockUp.new;
		element.addCommand(command);

		this.assert(element.hasCommand);
	}

	test_getCommands_basic {
		var command, retrievedCommands;

		command = CommandMockUp.new;
		element.addCommand(command);

		retrievedCommands = element.getCommands;

		this.assertEquals(retrievedCommands[0], command);
	}

	test_getCommands_correctNumberGotten {
		var retrievedCommands;

		element.addCommand(CommandMockUp.new);
		element.addCommand(CommandMockUp.new);
		element.addCommand(CommandMockUp.new);

		retrievedCommands = element.getCommands;

		this.assertEquals(retrievedCommands.size, 3);
	}

	test_getCommands_correctOrderCheckLast {
		var commandToCheck, retrievedCommands;

		element.addCommand(CommandMockUp.new);
		element.addCommand(CommandMockUp.new);
		commandToCheck = CommandMockUp.new;
		element.addCommand(commandToCheck);

		retrievedCommands = element.getCommands;

		this.assertEquals(retrievedCommands[2], commandToCheck);
	}

	test_getCommands_correctOrderCheckMiddle {
		var commandToCheck, retrievedCommands;

		element.addCommand(CommandMockUp.new);
		element.addCommand(CommandMockUp.new);
		commandToCheck = CommandMockUp.new;
		element.addCommand(commandToCheck);
		element.addCommand(CommandMockUp.new);
		element.addCommand(CommandMockUp.new);

		retrievedCommands = element.getCommands;

		this.assertEquals(retrievedCommands[2], commandToCheck);
	}
}



TestAbstractMidiModule : UnitTest {
	var module;

	setUp {
		module = MidiButtonModule(\tester, 4, 0, 19998);
	}

	tearDown {
		module = nil;
	}

	test_initialize_elementsAndElementsMap {
		module.initializeElements([19,20,21,22]);

		this.assertEquals(module.elements.size, 0);
		this.assertEquals(module.elementsMap.size, 4);
	}

	test_initialize_midiHandlerType {
		module.initializeElements([19,20,34,35]);

		this.assert(module.midiHandler.isKindOf(MidiHandlerCC));
	}

	test_initialize_emptyError {
		this.assertException({ module.initializeElements([]) }, Error);
	}

	test_initialize_nilError {
		this.assertException({ module.initializeElements(nil) }, Error);
	}

	test_initialize_tooManyMidiNums {
		this.assertException({ module.initializeElements([10,11,12,13,14]) }, Error);
	}

	test_setMappedDepth_correctSize {
		module.initializeElements([19,20,100,120]);
		module.setMappedDepth(3);

		this.assertEquals(module.elements.size, 3);
	}

	test_setMappedDepth_zeroError {
		module.initializeElements([19,20,21,22]);
		this.assertException({ module.setMappedDepth(0) }, Error);
	}

	test_setMappedDepth_depthTooLargeError {
		module.initializeElements([19,20,21,22]);
		this.assertException({ module.setMappedDepth(5) }, Error);
	}

	test_setMappedDepth_checkElements {
		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(2);

		this.assert(module.elements.at(20).isKindOf(MidiElement));
		this.assert(module.elements.at(21).isNil);
	}

	test_setMappedDepth_setTwice {
		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(4);
		module.setMappedDepth(2);

		this.assert(module.elements.at(20).isKindOf(MidiElement));
		this.assert(module.elements.at(21).isNil);
	}

	test_clearElements_basic {
		module.initializeElements([50,52,54,56]);
		module.setMappedDepth(4);

		module.clearElements;

		this.assertEquals(module.elements.size, 0);
	}

	test_clearElements_alreadyClear {
		module.initializeElements([50,52,54,56]);

		module.clearElements;

		this.assertEquals(module.elements.size, 0);
	}

	test_getSetCommands_commandsStartEmpty {
		var commandArray;

		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(4);
		commandArray = module.getCommands(0);
		commandArray = commandArray ++ module.getCommands(1);
		commandArray = commandArray ++ module.getCommands(2);
		commandArray = commandArray ++ module.getCommands(3);

		this.assert(commandArray.isEmpty);
	}

	test_getSetCommands_commandSetCorrectly {
		var commandArray;

		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(4);

		module.setCommand(CommandMockUp.new, 1);
		commandArray = module.getCommands(1);

		this.assert(commandArray[0].isKindOf(CommandMockUp));
	}

	test_getSetCommands_otherCommandsNotSet {
		var commandArray;

		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(4);

		module.setCommand(CommandMockUp.new, 1);
		commandArray = module.getCommands(0);

		this.assert(commandArray.isEmpty);
	}

	test_setCommand_nilError {
		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(4);

		this.assertException({ module.setCommand(nil, 1) }, Error);
	}

	test_clearCommands_oneCommand {
		var commandArray;

		module.initializeElements([19,20,21,22]);
		module.setMappedDepth(4);
		module.setCommand(CommandMockUp.new, 1);

		module.clearCommands(1);
		commandArray = module.getCommands(1);

		this.assert(commandArray.isEmpty);
	}

	test_clearCommands_severalCommands {
		var commandArray;

		module.initializeElements([50,52,53,54]);
		module.setMappedDepth(4);
		module.setCommand(CommandMockUp.new, 2);
		module.setCommand(CommandMockUp.new, 2);
		module.setCommand(CommandMockUp.new, 2);

		commandArray = module.getCommands(2);
		this.assertEquals(commandArray.size, 3);

		module.clearCommands(2);
		commandArray = module.getCommands(2);

		this.assert(commandArray.isEmpty);
	}

	test_clearCommands_alreadyClear {
		var commandArray;

		module.initializeElements([50,56,57,58]);
		module.setMappedDepth(4);

		module.clearCommands(2);
		commandArray = module.getCommands(2);

		this.assert(commandArray.isEmpty);
	}

	test_clearModuleCommands {
		var commandArray;

		module.initializeElements([10,11,12,13]);
		module.setMappedDepth(4);
		module.setCommand(CommandMockUp.new, 0);
		module.setCommand(CommandMockUp.new, 1);
		module.setCommand(CommandMockUp.new, 3);

		module.clearModuleCommands;
		commandArray = module.getCommands(0);
		commandArray = commandArray ++ module.getCommands(1);
		commandArray = commandArray ++ module.getCommands(2);
		commandArray = commandArray ++ module.getCommands(3);

		this.assert(commandArray.isEmpty);
	}

	test_setAllModuleCommands_basic {
		var commandToCheck, commandArray;

		module.initializeElements([10,11,12,13]);
		module.setMappedDepth(4);
		commandToCheck = CommandMockUp.new;

		module.setAllModuleCommands([CommandMockUp.new, commandToCheck, CommandMockUp.new, CommandMockUp.new]);
		commandArray = module.getCommands(1);

		this.assertEquals(commandArray[0], commandToCheck);
	}

	test_setAllModuleCommands_emptyError {
		module.initializeElements([10,11,12,13]);
		module.setMappedDepth(4);

		this.assertException({ module.setAllModuleCommands([]) }, Error);
	}

	test_setAllModuleCommands_tooManyCommandsError {
		var commandArray;

		module.initializeElements([10,11,12,13]);
		module.setMappedDepth(2);
		commandArray = [CommandMockUp.new, CommandMockUp.new, CommandMockUp.new];

		this.assertException({ module.setAllModuleCommands(commandArray) }, Error);
	}

	test_getModuleSnapshot_basic {
		var snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(2);

		snapshot = module.getModuleSnapshot;

		this.assertEquals(snapshot.size, 2);
	}

	test_getModuleSnapshot_midiNums {
		var snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(2);

		snapshot = module.getModuleSnapshot;

		this.assert((snapshot[0].midiNum == 20) || (snapshot[0].midiNum == 21));
		this.assert((snapshot[1].midiNum == 20) || (snapshot[1].midiNum == 21));
	}

	test_getModuleSnapshot_elementsEmpty {
		var snapshot;

		module.initializeElements([20,21,22,23]);

		snapshot = module.getModuleSnapshot;

		this.assert(snapshot.isEmpty);
	}

	test_getModuleSnapshot_elementsDeepCopied {
		var elementRef, snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(1);
		elementRef = module.getElementRef(0);

		snapshot = module.getModuleSnapshot;

		this.assertEquals(elementRef.value.midiNum, snapshot[0].midiNum);
		this.assert(elementRef.value != snapshot[0]);
	}

	test_getModuleSnapshot_commandsDeepCopied {
		var elementRef, snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(1);
		module.setCommand(CommandMockUp.new, 0);
		elementRef = module.getElementRef(0);

		snapshot = module.getModuleSnapshot;

		this.assertEquals(elementRef.value.commands[0].class, snapshot[0].commands[0].class);
		this.assert(elementRef.value.commands[0] != snapshot[0].commands[0]);
	}

	test_setModuleSnapshot_basic {
		var element1, element2, command1, snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(2);
		element1 = MidiElement(56);
		command1 = CommandMockUp.new;
		command1.execute(101);
		element1.addCommand(command1);
		element2 = MidiElement(32);
		snapshot = [element1, element2];

		module.setModuleSnapshot(snapshot);

		this.assertEquals(module.elements.size, 2);
	}

	test_setModuleSnapshot_elementIdentity {
		var element1, element2, command1, snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(2);
		element1 = MidiElement(56);
		command1 = CommandMockUp.new;
		command1.execute(101);
		element1.addCommand(command1);
		element2 = MidiElement(32);
		snapshot = [element1, element2];

		module.setModuleSnapshot(snapshot);

		this.assertEquals(element1, module.elements.at(56));
		this.assertEquals(element2, module.elements.at(32));
	}

	test_setModuleSnapshot_commandIdentity {
		var element1, element2, command1, snapshot;

		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(2);
		element1 = MidiElement(56);
		command1 = CommandMockUp.new;
		command1.execute(101);
		element1.addCommand(command1);
		element2 = MidiElement(32);
		snapshot = [element1, element2];

		module.setModuleSnapshot(snapshot);

		this.assertEquals(command1, module.elements.at(56).commands.at(0));
	}

	test_setModuleSnapshot_emptySnapshot {
		module.initializeElements([20,21,22,23]);
		module.setMappedDepth(4);

		module.setModuleSnapshot([]);

		this.assert(module.elements.isEmpty);
	}

	test_midiNumToIndex_basic {
		var index;

		module.initializeElements([20,21,22,23]);
		index = module.midiNumToIndex(21);

		this.assertEquals(index, 1);
	}

	test_midiNumToIndex_midiNumError {
		module.initializeElements([10,11,12,13]);
		this.assertException({ module.midiNumToIndex(74) }, Error);
	}

	test_indexToMidiNum_basic {
		var midiNum;

		module.initializeElements([32,33,34,35]);
		midiNum = module.indexToMidiNum(2);

		this.assertEquals(midiNum, 34);
	}

	test_indexToMidiNum_indexError {
		module.initializeElements([32,33,34,35]);
		this.assertException({ module.indexToMidiNum(5) }, Error);
	}
}

TestMidiButtonModule : UnitTest {
	var module;

	setUp {
		module = MidiButtonModule(\tester, 4, 0, 1888);
	}

	tearDown {
		module = nil;
	}

	test_initializeMidiHandler {
		module.initializeElements([12,13,14,15]);
		this.assert(module.midiHandler.isKindOf(MidiHandlerCC));
	}
}

TestMidiContinuousModule : UnitTest {
	var module;

	setUp {
		module = MidiContinuousModule(\tester, 4, 0, 19998);
	}

	tearDown {
		module = nil;
	}

	test_initializeMidiHandler {
		module.initializeElements([12,13,14,15]);
		this.assert(module.midiHandler.isKindOf(MidiHandlerCC));
	}
}

TestMidiPadModule : UnitTest {
	var module;

	setUp {
		module = MidiPadModule(\tester, 4, 0, 19998);
	}

	tearDown {
		module = nil;
	}

	test_initializeMidiHandler {
		module.initializeElements([12,13,14,15]);
		this.assert(module.midiHandler.isKindOf(MidiHandlerNoteOn));
	}
}

TestDummyModule : UnitTest {
	var module;

	setUp {
		module = DummyModule.new;
	}

	tearDown {
		module = nil;
	}

	test_setMappedDepth_correctSize {
		module.setMappedDepth(4);

		this.assertEquals(module.elements.size, 4);
	}

	test_setMappedDepth_zeroError {
		this.assertException({ module.setMappedDepth(0) }, Error);
	}

	test_setMappedDepth_checkElements {
		module.setMappedDepth(2);

		this.assert(module.elements.at(1).isKindOf(MidiElement));
		this.assert(module.elements.at(2).isNil);
	}

	test_setMappedDepth_setTwice {
		module.setMappedDepth(4);
		module.setMappedDepth(2);

		this.assert(module.elements.at(1).isKindOf(MidiElement));
		this.assert(module.elements.at(2).isNil);
	}

	test_clearElements_basic {
		module.setMappedDepth(4);

		module.clearElements;

		this.assertEquals(module.elements.size, 0);
	}

	test_clearElements_alreadyClear {
		module.clearElements;

		this.assertEquals(module.elements.size, 0);
	}

	test_getSetCommands_commandsStartEmpty {
		var commandArray;

		module.setMappedDepth(4);
		commandArray = module.getCommands(0);
		commandArray = commandArray ++ module.getCommands(1);
		commandArray = commandArray ++ module.getCommands(2);
		commandArray = commandArray ++ module.getCommands(3);

		this.assert(commandArray.isEmpty);
	}

	test_getSetCommands_commandSetCorrectly {
		var commandArray;

		module.setMappedDepth(4);

		module.setCommand(CommandMockUp.new, 1);
		commandArray = module.getCommands(1);

		this.assert(commandArray[0].isKindOf(CommandMockUp));
	}

	test_getSetCommands_otherCommandsNotSet {
		var commandArray;

		module.setMappedDepth(4);

		module.setCommand(CommandMockUp.new, 1);
		commandArray = module.getCommands(0);

		this.assert(commandArray.isEmpty);
	}

	test_setCommand_nilError {
		module.setMappedDepth(4);

		this.assertException({ module.setCommand(nil, 1) }, Error);
	}

	test_clearCommands_oneCommand {
		var commandArray;

		module.setMappedDepth(4);
		module.setCommand(CommandMockUp.new, 1);

		module.clearCommands(1);
		commandArray = module.getCommands(1);

		this.assert(commandArray.isEmpty);
	}

	test_clearCommands_severalCommands {
		var commandArray;

		module.setMappedDepth(4);
		module.setCommand(CommandMockUp.new, 2);
		module.setCommand(CommandMockUp.new, 2);
		module.setCommand(CommandMockUp.new, 2);

		commandArray = module.getCommands(2);
		this.assertEquals(commandArray.size, 3);

		module.clearCommands(2);
		commandArray = module.getCommands(2);

		this.assert(commandArray.isEmpty);
	}

	test_clearCommands_alreadyClear {
		var commandArray;

		module.setMappedDepth(4);

		module.clearCommands(2);
		commandArray = module.getCommands(2);

		this.assert(commandArray.isEmpty);
	}

	test_clearModuleCommands {
		var commandArray;

		module.setMappedDepth(4);
		module.setCommand(CommandMockUp.new, 0);
		module.setCommand(CommandMockUp.new, 1);
		module.setCommand(CommandMockUp.new, 3);

		module.clearModuleCommands;
		commandArray = module.getCommands(0);
		commandArray = commandArray ++ module.getCommands(1);
		commandArray = commandArray ++ module.getCommands(2);
		commandArray = commandArray ++ module.getCommands(3);

		this.assert(commandArray.isEmpty);
	}

	test_setAllModuleCommands_basic {
		var commandToCheck, commandArray;

		module.setMappedDepth(4);
		commandToCheck = CommandMockUp.new;

		module.setAllModuleCommands([CommandMockUp.new, commandToCheck, CommandMockUp.new, CommandMockUp.new]);
		commandArray = module.getCommands(1);

		this.assertEquals(commandArray[0], commandToCheck);
	}

	test_setAllModuleCommands_emptyError {
		module.setMappedDepth(4);

		this.assertException({ module.setAllModuleCommands([]) }, Error);
	}

	test_setAllModuleCommands_tooManyCommandsError {
		var commandArray;

		module.setMappedDepth(2);
		commandArray = [CommandMockUp.new, CommandMockUp.new, CommandMockUp.new];

		this.assertException({ module.setAllModuleCommands(commandArray) }, Error);
	}

	test_getModuleSnapshot_basic {
		var snapshot;

		module.setMappedDepth(2);

		snapshot = module.getModuleSnapshot;

		this.assertEquals(snapshot.size, 2);
	}

	test_getModuleSnapshot_checkValue {
		var snapshot;

		module.setMappedDepth(2);
		module.elements.at(0).setCurrentVal(101);
		module.elements.at(1).setCurrentVal(53);

		snapshot = module.getModuleSnapshot;

		this.assertEquals(snapshot[0].currentVal, 101);
		this.assertEquals(snapshot[1].currentVal, 53);
	}

	test_getModuleSnapshot_elementsEmpty {
		var snapshot;

		snapshot = module.getModuleSnapshot;

		this.assert(snapshot.isEmpty);
	}

	test_getModuleSnapshot_elementsDeepCopied {
		var elementRef, snapshot;

		module.setMappedDepth(1);
		module.elements.at(0).setCurrentVal(122);
		elementRef = module.getElementRef(0);

		snapshot = module.getModuleSnapshot;

		this.assertEquals(elementRef.value.currentVal, snapshot[0].currentVal);
		this.assert(elementRef.value != snapshot[0]);
	}

	test_getModuleSnapshot_commandsDeepCopied {
		var elementRef, snapshot;

		module.setMappedDepth(1);
		module.setCommand(CommandMockUp.new, 0);
	 	elementRef = module.getElementRef(0);

	 	snapshot = module.getModuleSnapshot;

	 	this.assertEquals(elementRef.value.commands[0].class, snapshot[0].commands[0].class);
	 	this.assert(elementRef.value.commands[0] != snapshot[0].commands[0]);
	}

	test_setModuleSnapshot_basic {
		var element1, element2, command1, snapshot;

	 	module.setMappedDepth(2);
	 	element1 = MidiElement(nil);
	 	command1 = CommandMockUp.new;
	 	command1.execute(101);
	 	element1.addCommand(command1);
	 	element2 = MidiElement(nil);
		snapshot = [element1, element2];

	 	module.setModuleSnapshot(snapshot);

	 	this.assertEquals(module.elements.size, 2);
	}

	 test_setModuleSnapshot_elementIdentity {
	 	var element1, element2, command1, snapshot;

	 	module.setMappedDepth(2);
	 	element1 = MidiElement(nil);
	 	command1 = CommandMockUp.new;
	 	command1.execute(101);
	 	element1.addCommand(command1);
	 	element2 = MidiElement(nil);
	 	snapshot = [element1, element2];

	 	module.setModuleSnapshot(snapshot);

	 	this.assertEquals(element1, module.elements.at(0));
		this.assertEquals(element2, module.elements.at(1));
	}

	test_setModuleSnapshot_commandIdentity {
	 	var element1, element2, command1, snapshot;

	 	module.setMappedDepth(2);
	 	element1 = MidiElement(nil);
	 	command1 = CommandMockUp.new;
	 	command1.execute(101);
	 	element1.addCommand(command1);
	 	element2 = MidiElement(nil);
	 	snapshot = [element1, element2];

	 	module.setModuleSnapshot(snapshot);

	 	this.assertEquals(command1, module.elements.at(0).commands.at(0));
	}

	test_setModuleSnapshot_emptySnapshot {
	 	module.setMappedDepth(4);

	 	module.setModuleSnapshot([]);

	 	this.assert(module.elements.isEmpty);
	}
}

TestMidiModel : UnitTest {
	var model;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;

		model = MidiModel(2);
		model.addContinuousModule(\instAmpCtrl, 8, 0, -1758150572, [0,1,2,3,4,5,6,7]);
		model.addContinuousModule(\instPanCtrl, 8, 0, -1758150572, [16,17,18,19,20,21,22,23]);
		model.addButtonModule(\instBuildCtrl, 8, 0, -1758150572, [64,65,66,67,68,69,70,71]);
		model.addButtonModule(\instDestroyCtrl, 8, 0, -1758150572, [48,49,50,51,52,53,54,55]);
		model.addButtonModule(\channelFocusCtrl, 8, 0, -1758150572, [32,33,34,35,36,37,38,39]);
		model.addContinuousModule(\instSendCtrl, 8, 0, -631467386, [12,13,14,15,16,17,18,19]);
		model.addContinuousModule(\instParamCtrl, 8, 0, -631467386, [22,23,24,25,26,27,28,29]);
		model.addContinuousModule(\fxAmpCtrl, 8, 1, -631467386, [12,13,14,15,16,17,18,19]);
		model.addButtonModule(\fxBuildCtrl, 8, 1, -631467386, [32,33,34,35,36,37,38,39]);
		model.addContinuousModule(\fxSendCtrl, 8, 2, -631467386, [12,13,14,15,16,17,18,19]);
		model.addContinuousModule(\fxParamCtrl, 8, 1, -631467386, [22,23,24,25,26,27,28,29]);
	}

	tearDown {
		model = nil;
		Server.local.quit;
	}

	test_numModules {
		this.assertEquals(model.modules.size, 11);
	}

	test_getModuleRef_basic {
		var moduleRef;

		moduleRef = model.getModuleRef(\instAmpCtrl);

		this.assert(moduleRef.notNil);
	}

	test_getModuleRef_checkElements {
		var moduleRef;

		moduleRef = model.getModuleRef(\channelFocusCtrl);
		moduleRef.value.setMappedDepth(8);

		this.assertEquals(moduleRef.value.elements.size, 8);
	}

	test_getModuleRef_midiChannel {
		var moduleRef;

		moduleRef = model.getModuleRef(\fxAmpCtrl);

		this.assertEquals(moduleRef.value.midiChannelNum, 1);
	}

	test_getModuleRef_midiID {
		var moduleRef;

		moduleRef = model.getModuleRef(\instPanCtrl);

		this.assertEquals(moduleRef.value.midiControllerID, -1758150572);
	}
}




TestMidiMap : UnitTest {
	var model, system;

	setUp {
		this.bootServer(Server.local);
		"/Users/kylekunkler/Applications/Supercollider Files/Patches/Austin/Outrun/Current System/synthdefs.scd".load;

		model = MidiModel(2);
		model.addContinuousModule(\instAmpCtrl, 8, 0, -1758150572, [0,1,2,3,4,5,6,7]);
		model.addContinuousModule(\instPanCtrl, 8, 0, -1758150572, [16,17,18,19,20,21,22,23]);
		model.addButtonModule(\instBuildCtrl, 8, 0, -1758150572, [64,65,66,67,68,69,70,71]);
		model.addButtonModule(\instDestroyCtrl, 8, 0, -1758150572, [48,49,50,51,52,53,54,55]);
		model.addButtonModule(\channelFocusCtrl, 8, 0, -1758150572, [32,33,34,35,36,37,38,39]);
		model.addContinuousModule(\instSendCtrl, 8, 0, -631467386, [12,13,14,15,16,17,18,19]);
		model.addContinuousModule(\instParamCtrl, 8, 0, -631467386, [22,23,24,25,26,27,28,29]);
		model.addContinuousModule(\fxAmpCtrl, 8, 1, -631467386, [12,13,14,15,16,17,18,19]);
		model.addButtonModule(\fxBuildCtrl, 8, 1, -631467386, [32,33,34,35,36,37,38,39]);
		model.addContinuousModule(\fxSendCtrl, 8, 2, -631467386, [12,13,14,15,16,17,18,19]);
		model.addContinuousModule(\fxParamCtrl, 8, 1, -631467386, [22,23,24,25,26,27,28,29]);
	}

	tearDown {
		model = nil;
		system = nil;
		Server.local.quit;
	}

	test_setModule_oneModule {
		var map, ampModuleRef;

		map = MidiMap.new;
		ampModuleRef = model.getModuleRef(\instAmpCtrl);
		map.setModule(\amp, ampModuleRef, 8);

		this.assertEquals(map.key.size, 1);
	}

	test_setModule_threeModules {
		var map, ampModuleRef, panModuleRef, buildModuleRef;

		map = MidiMap.new;
		ampModuleRef = model.getModuleRef(\instAmpCtrl);
		panModuleRef = model.getModuleRef(\instPanCtrl);
		buildModuleRef = model.getModuleRef(\instBuildCtrl);
		map.setModule(\amp, ampModuleRef, 8);
		map.setModule(\pan, panModuleRef, 8);
		map.setModule(\build, buildModuleRef, 8);

		this.assertEquals(map.key.size, 3);
	}

	test_setModule_oneDummyModule {
		var map, moduleRef;

		map = MidiMap.new;
		map.setModule(\amp, nil, 8);
		moduleRef = map.getModuleRef(\amp);

		this.assertEquals(map.key.size, 1);
		this.assertEquals(moduleRef.value.mappedDepth, 8);
	}

	test_setModule_threeDummyModules {
		var map;

		map = MidiMap.new;
		map.setModule(\thing, nil, 5);
		map.setModule(\other, nil, 1);
		map.setModule(\last, nil, 6);

		this.assertEquals(map.key.size, 3);
		this.assertEquals(map.getModuleRef(\thing).value.mappedDepth, 5);
		this.assertEquals(map.getModuleRef(\other).value.mappedDepth, 1);
	}

	test_setModule_partialMapping {
		var map, ampModuleRef;

		map = MidiMap.new;
		ampModuleRef = model.getModuleRef(\instAmpCtrl);
		map.setModule(\amp, ampModuleRef, 3);

		this.assertEquals(map.getModuleRef(\amp).value.elements.size, 3);
	}

	test_setModule_noMappingDepthError {
		var map;

		map = MidiMap.new;
		this.assertException({ map.setModule(\amp, nil, 0) }, Error);
	}

	test_setModule_badNameError {
		var map;

		map = MidiMap.new;
		this.assertException({ map.setModule(nil, nil, 8) }, Error);
	}

	test_setCommand_basic {
		var map, ampModuleRef;

		map = MidiMap.new;
		ampModuleRef = model.getModuleRef(\instAmpCtrl);
		map.setModule(\amp, ampModuleRef, 8);
		map.setModule(\pan, nil, 4);
		map.setCommand(CommandMockUp.new, \pan, 0);

		this.assert(map.getCommands(\pan, 0).notEmpty);
	}

	test_setCommand_commandIdentity {
		var map, ampModuleRef, command1, command2;

		map = MidiMap.new;
		ampModuleRef = model.getModuleRef(\instAmpCtrl);
		map.setModule(\amp, ampModuleRef, 8);
		map.setModule(\pan, nil, 4);
		command1 = CommandMockUp.new;
		command2 = CommandMockUp.new;
		map.setCommand(command1, \pan, 0);
		map.setCommand(command2, \amp, 4);

		this.assertEquals(map.getCommands(\pan, 0).at(0), command1);
		this.assertEquals(map.getCommands(\amp, 4).at(0), command2);
	}

	test_setCommand_nilError {
		var map, ampModuleRef;

		map = MidiMap.new;
		ampModuleRef = model.getModuleRef(\instAmpCtrl);
		map.setModule(\amp, ampModuleRef, 8);
		this.assertException({ map.setCommand(CommandMockUp.new, nil, 0) }, Error);
	}
}
