/*
ControlPoint
NoteControlPoint
*/

ControlPoint {
	var <index, <lowVal, <highVal, <default, <controlJitter, <constJitter;

	*new { |index, lowVal, highVal, default, controlJitter, constJitter|
		^super.newCopyArgs(index, lowVal, highVal, default, controlJitter, constJitter)
	}

	value { |instrument, key, type|
		// builds any structure needed, then contributes the value it was asked for
		instrument.addControl(index, key, lowVal, highVal, controlJitter);

		if (type == \synth, {
			if (default.notNil, {
				^default
			}, {
				^lowVal
			});
		});
		if (type == \pattern, {
			var ev;

			if (default.notNil, {
				instrument.setControlVal(key, default);
			}, {
				instrument.setControlVal(key, lowVal);
			});

			ev = instrument.getControlEventRef.value;
			if (constJitter.notNil, {
				^Pfunc( {ev.at(key) + (constJitter.rand * [-1, 1].choose)} )
			}, {
				^Pfunc( {ev.at(key)} )
			});
		});
	}

	setDefault { |value|
		default = value;
	}
}




NoteSourceControl {
	var <dataType, <lowVal, <highVal, <default, <module;

	*new { |dataType, lowVal, highVal, default, module|
		^super.new.init(dataType, lowVal, highVal, default, module)
	}

	init { |i_dataType, i_lowVal, i_highVal, i_default, i_module|
		dataType = i_dataType;
		lowVal = i_lowVal;
		highVal = i_highVal;
		default = i_default;
		module = i_module ? \keyboard;
	}

	value { |instrument, key, type|
		instrument.addNoteCommand(module, dataType, key, lowVal, highVal);

		if (type == \synth, {
			if (default.notNil, {
				^default
			}, {
				^lowVal
			});
		});
		if (type == \pattern, {
			var ev;

			if (default.notNil, {
				instrument.setControlVal(key, default);
			}, {
				instrument.setControlVal(key, lowVal);
			});

			ev = instrument.getControlEventRef.value;
			^Pfunc( {ev.at(key)} )
		});
	}
}












