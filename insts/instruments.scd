~system.registerInstrument(SynthInstrument(\tester, \sine, (freq: ControlPoint(1, 200, 4000, default: 500))));
~system.makeInstrumentVariant(\tester, \testerV1, nil, (freq: 1000));

~system.registerInstrument(SynthInstrument(\testMidi, \sine,
	(
		freq: NoteSourceControl(\monoNote, 0.midicps, 127.midicps, default: 60.midicps, module: \keyboard),
		amp: NoteSourceControl(\velocity, 0.0, 1.0, default: 0.5, module: \keyboard)
	)
));

~system.registerInstrument(SynthInstrument(\synthFxTest, \freqShiftBasic, (freq: ControlPoint(1, 0, 400))));
~system.makeInstrumentVariant(\synthFxTest, \synthFxTestV1, nil, (freq: 250));

~system.registerInstrument(SynthInstrument(\grainIn, \grainIn,
	(
		trigFreq: ControlPoint(1, 1.0, 20.0, default: 5.0),
		trigBalance: ControlPoint(2, 0.0, 1.0),
		dur: ControlPoint(3, 0.01, 0.7, default: 0.1),
		panVar: ControlPoint(4, 0.0, 1.0, default: 0.8)
	)
));
~system.makeInstrumentVariant(\grainIn, \grainInV2,
	(trigFreq: 9.0, trigBalance: 0.3, dur: 0.1, panVar: 0.95)
);

~system.registerInstrument(SynthInstrument(\liveSound, \liveSound));
~system.registerInstrument(SynthInstrument(\liveSoundMono, \liveSoundMono));

~system.registerInstrument(SynthInstrument(\playback, \playback));
~system.makeInstrumentVariant(\playback, \playbackV1,
	(recLevel: 0.5, preLevel: 0.5)
);
~system.makeInstrumentVariant(\playback, \playbackV2,
	(recLevel: 0.2, preLevel: 0.8)
);å
~system.registerInstrument(SynthInstrument(\playbackVarRate, \playback,
	(
		rate: ControlPoint(1, 0.25, 4.0, default: 1),
		loop: 1
	)
));
~system.registerInstrument(SynthInstrument(\playbackVarRateReverse, \playback,
	(
		rate: ControlPoint(1, -0.25, -4.0, default: -1.0),
		loop: 1
	)
));
~system.registerInstrument(SynthInstrument(\playbackReverse, \playback, (rate: -1, loop: 1)));
~system.registerInstrument(SynthInstrument(\loopingPlayback, \playback, (loop: 1)));
~system.registerInstrument(SynthInstrument(\loopingPlaybackRateControl, \playback,
	(
		loop: 1,
		rate: ControlPoint(1, 0.25, 4.0, default: 1)
	)
));
~system.registerInstrument(SynthInstrument(\loopingPlaybackReverse, \playback, (loop: 1, rate: -1.0)));
~system.registerInstrument(SynthInstrument(\loopingPlaybackReverseRateControl, \playback,
	(
		loop: 1,
		rate: ControlPoint(1, -0.25, -4.0, default: -1)
	)
));
~system.registerInstrument(SynthInstrument(\volumePlayback, \playback,
	(
		amp: ControlPoint(1, 0, 1.5, default: 0.5)
	)
));
~system.registerInstrument(SynthInstrument(\quietPlayback, \playback, (amp: 0.2)));

~system.registerInstrument(SynthInstrument(\monoPlayback, \monoPlayback));
~system.registerInstrument(SynthInstrument(\loopingMonoPlayback, \monoPlayback, (loop: 1)));

~system.registerInstrument(SynthInstrument(\chorus1, \chorus1,
	(
		preDelay: ControlPoint(1, 0.01, 0.5),
		speed: ControlPoint(2, 0.01, 0.4),
		depth: ControlPoint(3, 0.01, 0.1),
		ph_diff: ControlPoint(4, 0.0, 1.0),
		hiDev: ControlPoint(5, 1.0, 60.0),
		ffreq: ControlPoint(6, 0.0001, 1000)
	)
));
~system.makeInstrumentVariant(\chorus1, \chorus1V1,
	(lowPanSpeed: 0.05, hiPanSpeed: 1.0),
	(preDelay: 0.01, speed: 0.01, depth: 0.02, ph_diff: 0.25pi, hiDev: 2.0, ffreq: 100)
);
~system.makeInstrumentVariant(\chorus1, \chorus1V2,
	(lowPanSpeed: 0.05, hiPanSpeed: 1.0),
	(preDelay: 0.01, speed: 0.01, depth: 0.0025, ph_diff: 0.25pi, hiDev: 0.5, ffreq: 100)
);
~system.makeInstrumentVariant(\chorus1, \chorus1V3,
	(lowPanSpeed: 0.05, hiPanSpeed: 0.25),
	(preDelay: 0.05, speed: 3.0, depth: 0.0008, ph_diff: 0.5pi, hiDev: 2.0, ffreq: 200)
);
~system.makeInstrumentVariant(\chorus1, \chorus1V4,
	(lowPanSpeed: 0.02, hiPanSpeed: 0.5),
	(preDelay: 0.01, speed: 0.2, depth: 0.001, ph_diff: 0.25pi, hiDev: 1.0, ffreq: 100)
);
~system.makeInstrumentVariant(\chorus1, \chorus1V5,
	(
		lowPanSpeed: 0.05,
		hiPanSpeed: 0.8,
		speed: ControlPoint(2, 2.0, 6.0, default: 2.0),
		depth: ControlPoint(3, 0.0001, 0.001, default: 0.0007),
		hiDev: ControlPoint(5, 1.0, 6.0, default: 1.0),
	),
	(ffreq: 100)
);
~system.makeInstrumentVariant(\chorus1, \chorus1V6,
	(
		lowPanSpeed: 0.05,
		hiPanSpeed: 0.8,
		speed: ControlPoint(2, 0.001, 1.0, default: 0.01),
		depth: ControlPoint(3, 0.0008, 0.02, default: 0.001),
		hiDev: ControlPoint(5, 1.0, 6.0, default: 1.0)
	),
	(ffreq: 100)
);


// broken
~system.registerInstrument(SynthInstrument(\chorus2, \chorus2,
	(
		speed: ControlPoint(1, 0.01, 1.0),
		depth: ControlPoint(2, 0.01, 0.5),
		dev: ControlPoint(3, 0.0, 0.5),
		ffreq: ControlPoint(4, 0.0, 1000)
	)
));
~system.makeInstrumentVariant(\chorus2, \chorus2V1,
	(times: [0.1,0.11,0.15,0.2]),
	(speed: 0.1, depth: 0.1, dev: 0.1, ffreq: 200)
);
~system.makeInstrumentVariant(\chorus2, \chorus2V2,
	(times: [0.03,0.04,0.046,0.05], decaytime: 0.5),
	(speed: 1.0, depth: 0.01, dev: 0.15, ffreq: 100)
);
~system.makeInstrumentVariant(\chorus2, \chorus2V3,
	(times: [0.08,0.11,0.13,0.25], decaytime: 2.0, lpfFreq: 7000),
	(speed: 0.25, depth: 0.015, dev: 0.4, ffreq: 100)
);


// broken
~system.registerInstrument(SynthInstrument(\chorus3, \chorus3,
	(
		speed: ControlPoint(1, 0.01, 0.4),
		depth: ControlPoint(2, 0.01, 0.1),
		dev: ControlPoint(3, 0.0, 0.5),
		ffreq: ControlPoint(4, 0.0, 1000),
		factor: ControlPoint(5, 0.0, 1.0)
	)
));
~system.makeInstrumentVariant(\chorus3, \chorus3V1,
	(times: [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2], decaytime: 2.5),
	(speed: 0.4, depth: 0.005, dev: 0.3, ffreq: 100, factor: 0.5)
);
~system.makeInstrumentVariant(\chorus3, \chorus3V2,
	(times: [0.1,0.2,0.3,0.4,0.51,0.62,0.73,0.85,0.95,1.1,1.28,1.35], decaytime: 2.5),
	(speed: 0.8, depth: 0.002, dev: 0.4, ffreq: 100, factor: 0.4)
);


~system.registerInstrument(SynthInstrument(\chorus4, \chorus4,
	(
		speed: ControlPoint(1, 0.01, 0.4),
		depth: ControlPoint(2, 0.01, 0.1),
		dev: ControlPoint(3, 0.0, 0.5),
		ffreq: ControlPoint(4, 0.0, 1000),
		decay: ControlPoint(5, 0.5, 3.0),
		pitchDispersion: ControlPoint(6, 0.0, 0.2),
		timeDispersion: ControlPoint(7, 0.0, 1.0)
	)
));
~system.makeInstrumentVariant(\chorus4, \chorus4V1,
	nil,
	(speed: 0.3, depth: 0.01, dev: 0.15, ffreq: 50, decay: 1.0, pitchDispersion: 0.01, timeDispersion: 0.5)
);
~system.makeInstrumentVariant(\chorus4, \chorus4V2,
	nil,
	(speed: 0.15, depth: 0.011, dev: 0.15, ffreq: 50, decay: 2.0, pitchDispersion: 0.009, timeDispersion: 0.7)
);
~system.makeInstrumentVariant(\chorus4, \chorus4V3,
	nil,
	(speed: 0.15, depth: 0.0593, dev: 0.23, ffreq: 50, decay: 1.3, pitchDispersion: 0.006, timeDispersion: 0.3)
);
~system.makeInstrumentVariant(\chorus4, \chorus4V4,
	nil,
	(speed: 0.36, depth: 0.012, dev: 0.15, ffreq: 80, decay: 2.5, pitchDispersion: 0.012, timeDispersion: 0.65)
);
~system.makeInstrumentVariant(\chorus4, \chorus4V5,
	nil,
	(speed: 0.29, depth: 0.012, dev: 0.05, ffreq: 140, decay: 3.0, pitchDispersion: 0.009, timeDispersion: 0.75)
);
~system.makeInstrumentVariant(\chorus4, \chorus4V6,
	nil,
	(speed: 0.10, depth: 0.008, dev: 0.04, ffreq: 140, decay: 4.6, pitchDispersion: 0.005, timeDispersion: 0.91)
);



~system.registerInstrument(SynthInstrument(\phaser1, \phaser1,
	(
		preDelay: ControlPoint(1, 0.01, 2.0),
		speed: ControlPoint(2, 0.01, 1.0),
		depth: ControlPoint(3, 0.001, 0.5),
		ph_diff: ControlPoint(4, 0.0, 1.0),
		modLo: ControlPoint(5, 0.00022675, 0.5),
		modRange: ControlPoint(6, 0.01, 1.0),
		speedVar: ControlPoint(7, 0.0, 0.8)
	)
));
~system.makeInstrumentVariant(\phaser1, \phaser1V1,
	nil,
	(preDelay: 0.03, speed: 0.4, depth: 0.1, ph_diff: 0.5, modLo: 0.21, modRange: 0.1, speedVar: 0.1)
);
~system.makeInstrumentVariant(\phaser1, \phaser1V2,
	nil,
	(preDelay: 0.001, speed: 0.2, depth: 0.05, ph_diff: 0.75, modLo: 0.001, modRange: 0.15, speedVar: 0.5)
);
~system.makeInstrumentVariant(\phaser1, \phaser1V3,
	(decaytime: 1.0),
	(preDelay: 0.001, speed: 0.07, depth: 0.09, ph_diff: 0.5, modLo: 0.001, modRange: 0.6, speedVar: 0.15)
);
~system.makeInstrumentVariant(\phaser1, \phaser1V4,
	(decaytime: 2.0),
	(preDelay: 0.01, speed: 0.78, depth: 0.009, ph_diff: 0.5, modLo: 0.001, modRange: 0.6, speedVar: 0.11)
);


~system.registerInstrument(SynthInstrument(\combN, \combN,
	(
		delTime: ControlPoint(1, 0.001, 0.3),
		speed: ControlPoint(2, 0.001, 1.0),
		depth: ControlPoint(3, 0.001, 1.0),
		decay: ControlPoint(4, 0.0, 2.0),
		delTimeDev: ControlPoint(5, 0.001, 0.3),
		speedDev: ControlPoint(6, 0.0001, 1.5),
		depthDev: ControlPoint(7, 0.0001, 0.5)
	)
));
~system.makeInstrumentVariant(\combN, \combNV1,
	nil,
	(delTime: 0.01, speed: 0.1, decay: 0.4, depth: 0.001, delTimeDev: 0.005, speedDev: 0.001, depthDev: 0.0)
);
~system.makeInstrumentVariant(\combN, \combNV2,
	nil,
	(delTime: 0.005, speed: 0.03, decay: 0.5, depth: 0.002, delTimeDev: 0.001, speedDev: 0.0001, depthDev: 0.0)
);
~system.makeInstrumentVariant(\combN, \combNV3,
	nil,
	(delTime: 0.005, speed: 0.09, decay: 0.8, depth: 0.0015, delTimeDev: 0.001, speedDev: 0.0001, depthDev: 0.0001)
);


~system.registerInstrument(SynthInstrument(\combNClose, \combN,
	(
		delTime: ControlPoint(1, 0.001, 0.15, default: 0.001),
		speed: ControlPoint(2, 0.0001, 0.1, default: 0.001),
		depth: ControlPoint(3, 0.0001, 0.1, default: 0.001),
		decay: ControlPoint(4, 0.0, 2.0, default: 0.1),
		delTimeDev: ControlPoint(5, 0.0, 0.05, default: 0.0),
		speedDev: ControlPoint(6, 0.0, 0.05, default: 0.0),
		depthDev: ControlPoint(7, 0.0, 0.05, default: 0.0)
	)
));


~system.registerInstrument(SynthInstrument(\squiz, \squiz,
	(
		pitchratio: ControlPoint(1, 0.5, 2.0),
		zcperchunk: ControlPoint(2, 1, 10),
		drop: ControlPoint(3, 0, 40),
		ffreq: ControlPoint(4, 50, 10000),
		rq: ControlPoint(5, 1.0, 0.01)
	)
));
~system.makeInstrumentVariant(\squiz, \squizV1,
	nil,
	(pitchratio: 1.0, zcperchunk: 2, drop: 9, ffreq: 50, rq: 1.0)
);
~system.makeInstrumentVariant(\squiz, \squizV2,
	nil,
	(pitchratio: 1.0, zcperchunk: 10, drop: 20, ffreq: 1000, rq: 0.5)
);
~system.makeInstrumentVariant(\squiz, \squizV3,
	nil,
	(pitchratio: 0.5, zcperchunk: 10, drop: 6, ffreq: 303, rq: 0.3)
);


~system.registerInstrument(SynthInstrument(\scramble, \scramble,
	(
		wipe: ControlPoint(1, 0.0, 1.0),
		ffreq: ControlPoint(2, 3000, 15000)
	)
));
~system.makeInstrumentVariant(\scramble, \scrambleV1,
	(width: 0.3),
	(wipe: 0.15, ffreq: 4500)
);


~system.registerInstrument(SynthInstrument(\shift, \shift,
	(
		stretch: ControlPoint(1, 1.0, 2.0),
		shiftAmt: ControlPoint(2, -128, 128)
	)
));

~system.registerInstrument(SynthInstrument(\brickWall, \brickWall,
	(wipe: ControlPoint(1, -0.98, -0.4))
));
~system.makeInstrumentVariant(\brickWall, \brickWallV1, nil, (wipe: -0.91));
~system.makeInstrumentVariant(\brickWall, \brickWallV2, nil, (wipe: -0.81));
~system.makeInstrumentVariant(\brickWall, \brickWallV3, nil, (wipe: 0.01));
~system.makeInstrumentVariant(\brickWall, \brickWallV4, nil, (wipe: 0.03));

~system.registerInstrument(SynthInstrument(\conformalMap, \conformalMap,
	(
		areal: ControlPoint(1, 0.01, 10.0),
		aimag: ControlPoint(2, 0.01, 10.0)
	)
));

~system.registerInstrument(SynthInstrument(\conjugate, \conjugate));

~system.registerInstrument(SynthInstrument(\pvFreeze, \pvFreeze, (freeze: ControlPoint(1, -0.5, 0.5))));

~system.registerInstrument(SynthInstrument(\pvLocalMax, \pvLocalMax, (thresh: ControlPoint(1, 0, 50))));

~system.registerInstrument(SynthInstrument(\pvMagFreeze, \pvMagFreeze, (freeze: ControlPoint(1, -0.5, 0.5))));

~system.registerInstrument(SynthInstrument(\pvMagNoise, \pvMagNoise));

~system.registerInstrument(SynthInstrument(\pvMagShift, \pvMagShift,
	(
		stretch: ControlPoint(1, 0.25, 4.0),
		shift: ControlPoint(2, -128, 128)
	)
));

~system.registerInstrument(SynthInstrument(\pvMagSmear, \pvMagSmear, (bins: ControlPoint(1, 1, 90))));

~system.registerInstrument(SynthInstrument(\pvMagSmooth, \pvMagSmooth, (factor: ControlPoint(1, 0.0, 1.0)))); // ***
~system.makeInstrumentVariant(\pvMagSmooth, \pvMagSmoothV1, nil, (factor: 0.99));
~system.makeInstrumentVariant(\pvMagSmooth, \pvMagSmoothV2, nil, (factor: 0.995));
~system.makeInstrumentVariant(\pvMagSmooth, \pvMagSmoothV3, nil, (factor: 0.8));
~system.makeInstrumentVariant(\pvMagSmooth, \pvMagSmoothV4, nil, (factor: 0.9975));
~system.makeInstrumentVariant(\pvMagSmooth, \pvMagSmoothV5, nil, (factor: 0.999));
// combine with stuttering effect?

~system.registerInstrument(SynthInstrument(\pvMagSquared, \pvMagSquared));
// this would benefit from stereo spreading

~system.registerInstrument(SynthInstrument(\pvSpectralEnhance, \pvSpectralEnhance,
	(
		partials: ControlPoint(1, 1, 16),
		ratio: ControlPoint(2, 1.0, 4.0),
		strength: ControlPoint(3, 0, 0.99)
	)
));
// good with heavily distorted or broken sounds


~system.registerInstrument(SynthInstrument(\evenBin, \evenBin));

~system.registerInstrument(SynthInstrument(\pvMagBelow, \pvMagBelow, (threshold: ControlPoint(1, 0.0, 20.0))));

~system.registerInstrument(SynthInstrument(\pvMagAbove, \pvMagAbove, (threshold: ControlPoint(1, 0, 20))));

~system.registerInstrument(SynthInstrument(\pvPhaseShift, \pvPhaseShift,
	(
		shift: ControlPoint(1, 0.0, 60.0),
		integrate: ControlPoint(2, 0.0, 1.0)
	)
));

~system.registerInstrument(SynthInstrument(\binWipeLH, \binWipe, (wipe: ControlPoint(1, 0.0, 1.0))));
~system.registerInstrument(SynthInstrument(\binWipeHL, \binWipe, (wipe: ControlPoint(1, -1.0, 0.0))));

~system.registerInstrument(SynthInstrument(\bufGrab, \bufGrab,
	(
		trigFreq: ControlPoint(1, 1.0, 25.0),
		rate: ControlPoint(2, 0.1, 2.0),
		rateVar: ControlPoint(3, 0.001, 1.0),
		posVar: ControlPoint(4, 0.001, 3.0),
		durVar: ControlPoint(5, 0.001, 3.0),
		panVar: ControlPoint(6, 0.001, 1.0),
		ampVar: ControlPoint(7, 0.001, 0.5)
	)
));
~system.makeInstrumentVariant(\bufGrab, \bufGrabV1,
	(filePos: 0.05, durDivide: 3, trigBalance: 0.4),
	(panVar: 1.0, rate: 1.0, trigFreq: 6)
);
~system.makeInstrumentVariant(\bufGrab, \bufGrabV2,
	(filePos: 0.05, durDivide: 1, trigBalance: 0.2),
	(panVar: 1.0, rate: 2.0, trigFreq: 1)
);
~system.makeInstrumentVariant(\bufGrab, \bufGrabV3,
	(filePos: 0.05, durDivide: 12, trigBalance: 0.2),
	(panVar: 1.0, rate: 0.5, trigFreq: 5)
);


~system.registerInstrument(SynthInstrument(\cloud, \cloud,
	(
		dur: ControlPoint(1, 0.03, 2.0),
		trigFreq: ControlPoint(2, 0.5, 30),
		trigBalance: ControlPoint(3, 0.0, 1.0),
		recordBalance: ControlPoint(4, 0.0, 1.0),
		freeze: ControlPoint(5, 0.0, 1.0, default: 1),
		panDev: ControlPoint(6, 0.0, 1.0),
		ampDev: ControlPoint(7, 0.001, 1.0),
		durDev: ControlPoint(8, 0.001, 1.0)
	)
));
~system.makeInstrumentVariant(\cloud, \cloudV1,
	(posDev: 0),
	(trigFreq: 10, trigBalance: 0.25, dur: 0.25, recordBalance: 0.1,
		panDev: 1.0, ampDev: 0, durDev: 0)
);
~system.makeInstrumentVariant(\cloud, \cloudV2,
	(posDev: 0),
	(trigFreq: 10, trigBalance: 0.8, dur: 0.1, recordBalance: 0.5,
		panDev: 1.0, ampDev: 0, durDev: 0)
);
~system.makeInstrumentVariant(\cloud, \cloudV3,
	(posDev: 0),
	(trigFreq: 10, trigBalance: 1.0, dur: 0.2, recordBalance: 0.5,
		panDev: 1.0, ampDev: 0, durDev: 0)
);


~system.registerInstrument(SynthInstrument(\eqFull, \eqFull));

~system.registerInstrument(SynthInstrument(\hiShelf, \hiShelf));

~system.registerInstrument(SynthInstrument(\lowShelf, \lowShelf));

~system.registerInstrument(SynthInstrument(\singleBand, \singleBand));

~system.registerInstrument(SynthInstrument(\doubleBand, \doubleBand));

~system.registerInstrument(SynthInstrument(\mantissa, \mantissa, (bits: ControlPoint(1, 0, 23))));
~system.makeInstrumentVariant(\mantissa, \mantissaV1,
	(ffreq: 2000, rq: 0.8, extraAmp: 3.0),
	(bits: 0)
);
~system.makeInstrumentVariant(\mantissa, \mantissaV2,
	(ffreq: 2000, rq: 0.7, extraAmp: 3.0),
	(bits: 4)
);
~system.makeInstrumentVariant(\mantissa, \mantissaV3,
	(ffreq: 1600, rq: 0.7, extraAmp: 3.0),
	(bits: 8)
);
~system.makeInstrumentVariant(\mantissa, \mantissaV4,
	(ffreq: 1000, rq: 0.8, extraAmp: 3.0),
	(bits: 8)
);


~system.registerInstrument(SynthInstrument(\combMelt, \combMelt,
	(modFreq: 0.05, fbDev: 0.6, lagTime: 2.5, decayTime: 1.0,
		modDev: 0.1, fbAmt: 0.5, delTime: 0.009)
));
~system.makeInstrumentVariant(\combMelt, \combMeltV1,
	(modFreq: 0.08, decayTime: 3.0, modDev: 0.01, lagTime: 0.1)
);
~system.makeInstrumentVariant(\combMelt, \combMeltV2,
	(modFreq: 0.03, modDev: 0.01)
);
// beautiful when run into reverb


~system.registerInstrument(SynthInstrument(\fdbkPShift, \fdbkPShift,
	(pitchRatio1: 2.0, amp: 1.0, windowSize1: 0.3, pitchDispersion1: 0.03,
		timeDispersion1: 0.1, add2Amt: 0.6, freqShift: 3, modFreq: 0.1, modDev: 0.001)
));

~system.registerInstrument(SynthInstrument(\gurgleComb, \gurgleComb,
	(modFreq: 100, a: 1.1, b: 2, modDev: 0.2, decayTime: 1.0, bal: 0.5)
));
~system.makeInstrumentVariant(\gurgleComb, \gurgleCombV1,
	(modFreq: 1000, a: 1.1, b: 2, modDev: 0.1, decayTime: 1.0, bal: 0.7)
);


~system.registerInstrument(SynthInstrument(\combModder, \combModder,
	(modFreq: 10.0, modDev: 0.5, decayTime: 2.0)
));

~system.registerInstrument(SynthInstrument(\breakComb, \breakComb,
	(trigFreq: 10.0, step: 0.0001, start: 0.0001, decayTime: 2.0,
		vol: 0.7, modFreq: 0.1, modLow: 0.98, modHigh: 1.02, lagTime: 1.0)
));

~system.registerInstrument(SynthInstrument(\dfmNum1, \dfmNum1,
	(ffreq: 900, res: 1.0, amp: 0.1, gain: 10.0, noiseLevel: 0.02,
		delAmt: 0.1, decayTime: 1.0, delayTime: 0.01, modFreq: 0.1,
		modDev: 0.5, pitchRatio: 2.0, shiftedAmt: 0.8,
		pitchDispersion: 0.05, timeDispersion: 0.3)
));

~system.registerInstrument(SynthInstrument(\twoCombs, \twoCombs,
	(inputAmt: 0.5, delLAmt: 0.5, delayTimeL: 0.01, modFreqL: 0.1,
		modDevL: 0.005, decayTimeL: 1.0, pitchRatioL: 0.98,
		pitchDispersionL: 0.01, timeDispersionL: 1.0, delRAmt: 0.5,
		delayTimeR: 0.02, modFreqR: 0.15, modDevR: 0.0047,
		decayTimeR: 1.5, pitchRatioR: 1.02, pitchDispersionR: 0.01,
		timeDispersionR: 1.0)
));


~system.registerInstrument(SynthInstrument(\fourDelays, \fourDelays,
	(
		delTime: ControlPoint(1, 0.1, 1.6),
		decayTime: ControlPoint(2, 0.5, 6.0),
		offset: ControlPoint(3, 0.0, 0.5)
	)
));
~system.makeInstrumentVariant(\fourDelays, \fourDelaysV1,
	nil,
	(delTime: (110 / 60).reciprocal, decayTime: 2.0, offset: 0.05)
);



~system.registerInstrument(SynthInstrument(\warp, \warp,
	(
		ptrFreq: 1.0,
		freqScale: 1.0,
		windowSize: 0.2,
		atk: 0.1,
		sus: 0.05,
		rel: 0.1,
		trigFreq: 0.1,
		decayTime: 2.0,
		lowTime: 0.01,
		highTime: 0.99,
		timeScale: 3.0
	)
));
~system.makeInstrumentVariant(\warp, \warpHalfSpeedV1,
	(ptrFreq: 0.1, freqScale: 0.5, atk: 0.5, rel: 0.5, trigFreq: 0.1)
);
~system.makeInstrumentVariant(\warp, \warpHalfSpeedV2,
	(ptrFreq: 0.05, freqScale: 0.5, atk: 1.0, rel: 1.0, trigFreq: 0.09, decayTime: 3.0)
);
~system.makeInstrumentVariant(\warp, \warpV2,
	(ptrFreq: 0.05, freqScale: 1.0, atk: 1.0, rel: 1.0, trigFreq: 0.09, decayTime: 3.0, lowTime: 0.4, highTime: 0.6)
);
~system.makeInstrumentVariant(\warp, \warpChorus,
	(ptrFreq: 0.1, freqScale: 1.0, atk: 0.9, rel: 0.9, trigFreq: 0.07, decayTime: 2.2, lowTime: 0.45, highTime: 0.55)
);
~system.makeInstrumentVariant(\warp, \warpV3,
	(ptrFreq: 0.025, windowSize: 0.1, trigFreq: 4, timeScale: 5.0)
);
~system.makeInstrumentVariant(\warp, \warpV4,
	(ptrFreq: 0.0125, windowSize: 0.2, trigFreq: 5, atk: 0.05, rel: 0.05, sus: 0.01, timeScale: 5.0)
);



~system.registerInstrument(SynthInstrument(\warpLive, \warpLive,
	(
		ptrFreq: 1.0,
		freqScale: 1.0,
		windowSize: 0.2,
		atk: 0.1,
		sus: 0.05,
		rel: 0.1,
		trigFreq: 0.1,
		decayTime: 2.0,
		lowTime: 0.01,
		highTime: 0.99,
		timeScale: 3.0
	)
));
~system.makeInstrumentVariant(\warpLive, \warpLiveHalfSpeedV1,
	(ptrFreq: 0.1, freqScale: 0.5, atk: 0.5, rel: 0.5, trigFreq: 0.1)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveHalfSpeedV2,
	(ptrFreq: 0.05, freqScale: 0.5, atk: 1.0, rel: 1.0, trigFreq: 0.09, decayTime: 3.0)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveV2,
	(ptrFreq: 0.05, freqScale: 1.0, atk: 1.0, rel: 1.0, trigFreq: 0.09, decayTime: 3.0, lowTime: 0.4, highTime: 0.6)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveChorus,
	(ptrFreq: 0.1, freqScale: 1.0, atk: 0.9, rel: 0.9, trigFreq: 0.07, decayTime: 2.2, lowTime: 0.45, highTime: 0.55)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveV3,
	(ptrFreq: 0.025, windowSize: 0.1, trigFreq: 4, timeScale: 5.0)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveV4,
	(ptrFreq: 0.0125, windowSize: 0.2, trigFreq: 5, atk: 0.05, rel: 0.05, sus: 0.01, timeScale: 5.0)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveV5,
	(ptrFreq: 0.5, freqScale: 1.0, atk: 1.0, rel: 1.0, trigFreq: 0.09, decayTime: 3.0, lowTime: 0.425, highTime: 0.575)
);
~system.makeInstrumentVariant(\warpLive, \warpLiveV6,
	(ptrFreq: 0.24, freqScale: 1.0, atk: 1.0, rel: 1.0, trigFreq: 0.09, decayTime: 3.0, lowTime: 0.435, highTime: 0.565)
);


~system.registerInstrument(SynthInstrument(\feedbackPitchShift, \feedbackPitchShift,
	(
		freq: NoteSourceControl(\monoNote, 0.midicps, 127.midicps, 50.midicps, \keyboard),
		pitchRatio: 4,
		freqRand: 0.01,
		lpfCutoff: ControlPoint(1, 500, 16000, default: 5000)
	)
));


~system.registerInstrument(SynthInstrument(\feedbackFreqShift, \feedbackFreqShift,
	(
		freq: NoteSourceControl(\monoNote, 0.midicps, 127.midicps, 50.midicps, \keyboard),
		freqShift: ControlPoint(1, 0, 100, default: 10),
		freqRand: 0.01,
		lpfCutoff: ControlPoint(2, 500, 16000, default: 5000)
	)
));
~system.makeInstrumentVariant(\feedbackFreqShift, \feedbackFreqShiftV1,
	(freqShift: ControlPoint(1, 0, 10, default: 1))
);


~system.registerInstrument(SynthInstrument(\inputFeedbackPitchShift, \inputFeedbackPitchShift,
	(
		pitchRatio: 5,
		lpfCutoff: 20000
	)
));


~system.registerInstrument(SynthInstrument(\inputFeedbackFreqShift, \inputFeedbackFreqShift,
	(
		freqShift: ControlPoint(1, 0, 100, default: 10),
		lpfCutoff: 20000
	)
));
~system.makeInstrumentVariant(\inputFeedbackFreqShift, \inputFeedbackFreqShiftV1,
	(freqShift: ControlPoint(1, 0, 10, default: 1))
);
~system.makeInstrumentVariant(\inputFeedbackFreqShift, \inputFeedbackFreqShiftV2,
	(freqShift: ControlPoint(1, 0, 5, default: 1))
);


~system.registerInstrument(SynthInstrument(\sineOscFB, \sineOscFB,
	(
		freq: NoteSourceControl(\monoNote, 0.midicps, 127.midicps, 50.midicps, \keyboard),
		fdbk: ControlPoint(3, 0.1, 5.0, default: 1.1),
		freqDetune: ControlPoint(1, 0, 0.1, default: 0.02),
		panOffset: ControlPoint(2, 0, 1.0, default: 0.8),
		lpfCutoff: ControlPoint(4, 3000, 16000, default: 5000)
	)
));


~system.registerInstrument(SynthInstrument(\tGrainScrub, \tGrainScrub,
	(
		trate: ControlPoint(1, 8, 200, default: 12),
		rate: ControlPoint(2, 0.25, 4.0, default: 1.0),
		durAmt: ControlPoint(3, 8, 16, default: 12),
		posVar: ControlPoint(4, 0, 1.0, default: 0.01),
		pos: ControlPoint(5, 0, 1.0, default: 0),
		amp: 0.5
	)
));
~system.makeInstrumentVariant(\tGrainScrub, \tGrainScrubHalfSpeed,
	nil,
	(rate: 0.5)
);
~system.makeInstrumentVariant(\tGrainScrub, \tGrainScrubQuarterSpeed,
	nil,
	(rate: 0.25)
);
~system.makeInstrumentVariant(\tGrainScrub, \tGrainScrubDoubleSpeed,
	nil,
	(rate: 2.0)
);
~system.makeInstrumentVariant(\tGrainScrub, \tGrainScrubQuadrupleSpeed,
	nil,
	(rate: 4.0)
);
~system.makeInstrumentVariant(\tGrainScrub, \tGrainScrubLowVar,
	nil,
	(posVar: 0.0001, durAmt: 20)
);


~system.registerInstrument(SynthInstrument(\tGrainWalk, \tGrainWalk,
	(
		trigFreq: ControlPoint(1, 8, 200, default: 12),
		trigBal: ControlPoint(2, 0, 1.0, default: 0.5),
		rate: ControlPoint(3, 0.25, 4.0, default: 1.0),
		durAmt: ControlPoint(4, 8, 16, default: 12),
		readSpeed: ControlPoint(5, 0.5, 2.0, default: 1.0),
		panFreq: ControlPoint(6, 1, 10, default: 6),
		panWidth: ControlPoint(7, 0, 1.0, default: 0.2),
		amp: 0.2,
		dev: ControlPoint(8, 0, 1.0, default: 0.1)
	)
));
~system.makeInstrumentVariant(\tGrainWalk, \tGrainWalkV1,
	nil,
	(readSpeed: 0.5)
);
~system.makeInstrumentVariant(\tGrainWalk, \tGrainWalkV2,
	nil,
	(rate: 0.5, panWidth: 1.0, dev: 0.4)
);
~system.makeInstrumentVariant(\tGrainWalk, \tGrainWalkV3,
	nil,
	(readSpeed: 0.25)
);
~system.makeInstrumentVariant(\tGrainWalk, \tGrainWalkV4,
	nil,
	(readSpeed: 0.05, rate: 0.25)
);


~system.registerInstrument(SynthInstrument(\sampleJump, \sampleJump,
	(
		trigDens: ControlPoint(1, 0.5, 5.0, default: 2),
		lowPos: ControlPoint(2, 0, 0.95, default: 0),
		hiPos: ControlPoint(3, 0.05, 1, default: 1),
		rate: ControlPoint(4, 0.5, 4.0, default: 1.0),
		lagTime: ControlPoint(5, 0.0001, 0.01, default: 0.0006)
	)
));





