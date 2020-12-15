ComponentBasic {
	var <parent, <indexInParent, <depth, <level, <group, <sumBus, <endMixer, <children;

	*new { |parent, indexInParent, depth|
		^super.newCopyArgs(parent, indexInParent, depth);
	}

	freeAndClear { |item|
		if (item.notNil, {
			item.free;
			item = nil;
		});
	}

	getChild { |index|
		^children[index]
	}

	clearChild { |index|
		children[index] = nil;
	}

	setLevel {
		if (parent.isNil, {
			level = 0;
		}, {
			level = parent.level + 1;
		});
	}

	setGroup {
		var nearest;

		if (parent.notNil, {
			if (parent.hasChild, {
				nearest = parent.getNearestChild(indexInParent);
				if (nearest.indexInParent < indexInParent, {
					group = Group(nearest.group, \addAfter);
				}, {
					group = Group(nearest.group, \addBefore);
				});
			}, {
				group = Group(parent.group, \addToHead);
			});
		}, {
			group = Group(Server.local.defaultGroup, \addToTail);
		});
	}

	assembleComponent {
		if (parent.isNil, {
			sumBus = Bus.audio(Server.local, 2);
			endMixer = Synth(\finalMix, [
				\mainIn, sumBus, \sendFxIn, 2
			], group, \addToTail);
		});
	}

	resolveChildInput { |index| } //subclasses must implement

	resolveChildOutput { |index| } //subclasses must implement

	getInput {
		if (parent.isNil, {
			^nil
		}, {
			^parent.resolveChildInput(indexInParent);
		});
	}

	getOutput {
		if (parent.isNil, {
			^sumBus
		}, {
			^parent.resolveChildOutput(indexInParent);
		});
	}

	getGroup {
		^group
	}

	backtraceIndices { |indexArray|
		if (indexArray.isNil, {
			indexArray = Array.newClear(level);
		});
		if (level == 0, {
			^indexArray
		});
		indexArray[level - 1] = indexInParent;
		^parent.backtraceIndices(indexArray)
	}

	checkActive {
		children.do({ |item, i|
			if (item.checkActive == true, {
				^true
			});
		});
		^false
	}

	hasChild {
		var bool;

		bool = false;
		children.do({ |item, i|
			if (item.notNil, {
				bool = true;
			});
		});
		^bool
	}

	numChildren {
		var count;

		count = 0;
		children.do({ |item, i|
			if (item.notNil, {
				count = count + 1;
			});
		});
		^count
	}

	getNearestChild { |index|
		var numChecked;

		numChecked = 0;

		depth.do({ |i|
			var offset;

			offset = i + 1;
			// if all possible slots have been checked, return nil
			if (numChecked == (depth - 1), {
				^nil
			});
			// checked for nearest filled index by radiating out from index
			// passed into the function
			if ((index - offset) >= 0, {
				numChecked = numChecked + 1;
				if (children[index - offset].notNil, {
					^children[index - offset]
				});
			});
			if ((index + offset) < depth, {
				numChecked = numChecked + 1;
				if (children[index + offset].notNil, {
					^children[index + offset]
				});
			});
		});
	}

	record {
		var recordBus;

		recordBus = this.getOutput(indexInParent);
		Server.local.record(bus: recordBus, numChannels: 2);
	}

	stopRecord {
		Server.local.stopRecording;
	}

	getStructRepresentation {
		^StructNode(this, indexInParent, depth, level)
	}
}

/*
Component Types:

Parallel will always produce output.  Any channel nested in it will write to the output of the component

SerialSimple will only produce output with a complete chain from some previous layer down to the final layer.  Only
the last layer is connected to the output, and signal only propagates if there are no breaks in the chain from the
signal producer to this final layer.

SerialComplex can support more complicated routings.  Any sound producer will create output, since each layer has its
own mixer that sends out of the component.  Effects will require a chain from some sound producer so that they have
signal to process, but the output of that layer will be sent out the component just as the sound producers are.
Any layer can output out of the component, but a layer may require a chain up to it depending on whether it creates a
signal or just processes a previously created signal.
*/

ComponentParallel : ComponentBasic {

	*new { |parent, indexInParent, depth|
		^super.new(parent, indexInParent, depth).init;
	}

	init {
		this.setLevel;
		this.setGroup;
		children = Array.newClear(depth);
		this.assembleComponent;
	}

	addComponent { |index, depth, type|
		if (type == \parallel, {
			children[index] = ComponentParallel(this, index, depth);
			^children[index]
		});
		if (type == \serialSimple, {
			children[index] = ComponentSerialSimple(this, index, depth);
			^children[index]
		});
		if (type == \serialComplex, {
			children[index] = ComponentSerialComplex(this, index, depth);
			^children[index]
		});
		if (type == \host, {
			children[index] = ComponentHost(this, index);
			^children[index]
		})
	}

	cleanUp {
		children.do({ |item, i|
			if (item.notNil, {
				children[i].cleanUp;
				children[i] = nil;
			});
		});
		this.freeAndClear(endMixer);
		this.freeAndClear(sumBus);
		if (parent.notNil, {
			parent.clearChild(indexInParent);
			parent = nil;
		});
		this.freeAndClear(group);
	}

	/* if there is not a parent, then there is no input, if there is a parent, have the parent look for an input */
	resolveChildInput { |index|
		if (parent.isNil, {
			^nil
		}, {
			^parent.resolveChildInput(indexInParent)
		});
	}

	/* If there is not a parent, then output is to the sumBus that must reside in the component.  If there is a
	parent, have them resolve the output */
	resolveChildOutput { |index|
		if (parent.isNil, {
			^sumBus
		}, {
			^parent.resolveChildOutput(indexInParent)
		});
	}

	printSelf {
		"--------------".postln;
		"this:   ".post;
		this.postln;
		"parent:   ".post;
		parent.postln;
		"indexInParent:   ".post;
		indexInParent.postln;
		if (sumBus.notNil, {
			"sumBus:   ".post;
			sumBus.postln;
		}, {
			"no sumBus".postln;
		});
		"--------------".postln;
		if (this.hasChild, {
			children.do({ |item, i|
				if (item.notNil, {
					item.printSelf;
				});
			});
		});
	}
}

ComponentSerialSimple : ComponentBasic {
	var <busses;

	*new { |parent, indexInParent, depth|
		^super.new(parent, indexInParent, depth).init;
	}

	init {
		this.setLevel;
		this.setGroup;
		busses = Array.newClear(depth);
		children = Array.newClear(depth);
		this.assembleComponent;
		this.setResources;
	}

	setResources {
		// do not need a bus for the last in the chain
		if (depth > 1, {
			(depth - 1).do({ |i|
				busses[i] = Bus.audio(Server.local, 2);
			});
		});
	}

	cleanUp {
		children.do({ |item, i|
			if (item.notNil, {
				children[i].cleanUp;
				children[i] = nil;
			});
		});
		busses.do({ |item, i|
			this.freeAndClear(item);
		});
		this.freeAndClear(endMixer);
		this.freeAndClear(sumBus);
		if (parent.notNil, {
			parent.clearChild(indexInParent);
			parent = nil;
		});
		this.freeAndClear(group);
	}

	/* If top level and no parent, then no input.  If top level and has a parent, have the parent check for input (it
	may be nested in the parent structure).  If it is not top level, hook it into the chain of busses */
	resolveChildInput { |index|
		if (index == 0, {
			if (parent.isNil, {
				^nil
			}, {
				^parent.resolveChildInput(indexInParent)
			});
		}, {
			^busses[index - 1]
		});
	}

	/* If it is the last index and there is no parent, hook it to the sumBus that must reside in this component.  If
	it is the last index and there is a parent, have the parent resolve it.  If it is not the last index, have it
	output to the buffer allocated for its index.
	*** Since the simple version does not have mixers forwarding signals, there will not be any output if the chain
	is not complete */
	resolveChildOutput { |index|
		if (index == (depth - 1), {
			if (parent.isNil, {
				^sumBus
			}, {
				^parent.resolveChildOutput(indexInParent)
			});
		}, {
			^busses[index]
		});
	}

	addComponent { |index, depth, type|
		if (type == \parallel, {
			busses[index] = Bus.audio(Server.local, 2);
			children[index] = ComponentParallel(this, index, depth);
			^children[index]
		});
		if (type == \serialSimple, {
			busses[index] = Bus.audio(Server.local, 2);
			children[index] = ComponentSerialSimple(this, index, depth);
			^children[index]
		});
		if (type == \serialComplex, {
			busses[index] = Bus.audio(Server.local, 2);
			children[index] = ComponentSerialComplex(this, index, depth);
			^children[index]
		});
		if (type == \host, {
			busses[index] = Bus.audio(Server.local, 2);
			children[index] = ComponentHost(this, index);
			^children[index]
		});
	}

	printSelf {
		"--------------".postln;
		"this:   ".post;
		this.postln;
		"parent:   ".post;
		parent.postln;
		"indexInParent:   ".post;
		indexInParent.postln;
		"busses:".postln;
		busses.do({ |item, i|
			(i + ":   ").post;
			item.postln;
		});
		if (sumBus.notNil, {
			"sumBus:   ".post;
			sumBus.postln;
		}, {
			"no sumBus".postln;
		});
		"--------------".postln;
		if (this.hasChild, {
			children.do({ |item, i|
				if (item.notNil, {
					item.printSelf;
				});
			});
		});
	}
}

ComponentSerialComplex : ComponentBasic {
	var <busses, <mixers;

	*new { |parent, indexInParent, depth|
		^super.new(parent, indexInParent, depth).init;
	}

	init {
		this.setLevel;
		this.setGroup;
		busses = Array.newClear(depth);
		mixers = Array.newClear(depth);
		children = Array.newClear(depth);
		this.assembleComponent;
		this.setResources;
	}

	setResources {
		var outBus;

		if (parent.notNil, {
			outBus = parent.getOutput(indexInParent);
		}, {
			outBus = sumBus;
		});
		depth.do({ |i|
			busses[i] = Bus.audio(Server.local, 2);
			mixers[i] = Synth(\basicMixer, [
				\outBus, outBus, \sendBus, 2, \inBus, busses[i],
				\amp, 0.0, \pan, 0.0, \sendAmp, 0.0
			], group, \addToTail);
		});
	}

	addComponent { |index, depth, type|
		if (type == \parallel, {
			children[index] = ComponentParallel(this, index, depth);
			^children[index]
		});
		if (type == \serialSimple, {
			children[index] = ComponentSerialSimple(this, index, depth);
			^children[index]
		});
		if (type == \serialComplex, {
			children[index] = ComponentSerialComplex(this, index, depth);
			^children[index]
		});
		if (type == \host, {
			children[index] = ComponentHost(this, index);
			^children[index]
		});
	}

	setAmp { |index, val|
		mixers[index].set(\amp, val.linlin(0, 127, 0.0, 1.0));
	}

	setPan { |index, val|
		mixers[index].set(\pan, val.linlin(0, 127, -1.0, 1.0));
	}

	setSend { |index, val|
		mixers[index].set(\sendAmp, val.linlin(0, 127, 0.0, 1.0));
	}

	setMixerByIndex { |index, symbol, val|
		mixers[index].set(symbol, val);
	}

	cleanUp {
		children.do({ |item, i|
			if (item.notNil, {
				children[i].cleanUp;
				children[i] = nil;
			});
		});
		mixers.do({ |item, i|
			this.freeAndClear(item);
		});
		busses.do({ |item, i|
			this.freeAndClear(item);
		});
		this.freeAndClear(endMixer);
		this.freeAndClear(sumBus);
		if (parent.notNil, {
			parent.clearChild(indexInParent);
			parent = nil;
		});
		this.freeAndClear(group);
	}

	/* If at top level with no parent, then no input.  If top level and there is a parent, have them check for input
	(may be nested in parent level).  If not at top level, then hook it into the chain of busses */
	resolveChildInput { |index|
		if (index == 0, {
			if (parent.isNil, {
				^nil
			}, {
				^parent.resolveChildInput(indexInParent)
			});
		}, {
			^busses[index - 1]
		});
	}

	/* every level has a bus and mixer that depends on the bus.  Output will always be to the bus at that index.  The
	mixers take care of forwarding audio out of the component */
	resolveChildOutput { |index|
		^busses[index]
	}

	printSelf {
		"--------------".postln;
		"this:   ".post;
		this.postln;
		"parent:   ".post;
		parent.postln;
		"indexInParent:   ".post;
		indexInParent.postln;
		"busses:".postln;
		busses.do({ |item, i|
			(i + ":   ").post;
			item.postln;
		});
		if (sumBus.notNil, {
			"sumBus:   ".post;
			sumBus.postln;
		}, {
			"no sumBus".postln;
		});
		"--------------".postln;
		if (this.hasChild, {
			children.do({ |item, i|
				if (item.notNil, {
					item.printSelf;
				});
			});
		});
	}
}

ComponentHost : ComponentBasic {
	var <synth, <active;

	*new { |parent, indexInParent|
		^super.new(parent, indexInParent, nil).init;
	}

	init {
		active = false;
		this.setLevel;
		this.setGroup;
		synth = nil;
		this.assembleComponent;
	}

	cleanUp {
		if (active, {
			this.clearPattern;
		});
		if (synth.notNil, {
			synth.free;
			synth = nil;
		});
		if (endMixer.notNil, {
			endMixer.free;
			endMixer = nil;
		});
		if (sumBus.notNil, {
			sumBus.free;
			sumBus = nil;
		});
		if (parent.notNil, {
			parent.clearChild(indexInParent);
			parent = nil;
		});
		this.freeAndClear(group);
	}

	setSynth { |synthDef, setArray|
		setArray = setArray ++ [\inBus, this.getInput];
		setArray = setArray ++ [\outBus, this.getOutput];
		setArray.postln;
		synth = Synth(synthDef, setArray, group, \addToHead);
		this.setActive(true);

		^Ref(synth)
	}

	getSynth {
		^synth
	}

	clearSynth {
		synth.free;
		synth = nil;
		this.setActive(false);
	}

	setMainAmp { |val|
		parent.setMixerByIndex(indexInParent, \amp, val);
	}

	setSendAmp { |val|
		parent.setMixerByIndex(indexInParent, \sendAmp, val);
	}

	playPattern { |name|
		var pattInput, pattOutput, pattGroup;

		pattInput = this.getInput;
		pattOutput = this.getOutput;
		pattGroup = this.getGroup;

		if (pattInput.notNil, {
			Pbindef(name, \inBus, pattInput);
		});
		Pbindef(name, \outBus, pattOutput);
		Pbindef(name, \group, pattGroup);
		Pbindef(name, \addAction, \addToHead);
		Pbindef(name).play;
		this.setActive(true);
	}

	clearPattern { |name|
		Pbindef(name).stop;
		Pbindef(name).clear;
		this.setActive(false);
	}

	printSelf {
		"--------------".postln;
		"this:   ".post;
		this.postln;
		"parent:   ".post;
		parent.postln;
		"indexInParent:   ".post;
		indexInParent.postln;
		if (sumBus.notNil, {
			"sumBus:   ".post;
			sumBus.postln;
		}, {
			"no sumBus".postln;
		});
		"--------------".postln;
		if (this.hasChild, {
			children.do({ |item, i|
				if (item.notNil, {
					item.printSelf;
				});
			});
		});
	}

	setActive { |bool|
		active = bool;
	}

	checkActive {
		^active
	}
}
