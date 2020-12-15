~system.registerInstrument(SynthInstrument(\tester, \sine,
   (
      freq: ControlPoint(1, 200, 4000)
   )
));

/*
~system.registerInstrument(SynthInstrument(\tester, \sine,
   (
      freq: NoteSourceControl(\monoNote, 0.midicps, 127.midicps, default: 440),
      amp: NoteSourceControl(\velocity, 0.2, 1.0, default: 0.8)
   )
))
*/

~system.registerInstrument(SynthInstrument(\synthFxTest, \freqShiftBasic,
	(
		freq: ControlPoint(1, 0, 400)
	)
));

~system.registerInstrument(SynthInstrument(\liveSound, \liveSound));
~system.registerInstrument(SynthInstrument(\playback, \playback));

~system.registerInstrument(PatternInstrument(\patternInstTest, \sine2,
   (
      dur: ControlPoint(2, 0.1, 2.0, default: 0.5),
      freq: ControlPoint(1, 200, 5000, default: 500),
      amp: 0.8,
      atk: ControlPoint(3, 0.001, 2.0, default: 0.001),
      rel: ControlPoint(4, 0.001, 2.0, default: 0.5)
   )
));

~system.registerInstrument(PatternInstrument(\patternFxTest, \freq2,
   (
      dur: ControlPoint(1, 0.1, 2.0),
      freq: ControlPoint(2, 200, 5000),
      pan: Pwhite(-1.0, 1.0, inf)
   )
));





(
SynthDef(\testShortBufferCopy, {
	arg outBus, liveBufferShortCopy, rate=1.0, amp=0;
	var sig;

	sig = PlayBuf.ar(2, liveBufferShort, rate, doneAction: 2);
	sig = sig * amp;

	Out.ar(outBus, sig);
}).add;

SynthInstrument(\testShortBufferCopy, \testShortBufferCopy);
)



(
SynthDef(\playLiveBuffer, {
	arg outBus, liveBuffer, rate=1.0, amp=0;
	var sig;

	sig = PlayBuf.ar(2, liveBuffer, rate, doneAction: 2);
	sig = sig * amp;

	Out.ar(outBus, sig);
})add;

PatternInstrument(\testLongBuffer, {
	arg event;

	Pbindef(event.at(\name),
		\instrument, \playLiveBuffer,
		\liveBufferLong, nil,
		\rate, 1.0,
		\amp, 0.9
	);
});
)

