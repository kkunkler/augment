/*
Amp, Pan, and Send are tied to the Components.  They reference the mixers used in the Component implementation
*/
AmpCommand {
	var <component, <index;

	*new { |component, index|
		^super.newCopyArgs(component, index);
	}

	execute { |val|
		component.setAmp(index, val);
	}
}

PanCommand {
	var <component, <index;

	*new { |component, index|
		^super.newCopyArgs(component, index);
	}

	execute { |val|
		component.setPan(index, val);
	}
}

SendCommand {
	var <component, <index;

	*new { |component, index|
		^super.newCopyArgs(component, index);
	}

	execute { |val|
		component.setSend(index, val);
	}
}

/*
Focus, Build, and Destroy have to do with the node structure overlaying the Components.
*/
FocusCommand {
	var <metaStructure, <channelNum, <layerNum;

	*new { |metaStructure, channelNum, layerNum|
		^super.newCopyArgs(metaStructure, channelNum, layerNum);
	}

	execute {
		metaStructure.setFocusedBlock(channelNum, layerNum);
	}
}

BuildCommand {
	var <metaStructure, <channelNum, <layerNum;

	*new { |metaStructure, channelNum, layerNum|
		^super.newCopyArgs(metaStructure, channelNum, layerNum);
	}

	execute {
		metaStructure.buildInstrument(channelNum, layerNum);
	}
}

DestroyCommand {
	var <metaStructure, <channelNum, <layerNum;

	*new { |metaStructure, channelNum, layerNum|
		^super.newCopyArgs(metaStructure, channelNum, layerNum);
	}

	execute {
		metaStructure.destroyInstrument(channelNum, layerNum);
	}
}
/*
These are parameterized by individual instruments to tie the instrument arguments to midiControls or GUI controls
*/
SynthParamCommand {
	var <instrument, <paramName, <low, <high, <jitter;

	*new { |instrument, paramName, low, high, jitter|
		^super.newCopyArgs(instrument, paramName, low, high, jitter);
	}

	execute { |val|
		if (jitter.notNil, {
			instrument.setControlVal(paramName, val.linlin(0, 127, low, high) + {jitter.rand}.value)
		}, {
			instrument.setControlVal(paramName, val.linlin(0, 127, low, high));
		});
	}
}

PatternParamCommand {
	var <instrument, <paramName, <low, <high, <jitter;

	*new { |instrument, paramName, low, high, jitter|
		^super.newCopyArgs(instrument, paramName, low, high, jitter);
	}

	execute { |val|
		if (jitter.notNil, {
			instrument.setControlVal(paramName, val.linlin(0, 127, low, high) + {jitter.rand}.value);
		}, {
			instrument.setControlVal(paramName, val.linlin(0, 127, low, high));
		});
	}
}

NoteCommand {
	var <instrument, <dataType, <paramName, <lowVal, <highVal;

	*new { |instrument, dataType, paramName, lowVal, highVal|
		^super.newCopyArgs(instrument, dataType, paramName, lowVal, highVal)
	}

	execute { |notes, monoNote, velocity|
		if (dataType == \monoNote, {
			instrument.setControlVal(paramName, monoNote.midicps);
		});
		if (dataType == \velocity, {
			instrument.setControlVal(paramName, velocity.linlin(0, 127, lowVal, highVal));
		});
	}
}









