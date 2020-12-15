TempoKeeper {
	var parentSystem, lastTime, midiFunc, count, diffArray, <>tempoClock;

	*new { |parentSystem, tempo, num, chan, id|
		^super.new.init(parentSystem, tempo, num, chan, id);
	}

	init { |init_parentSystem, init_tempo, num, chan, id|
		parentSystem = init_parentSystem;
		tempoClock = TempoClock.new(init_tempo);
		lastTime = 0;
		count = 0;
		diffArray = Array.newClear(4);

		midiFunc = MIDIFunc.cc({ |val, num, chan, src|
			var time, diff;

			time = SystemClock.seconds;
			diff = time - lastTime;
			lastTime = time;
			"pushed".postln;
			diff.postln;

			this.tempoCalc(diff);
			(tempoClock.tempo.reciprocal * 60).postln;
		}, num, chan, id);
	}

	tempoCalc { |timeDiff|
		if (timeDiff > 5.0, {
			count = 0;
		});
		count.postln;
		diffArray[count % diffArray.size] = timeDiff;
		if (count >= 4, {
			tempoClock.tempo = diffArray.mean;
		});
		count = count + 1;
	}
}

// to integrate this, the tempoClock will need to be passed into patterns
// when they are created