InstrumentDictionary {
	var <dictionary;

	*new {
		^super.new.init();
	}

	init {
		dictionary = Dictionary[];
	}

	registerInstrument { |instrument, replaceIfDuplicate=true|
		if ((dictionary.at(instrument.name).notNil && replaceIfDuplicate) || dictionary.at(instrument.name).isNil, {
			dictionary.put(instrument.name, instrument);
		});
	}

	// currently user must specify a value for all defaults if they specify for any
	makeInstrumentVariant { |origName, varName, definition, controlDefaults|
		var orig, variant;

		orig = this.getInstrument(origName);
		variant = orig.copy;
		variant.setName(varName);

		if (definition.notNil, {
			definition.keysValuesDo({ |key, val|
				variant.changeDefinition(key, val);
			});
		});
		if (controlDefaults.notNil, {
			controlDefaults.keysValuesDo({ |key, val|
				variant.changeControlDefault(key, val)
			});
		});

		this.registerInstrument(variant);
	}

	getInstrument { |name|
		^dictionary.at(name).deepCopy;
	}
}


InstrumentBinding {
	var <buffers, <mainAmp, <sendAmp, <hostComponent, <options, <midiModel;

	*new {
		^super.newCopyArgs;
	}

	setBuffers { |bufferArray|
		buffers = bufferArray;
	}

	setOptions { |optionsArray|
		options = optionsArray;
	}

	setMainAmp { |amp|
		mainAmp = amp;
	}

	bindMainAmp {
		hostComponent.setMainAmp(mainAmp);
	}

	setSendAmp { |amp|
		sendAmp = amp;
	}

	bindSendAmp {
		hostComponent.setSendAmp(sendAmp);
	}

	setHostComponent { |component|
		hostComponent = component;
	}

	setMidiModel { |model|
		midiModel = model;
	}
}

SynthInstrumentBinding : InstrumentBinding {

	*new {
		^super.new;
	}

	bindBuffers { |setArray|
		if (buffers.notEmpty, {

			buffers.do({ |item, i|
				var sym, num;

				num = i + 1;
				sym = ("buffer" ++ num.asDigit).asSymbol;

				setArray = setArray ++ [sym, item];
			});
		});
		^setArray
	}

	bindOptions { |setArray, resourceManager|
		options.do({ |item, i|
			if (item == \liveBuffer, {
				setArray = resourceManager.getLiveBuffer(hostComponent.backtraceIndices(), setArray);
			});
			if (item == \liveBufferCopy, {
				setArray = resourceManager.getLiveBufferCopy(hostComponent.backtraceIndices(), setArray);
			});
			if (item == \emptyBufferShort, {
				setArray = resourceManager.getEmptyBuffer(\short, setArray);
			});
			if (item == \emptyBufferLong, {
				setArray = resourceManager.getEmptyBuffer(\long, setArray);
			});
			if (item == \emptyBufferShortMono, {
				setArray = resourceManager.getEmptyBufferMono(\short, setArray);
			});
			if (item == \emptyBufferLongMono, {
				setArray = resourceManager.getEmptyBufferMono(\long, setArray);
			});
			if (item == \outBufferShort, {
				setArray = resourceManager.getOutBuffer(\short, setArray, hostComponent.backtraceIndices());
			});
			if (item == \outBufferLong, {
				setArray = resourceManager.getOutBuffer(\long, setArray, hostComponent.backtraceIndices());
			});
		});

		^setArray
	}
}


PatternInstrumentBinding : InstrumentBinding {

	*new {
		^super.new;
	}

	bindBuffers { |patternName|
		if (buffers.notEmpty, {

			buffers.do({ |item, i|
				var sym, num;

				num = i + 1;
				sym = ("buffer" ++ num.asDigit).asSymbol;

				Pbindef(patternName, sym, item);
			});
		});
	}

	bindOptions { |patternName, resourceManager|
		var optionSet;

		options.do({ |item, i|
			if (item == \liveBuffer, {
				optionSet = resourceManager.getLiveBuffer(hostComponent.backtraceIndices(), []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \liveBufferCopy, {
				optionSet = resourceManager.getLiveBufferCopy(hostComponent.backtraceIndices(), []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \emptyBufferShort, {
				optionSet = resourceManager.getEmptyBuffer(\short, []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \emptyBufferLong, {
				optionSet = resourceManager.getEmptyBuffer(\long, []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \emptyBufferShortMono, {
				optionSet = resourceManager.getEmptyBufferMono(\short, []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \emptyBufferLongMono, {
				optionSet = resourceManager.getEmptyBufferMono(\long, []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \outBufferShort, {
				optionSet = resourceManager.getOutBuffer(\short, []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
			if (item == \outBufferLong, {
				optionSet = resourceManager.getOutBuffer(\long, []);
				Pbindef(patternName, optionSet[0], optionSet[1]);
			});
		});
	}
}

MidiInstrumentBinding : InstrumentBinding {

	*new {
		^super.new
	}
}

Instrument {
	var <name, <binding, <definition, <controls, <componentDepth, <componentType;
	// component depth and type are a first idea for handling serial and parallel block types being made for
	// composite instruments

	*new {
		^super.new;
	}

	getBinding {
		^binding
	}

	setName { |newName|
		name = newName;
	}

	setControls { |newControls|
		controls = newControls;
	}

	changeDefinition { |key, value|
		definition.put(key, value);
	}
}


SynthInstrument : Instrument {
	var <synthDef, <freeSelf, <osc, <synthRef, <noteCommands, <registeredLocations;

	*new { |name, synthDef, definition, freeSelf|
		^super.new.init(name, synthDef, definition, freeSelf);
	}

	init { |i_name, i_synthDef, i_definition, i_freeSelf|
		name = i_name;
		synthDef = i_synthDef;
		definition = i_definition ? ();
		controls = Dictionary.new;
		freeSelf = i_freeSelf ? false;
		binding = SynthInstrumentBinding.new;
		noteCommands = Dictionary.new;
		registeredLocations = [];
	}


	build { |resourceManager|
		var setArray, hostComponent, buffers;

		setArray = this.processDefinition();

		binding.setOptions(SynthPreProc.processForOptions(synthDef));
		setArray = binding.bindBuffers(setArray, resourceManager);
		setArray = binding.bindOptions(setArray, resourceManager);

		hostComponent = binding.hostComponent;
		binding.bindMainAmp;
		binding.bindSendAmp;

		Task({
			Server.local.sync; //ensures that buffers and such are set so the build doesn't fail
			synthRef = hostComponent.setSynth(synthDef, setArray);

			if (freeSelf == true, {
				osc = OSCFunc({
					|msg, time, addr, recvPort|

					if(msg[1] == hostComponent.getSynth.nodeID,
						{
							hostComponent.setActive(false);
							this.osc.free;
						}
					);
				});
			});
		}).play;
	}

	destroy {
		if (freeSelf == false, {
			synthRef = nil;
			binding.hostComponent.clearSynth;
		});
		registeredLocations.do({ |item, i|
			var moduleRef;

			moduleRef = binding.midiModel.getModuleRef(item);
			moduleRef.value.unregisterForNotes(this);
		});
	}

	processDefinition {
		var tempArray;

		tempArray = [];
		definition.keysValuesDo({ |key, val|
			tempArray = tempArray ++ key;
			tempArray = tempArray ++ val.value(this, key, \synth);
		});
		^tempArray
	}

	addControl { |index, paramName, lowVal, highVal, jitter|
		controls.put(index, SynthParamCommand(this, paramName, lowVal, highVal, jitter));
	}

	addNoteCommand { |module, dataType, paramName, lowVal, highVal|
		if (registeredLocations.indexOf(module).isNil, {
			var moduleRef;

			moduleRef = binding.midiModel.getModuleRef(module);
			moduleRef.value.registerForNotes(this);
		});
		noteCommands.put(paramName, NoteCommand(this, dataType, paramName, lowVal, highVal));
	}

	getParamCommands {
		var commandArray;

		commandArray = [];
		controls.size.do({ |i|
			commandArray = commandArray.add(controls.at(i + 1));
		});
		^commandArray
	}

	setControlVal { |paramName, val|
		synthRef.value.set(paramName, val);
	}

	changeControlDefault { |key, value|
		definition.at(key).setDefault(value);
	}

	setFreeSelf { |bool|
		freeSelf = bool;
	}

	notesChanged { |notes, monoNote, velocity, type|
		if (type == \noteOn, {
			noteCommands.keysValuesDo({ |key, val|
				val.execute(notes, monoNote, velocity);
			});
		});
	}
}

PatternInstrument : Instrument {
	var <synthDef, <controlEvent, <noteCommands, <registeredLocations;

	*new { |name, synthDef, definition|
		^super.new.init(name, synthDef, definition);
	}

	init { |i_name, i_synthDef, i_definition|
		name = i_name;
		synthDef = i_synthDef;
		definition = i_definition ? ();
		controlEvent = ();
		controls = Dictionary.new;
		binding = PatternInstrumentBinding.new;
		noteCommands = Dictionary.new;
		registeredLocations = [];
	}

	build { |resourceManager|
		var hostComponent;

		hostComponent = binding.hostComponent;

		this.processDefinition();

		binding.bindBuffers(name, resourceManager);
		binding.setOptions(PatternPreProc.processForOptions(name));
		binding.bindOptions(resourceManager);
		binding.bindMainAmp;
		binding.bindSendAmp;
		Pbindef(name).repositoryArgs.postln;

		Task({
			Server.local.sync;
			hostComponent.playPattern(name);
		}).play;
	}

	destroy {

		binding.hostComponent.clearPattern(name);
		registeredLocations.do({ |item, i|
			var moduleRef;

			moduleRef = binding.midiModel.getModuleRef(item);
			moduleRef.value.unregisterForNotes(this);
		});
	}

	addControl { |index, paramName, lowVal, highVal, jitter|
		controls.put(index, PatternParamCommand(this, paramName, lowVal, highVal, jitter));
	}

	addNoteCommand { |module, dataType, paramName, lowVal, highVal|
		if (registeredLocations.indexOf(module).isNil, {
			var moduleRef;

			moduleRef = binding.midiModel.getModuleRef(module);
			moduleRef.value.registerForNotes(this);
		});
		noteCommands.put(paramName, NoteCommand(this, dataType, paramName, lowVal, highVal));
	}

	setControlVal { |key, val|
		controlEvent.put(key, val);
	}

	changeControlDefault { |key, value|
		definition.at(key).setDefault(value);
	}

	getControlEventRef {
		^Ref(controlEvent)
	}

	processDefinition {
		Pbindef(name, \instrument, synthDef);
		definition.keysValuesDo({ |key, val|
			Pbindef(name, key, val.value(this, key, \pattern));
		});
	}

	getParamCommands {
		var commandArray, hostComponent;

		hostComponent = binding.hostComponent;
		commandArray = [];

		controls.size.do({ |i|
			commandArray = commandArray.add(controls.at(i + 1));
		});

		^commandArray
	}

	notesChanged { |notes, monoNote, velocity, type|
		if (type == \noteOn, {
			noteCommands.keysValuesDo({ |key, val|
				val.execute(notes, monoNote, velocity);
			});
		});
	}
}

MidiOutInstrument : Instrument {
	var <definition, <synthDef, <controlEvent;

	*new { |name, definition, numInputs|
		^super.new.init(name, definition, numInputs);
	}

	init { |i_name, i_definition, numInputs|
		name = i_name;
		definition = i_definition;
		controlEvent = ();
		controls = Dictionary.new;
		binding = MidiInstrumentBinding.new;
		if (numInputs == 2, {
			synthDef = \liveSound;
		}, {
			synthDef = \liveSoundMono;
		});
	}

	build { |resourceManager|
		var hostComponent;

		hostComponent = binding.hostComponent;

		this.processDefinition();

		binding.bindMainAmp;
		binding.bindSendAmp;

		Task({
			Server.local.sync;
			hostComponent.setSynth(synthDef, []);
			hostComponent.playPattern(name);
		}).play;
	}

	destroy {
		binding.hostComponent.clearPattern(name);
		binding.hostComponent.clearSynth;
	}

	addControl { |index, paramName, lowVal, highVal|
		controls.put(index, PatternParamCommand(this, paramName, lowVal, highVal));
	}

	setControlVal { |key, val|
		controlEvent.put(key, val);
	}

	changeControlDefault { |key, value|
		definition.at(key).setDefault(value);
	}

	getControlEventRef {
		^Ref(controlEvent)
	}

	/*
	Need a more flexible way to specify where the midi is sent to
	*/
	processDefinition {
		var midiDestination;

		midiDestination = binding.midiModel.getMidiDestination;

		Pbindef(name,
			\type, \midi,
			\midiout, midiDestination,
			\midicmd, \noteOn,
		);
		definition.keysValuesDo({ |key, val|
			Pbindef(name, key, val.value(this, key, \pattern));
		});
	}

	getParamCommands {
		var commandArray, hostComponent;

		hostComponent = binding.hostComponent;
		commandArray = [];

		controls.size.do({ |i|
			commandArray = commandArray.add(controls.at(i + 1));
		});

		^commandArray
	}
}


