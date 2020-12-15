/*
Describes commont elements of the MidiModules.  MidiModules describe the hardware of the controllers,
adding in the ability to track values and have commands set for controls
*/
AbstractMidiModule {
	var <name, <numElements, <midiChannelNum, <midiControllerID, <mappedDepth, <elements, <elementsMap, <midiHandler;

	*new { |name, numElements, midiChannelNum, midiControllerID|
		^super.newCopyArgs(name, numElements, midiChannelNum, midiControllerID);
	}

	initializeElements { |midiNumArray|
		if (midiNumArray.isEmpty || midiNumArray.isNil, {
			Error("No midi numbers provided.").throw;
		});
		if (midiNumArray.size() > numElements) {
			Error("Set array contains too many elements.").throw;
		};
		mappedDepth = nil;
		// elements makes the MidiElements accessible by midiNum, while elementsMap keeps the linear ordering
		elements = Dictionary.new(numElements);
		elementsMap = midiNumArray;
		this.initializeMidiHandler;
	}

	// implemented by concrete classes
	initializeMidiHandler { }

	// elements do not get set unless this is called.  This happens when mappings are set
	setMappedDepth { |depth|
		if (depth.isNil || (depth == 0), {
			Error("No depth provided.").throw;
		});
		if (depth > numElements, {
			Error("Not that many elements in this module.").throw;
		});
		mappedDepth = depth;
		this.clearElements;
		depth.do({ |i|
			var midiNum;

			midiNum = this.indexToMidiNum(i);
			elements.put(midiNum, MidiElement(midiNum));
		});
	}

	clearElements {
		elements = Dictionary.new(numElements);
	}

	setCommand { |command, index|
		var midiNum;

		if (command.isNil, {
			Error("No command provided").throw;
		});
		midiNum = this.indexToMidiNum(index);
		elements.at(midiNum).addCommand(command);
	}

	getCommands { |index|
		var midiNum;

		midiNum = this.indexToMidiNum(index);
		^elements.at(midiNum).getCommands
	}

	clearCommands { |index|
		var midiNum;

		midiNum = this.indexToMidiNum(index);
		elements.at(midiNum).clearCommands;
	}

	clearModuleCommands {

		numElements.do({ |i|
			this.clearCommands(i);
		});
	}

	// this is a bad name, since it doesn't necessarily set all of them, only as many as are in the
	// commandArray.  If there was only 1 in the array then it would be just like setCommand
	setAllModuleCommands { |commandArray|
		if (commandArray.size > mappedDepth, {
			Error("Too many commands provided.").throw;
		});
		commandArray.do({ |item, i|
			this.setCommand(item, i);
		});
	}

	getAllModuleCommands {
		var commands;

		commands = [];
		mappedDepth.do({ |i|
			commands = commands.add(this.getCommands(i));
		});
		^commands
	}

	getModuleSnapshot {
		var snapshot;

		snapshot = [];
		if (elements.notEmpty, {
			elements.keysValuesDo({ |key, value|
				snapshot = snapshot.add(value.shallowCopy);
			});
		});

		^snapshot
	}

	setModuleSnapshot { |snapshot|
		this.clearElements;

		snapshot.do({ |item, i|
			elements.put(item.midiNum, item);
		});
	}

	midiNumToIndex { |midiNum|
		var index;

		index = elementsMap.indexOf(midiNum);
		if (index.isNil, {
			Error("Cannot find that midiNum in elementsMap.").throw;
		}, {
			^index
		});
	}

	indexToMidiNum { |index|
		var midiNum;

		midiNum = elementsMap.at(index);
		if (midiNum.isNil, {
			Error("Nothing set for that index.").throw;
		}, {
			^midiNum
		});
	}

	setVal { |midiNum, val|
		if (elements.at(midiNum).notNil, {
			elements.at(midiNum).setCurrentVal(val);
		}, {
			Error("No element to set for that midiNum.").throw;
		});
	}

	getVal { |midiNum|
		if (elements.at(midiNum).notNil, {
			^elements.at(midiNum).currentVal
		}, {
			Error("No element at that midiNum.").throw;
		});
	}

	getMidiNums {
		var midiNums;

		midiNums = [];
		elementsMap.do({ |item, i|
			midiNums = midiNums.add(item);
		});
		^midiNums;
	}

	getMidiChan {
		^midiChannelNum
	}

	getMidiID {
		^midiControllerID
	}

	getElementRef { |index|
		var midiNum;

		midiNum = this.indexToMidiNum(index);
		if (elements.at(midiNum).notNil, {
			^Ref(elements.at(midiNum))
		}, {
			^nil
		})
	}

	getModuleRef {
		^Ref(this)
	}

	makeGuiControl { |name|
		var window, grid;

		window = Window(name, Rect(50.rand + 50, 100.rand + 100, 450, 250));
		grid = GridLayout.new;

		elements.keysValuesDo({ |key, value|
			var row, column, layout1, layout2, knob, text, numBox, num;

			if (value.commands.notEmpty, {
				num = this.midiNumToIndex(key.asInteger);
				row = (num / 4).asInt;
				column = (num % 4);

				knob = Knob.new;
				numBox = NumberBox.new;
				knob.action = { |item|
					this.setVal(key.asInteger, (item.value * 127).asInt);
					numBox.value_((item.value * 127).asInt);
				};
				numBox.action = { |item|
					knob.valueAction_(item.value / 127);
				};
				text = StaticText.new;
				text.string = "El " + num;
				layout1 = VLayout(text, numBox);
				layout2 = VLayout(layout1, knob);
				layout2.setStretch(knob, 1);

				grid.add(layout2, row, column);
			});
		});
		window.layout_(grid);
		window.alwaysOnTop_(true);
		window.front;
	}
}


MidiButtonModule : AbstractMidiModule {

	*new { |name, numElements, midiChannelNum, midiControllerID|
		^super.new(name, numElements, midiChannelNum, midiControllerID);
	}

	initializeMidiHandler {
		midiHandler = MidiHandlerCC(this);
	}
}

MidiContinuousModule : AbstractMidiModule {

	*new { |name, numElements, midiChannelNum, midiControllerID|
		^super.new(name, numElements, midiChannelNum, midiControllerID);
	}

	// overwrites method to provide passthru support
	setVal { |num, val|
		if (((val - elements.at(num).currentVal).abs <= 10), {
			elements.at(num).setCurrentVal(val);
			if (elements.at(num).hasCommand, {
				elements.at(num).executeCommands(val);
			});
		});
	}

	initializeMidiHandler {
		midiHandler = MidiHandlerCC(this);
	}
}

MidiNoteModule {
	var <name, midiChannelNum, midiControllerID, midiHandler;

	*new { |name, midiChannelNum, midiControllerID|
		^super.new.init(name, midiChannelNum, midiControllerID);
	}

	init { |i_name, i_midiChannelNum, i_midiControllerID|
		name = i_name;
		midiChannelNum = i_midiChannelNum;
		midiControllerID = i_midiControllerID;
	}

	initializeMidiHandler {
		midiHandler = MidiHandlerNotes(this);
	}

	getMidiChan {
		^midiChannelNum
	}

	getMidiID {
		^midiControllerID
	}

	getModuleRef {
		^Ref(this)
	}

	registerForNotes { |watcher|
		midiHandler.registerForNotes(watcher);
	}

	unregisterForNotes { |watcher|
		midiHandler.unregisterForNotes(watcher);
	}
}

/*
DummyModule is substituted in by a system driver when there isn't a module provided for a function.  This allows
it to still work with commands and create GUIs, but does not support midi at all.
*/
DummyModule {
	var <elements, <mappedDepth;

	*new {
		^super.new.init;
	}

	init {
		elements = Dictionary.new;
	}

	setMappedDepth { |depth|
		if ((depth == 0) || depth.isNil, {
			Error("No depth provided.").throw;
		});
		this.clearElements;
		mappedDepth = depth;
		depth.do({ |i|
			elements.put(i, MidiElement(nil));
		});
	}

	clearElements {
		elements = Dictionary.new;
	}

	setCommand { |command, index|
		if (command.isNil, {
			Error("No command provided.").throw;
		});
		elements.at(index).addCommand(command);
	}

	getCommands { |index|
		^elements.at(index).getCommands;
	}

	clearCommands { |index|
		elements.at(index).clearCommands;
	}

	clearModuleCommands {
		elements.keysValuesDo({ |key, value|
			elements.at(key).clearCommands;
		});
	}

	setAllModuleCommands { |commandArray|
		if (commandArray.size > mappedDepth, {
			Error("Too many commands.").throw;
		});
		commandArray.do({ |item, i|
			this.setCommand(item, i);
		});
	}

	getAllModuleCommands {
		var commands;

		commands = [];
		mappedDepth.do({ |i|
			commands = commands.add(this.getCommands(i));
		});
		^commands
	}

	getModuleSnapshot {
		var snapshot;

		snapshot = Array.newClear(elements.size);
		elements.keysValuesDo({ |key, value|
			snapshot.put(key.asInteger, value.shallowCopy);
		});
		elements.size.do({ |i|
			var index;

			index = elements.size - i -1;
			if (snapshot.at(index).isNil, {
				snapshot.removeAt(index);
			});
		});

		^snapshot
	}

	setModuleSnapshot { |snapshot|
		this.clearElements;

		snapshot.do({ |item, i|
			elements.put(i, item);
		});
	}

	setVal { |index, val|
		elements.at(index).setCurrentVal(val);
	}

	getVal { |index|
		^elements.at(index).currentVal
	}

	makeGuiControl { |name|
		var window, grid;

		window = Window(name, Rect(50.rand + 50, 100.rand + 100, 450, 250));
		grid = GridLayout.new;

		elements.keysValuesDo({ |key, value|
			var row, column, layout1, layout2, knob, text, numBox, num;

			if (value.commands.notEmpty, {
				num = key.asInteger;
				row = (num / 4).asInt;
				column = (num % 4);

				knob = Knob.new;
				numBox = NumberBox.new;
				knob.action = { |item|
					this.setVal(num, (item.value * 127).asInt);
					numBox.value_((item.value * 127).asInt);
				};
				numBox.action = { |item|
					knob.valueAction_(item.value / 127);
				};
				text = StaticText.new;
				text.string = value.commands.at(0).paramName.asString;
				layout1 = VLayout(text, numBox);
				layout2 = VLayout(layout1, knob);
				layout2.setStretch(knob, 1);

				grid.add(layout2, row, column);
			});
		});
		window.layout_(grid);
		window.alwaysOnTop_(true);
		window.front;
	}

	getElementRef { |index|
		if (elements.at(index).notNil, {
			^Ref(elements.at(index))
		}, {
			^nil
		});
	}

	getModuleRef {
		^Ref(this)
	}
}

/*
MidiElement creates the basic command support
*/
MidiElement {
	var <midiNum, <currentVal, <commands;

	*new { |midiNum|
		^super.new.init(midiNum);
	}

	init { |i_midiNum|
		midiNum = i_midiNum;
		currentVal = 0;
		commands = [];
	}

	cleanUp {
		this.clearCommands;
		midiNum = nil;
		currentVal = nil;
	}

	setCurrentVal { |val|
		if ((val >= 0) && val.notNil, {
			currentVal = val;
			if (commands.notEmpty, {
				this.executeCommands(currentVal);
			});
		}, {
			Error("setCurrentVal failed.  val is invalid").throw;
		});
	}

	addCommand { |command|
		if (command.notNil, {
			commands = commands.add(command);
		}, {
			Error("Command received was nil").throw;
		});
	}

	getCommands {
		^commands
	}

	clearCommands {
		commands = [];
	}

	executeCommands { |value|
		commands.do({ |item, i|
			item.execute(value);
		});
	}

	hasCommand {
		if (commands.notEmpty, {
			^true
		}, {
			^false
		});
	}
}


/*
User defines hardware control sets through MidiModel

Should maybe separate 'modules' into parts for button, contiuous, note, etc...
*/
MidiModel {
	var <modules, <midiDestination;

	*new {
		^super.new.init;
	}

	init {
		modules = Dictionary.new;
		midiDestination = nil;
	}

	addButtonModule { |name, numElements, midiChannelNum, midiControllerID, midiNumArray|
		var newModule;

		newModule = MidiButtonModule(name, numElements, midiChannelNum, midiControllerID);
		newModule.initializeElements(midiNumArray);
		modules.put(name, newModule);
	}

	addContinuousModule { |name, numElements, midiChannelNum, midiControllerID, midiNumArray|
		var newModule;

		newModule = MidiContinuousModule(name, numElements, midiChannelNum, midiControllerID);
		newModule.initializeElements(midiNumArray);
		modules.put(name, newModule);
	}

	addNoteModule { |name, midiChannelNum, midiControllerID|
		var newModule;

		newModule = MidiNoteModule(name, midiChannelNum, midiControllerID);
		newModule.initializeMidiHandler();
		modules.put(name, newModule);
	}

	getModuleRef { |name|
		if (modules.at(name).notNil, {
			^Ref(modules.at(name))
		}, {
			^nil
		});
	}

	setMidiDestination { |nameString|
		midiDestination = MIDIOut.newByName(nameString, nameString);
	}

	getMidiDestination {
		^midiDestination
	}

	printModuleNames {
		modules.keysValuesDo({ |key, value|
			key.post;
			"    ".post;
			modules.numElements;
		});
	}
}


/*
Provides the means for the system driver to map the midiModel to the system functions.
*/
MidiMap {
	var <key;

	*new {
		^super.new.init;
	}

	init {
		key = Dictionary.new;
	}

	setModule { |mappingName, moduleRef, mappingDepth|
		var module;

		if ((mappingDepth < 1) || mappingDepth.isNil, {
			Error("mappingDepth must be integer greater than 0").throw;
		});
		if (mappingName.isSymbol.not, {
			Error("mappingName must be a symbol").throw;
		});
		if (moduleRef.isNil, {
			module = Ref(DummyModule.new);
		}, {
			module = moduleRef;
		});

		module.value.setMappedDepth(mappingDepth);
		key.put(mappingName, module);
	}

	getModuleRef { |mappingName|
		^key.at(mappingName)
	}

	setCommand { |command, mappingName, index|
		if (command.isNil || mappingName.isNil || index.isNil, {
			Error("Inappropriate Nil value as argument").throw;
		});
		key.at(mappingName).value.setCommand(command, index);
	}

	setAllModuleCommands { |commandArray, mappingName|
		key.at(mappingName).value.setAllModuleCommands(commandArray);
	}

	clearCommands { |mappingName, index|
		key.at(mappingName).value.clearCommands(index);
	}

	clearModuleCommands { |mappingName|
		key.at(mappingName).value.clearModuleCommands;
	}

	getCommands { |mappingName, index|
		^key.at(mappingName).value.getCommands(index)
	}

	getAllModuleCommands { |mappingName|
		^key.at(mappingName).value.getAllModuleCommands;
	}

	getModuleSnapshot { |mappingName|
		^key.at(mappingName).value.getModuleSnapshot
	}

	setModuleSnapshot { |mappingName, snapshot|
		key.at(mappingName).value.setModuleSnapshot(snapshot);
	}

	makeGuiControl { |mappingName|
		key.at(mappingName).value.makeGuiControl(mappingName);
	}
}
