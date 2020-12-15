SynthPreProc {

	*new {
		^super.new;
	}

	*processForOptions { |synthDefName|
		var options, controlNames;

		options = [];
		controlNames = SynthDescLib.at(synthDefName).controlNames;
		controlNames.do({ |item, i|
			if (item == \liveBuffer, {
				options = options.add(item);
			});
			if (item == \liveBufferCopy, {
				options = options.add(item);
			});
			if (item == \emptyBufferShort, {
				options = options.add(item);
			});
			if (item == \emptyBufferLong, {
				options = options.add(item);
			});
			if (item == \emptyBufferLongMono, {
				options = options.add(item);
			});
			if (item == \emptyBufferShortMono, {
				options = options.add(item);
			});
			if (item == \outBufferShort, {
				options = options.add(item);
			});
			if (item == \outBufferLong, {
				options = options.add(item);
			});
		});

		^options
	}
}



PatternPreProc {

	*new {
		^super.new;
	}

	*preProcess { |pattern|
		var locations, processedString, processedPattern;

		processedString = pattern.asString;
		locations = processedString.findRegexp("(?<='\\$)\\w+");
		locations.do({ |item, i|
			var searchString, replaceString;

			searchString = "'$" ++ item[1] ++ "'";
			replaceString = "Pfunc({ event.at(\\" ++ item[1] ++ ") })";
			processedString = processedString.replace(searchString, replaceString);
		});
		processedString = "{ arg event; " ++ processedString ++ "; }";
		processedPattern = processedString.interpret;

		^processedPattern
	}

	*processForOptions { |patternName|
		var options, controlNames;

		options = [];
		controlNames = Pbindef(patternName).repositoryArgs;
		controlNames.do({ |item, i|
			if (item == \liveBuffer, {
				options = options.add(item);
			});
			if (item == \liveBufferCopy, {
				options = options.add(item);
			});
			if (item == \emptyBufferShort, {
				options = options.add(item);
			});
			if (item == \emptyBufferLong, {
				options = options.add(item);
			});
			if (item == \emptyBufferShortMono, {
				options = options.add(item);
			});
			if (item == \emptyBufferLongMono, {
				options = options.add(item);
			});
			if (item == \outBufferShort, {
				options = options.add(item);
			});
			if (item == \outBufferLong, {
				options = options.add(item);
			});
		});

		^options
	}
}
