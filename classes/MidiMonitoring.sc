MidiHandlerCC {
	var <midiFunc, <parentModule;

	*new { |parentModule|
		^super.new.init(parentModule);
	}

	init { |i_parentModule|
		parentModule = i_parentModule;
		this.setMidiFunc;
	}

	setMidiFunc {
		midiFunc = MIDIFunc.cc({
			|val, num, chan, src|

			parentModule.setVal(num, val);

		}, parentModule.getMidiNums, parentModule.getMidiChan, parentModule.getMidiID);
	}

	clearMidiFunc {
		midiFunc.clear;
		midiFunc.free;
	}
}


MidiHandlerNotes {
	var <parentModule, noteOnFunc, noteOffFunc, <noteWatchers, <monoNote, <heldNotes;

	*new { |parentModule|
		^super.new.init(parentModule);
	}

	init { |i_parentModule|
		parentModule = i_parentModule;
		noteOnFunc = nil;
		noteOffFunc = nil;
		noteWatchers = [];
		monoNote = nil;
		heldNotes = [];
	}

	registerForNotes { |watcher|
		this.addNoteWatcher(watcher);

		if (noteOnFunc.isNil && noteOffFunc.isNil, {
			this.buildNoteFuncs;
		});
	}

	unregisterForNotes { |watcher|
		this.removeNoteWatcher(watcher);

		if (noteWatchers.isEmpty, {
			this.clearNoteFuncs;
		});
	}

	buildNoteFuncs {
		noteOnFunc = MIDIFunc.noteOn({
			|val, num, chan, src|

			this.addNote(num, val);
		}, nil, parentModule.getMidiChan, parentModule.getMidiID);
		noteOffFunc = MIDIFunc.noteOff({
			|val, num, chan, src|

			this.removeNote(num, val);
		}, nil, parentModule.getMidiChan, parentModule.getMidiID);
	}

	clearNoteFuncs {
		noteOnFunc.free;
		noteOnFunc = nil;
		noteOffFunc.free;
		noteOffFunc = nil;
	}

	addNote { |noteNum, velocity|
		monoNote = noteNum;
		if (heldNotes.indexOf(noteNum).isNil, {
			heldNotes = heldNotes.add(noteNum);
			heldNotes = heldNotes.sort({ |a, b| a < b });
		});
		this.notifyNoteWatchers(heldNotes, monoNote, velocity, \noteOn);
	}

	removeNote { |noteNum, velocity|
		var index;

		index = heldNotes.indexOf(noteNum);
		if (index.notNil, {
			heldNotes.removeAt(index);
		});
		this.notifyNoteWatchers(heldNotes, monoNote, velocity, \noteOff);
	}

	addNoteWatcher { |watcher|
		noteWatchers = noteWatchers.add(watcher);
		^this
	}

	removeNoteWatcher { |watcher|
		var index;

		index = noteWatchers.indexOf(watcher);
		noteWatchers.removeAt(index);
	}

	notifyNoteWatchers { |notes, monoNote, velocity, type|
		noteWatchers.do({ |item, i|
			item.notesChanged(notes, monoNote, velocity, type);
		});
	}
}


MidiNoteResponse {
	var <parentInstrument, <notesResponses, <monoNoteResponses, <velocityResponses;

	*new { |parentInstrument|
		^super.new.init(parentInstrument);
	}

	init { |i_parentInstrument|
		parentInstrument = i_parentInstrument;
		notesResponses = [];
		monoNoteResponses = [];
		velocityResponses = [];
	}

	parseMapping { |type, attribute, low, high|
		if (type == \notes, {
			notesResponses = notesResponses.add([attribute, low, high]);
		});
		if (type == \monoNote, {
			monoNoteResponses = monoNoteResponses.add([attribute, low, high]);
		});
		if (type == \velocity, {
			velocityResponses = velocityResponses.add([attribute, low, high]);
		});
	}

	executeMidiResponse { |heldNotes, monoNote, velocity, type|
		var hostComponent;

		hostComponent = parentInstrument.host
	}

	executeNotesResponses { |heldNotes, hostComponent|
		notesResponses.do({ |item, i|

		})
	}
}









