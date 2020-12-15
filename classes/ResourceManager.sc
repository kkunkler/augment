ResourceManager {
	var <buffers, <outBuffers;

	*new {
		^super.new.init();
	}

	init {
		buffers = Dictionary.new;
		outBuffers = Dictionary.new;
	}

	// path comes in as string
	// startPos and length are in seconds
	addBuffer { |path, startPos=0, length= -1, replaceIfDuplicate=false|
		var pathObject, pathToken;

		pathObject = PathName(path);
		pathToken = pathObject.fileNameWithoutExtension.asSymbol;
		if (startPos.notNil && (startPos != 0), {
			startPos = startPos * Server.local.sampleRate;
		});
		if (length.notNil && (length != -1) && (length != 0), {
			length = length * Server.local.sampleRate;
		});

		if ((buffers.at(pathToken).notNil && replaceIfDuplicate) || buffers.at(pathToken).isNil, {
			buffers.put(pathToken, Buffer.read(Server.local, pathObject.fullPath, startPos, length));
		});
	}

	addBufferAsMono { |path, startPos=0, length= -1, replaceIfDuplicate=false|
		var pathObject, pathToken;

		pathObject = PathName(path);
		pathToken = (pathObject.fileNameWithoutExtension.asSymbol ++ \Mono).asSymbol;
		if (startPos.notNil && (startPos != 0), {
			startPos = startPos * Server.local.sampleRate;
		});
		if (length.notNil && (length != -1) && (length != 0), {
			length = length * Server.local.sampleRate;
		});

		if ((buffers.at(pathToken).notNil && replaceIfDuplicate) || buffers.at(pathToken).isNil, {
			var buffer;

			buffer = Buffer.readChannel(Server.local, pathObject.fullPath, startPos, length, [0]);
			buffers.put(pathToken, buffer);
		});
	}

	addBufferArray { |pathArray, replaceIfDuplicate=false|
		pathArray.do({ |item, i|
			this.addBuffer(item, replaceIfDuplicate);
		});
	}

	getBufferFromPathToken { |pathToken|
		^buffers.at(pathToken)
	}

	getBufferFromFileName { |fileName|
		var nameObject, pathToken;

		nameObject = PathName(fileName);
		pathToken = nameObject.fileNameWithoutExtension.asSymbol;
		^buffers.at(pathToken)
	}

	getBufferFromPath { |path|
		var pathObject, pathToken;

		pathObject = PathName(path);
		pathToken = pathObject.fileNameWithoutExtension.asSymbol;
		^buffers.at(pathToken)
	}

	getBuffersFromFileNameArray { |nameArray|
		var bufferArray;

		bufferArray = [];
		nameArray.do({ |item, i|
			var buffer;

			buffer = this.getBufferFromFileName(item);
			bufferArray = bufferArray.add(buffer);
		});
		^bufferArray
	}

	clearBuffers {
		buffers.keys.do({ |item|
			buffers.at(item).free;
			buffers.removeAt(item);
		});
		buffers = Dictionary.new;
	}

	// make more general
	getLiveBuffer { |indexTrace, setArray|
		var channelNum, layerNum;

		channelNum = indexTrace[0];
		layerNum = indexTrace[1] - 1;

		setArray = setArray ++ [\liveBuffer, outBuffers.at([channelNum, layerNum])];

		^setArray
	}

	getLiveBufferCopy { |indexTrace, setArray|
		var channelNum, layerNum, liveBuffer, newBuffer;

		channelNum = indexTrace[0];
		layerNum = indexTrace[1] - 1;

		liveBuffer = outBuffers.at([channelNum, layerNum]);
		newBuffer = Buffer.alloc(Server.local, liveBuffer.numFrames, 1);
		liveBuffer.copyData(newBuffer);

		setArray = setArray ++ [\liveBufferCopy, newBuffer];

		^setArray
	}

	getEmptyBuffer { |nameSymbol, setArray|
		if (nameSymbol == \short, {
			setArray = setArray ++ [\emptyBufferShort, Buffer.alloc(Server.local, Server.local.sampleRate * 0.5, 2)];
		});
		if (nameSymbol == \long, {
			setArray = setArray ++ [\emptyBufferLong, Buffer.alloc(Server.local, Server.local.sampleRate * 2, 2)];
		});

		^setArray
	}

	getEmptyBufferMono { |nameSymbol, setArray|
		if (nameSymbol == \short,  {
			setArray = setArray ++ [
				\emptyBufferShortMono, Buffer.alloc(Server.local, Server.local.sampleRate * 0.5, 1)
			];
		});
		if (nameSymbol == \long, {
			setArray = setArray ++ [
				\emptyBufferLongMono, Buffer.alloc(Server.local, Server.local.sampleRate * 2, 1)
			];
		});

		^setArray
	}

	getOutBuffer { |nameSymbol, setArray, indexTrace|

		if (nameSymbol == \short, {
			var buffer;

			buffer = Buffer.alloc(Server.local, Server.local.sampleRate * 0.5, 1);
			setArray = setArray ++ [
				\outBufferShort, buffer
			];
			this.addOutBuffer(indexTrace, buffer);
		});
		if (nameSymbol == \long, {
			var buffer;

			buffer = Buffer.alloc(Server.local, Server.local.sampleRate * 2, 1);
			setArray = setArray ++ [
				\outBufferLong, buffer
			];
			this.addOutBuffer(indexTrace, buffer);
		});

		^setArray
	}

	addOutBuffer { |indexTrace, buffer|
		outBuffers.put(indexTrace, buffer);
	}

	clearOutBuffer { |indexTrace|
		if (outBuffers.at(indexTrace).notNil, {
			outBuffers.at(indexTrace).free;
			outBuffers.put(indexTrace, nil);
		});
	}
}


