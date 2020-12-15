~envs = ( );
~envs.envelopes = ( );
~envs.buffers = ( );
~envs.envelopes.num1 = Env.sine;
~envs.buffers.num1 = Buffer.sendCollection(s, ~envs.envelopes.num1.discretize, 1);
~envs.envelopes.num2 = Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \sin);
~envs.buffers.num2 = Buffer.sendCollection(s, ~envs.envelopes.num2.discretize, 1);
~envs.envelopes.num3 = Env([0, 1, 1, 0], [0.33, 0.34, 0.33], \sin);
~envs.buffers.num3 = Buffer.sendCollection(s, ~envs.envelopes.num3.discretize, 1);
~envs.envelopes.num4 = Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \lin);
~envs.buffers.num4 = Buffer.sendCollection(s, ~envs.envelopes.num4.discretize, 1);
~envs.envelopes.num5 = Env([0, 1, 1, 0], [0.33, 0.34, 0.33], \lin);
~envs.buffers.num5 = Buffer.sendCollection(s, ~envs.envelopes.num5.discretize, 1);
~envs.envelopes.num6 = Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \welch);
~envs.buffers.num6 = Buffer.sendCollection(s, ~envs.envelopes.num6.discretize, 1);
~envs.envelopes.num7 = Env([0, 1, 1, 0], [0.33, 0.34, 0.33], \welch);
~envs.buffers.num7 = Buffer.sendCollection(s, ~envs.envelopes.num7.discretize, 1);
~envs.envelopes.num8 = Env([1, 0.001], [0.1], \exp);
~envs.buffers.num8 = Buffer.sendCollection(s, ~envs.envelopes.num8.discretize, 1);
~envs.envelopes.num9 = Env([0.001, 1], [0.1], \exp);
~envs.buffers.num9 = Buffer.sendCollection(s, ~envs.envelopes.num9.discretize, 1);
~envs.envelopes.num10 = Env.perc(0.01, 0.09);
~envs.buffers.num10 = Buffer.sendCollection(s, ~envs.envelopes.num10.discretize, 1);
~envs.envelopes.num11 = Env.perc(0.05, 0.95);
~envs.buffers.num11 = Buffer.sendCollection(s, ~envs.envelopes.num11.discretize, 1);
~envs.envelopes.num12 = Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \sqr);
~envs.buffers.num12 = Buffer.sendCollection(s, ~envs.envelopes.num12.discretize, 1);
~envs.envelopes.num13 = Env([0, 1, 1, 0], [0.25, 0.5, 0.25], \cubed);
~envs.buffers.num13 = Buffer.sendCollection(s, ~envs.envelopes.num13.discretize, 1);
~envs.envelopes.num14 = Env.triangle(1, 1);
~envs.buffers.num14 = Buffer.sendCollection(s, ~envs.envelopes.num14.discretize, 1);


~system.registerInstrument(PatternInstrument(\patternInstTest, \sine2,
	(
		dur: ControlPoint(1, 0.1, 2.0),
		freq: ControlPoint(2, 200, 5000),
		amp: 0.8,
		atk: ControlPoint(3, 0.001, 2.0, default: 0.5),
		rel: ControlPoint(4, 0.001, 2.0, default: 0.5)
	)
));


~system.registerInstrument(PatternInstrument(\patternFxTest, \freq2,
	(
		dur: ControlPoint(1, 0.1, 2.0, default: 0.3),
		freq: ControlPoint(2, 200, 5000),
		pan: Pwhite(-1.0, 1.0, inf)
	)
));
~system.makeInstrumentVariant(\patternFxTest, \patternFxTestV1, nil, (dur: 0.5, freq: 600));



~system.registerInstrument(PatternInstrument(\freezePattern, \freezeFrame,
	(
		dur: ControlPoint(1, 0.1, 2.0, default: 0.3),
		atk: 0.2,
		sus: 0.1,
		rel: 0.3,
		wipe: ControlPoint(2, -0.98, 0.98, default: -0.1),
		pan: Pwhite(-1.0, 1.0, inf),
		amp: 2.0
	)
));
~system.makeInstrumentVariant(\freezePattern, \freezePatternV1,
	(
		atk: 0.2,
		sus: 1.0,
		rel: 1.0,
		wipe: ControlPoint(2, -0.20, 0.0, default: -0.10),
	),
	(dur: 1.0)
);
~system.makeInstrumentVariant(\freezePattern, \freezePatternV2, (atk: 1.0, sus: 0.2, rel: 2.0), (dur: 1.0, wipe: -0.5));
~system.makeInstrumentVariant(\freezePattern, \freezePatternV3, (atk: 1.0, sus: 0.2, rel: 2.0), (dur: 2.0, wipe: -0.75));
~system.makeInstrumentVariant(\freezePattern, \freezePatternV4, (atk: 0.2, sus: 0.1, rel: 0.3), (dur: 0.4, wipe: -0.95));
~system.makeInstrumentVariant(\freezePattern, \freezePatternV5, (atk: 0.2, sus: 0.1, rel: 0.3), (dur: 0.4, wipe: -0.90));




/*~system.registerInstrument(PatternInstrument(\grainFM,
	{
		arg event;

		Pbindef(event.at(\name),
			\instrument, \grainFM1,
			\envbuf, ~envs.buffers.num3,
			\dur, Pfunc({ event.at(\con3) }),
			\carfreq, Pfunc({ event.at(\monoNote) }).midicps,
			\modfreq, Pfunc({ event.at(\con1) }),
			\moddepth, Pfunc({ event.at(\con2) }),
			\sustain, Pfunc({ event.at(\con4) }),
			\panDev, Pfunc({ event.at(\con5) }),
			\pan, Pfunc({ event.at(\con6) }) + Pwhite(-1 * Pkey(\panDev), Pkey(\panDev))
		);
	},
	[
		[\con1, 50, 10000], [\con2, 1.0, 4.0], [\con3, 0.1, 1.0],
		[\con4, 0.1, 1.0], [\con5, 0.0, 1.0], [\con6, -1.0, 1.0]
	], registerForNotes: true
));*/

/*
~system.registerInstrument(PatternInstrument(\grainFM,
   (
      instrument: \grainFM1,
      envbuf: ~envs.buffers.num3,
      dur: ControlPoint(3, 0.1, 1.0),
      carfreq: NoteControlPoint(\monoNote),
      modfreq: ControlPoint(1, 50, 10000),
      moddepth: ControlPoint(2, 1.0, 4.0),
      sustain: ControlPoint(4, 0.1, 1.0),
      panDev: ControlPoint(5, 0.0, 1.0),
      panControl: ControlPoint(6, -1.0, 1.0),
      pan: Pkey(\panControl) + Pwhite(-1 * Pkey(\panDev), Pkey(\panDev))
   )
))
*/

~system.registerInstrument(PatternInstrument(\grainBuf1, \grainBuf1,
	(
		dur: ControlPoint(1, 0.03, 0.4, default: 0.2),
		sustain: ControlPoint(2, 0.1, 1.0, default: 0.2),
		envbuf: ~envs.buffers.num3,
		filepos: ControlPoint(3, 0.0, 1.0, default: 0.1),
		posDev: ControlPoint(4, 0, 0.5),
		playRate: 1.0,
		pan: ControlPoint(5, 0.0, 1.0, constJitter: 1.0),
		susDev: ControlPoint(6, 0, 0.75)
	)
));
~system.makeInstrumentVariant(\grainBuf1, \grainBuf1V1, nil, (dur: 0.1, sustain: 0.5, filepos: 0.23, posDev: 0.02, pan: 0.9, susDev: 0.05));



/*~system.registerInstrument(PatternInstrument(\glisson,
	{
		arg event;

		Pbindef(event.at(\name),
			\instrument, \glisson,
			\dur, Pfunc({ event.at(\con1) }),
			\sustain, Pfunc({ event.at(\con2) }),
			\freq, Pfunc({ event.at(\monoNote) }).midicps,
			\freqDev, Pfunc({ event.at(\con3) }),
			\envwide, Pfunc({ event.at(\con4) })
		);
	},
	[[\con1, 0.1, 1.0], [\con2, 0.1, 1.0],[\con3, 0, 500], [\con4, 0.01, 0.99]],
	registerForNotes: true
));*/


~system.registerInstrument(PatternInstrument(\liveGran, \basicLiveGran,
	(
		dur: ControlPoint(1, 0.03, 0.5, default: 0.5, controlJitter: 0.25, constJitter: 0.25),
		envbuf: ~envs.buffers.num3,
		sus: ControlPoint(2, 0.03, 0.6, default: 0.09),
		pan: ControlPoint(3, 0, 1.0, default: 0, constJitter: 1.0),
		susDev: ControlPoint(4, 0, 1.0, default: 0)
	)
));
~system.makeInstrumentVariant(\liveGran, \liveGranV1, nil, (dur: 0.1, sus: 0.07, pan: 0.5, susDev: 0.25));
~system.makeInstrumentVariant(\liveGran, \liveGranV2, nil, (dur: 0.15, sus: 0.2, pan: 0.5, susDev: 0.4));


~system.registerInstrument(PatternInstrument(\glissonLive, \glissonLive,
	(
		dur: ControlPoint(1, 0.2, 2.0, default: 1.0, controlJitter: 0.03),
		envbuf: ~envs.buffers.num3,
		sustain: ControlPoint(2, 0.5, 1.5),
		freqShift: ControlPoint(3, 0, 300),
		pan: ControlPoint(4, 0, 1.0, constJitter: 1.0),
	)
));
~system.makeInstrumentVariant(\glissonLive, \glissonLiveV1, nil, (dur: 2.0, sustain: 1.0, freqShift: 175, pan: 0.9));

/*~system.registerInstrument(PatternInstrument(\constQLive,
	{
		arg event;

		Pbindef(event.at(\name),
			\instrument, \constQLive,
			\dur, Pfunc({ event.at(\con1) }),
			\freq, Pfunc({ event.at(\monoNote) }),
			\sustain, Pfunc({ event.at(\con2) }),
			\freqDev, Pfunc({ event.at(\con3) }),
			\rq, Pfunc({ event.at(\con4) }),
			\panDev, Pfunc({ event.at(\con5) }),
			\pan, 0 + Pwhite(-1.0 * Pkey(\panDev), Pkey(\panDev), inf)
		);
	},
	[
		[\con1, 0.1, 1.0], [\con2, 0.1, 1.0],
		[\con3, 0.0, 500], [\con4, 0.05, 1.0],
		[\con5, 0.0, 1.0]
	], registerForNotes: true
));*/


~system.registerInstrument(PatternInstrument(\samplerStereo, \stereoPlayer,
	(
		dur: ControlPoint(1, 0.5, 6.0, default: 0.5, constJitter: 0.2),
		rate: ControlPoint(2, -12.midiratio, 12.midiratio, default: 1, constJitter: 0.01),
		startMult: Pxrand(Array.series(10, 0, 0.1), inf),
		atkRelRatio: ControlPoint(3, 0.01, 0.49, default: 0.1),
		len: ControlPoint(4, 0.1, 2.0, default: 0.4, constJitter: 0.05),
		curve: ControlPoint(5, -4.0, 4.0, default: 1.0),
		pan: ControlPoint(6, 0, 1.0, default: 0, constJitter: 1.0),
		amp: 1.0
	)
));

~system.registerInstrument(PatternInstrument(\samplerMono, \monoPlayer,
	(
		dur: ControlPoint(1, 0.5, 6.0, default: 0.5, constJitter: 0.2),
		rate: ControlPoint(2, -12.midiratio, 12.midiratio, default: 1, constJitter: 0.01),
		startMult: Pxrand(Array.series(10, 0, 0.1), inf),
		atkRelRatio: ControlPoint(3, 0.01, 0.49, default: 0.1),
		len: ControlPoint(4, 0.1, 2.0, default: 0.4, constJitter: 0.05),
		curve: ControlPoint(5, -4.0, 4.0, default: 1.0),
		pan: ControlPoint(6, 0, 1.0, default: 0, constJitter: 1.0),
		amp: 1.0
	)
));

~system.registerInstrument(PatternInstrument(\granularSampling, \stereoPlayer,
	(
		dur: ControlPoint(1, 0.1, 2.0, default: 0.2, constJitter: 0.05),
		rate: ControlPoint(2, 0.25, 4.0, default: 1.0, constJitter: 0.01),
		startMult: ControlPoint(3, 0, 1, default: 0, constJitter: 0.05),
		atkRelRatio: ControlPoint(4, 0.01, 0.49, default: 0.1),
		len: ControlPoint(5, 0.1, 2.0, default: 0.4, constJitter: 0.05),
		curve: ControlPoint(6, -4.0, 4.0, default: 1.0),
		pan: ControlPoint(7, 0, 1.0, default: 0, constJitter: 1.0),
		amp: 1.0
	)
));
~system.makeInstrumentVariant(\granularSampling, \granularClose, nil, (
	dur: 0.01,
	len: 0.2
));
~system.registerInstrument(PatternInstrument(\granularSamplingReverse, \stereoPlayer,
	(rate: ControlPoint(2, -0.25, -4.0, default: -1.0, constJitter: 0.01))
));


~system.registerInstrument(PatternInstrument(\danger1, \stereoPlayer,
	(
		dur: Pwhite(2.0, 4.0, inf),
		rate: Pwrand([0.5, 1.0, 2.0], [2,4,1].normalizeSum, inf) * Pwrand([-1.0, 1.0], [4,1].normalizeSum, inf),
		startPos: 44100 * Pwrand([0,14,29,42], [6,4,1,3].normalizeSum, inf),
		atk: Pwhite(0.8, 1.5, inf),
		sus: Pwhite(0.9, 1.1, inf),
		rel: Pwhite(3.0, 4.0, inf),
		curve: 3.0,
		pan: Pwhite(-0.5, 0.5, inf),
		amp: 0.5
	)
));

~system.registerInstrument(PatternInstrument(\danger2, \stereoPlayer,
	(
		dur: Pwhite(3.0, 6.0, inf),
		rate: Prand([0.5, 1.0], inf) * Pwrand([-1.0, 1.0], [2, 4].normalizeSum, inf),
		startPos: 44100 * Pxrand([0, 16], inf),
		atk: Pwhite(1.0, 3.0),
		sus: 1.0,
		rel: Pwhite(3.0, 5.0, inf),
		curve: 3.0,
		pan: Pwhite(-0.8, 0.8, inf),
		amp: 0.7
	)
));

~system.registerInstrument(PatternInstrument(\danger3, \stereoPlayer,
	(
		dur: Pwhite(3.0, 6.0, inf),
		rate: Prand([1.0, 2.0], inf),
		startPos: 44100 * Prand([0, 16], inf),
		atk: Pwhite(1.0, 3.0),
		sus: 1.0,
		rel: Pwhite(3.0, 5.0, inf),
		curve: 3.0,
		pan: Pwhite(-1.0, 1.0, inf),
		amp: 0.5
	)
));

~system.registerInstrument(PatternInstrument(\danger4, \stereoPlayer,
	(
		dur: Pwhite(1.2, 2.0, inf),
		rate: Pwrand([1.0, 2.0], [8,1].normalizeSum, inf),
		startPos: 44100 * Pwrand([0,13,23,38,45], [3,4,1,2,3].normalizeSum, inf),
		atk: Pwhite(0.9, 1.5, inf),
		sus: Pwhite(0.8, 1.2, inf),
		rel: Pwhite(1.5, 3.0, inf),
		curve: 3.0,
		pan: Pwhite(-0.9, 0.9, inf),
		amp: 0.8
	)
));

~system.registerInstrument(PatternInstrument(\pitchShiftFrag, \pitchShiftComponent,
	(
		dur: ControlPoint(1, 0.1, 1.0, default: 0.2),
		envPitchAmt: ControlPoint(2, 0, 0.5, default: 0.005),
		amp: 2.0,
		atk: ControlPoint(3, 0.001, 1.5, default: 0.001),
		sus: ControlPoint(4, 0.1, 2.0, default: 0.8),
		rel: ControlPoint(5, 0.2, 2.0, default: 1.0),
		pitchRatio: Pwrand([1.0, 2.0], [3,1].normalizeSum, inf),
		pitchDispersion: ControlPoint(6, 0.01, 0.2, default: 0.01),
		ffreq: 5000,
		modFreq: ControlPoint(7, 0.2, 3.0, default: 1.0),
		modDev: ControlPoint(8, 0.05, 1.0, default: 0.1),
		pan: Pwhite(-1.0, 1.0, inf)
	)
));
~system.makeInstrumentVariant(\pitchShiftFrag, \pitchShiftFragV1,
	nil,
	(dur: 0.5, envPitchAmt: 0.01, atk: 0.5, sus: 0.2, rel: 0.7,
		pitchDispersion: 0.03, modFreq: 1.0, modDev: 0.5)
);
~system.makeInstrumentVariant(\pitchShiftFrag, \pitchShiftFragV2,
	nil,
	(dur: 0.5, envPitchAmt: 0.02, atk: 0.5, sus: 0.2, rel: 0.8,
		pitchDispersion: 0.02, modFreq: 0.5, modDev: 1.0)
);


~system.registerInstrument(PatternInstrument(\pitchShiftFragBuffer, \pitchShiftComponentBuffer,
	(
		dur: ControlPoint(1, 0.5, 2.0),
		envPitchAmt: ControlPoint(2, 0, 0.5),
		amp: 2.0,
		atk: 0.01,
		sus: Pwhite(0.5, 2.0, inf),
		rel: Pwhite(0.5, 3.0, inf),
		pitchRatio: Pwrand([1.0, 2.0], [3,1].normalizeSum, inf),
		pitchDispersion: Pwhite(0.01, 0.05, inf),
		ffreq: 5000,
		modFreq: Pwhite(0.5, 2.0, inf),
		modDev: 0.1,
		pan: Pwhite(-1.0, 1.0, inf)
	)
));


~system.registerInstrument(PatternInstrument(\flutter, \flutter,
	(
		rate: 1.0,
		start: Pwhite(5.0, 30.0, inf),
		len: 4.0,
		fSpeed: Pwhite(0.1, 0.2, inf),
		fAmt: Pwhite(0.002, 0.005, inf),
		tSpeed: 3.0,
		tThresh: 0.9,
		amp: 1.0,
		pan: 0,
		dur: Pkey(\len) * 0.99,
		atk: 0.01,
		rel: 0.01,
		sus: Pkey(\len) - Pkey(\atk) - Pkey(\rel),
		lagAmt: 0.3
	)
));


~system.registerInstrument(PatternInstrument(\flutterLive, \flutterLive,
	(
		rate: 1.0,
		start: 0,
		len: 2.0,
		fSpeed: Pwhite(0.1, 0.2, inf),
		fAmt: Pwhite(0.002, 0.005, inf),
		tSpeed: 3.0,
		tThresh: 0.9,
		amp: 1.0,
		pan: Pwhite(-1.0, 1.0, inf),
		dur: 0.5,
		atk: 0.01,
		rel: 0.01,
		lagAmt: 0.3
	)
));


~system.registerInstrument(PatternInstrument(\minorPhrases, \threeNoteSeq,
	(
		dur: Pwrand([1,2,3,4], [2,2,1,1].normalizeSum, inf),
		noteArray: Prand([[[0,3,7]], [[0,7,10]], [[0,2,3]], [[3,5,10]]], inf),
		transArray: Prand([[[0,0,0]], [[0,12,0]]], inf),
		numNotes: Pwhite(40, 80, inf),
/*		noteOffsetMod: ControlPoint(1, 40, 70),
		noteOffset: Pkey(\noteOffsetMod) + Pwrand([0, 12], [4, 1].normalizeSum, inf),*/
		trigFreq: Pwhite(7, 12, inf),
		atkRatio: 0.5,
		relRatio: 0.5,
		panFreq: Pwhite(0.3, 1.0),
		modulo: Prand([3,4,5,6], inf),
		filFreqMult: Pwhite(2.0, 4.0, inf),
		amp: Pwhite(0.05, 0.15, inf),
	)
));


~system.registerInstrument(PatternInstrument(\majorPhrases, \threeNoteSeq,
	(
		dur: Pwrand([1,2,3,4], [2,2,1,1].normalizeSum, inf),
		noteArray: Prand([[[0,2,7]], [[0,4,9]], [[0,2,4]], [[7,9,4]]], inf),
		transArray: Prand([[[0,0,0]], [[0,12,0]]], inf),
		numNotes: Pwhite(40, 80, inf),
		noteOffsetMod: ControlPoint(1, 40, 70, default: 43),
		noteOffset: Pkey(\noteOffsetMod) + Pwrand([0, 12], [4, 1].normalizeSum, inf),
		trigFreq: Pwhite(7, 12, inf),
		atkRatio: 0.5,
		relRatio: 0.5,
		panFreq: Pwhite(0.3, 1.0),
		modulo: Prand([3,4,5,6], inf),
		filFreqMult: Pwhite(2.0, 4.0, inf),
		amp: Pwhite(0.05, 0.15, inf),
	)
));

~system.registerInstrument(PatternInstrument(\feedbackNasty, \feedbackNasty,
	(
		dur: ControlPoint(1, 0.1, 1.0, default: 0.3),
		freq: NoteSourceControl(\noteOn, 0.midicps, 127.midicps, 50.midicps, \keyboard),
		fb: ControlPoint(2, 1, 4, default: 3),
		fbDelay: ControlPoint(3, 0.01, 0.5, default: 0.02),
		lfoAmt: ControlPoint(4, 1, 200, default: 100),
		lfoRate: ControlPoint(5, 0.1, 200, default: 100),
		envAtk: ControlPoint(6, 0.1, 5, default: 1),
		envRel: ControlPoint(7, 0.3, 5, default: 3),
		cutoff: ControlPoint(8, 50, 5000, default: 400)
	)
));











~system.registerInstrument(MidiOutInstrument(\majorWalk,
	(
		tempo: (120 / 60),
		dur: ControlPoint(1, 0.05, 0.8, default: 0.1),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.5),
		midinote: Pwalk(Scale.major.degrees, Pwrand([-1,1], [1,9].normalizeSum, inf), Prand([-1,1], inf)) + 50
	), 2
));


~system.registerInstrument(MidiOutInstrument(\majorWalkQuant,
	(
		tempo: (120 / 60),
		durMod: ControlPoint(1, 0.0625, 2.0, default: 0.125),
		dur: ControlSpec(0.0625, 2.0, \lin, 0.0625).map(Pkey(\durMod)),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.5),
		midinote: Pwalk(Scale.major.degrees, Pwrand([-1,1], [1,9].normalizeSum, inf), Prand([-1,1], inf)) + 50
	), 2
));


~system.registerInstrument(MidiOutInstrument(\majorPentaPermute,
	(
		tempo: (120 / 60),
		dur: ControlPoint(1, 0.05, 1.0, default: 0.08),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.5),
		midinote: Pn(Pshuf(Scale.majorPentatonic.degrees, 1), inf) + Pstutter(3, Prand([0,12,24], inf)) + 50
	), 2
));



~system.registerInstrument(MidiOutInstrument(\majorPentaPermuteQuant,
	(
		tempo: (120 / 60),
		durMod: ControlPoint(1, 0.0625, 2.0, default: 0.08),
		dur: ControlSpec(0.0625, 2.0, \lin, 0.0625).map(Pkey(\durMod)),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.5),
		midinote: Pn(Pshuf(Scale.majorPentatonic.degrees, 1), inf) + Pstutter(3, Prand([0,12,24], inf)) + 50
	), 2
));



~system.registerInstrument(MidiOutInstrument(\clutchSeq,
	(
		tempo: (120 / 60),
		durMod: ControlPoint(1, 0.0625, 2.0, default: 0.08),
		dur: ControlSpec(0.0625, 2.0, \lin, 0.0625).map(Pkey(\durMod)),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.1),
		midinote: Pclutch(Pseq(Scale.majorPentatonic.degrees, inf),
		Pseq([1, Pn(0, {4.rand}), Pn(1, {4.rand})], inf)
			) + Pstutter(3, Pwrand([0,12], [4,1].normalizeSum, inf)) + 50
	), 2
));
~system.makeInstrumentVariant(\clutchSeq, \clutchSeqV1,
	(
		amp: 1.0 * Pwhite(0.8, 1.2, inf),
		midinote: Pclutch(Pseq([0,2,3,5], inf),
		Pseq([1, Pn(0, {4.rand}), Pn(1, {4.rand})], inf)
			) + Pstutter(3, Pwrand([0,12], [4,1].normalizeSum, inf)) + 57
	)
);


~system.registerInstrument(MidiOutInstrument(\randomRuns,
	(
		tempo: (120 / 60),
		dur: ControlPoint(1, 0.05, 1.0, default: 0.08),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.1),
		midinote: Pn(Pfin({ rrand(3, 10) }, PdegreeToKey(Pseries(0,1,inf), Scale.majorPentatonic)), inf) + 50
	), 2
));


~system.registerInstrument(MidiOutInstrument(\randomRunsQuant,
	(
		tempo: (118 / 60),
		durMod: ControlPoint(1, 0.0625, 2.0, default: 0.0625),
		dur: ControlSpec(0.0625, 2.0, \lin, 0.0625).map(Pkey(\durMod)),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.1),
		midinote: Pn(Pfin({ rrand(3, 10) }, PdegreeToKey(Pseries(0,1,inf), Scale.majorPentatonic)), inf) + 57
	), 2
));



~system.registerInstrument(MidiOutInstrument(\chordArp,
	(
		tempo: (120 / 60),
		dur: ControlPoint(1, 0.05, 1.0, default: 0.08),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.1),
		midinote: Pseq([0,2,7], inf) + Pstutter(4, Prand([0,12], inf)) + 50
	), 2
));



~system.registerInstrument(MidiOutInstrument(\chordArpQuant,
	(
		tempo: (118 / 60),
		durMod: ControlPoint(1, 0.0625, 2.0, default: 0.0625),
		dur: ControlSpec(0.0625, 2.0, \lin, 0.0625).map(Pkey(\durMod)),
		amp: 1.0 * Pwhite(0.9, 1.1, inf),
		sustain: ControlPoint(2, 0.05, 3.0, default: 0.1),
		midinote: Pseq([0,4,7,11], inf) + Pstutter(3, Prand([0,12], inf)) + 60
	), 2
));



~system.registerInstrument(MidiOutInstrument(\scattering,
	(
		tempo: (110/ 60),
		sustain: ControlPoint(1, 0.05, 1.0, default: 0.1),
		midinote: Pn(Pshuf(Scale.majorPentatonic.degrees, 1), inf) + Pstutter(3, Pwrand([0,12,24], [4,4,1].normalizeSum, inf)) + 62,
		dur: Pstutter(Pwhite(2, 10, inf), Pfunc({ exprand(0.01, 0.3) })),
		amp: 1.0 * Pfunc({ |ev| ev.at(\dur).expexp(0.01, 0.3, 0.1, 0.7) })
	), 2
));
~system.makeInstrumentVariant(\scattering, \scatteringV1,
	(
		tempo: (118 / 60),
		midinote: Pn(Pshuf([0,2,3,5,7,9], 1), inf) + Pstutter(3, Pwrand([0,12,24], [4,4,1].normalizeSum, inf)) + 55,
		dur: Pstutter(Pwhite(2, 6, inf), Pfunc({ exprand(0.5, 1.0) }))
	),
	(sustain: 0.8)
);
~system.makeInstrumentVariant(\scattering, \scatteringV2,
	(
		tempo: (100 / 60),
		midinote: Pn(Pshuf([0,2,3,5], 1), inf) + Pstutter(3, Pwrand([0,12,24], [4,4,1].normalizeSum, inf)) + 75,
		dur: Pstutter(Pwhite(2, 6, inf), Pfunc({ exprand(0.5, 1.5) }))
	),
	(sustain: 0.8)
);
~system.makeInstrumentVariant(\scattering, \scatteringV3,
	(
		tempo: (100 / 60),
		midinote: Pn(Pshuf([0,3,7,10], 1), inf) + Pstutter(3, Pwrand([0,12], [3,4].normalizeSum, inf)) + 63,
		dur: Pstutter(Pwhite(2, 3, inf), Pfunc({ exprand(0.7, 1.5) })),
		amp: 0.1 * Pfunc({ |ev| ev.at(\dur).expexp(0.01, 0.3, 0.1, 0.7) })
	),
	(sustain: 1.0)
);


~system.registerInstrument(MidiOutInstrument(\chordSmear,
	(
		tempo: (50 / 60),
		midinote: Pseq([0,2,7], inf) + Pstutter(4, Prand([0,12], inf)) + 62,
		a_durMod: ControlPoint(1, 0.06, 0.55, default: 0.08),
		dur: Pkey(\a_durMod) * Pfunc({ (0.1.linrand * 3) + 0.8 }),
		sustain: Pfunc({ |ev| ev.at(\dur).expexp(0.1, 0.75, 0.2, 0.07)}),
		amp: Pseg([0.1,0.8,0.1], [Pwhite(1, 5, inf), Pwhite(1, 5, inf)], \lin, inf)
	), 2
));
~system.makeInstrumentVariant(\chordSmear, \chordSmearV1,
	(
		midinote: Pseq([10,0,7,3], inf) + Pstutter(2, Prand([0,7,12], inf)) + 59,
		amp: Pseg([0.1,0.8,0.1], [Pwhite(2, 8, inf), Pwhite(2, 8, inf)], \lin, inf)
	),
	(a_durMod: 15 / 155)
);
~system.makeInstrumentVariant(\chordSmear, \chordSmearV2,
	(
		midinote: Pseq([11,7,4,2,0], inf) + Pstutter(3, Prand([0,12], inf)) + 62,
		amp: Pseg([0.1,0.8,0.1], [Pwhite(1, 3, inf), Pwhite(1, 3, inf)], \lin, inf)
	),
	(a_durMod: 15 / 155)
);
~system.makeInstrumentVariant(\chordSmear, \chordSmearV3,
	(
		midinote: Pseq([0,7,10], inf) + Pstutter(2, Prand([0,12], inf)) + 55,
		amp: Pseg([0.1,0.8,0.1], [Pwhite(4, 8, inf), Pwhite(4, 8, inf)], \lin, inf)
	),
	(a_durMod: 0.1)
);
~system.makeInstrumentVariant(\chordSmear, \chordSmearV4,
	(
		a_index: ControlPoint(2, 0, 1, default: 0),
		midinote: Pindex(
			[
				0,
				Pseq([0,7]),
				Pseq([0,7,10])
			],
			ControlSpec(0, 2, \lin, 1).map(Pkey(\a_index))
		) + 61,
	)
);

~system.registerInstrument(MidiOutInstrument(\chordSmearIndex,
	(
		tempo: (50 / 60),
		a_index: ControlPoint(1, 0, 1, default: 0),
		midinote: Pindex(
			[
				0,
				Prand([0,7]),
				Prand([0,7,10])
			],
			ControlSpec(0, 2, \lin, 1).map(Pkey(\a_index))
		) + Pstutter(4, Prand([0,12], inf)) + 61,
		a_durMod: ControlPoint(2, 0.06, 0.55, default: 0.08),
		dur: Pkey(\a_durMod) * Pfunc({ (0.1.linrand * 3) + 0.8 }),
		sustain: Pfunc({ |ev| ev.at(\dur).expexp(0.1, 0.75, 0.2, 0.07)}),
		amp: Pseg([0.1,0.8,0.1], [Pwhite(1, 5, inf), Pwhite(1, 5, inf)], \lin, inf)
	), 2
));

/*
Use Pwalk, using value from control as the pointer for array reading
*/
~system.registerInstrument(MidiOutInstrument(\walk,
	(
		tempo: 1,
		lump: ControlPoint(1, 0, 1, default: 0),
		midinote: Pindex(
			[
				0,
				Pseq([0,7]),
				Pseq([0,7,10])
			],
			ControlSpec(0, 2, \lin, 1).map(Pkey(\lump))
		) + 67,
		dur: 0.25
	)
));















