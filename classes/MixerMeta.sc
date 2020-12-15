/*
Channels and Layers are vocab of the MixerSystem.  The MixerSystem is the class that drives the whole system.
*/

MixerSystem {
	var <numChannels, <numLayers, <structure, <midiModel, <mappings, <resourceManager, <instrumentDictionary;

	*new { |numChannels, numLayers, midiModel|
		^super.new.init(numChannels, numLayers, midiModel);
	}

	init { |i_numChannels, i_numLayers, i_midiModel|
		numChannels = i_numChannels;
		numLayers = i_numLayers;
		midiModel = i_midiModel;
		mappings = Array.newClear(numLayers);
		resourceManager = ResourceManager.new;
		instrumentDictionary = InstrumentDictionary.new;
		structure = ComponentStruct(numChannels);
	}
	/*
	Interface to InstrumentDictionary
	*/
	registerInstrument { |instrument, replaceIfDuplicate=true|
		instrumentDictionary.registerInstrument(instrument, replaceIfDuplicate);
	}

	makeInstrumentVariant { |origName, varName, definition, controlDefaults|
		instrumentDictionary.makeInstrumentVariant(origName, varName, definition, controlDefaults);
	}
	/*
	Interface for midi functionality
	*/
	defineLayerMidi { |layerNum, ampModule, panModule, buildModule, destroyModule, focusModule, sendModule|
		var map, moduleNameArray, propertyArray;

		if (mappings[layerNum].isNil, {
			mappings.put(layerNum, MidiMap.new);
		});
		map = mappings[layerNum];
		moduleNameArray = [ampModule, panModule, buildModule, destroyModule, focusModule, sendModule];
		propertyArray = [\amp, \pan, \build, \destroy, \focus, \send];

		moduleNameArray.do({ |item, i|
			var moduleRef;

			moduleRef = midiModel.getModuleRef(item);
			map.setModule(propertyArray[i], moduleRef, numChannels);
		});
	}

	// defaultNumElements is used if no module is found. That number of DummyModules will be created to support
	// control through Gui
	defineAssignableMidi { |layerNum, ctrlModule, defaultNumElements=8|
		var map, moduleRef;

		if (mappings[layerNum].isNil, {
			mappings.put(layerNum, MidiMap.new)
		});
		map = mappings[layerNum];
		moduleRef = midiModel.getModuleRef(ctrlModule);
		if (moduleRef.notNil, {
			map.setModule(\ctrls, moduleRef, moduleRef.value.numElements);
		}, {
			map.setModule(\ctrls, moduleRef, defaultNumElements);
		});
	}

	printMidiModuleNames {
		midiModel.printModuleNames;
	}

	// ----------------------
	setInstrument { |instrumentName, channelNum, layerNum, sampleFileNames, mainAmp, sendAmp|
		var bufferArray, binding, block, instrument;


		instrument = instrumentDictionary.getInstrument(instrumentName);
		if (this.blockExists(channelNum, layerNum), {
			block = this.getBlock(channelNum, layerNum);
		}, {
			block = this.setBlock(channelNum, layerNum);
		});
		if (block.checkActive, {
			this.destroyInstrument(channelNum, layerNum);
		});

		bufferArray = resourceManager.getBuffersFromFileNameArray(sampleFileNames);

		binding = instrument.getBinding;
		binding.setBuffers(bufferArray);
		binding.setMainAmp(mainAmp);
		binding.setSendAmp(sendAmp);
		binding.setHostComponent(block.component);
		binding.setMidiModel(midiModel);

		structure.setInstrument(instrument, [channelNum, layerNum]);
		mappings[layerNum].clearCommands(\build, channelNum);
		mappings[layerNum].setCommand(BuildCommand(this, channelNum, layerNum), \build, channelNum);
	}

	buildInstrument { |channelNum, layerNum|
		var channel, node, instrument, paramCommands;

		if (this.getBlock(channelNum, layerNum).checkActive.not, {
			instrument = structure.getInstrument([channelNum, layerNum]);
			channel = structure.getNode([channelNum]).component;

			mappings[layerNum].setCommand(AmpCommand(channel, layerNum), \amp, channelNum);
			mappings[layerNum].setCommand(PanCommand(channel, layerNum), \pan, channelNum);
			mappings[layerNum].setCommand(SendCommand(channel, layerNum), \send, channelNum);
			mappings[layerNum].setCommand(DestroyCommand(this, channelNum, layerNum), \destroy, channelNum);
			mappings[layerNum].setCommand(FocusCommand(this, channelNum, layerNum), \focus, channelNum);

			this.setFocusedChannel(channelNum);
			instrument.build(resourceManager);
			paramCommands = instrument.getParamCommands;
			mappings[layerNum].clearModuleCommands(\ctrls);
			mappings[layerNum].setAllModuleCommands(paramCommands, \ctrls);
		});
	}

	destroyInstrument { |channelNum, layerNum|
		var instrument;

		if (this.getBlock(channelNum, layerNum).checkActive, {
			instrument = structure.getInstrument([channelNum, layerNum]);
			instrument.destroy();

			mappings[layerNum].clearCommands(\amp, channelNum);
			mappings[layerNum].clearCommands(\pan, channelNum);
			mappings[layerNum].clearCommands(\send, channelNum);
			mappings[layerNum].clearCommands(\focus, channelNum);

			mappings[layerNum].clearModuleCommands(\ctrls);
			structure.clearSnapshot([channelNum, layerNum]);
			structure.removeFocusedNode("layer" + layerNum);
			resourceManager.clearOutBuffer([channelNum, layerNum]);
		});
	}

	clearInstrument { |channelNum, layerNum|
		if (structure.checkActivity([channelNum, layerNum]), {
			this.destroyInstrument(channelNum, layerNum);
		});
		structure.clearInstrument([channelNum, layerNum]);

		mappings[layerNum].clearCommands(\build, channelNum);
		mappings[layerNum].clearCommands(\destroy, channelNum);
	}
	// -------------------

	setChannel { |channelNum|
		structure.addComponent([channelNum], numLayers, \serialComplex);
	}

	clearChannel { |channelNum|
		structure.clearComponent([channelNum]);
	}

	channelExists { |channelNum|
		if (structure.getNode([channelNum]).notNil, {
			^true
		}, {
			^false
		});
	}

	clearInactiveChannels { }

	setBlock { |channelNum, layerNum|
		if (this.channelExists(channelNum) && this.blockExists(channelNum, layerNum).not, {
			structure.addComponent([channelNum, layerNum], nil, \host);
		});
		if (this.channelExists(channelNum).not, {
			this.setChannel(channelNum);
			structure.addComponent([channelNum, layerNum], nil, \host);
		});
		^structure.getNode([channelNum, layerNum])
	}

	getBlock { |channelNum, layerNum|
		^structure.getNode([channelNum, layerNum]);
	}

	clearBlock { |channelNum, layerNum|
		structure.clearComponent([channelNum, layerNum]);
	}

	blockExists { |channelNum, layerNum|
		if (this.channelExists(channelNum), {
			if (structure.getNode([channelNum, layerNum]).notNil, {
				^true
			}, {
				^false
			})
		}, {
			^false
		})
	}

	storeFocusedSnapshot { |layerNum|
		var layerSymbol, lastFocused, lastCtrlSnapshot, indexTrace;

		lastFocused = structure.getFocusedNode(layerNum);
		if (lastFocused.notNil, {
			lastCtrlSnapshot = mappings[layerNum].getModuleSnapshot(\ctrls);
			indexTrace = lastFocused.component.backtraceIndices;
			structure.storeSnapshot(lastCtrlSnapshot, indexTrace);
		});
	}

	pushSnapshot { |channelNum, layerNum|
		var newSnapshot;

		newSnapshot = structure.retrieveSnapshot([channelNum, layerNum]);
		if (newSnapshot.notNil, {
			mappings[layerNum].setModuleSnapshot(\ctrls, newSnapshot);
		});
	}

	setFocusedChannel { |channelNum|
		numLayers.do({ |i|
			var layerSymbol;

			this.storeFocusedSnapshot(i);
			this.pushSnapshot(channelNum, i);
			layerSymbol = ("layer" + i).asSymbol;
			structure.setFocusedNode([channelNum, i], layerSymbol);
		});
	}

	printComponents {
		structure.printComponents;
	}

	makeGuiControl { |nameSymbol, layerNum|
		mappings[layerNum].makeGuiControl(nameSymbol);
	}

	recordBlock { |channelNum, layerNum|
		var node;

		node = structure.getNode([channelNum, layerNum]);
		node.component.record;
	}

	stopRecordBlock { |channelNum, layerNum|
		var node;

		node = structure.getNode([channelNum, layerNum]);
		node.component.stopRecord;
	}

	// recordNode allows more specificity
	recordNode { |indexTrace|
		var node;

		node = structure.getNode(indexTrace);
		node.component.record;
	}

	stopRecordNode { |indexTrace|
		var node;

		node = structure.getNode(indexTrace);
		node.component.stopRecord;
	}
}

ComponentStruct {
	var <mainComponent, <rootNode, <focusedNodes;

	*new { |rootDepth|
		^super.new.init(rootDepth)
	}

	init { |rootDepth|
		mainComponent = ComponentParallel(nil, nil, rootDepth);
		rootNode = mainComponent.getStructRepresentation;
		focusedNodes = Dictionary.new;
	}

	getNode { |indexTrace|
		var levelRef;

		levelRef = rootNode;
		indexTrace.do({ |item, i|
			levelRef = levelRef.getChildNode(item);
			if (levelRef.isNil, {
				^nil
			});
		});
		^levelRef
	}

	addComponent { |indexTrace, depth, type|
		var levelRef, levelsTravelled;

		if (this.getNode(indexTrace).notNil, {
			Error("A component is already set at this position.  Delete it before adding another.");
		});

		levelsTravelled = 0;
		levelRef = rootNode;
		indexTrace.do({ |item, i|
			levelsTravelled = levelsTravelled + 1;
			if (levelRef.getChildNode(item).notNil, {
				levelRef = levelRef.getChildNode(item);
			}, {
				if (levelsTravelled < indexTrace.size, {
					Error("The structure has not been built deep enough to support this indexTrace.");
				});
			});
		});
		levelRef.addComponent(indexTrace[indexTrace.size - 1], depth, type);
	}

	clearComponent { |indexTrace|
		var nodeRef;

		nodeRef = this.getNode(indexTrace);
		nodeRef.clearComponent;
	}

	getInstrument { |indexTrace|
		^this.getNode(indexTrace).getInstrument
	}

	setInstrument { |instrument, indexTrace|
		this.getNode(indexTrace).setInstrument(instrument);
	}

	clearInstrument { |indexTrace|
		this.getNode(indexTrace).clearInstrument;
	}

	storeSnapshot { |snapshot, indexTrace|
		this.getNode(indexTrace).storeSnapshot(snapshot);
	}

	retrieveSnapshot { |indexTrace|
		var node;

		node = this.getNode(indexTrace);
		if (node.notNil, {
			^node.retrieveSnapshot
		}, {
			^nil
		});
	}

	clearSnapshot { |indexTrace|
		this.getNode(indexTrace).clearSnapshot;
	}

	setFocusedNode { |indexTrace, symbol|
		focusedNodes.put(symbol, indexTrace);
	}

	getFocusedNode { |layerNum|
		var symbol, trace;

		symbol = ("layer" + layerNum).asSymbol;
		trace = focusedNodes.at(symbol);
		if (trace.notNil, {
			^this.getNode(trace)
		}, {
			^nil;
		});
	}

	getFocusedTrace { |layerNum|
		var symbol, trace;

		symbol = ("layer" + layerNum).asSymbol;
		trace = focusedNodes.at(symbol);

		^trace
	}

	removeFocusedNode { |symbol|
		focusedNodes.put(symbol, nil);
	}

	checkActivity { |indexTrace|
		^this.getNode(indexTrace).checkActive;
	}

	printComponents {
		rootNode.component.printSelf;
	}

	clearStruct {
		mainComponent.cleanUp;
	}
}

/*clearInactiveChannels {
	channels.do({ |item, i|
		if (item.notNil, {
			if (item.checkActive.not, {
				item.cleanUp;
				channels.put(i, nil);
				blocks[i].do({ |item, j|
					blocks[i].put(j, nil);
				});
				// need to dispose of any controls as well
				this.checkFocusedExist;
			});
		});
	});
}*/



StructNode {
	var <component, <parentNode, <childNodes, <depth, <level, <indexInParent, <instrument, <snapshot;

	*new { |component, indexInParent, depth, level|
		^super.new.init(component, indexInParent, depth, level)
	}

	init { |i_component, i_indexInParent, i_depth, i_level|
		component = i_component;
		parentNode = nil;
		indexInParent = i_indexInParent;
		depth = i_depth;
		level = i_level;
		childNodes = Array.newClear(depth);
		instrument = nil;
	}

	setParent { |node|
		parentNode = node;
	}

	getChildNode { |index|
		^childNodes[index]
	}

	clearChildNode { |index|
		childNodes.put(index, nil);
	}

	addComponent { |index, depth, type|
		var newComponent, child;

		newComponent = component.addComponent(index, depth, type);
		child = newComponent.getStructRepresentation;
		child.setParent(this);
		childNodes.put(index, child);
	}

	clearComponent {
		parentNode.clearChildNode(indexInParent);
		component.cleanUp;
		component = nil;
	}

	getInstrument {
		^instrument
	}

	setInstrument { |newInstrument|
		instrument = newInstrument;
	}

	clearInstrument {
		instrument = nil;
	}

	storeSnapshot { |newSnapshot|
		snapshot = newSnapshot;
	}

	retrieveSnapshot {
		^snapshot
	}

	clearSnapshot {
		snapshot = nil;
	}

	checkActive {
		^component.checkActive
	}
}

