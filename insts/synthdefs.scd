SynthDef(\basicMixer, {
	arg inBus, outBus, sendBus, amp=0.0, pan=0.0, sendAmp=0.0;
	var sig, sigSend;

	sig = In.ar(inBus, 2);
	sig = Balance2.ar(sig[0], sig[1], pan);
	sigSend = Mix(sig);
	Out.ar(sendBus, sig * sendAmp);
	sig = sig * amp;

	Out.ar(outBus, sig);
}, (\ir ! 3) ++ (0.2 ! 3)).add;

SynthDef(\finalMix, {
	arg mainIn, sendFxIn, recordBus;
	var main, sendFx, sig;

	main = In.ar(mainIn, 2);
	sendFx = SoundIn.ar([sendFxIn, sendFxIn + 1]);

	sig = main + sendFx;
	Out.ar(recordBus, sig);

	Out.ar(0, sig);
}).add;

SynthDef(\sine, {
	arg outBus, freq=400, amp=0.5, pan=0;
	var sig;

	sig = SinOsc.ar(freq) * amp;
	sig = Pan2.ar(sig, pan);

	Out.ar(outBus, sig);
}, [\ir] ++ (0.2 ! 3)).add;

SynthDef(\freqShiftBasic, {
	arg inBus, outBus, freq=100;
	var sig;

	sig = In.ar(inBus, 2);
	sig = FreqShift.ar(sig, freq);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.2]).add;

SynthDef(\sine2, {
	arg outBus, freq=400, amp=0.5, atk=0.1, rel=1.0;
	var sig, env;

	sig = SinOsc.ar(freq) * amp;
	env = EnvGen.kr(Env.perc(atk, rel), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, 0);

	Out.ar(outBus, sig);
}, [\ir] ++ (0.2 ! 2) ++ (0.1 ! 2)).add;

SynthDef(\freq2, {
	arg inBus, outBus, freq=400, pan=0, atk=0.01, rel=1.0;
	var sig;

	sig = In.ar(inBus, 2);
	sig = FreqShift.ar(sig, freq);
	sig = sig * EnvGen.kr(Env.perc(atk, rel), doneAction: 2);
	sig = Balance2.ar(sig[0], sig[1], pan);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 2) ++ (0.1 ! 2)).add;

SynthDef(\sawFull, {
	arg outBus, freq=400, dev=0, ffreq=1000, res=0.1, pan=0, onOff=0, amp=0;
	var env, sig;

	sig = Saw.ar([freq, freq + dev]);
	sig = DFM1.ar(sig, ffreq, res);
	sig = Balance2.ar(sig[0], sig[1], pan, amp);

	Out.ar(outBus, sig * onOff);
}, [\ir, 0.2, 0.1] ++ (0.2 ! 3) ++ [0.1, 0.2]).add;


SynthDef(\playback, {
	arg outBus, outBufferShort, buffer1, t_trig=0, startPos=0, loop=0, amp=1.0, recLevel=1.0, preLevel=0.0, rate=1;
	var sig;

	sig = PlayBuf.ar(2, buffer1, rate * BufRateScale.kr(buffer1), t_trig, startPos * Server.local.sampleRate, loop, 2);
	sig = sig * amp;

	RecordBuf.ar(sig[0], outBufferShort, 0, recLevel, preLevel, 1, 1);
	Out.ar(outBus, sig);
}, (\ir ! 4) ++ (0.1 ! 2) ++ [0.2]).add;

SynthDef(\monoPlayback, {
	arg outBus, outBufferShort, buffer1, t_trig=0, startPos=0, loop=0, amp=1.0, recLevel=1.0, preLevel=0.0, rate=1;
	var sig;

	sig = PlayBuf.ar(1, buffer1, rate * BufRateScale.kr(buffer1), t_trig, startPos * Server.local.sampleRate, loop, 2);
	sig = sig * amp;
	RecordBuf.ar(sig, outBufferShort, 0, recLevel, preLevel, 1, 1);
	sig = Pan2.ar(sig, 0);

	Out.ar(outBus, sig);
}, (\ir ! 4) ++ (0.1 ! 2) ++ [0.2]).add;

SynthDef(\monoPlayer, {
	arg outBus, buffer1, rate=1.0, startMult=0, atkRelRatio=0.1, len=1.0, curve=1.0, pan=0, amp=1.0;
	var sig, env;

	sig = PlayBuf.ar(1, buffer1, BufRateScale.kr(buffer1) * rate, startPos: Wrap.kr(startMult, 0.0, 1.0) * BufFrames.kr(buffer1));
	env = EnvGen.kr(Env.linen(len * atkRelRatio, len * (1 - (2 * atkRelRatio)), len * atkRelRatio, curve: curve), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, pan, amp);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.2] ++ (0.1 ! 5) ++ (0.2 ! 2)).add;

SynthDef(\stereoPlayer, {
	arg outBus, buffer1, rate=1.0, startMult=0, atkRelRatio=0.1, len=1.0, curve=1.0, pan=0, amp=1.0;
	var sig, env;

	sig = PlayBuf.ar(2, buffer1, BufRateScale.kr(buffer1) * rate, startPos: Wrap.kr(startMult, 0.0, 1.0) * BufFrames.kr(buffer1));
	env = EnvGen.kr(Env.linen(len * atkRelRatio, len * (1 - (2 * atkRelRatio)), len * atkRelRatio, curve: curve), doneAction: 2);
	sig = sig * env;
	sig = Balance2.ar(sig[0], sig[1], pan, amp);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.2] ++ (0.1 ! 5) ++ (0.2 ! 2)).add;

SynthDef(\liveSound, {
	arg outBus, outBufferShort, recLevel=1.0, preLevel=0.0;
	var input;

	input = SoundIn.ar([0, 1]);
	RecordBuf.ar(input[0], outBufferShort, 0, recLevel, preLevel, 1, 1);
	Out.ar(outBus, input);
}).add;

SynthDef(\liveSoundMono, {
	arg outBus, outBufferShort, recLevel=1.0, preLevel=0.0;
	var input;

	input = SoundIn.ar(0);
	RecordBuf.ar(input, outBufferShort, 0, recLevel, preLevel, 1, 1);
	input = Pan2.ar(input, 0);

	Out.ar(outBus, input);
}).add;

SynthDef(\test, {
	arg outBus, freq=400, ffreq=1000, rq=0.4;
	var sig;

	sig = Saw.ar(Lag.kr(freq));
	sig = RLPF.ar(sig, Lag.kr(ffreq), rq);
	sig = Pan2.ar(sig, 0);

	Out.ar(outBus, sig);
}, [\ir] ++ (0.2 ! 3)).add;

// tends to be too loud
SynthDef(\dynKlank, {
	arg inBus, outBus, freqs (#[200,300,400,500]), amps (#[1,0.3,0.2,0.05]),
	rings (#[1,1,1,2]), freqScale=1, freqOffset=0, decayScale=1;
	var sig;

	sig = In.ar(inBus, 2) * 0.5;
	sig = DynKlank.ar(`[freqs, amps, rings], sig, freqScale, freqOffset, decayScale);
	sig = Limiter.ar(sig, 1.0);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 3) ++ (0.1 ! 3)).add;

SynthDef(\grainIn, {
	arg inBus, outBus, trigFreq=5.0, trigBalance=0.5, dur=0.1, pan=0, panVar=0;
	var in, trig, sig, panTotal;

	in = SoundIn.ar(0, 1.0);
	trig = (Impulse.ar(trigFreq) * trigBalance) + (Dust.ar(trigFreq) * (1.0 - trigBalance));
	panTotal = pan + TRand.ar(-1 * panVar, panVar, trig);
	sig = GrainIn.ar(2, trig, dur, in, panTotal, -1, 512);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 3) ++ [0.2, 0.1]).add;

SynthDef(\bufGrab, {
	arg liveBufferCopy, outBus, filePos=0.05, trigFreq=10, trigBalance=0.5, rate=1, pan=0,
	amp=0.5, posVar=0.1, rateVar=0.001, durVar=0.01, panVar=0.5, ampVar=0.01, durDivide=10;
	var trig, sig, dur;

	trig = (Impulse.ar(trigFreq) * trigBalance) + (Dust.ar(trigFreq) * (1.0 - trigBalance));
	dur = durDivide / trigFreq;
	sig = TGrains.ar(
		2,
		trig,
		liveBufferCopy,
		rate + TRand.ar(-1 * rateVar, rateVar, trig),
		BufDur.kr(liveBufferCopy) * filePos + TRand.ar(0, posVar, trig),
		dur + TRand.ar(-1 * durVar, durVar, trig),
		pan + TRand.ar(-1 * panVar, panVar, trig),
		amp + TRand.ar(-1 * ampVar, ampVar, trig),
		2
	);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 3) ++ (0.2 ! 3) ++ (0.1 ! 6)).add;

SynthDef(\chorus1, {
	arg inBus, outBus, preDelay=0.08, speed=0.05, depth=0.1, ph_diff=0.5,
	hiDev=2.0, ffreq=100, lowPanSpeed=0.001, hiPanSpeed=0.01;
	var sig, mod, temp, mix, numDelays=4;

	sig = In.ar(inBus, 2);
	sig = Mix(sig) * numDelays.reciprocal;
	mix = Mix.fill(numDelays, {
		arg i;

		mod = LFPar.kr(speed * rrand(0.94, 1.06), ph_diff * i, Lag.kr(depth, 0.5), Lag.kr(preDelay, 0.5));
		temp = FreqShift.ar(DelayC.ar(sig, 0.5, mod), rrand(0, hiDev));
		temp = HPF.ar(temp, Lag.kr(ffreq));
		temp = Pan2.ar(temp, SinOsc.kr(rrand(lowPanSpeed, hiPanSpeed), rrand(0.0, 2 * pi)).range(-1.0,1.0));
	});

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 5) ++ [0.2] ++ (0.1 ! 2)).add;

SynthDef(\chorus2, {
	arg inBus, outBus, speed=0.05, depth=0.1, dev=0.1, ffreq=100,
	times = #[0.1,0.2,0.3,0.4], decaytime=0, lpfFreq=6000;
	var sig, mod, temp, mix, numDelays=4;

	sig = In.ar(inBus, 2);
	sig = Mix(sig) * numDelays.reciprocal;
	mix = Mix.fill(numDelays, {
		arg i;

		mod = SinOsc.ar(speed * rrand(1.0 - dev, 1.0 + dev)).range(1.0 - depth, 1.0 + depth);
		temp = CombC.ar(sig, 2.0, times[i] * mod, decaytime);
		temp = HPF.ar(temp, ffreq);
		temp = Pan2.ar(temp, SinOsc.kr(rrand(0.05, 0.5), rrand(0.0, 2 * pi)).range(-1.0,1.0));
	});
	mix = LPF.ar(mix, lpfFreq);

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 3) ++ [0.2, 0.1]).add;

SynthDef(\chorus3, {
	arg inBus, outBus, speed=0.05, depth=0.1, dev=0.1, ffreq=100, factor=0,
	times = #[0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2], decaytime=0.2;
	var sig, mod, temp, mix, numDelays=12;

	sig = In.ar(inBus, 2);
	sig = Mix(sig) * numDelays.reciprocal;
	mix = Mix.fill(numDelays, {
		arg i;

		mod = LFPar.ar(speed * rrand(1.0 - dev, 1.0 + dev)).range(1.0 - depth, 1.0 + depth);
		temp = CombC.ar(sig, 2.0, times[i] * mod, decaytime);
		temp = HPF.ar(temp, ffreq);
		temp = Pan2.ar(temp, SinOsc.kr(rrand(0.05, 0.5)).range(-1.0,1.0));
	});
	mix = FFT({LocalBuf(2048)} ! 2, mix);
	mix = PV_MagSmooth(mix, factor);
	mix = IFFT(mix);

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 3) ++ [0.2] ++ (0.1 ! 2)).add;

SynthDef(\chorus4, {
	arg inBus, outBus, speed=0.05, depth=0.1, dev=0.1, ffreq=100, decay=1.0,
	pitchDispersion=0.01, timeDispersion=0, times = #[0.1,0.2,0.3,0.4];
	var sig, mod, temp, mix, numDelays=4;

	sig = In.ar(inBus, 2);
	sig = Mix(sig) * numDelays.reciprocal;
	mix = Mix.fill(numDelays, {
		arg i;

		mod = LFPar.ar(speed * rrand(1.0 - dev, 1.0 + dev)).range(1.0 - depth, 1.0 + depth);
		temp = CombC.ar(sig, 2.0, times[i] * mod, decay);
		temp = PitchShift.ar(temp, 0.2, 1.0, pitchDispersion, timeDispersion);
		temp = HPF.ar(temp, ffreq);
		temp = Pan2.ar(temp, SinOsc.kr(rrand(0.05, 0.5), rrand(0, 2 * pi)).range(-1.0,1.0));
	});

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 3) ++ [0.2] ++ (0.1 ! 4)).add;

SynthDef(\phaser1, {
	arg inBus, outBus, preDelay=0.0, speed=0.05, depth=1.0, ph_diff=0.1, modLo=0.000022675, modRange=0.01,
	speedVar=0.6, decaytime=0.0;
	var sig, mod, temp, mix, num=6;

	sig = In.ar(inBus, 2);
	sig = Mix(sig) * num.reciprocal;
	mix = Mix.fill(num, {
		arg i;

		mod = SinOsc.kr(speed * LFNoise1.kr(0.6 + rrand(0.0, 0.3)).range(1.0 - speedVar, 1.0 + speedVar), ph_diff * i, depth, preDelay).range(modLo, modLo + modRange);
		temp = AllpassC.ar(sig, 2.0, mod, decaytime);
		temp = Pan2.ar(temp, SinOsc.kr(rrand(0.05, 0.5)).range(-1.0,1.0));
	});

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 7)).add;

SynthDef(\combN, {
	arg inBus, outBus, delTime=0.4, speed=1.0, decay=0.0, depth=0.1, delTimeDev=0.0,
	speedDev=0.0, depthDev=0.0, amp=0.5;
	var sig, del1, del2, mix;

	sig = In.ar(inBus, 2);

	del1 = CombN.ar(sig, 2.0, Clip.kr(delTime + SinOsc.kr(speed, mul: depth), 0.0, 2.0), decay);
	del1 = Balance2.ar(del1[0], del1[1], -0.9);

	del2 = CombN.ar(sig, 2.0, Clip.kr(delTime + delTimeDev + SinOsc.kr(speed + speedDev, mul: depth + depthDev), 0.0, 2.0), decay);
	del2 = Balance2.ar(del2[0], del2[1], 0.9);

	mix = (del1 + del2) * amp;
	mix = Limiter.ar(mix);

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 8)).add;

SynthDef(\allpassN, {
	arg inBus, outBus, delTime=0.4, speed=1.0, decay=0.0, depth=0.1, delTimeDev=0.0,
	speedDev=0.0, depthDev=0.0;
	var sig, del1, del2, mix;

	sig = In.ar(inBus, 2);

	del1 = AllpassN.ar(sig, 2.0, Clip.kr(delTime + SinOsc.kr(speed, mul: depth), 0.0, 2.0), decay);
	del1 = Balance2.ar(del1[0], del1[1], -0.8);

	del2 = AllpassN.ar(sig, 2.0, Clip.kr(delTime + delTimeDev + SinOsc.kr(speed + speedDev, mul: depth + depthDev), 0.0, 2.0), decay);
	del2 = Balance2.ar(del2[0], del2[1], 0.8);

	mix = (del1 + del2);

	Out.ar(outBus, mix);
}, (\ir ! 2) ++ (0.1 ! 7)).add;

SynthDef(\squiz, {
	arg inBus, outBus, pitchratio=1.0, zcperchunk=1, drop=20, mode=1, ffreq=50, rq=1.0;
	var sig;

	sig = In.ar(inBus, 2);
	sig = Squiz.ar(sig, pitchratio, zcperchunk);
	sig = WaveLoss.ar(sig, drop, 40, mode);
	sig = RHPF.ar(sig, Lag.kr(ffreq), rq);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.2] ++ (0.1 ! 3) ++ (0.2 ! 2)).add;

SynthDef(\scramble, {
	arg inBus, outBus, wipe=1.0, width=0.5, amp=1.0, t_trig=0, ffreq=6000;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_BinScramble(chain, wipe, width, t_trig);
	sig = IFFT(chain) * amp;
	sig = BLowPass4.ar(sig, ffreq);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 3) ++ [\ir]).add;

SynthDef(\shift, {
	arg inBus, outBus, stretch=0, stretchModFreq=0.5, stretchDev=0, shiftAmt=0, amp=1.0, ffreq=7000;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_BinShift(chain, stretch + LFTri.kr(stretchModFreq).range(0, stretchDev), shiftAmt, 1.0);
	sig = IFFT(chain) * amp;
	sig = BLowPass4.ar(sig, ffreq);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 3)).add;

SynthDef(\brickWall, {
	arg inBus, outBus, wipe=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_BrickWall(chain, wipe);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\conformalMap, {
	arg inBus, outBus, areal=0.01, aimag=0.01, amp=1.0, ffreq=12000;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_ConformalMap(chain, areal, aimag);
	sig = IFFT(chain) * amp;
	sig = LPF.ar(sig, ffreq);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 3) ++ [0.2]).add;

SynthDef(\conjugate, {
	arg inBus, outBus, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_Conj(chain);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.2]).add;

SynthDef(\pvFreeze, {
	arg inBus, outBus, freeze=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_Freeze(chain, freeze);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\pvLocalMax, {
	arg inBus, outBus, thresh=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_LocalMax(chain, thresh);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\pvMagFreeze, {
	arg inBus, outBus, freeze=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagFreeze(chain, freeze);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\pvMagNoise, {
	arg inBus, outBus, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagNoise(chain);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.1]).add;

SynthDef(\pvMagShift, {
	arg inBus, outBus, stretch=1, shift=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagShift(chain, stretch, shift);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 3)).add;

SynthDef(\pvMagSmear, {
	arg inBus, outBus, bins=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagSmear(chain, bins);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\pvMagSmooth, {
	arg inBus, outBus, factor=0, amp=1.0;
	var in, sig, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagSmooth(chain, factor);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\pvMagSquared, {
	arg inBus, outBus, amp=0.05;
	var in, chain, sig;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagSquared(chain);
	sig = IFFT(chain) * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.1]).add;

SynthDef(\pvSpectralEnhance, {
	arg inBus, outBus, partials=8, ratio=2, strength=0.1;
	var in, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_SpectralEnhance(chain, partials, ratio, strength);
	chain = IFFT(chain);

	Out.ar(outBus, chain);
}, (\ir ! 2) ++ (0.1 ! 3)).add;

SynthDef(\evenBin, {
	arg inBus, outBus;
	var in, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_EvenBin(chain);
	chain = IFFT(chain);

	Out.ar(outBus, chain);
}, (\ir ! 2)).add;

SynthDef(\pvMagBelow, {
	arg inBus, outBus, threshold=0;
	var in, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagBelow(chain, threshold);
	chain = IFFT(chain);

	Out.ar(outBus, chain);
}, (\ir ! 2) ++ [0.1]).add;

SynthDef(\pvMagAbove, {
	arg inBus, outBus, threshold=0;
	var in, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_MagAbove(chain, threshold);
	chain = IFFT(chain);

	Out.ar(outBus, chain);
}, (\ir ! 2) ++ [0.1]).add;

SynthDef(\pvPhaseShift, {
	arg inBus, outBus, shift=0, integrate=0;
	var in, chain;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_PhaseShift(chain, shift, integrate);
	chain = IFFT(chain);

	Out.ar(outBus, chain);
}, (\ir ! 2) ++ (0.1 ! 2)).add;

SynthDef(\binWipe, {
	arg buffer1, inBus, outBus, wipe=0;
	var inA, inB, chainA, chainB, sig;

	inA = In.ar(inBus, 2);
	inB = SoundIn.ar([0, 1]);
	chainA = FFT({LocalBuf(2048)} ! 2, inA);
	chainB = FFT({LocalBuf(2048)} ! 2, inB);

	sig = PV_BinWipe(chainA, chainB, wipe);
	sig = IFFT(sig);

	Out.ar(outBus, sig);
}, (\ir ! 3) ++ [0.1]).add;

SynthDef(\cloud, {
	arg inBus, outBus, emptyBufferLongMono, freeze=1, trigFreq=10, trigBalance=1.0, rate=1.0,
	pos=0, dur=0.1, pan=0, amp=0.5, recordBalance=0.9, env= -1, panDev=0,
	ampDev=0, posDev=0, durDev=0;
	var in, trig, sig;

	in = In.ar(inBus, 2);
	RecordBuf.ar(in[0], emptyBufferLongMono, 0, recordBalance, (1.0 - recordBalance), freeze, 1);

	trig = (Impulse.ar(trigFreq) * trigBalance) + (Dust.ar(trigFreq) * (1.0 - trigBalance));
	sig = GrainBuf.ar(
		2,
		trig,
		dur + TRand.ar(-1 * durDev, durDev, trig),
		emptyBufferLongMono,
		rate,
		pos + TRand.ar(-1 * posDev, posDev, trig),
		0,
		pan + TRand.ar(-1 * panDev, panDev, trig),
		env,
		512,
		amp + TRand.ar(-1 * ampDev, ampDev, trig)
	);

	Out.ar(outBus, sig);
}, (\ir ! 3) ++ (0.1 ! 14)).add;

SynthDef(\freezeFrame, {
	arg inBus, outBus, pan=0, atk=0.01, sus=1.0, rel=0.01, wipe= -0.91, amp=1.0;
	var in, chain, sig;

	in = In.ar(inBus, 2);
	chain = FFT({LocalBuf(2048)} ! 2, in);
	chain = PV_Freeze(chain, 1.0);
	chain = PV_MagSmooth(chain, 0.9);
	chain = PV_BrickWall(chain, wipe);
	sig = IFFT(chain);
	sig = LPF.ar(sig, 5000);
	sig = sig * EnvGen.kr(Env.linen(atk, sus, rel), doneAction: 2);
`	sig = Balance2.ar(sig[0], sig[1], pan, amp);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 5)).add;

SynthDef(\grainFM1, {
	|outBus, envbuf, carfreq=440, modfreq=200, moddepth=1,
	sustain=0.02, amp=0.8, pan=0|
	var env, sig;

	env = Osc1.ar(envbuf, sustain, doneAction: 2);
	sig = SinOsc.ar(carfreq, SinOsc.ar(modfreq) * moddepth) * env;
	sig = Pan2.ar(sig, Fold.kr(pan, -1.0, 1.0), amp);

	OffsetOut.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 2) ++ (0.1 ! 4)).add;



// the shelf filters don't seem helpful at all.  Maybe switch for simple LPF and HPF
SynthDef(\eqFull, {
	arg inBus, outBus, lowShelfFreq=400, lowShelfRS=0.5, lowShelfDB=0,
	band1Freq=1200, band1RQ=1.0, band1DB=0, band2Freq=1200, band2RQ=1.0, band2DB=0,
	hiShelfFreq=5000, hiShelfRS=0.5, hiShelfDB=0;
	var in, lowShelf, band1, band2, hiShelf, sig;

	in = In.ar(inBus, 2);
	lowShelf = BLowShelf.ar(in, lowShelfFreq, lowShelfRS, lowShelfDB);
	band1 = BPeakEQ.ar(in, band1Freq, band1RQ, band1DB);
	band2 = BPeakEQ.ar(in, band2Freq, band2RQ, band2DB);
	hiShelf = BHiShelf.ar(in, hiShelfFreq, hiShelfRS, hiShelfDB);

	sig = (lowShelf + band1 + band2 + hiShelf) / 4;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 12)).add;

SynthDef(\hiShelf, {
	arg inBus, outBus, freq=5000, rs=0.5, db=0;
	var sig;

	sig = In.ar(inBus, 2);
	sig = BHiShelf.ar(sig, freq, rs, db);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 3)).add;

SynthDef(\lowShelf, {
	arg inBus, outBus, freq=400, rs=0.5, db=0;
	var sig;

	sig = In.ar(inBus, 2);
	sig = BLowShelf.ar(sig, freq, rs, db);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 3)).add;

SynthDef(\singleBand, {
	arg inBus, outBus, freq=1200, rq=1.0, db=0;
	var sig;

	sig = In.ar(inBus, 2);
	sig = BPeakEQ.ar(sig, freq, rq, db);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 3)).add;

SynthDef(\doubleBand, {
	arg inBus, outBus, band1Freq=1200, band1RQ=1.0, band1DB=0,
	band2Freq=2000, band2RQ=1.0, band2DB=0;
	var in, band1, band2, sig;

	in = In.ar(inBus, 2);
	band1 = BPeakEQ.ar(in, band1Freq, band1RQ, band1DB);
	band2 = BPeakEQ.ar(in, band2Freq, band2RQ, band2DB);
	sig = (band1 + band2) / 2;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.2 ! 6)).add;

SynthDef(\mantissa, {
	arg inBus, outBus, bits=5, ffreq=100, rq=0.5, extraAmp=1.0;
	var sig;

	sig = In.ar(inBus, 2);
	sig = MantissaMask.ar(sig, bits);
	sig = BHiPass4.ar(sig, ffreq, rq);
	sig = sig * extraAmp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ [0.1] ++ (0.2 ! 2) ++ [0.1]).add;

SynthDef(\grainBuf1, {
	|outBus, envbuf, buffer1, filepos=0, playRate=1, sustain=0.02, amp=0.3, pan=0, posDev=0, susDev=0|
	var env, sound;

	env = Osc1.ar(envbuf, sustain * Rand(1 - susDev, 1 + susDev), doneAction: 2);
	sound = PlayBuf.ar(2, buffer1,
		playRate * BufRateScale.ir(buffer1), 1,
		startPos: BufFrames.ir(buffer1) * filepos * Rand(1 - posDev, 1 + posDev)) * env * amp;
	sound = Balance2.ar(sound[0], sound[1], pan);

	OffsetOut.ar(outBus, sound);
}, \ir.dup(8)).add;


SynthDef(\glisson, {
	|outBus, freq, freqDev=100, sustain=0.05, amp=0.3, pan=0, envwide=0.5|
	var slopetime, sig, env;

	slopetime = (1 - envwide) * 0.5;

	sig = SinOsc.ar(XLine.ar(freq, freq + freqDev, sustain));
	env = EnvGen.ar(
		Env([0, 1, 1, 0], [slopetime, envwide, slopetime], \sin),
		timeScale: sustain,
		levelScale: amp,
		doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, pan);

	OffsetOut.ar(outBus, sig);
}, \ir ! 7).add;


SynthDef(\grainSinBlend, {
	arg outBus, freq=400, envBuf= -1, density=10, bal=0.2, grainDur=0.1, amp=1.0,
	panCenter=0, panModDepth=0.2;
	var sig, trig, pan;

	trig = Dust.kr(density * (1-bal)) + Impulse.kr(density * bal);
	pan = Clip.ar(panCenter + TRand.ar(-1 * panModDepth, panModDepth, trig), -1.0, 1.0);
	sig = GrainSin.ar(2, trig, grainDur, freq, pan, envBuf) * amp;

	OffsetOut.ar(outBus, sig);
}, [\ir, 0.2] ++ (0.1 ! 7)).add;


SynthDef(\basicLiveGran, {
	|outBus, inBus, envbuf, amp=0.3, sus=0.01, pan=0, susDev=0|
	var sig, env;

	sig = Mix.ar(In.ar(inBus, 2));
	env = Osc1.ar(envbuf, sus * Rand(1- susDev, 1 + susDev), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, pan);

	OffsetOut.ar(outBus, sig);
}, \ir ! 6).add;



SynthDef(\glissonLive, {
	|outBus, inBus, envbuf, freqShift=100, sustain=0.05, amp=0.2, pan=0|
	var sig, env;

	sig = Mix.ar(In.ar(inBus, 2));
	sig = FreqShift.ar(sig, XLine.ar(0.001, freqShift, sustain));
	env = Osc1.ar(envbuf, sustain, doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, pan);

	OffsetOut.ar(outBus, sig);
}, \ir ! 7).add;


SynthDef(\constQLive, {
	|outBus, inBus, freq, amp=0.3, pan=0, sustain=0.1, freqDev=0.0, rq=0.3|
	var ringtime, ampcomp, envSig, cutoffEnv, grain, filtered;

	ringtime = (2.4 / (freq * rq) * 0.66).min(0.5); // estimated
	ampcomp = (rq ** -1) * (400 / freq ** 0.5);
	envSig = EnvGen.ar(Env([0, amp, 0], [0.5, 0.5] * sustain, \welch));
	cutoffEnv = EnvGen.kr(Env([1, 1, 0], [sustain+ringtime,0.01]), doneAction: 2);
	grain = Mix.ar(In.ar(inBus, 2));
	grain = grain * envSig;
	filtered = BPF.ar( grain, freq + freqDev, rq, ampcomp );

	OffsetOut.ar(outBus, Pan2.ar(filtered, pan, cutoffEnv))
}, \ir.dup(8)).add;

SynthDef(\combMelt, {
	arg inBus, outBus, modDev=0.3, decayTime=1.0, bal=0.5, lagTime=1.0,
	amp=0.6, a=1, b=1.9, xi=0, delTime=0.1, fbDev=0.1, modFreq=10, fbAmt=0;
	var sig, del, fb, fbCon;

	sig = In.ar(inBus, 2);
	fb = LocalIn.ar(2);
	fbCon = Lag.ar(fb.range(1 - fbDev, 1 + fbDev), lagTime);
	del = CombC.ar(sig, 2.0,
		delTime * SinOsc.ar(modFreq * fbCon).range(1 - modDev, 1 + modDev) - ControlRate.ir.reciprocal,
		decayTime);
	sig = (sig * bal) + (del * (1 - bal)) + (fb * fbAmt);
	sig = sig * amp;
	sig = Limiter.ar(sig);
	LocalOut.ar(sig);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 12)).add;

SynthDef(\fdbkPShift, {
	arg inBus, outBus, amp=1.0, windowSize1=0.2, pitchRatio1=1,
	pitchDispersion1=0, timeDispersion1=0, freqShift=0, mainAmt=0.6, add1Amt=0.4, add2Amt=0,
	modFreq=0.1, modDev=0.1;
	var sig, fb, sigAdd1, sigAdd2;

	sig = In.ar(inBus, 2);
	fb = LocalIn.ar(2);
	sigAdd1 = PitchShift.ar(fb, windowSize1, pitchRatio1, pitchDispersion1, timeDispersion1);
	sigAdd2 = FreqShift.ar(fb, freqShift * SinOsc.ar(modFreq).range(1 - modDev, 1 + modDev));
	sig = (sig * mainAmt) + (sigAdd1 * add1Amt) + (sigAdd2 * add2Amt);
	sig = sig * amp;
	sig = Limiter.ar(sig);

	LocalOut.ar(sig);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 11)).add;

SynthDef(\gurgleComb, {
	arg inBus, outBus, modFreq=100, modDev=0.3, decayTime=1.0, bal=0.5,
	amp=1.0, delTime=0.1, a=1, b=1.9, xi=0;
	var sig, del;

	sig = In.ar(inBus, 2);
	del = CombC.ar(sig, 2.0, delTime * CuspL.ar(modFreq, a, b, xi).range(1 - modDev, 1 + modDev), decayTime);
	sig = (sig * bal) + (del * (1 - bal));
	sig = sig * amp;
	sig = Limiter.ar(sig);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 9)).add;

SynthDef(\combModder, {
	arg inBus, outBus, modFreq=0.1, modDev=0.3, decayTime=1.0, bal=0.5,
	amp=1.0, delTime=0.1;
	var sig, del;

	sig = In.ar(inBus, 2);
	del = CombC.ar(sig, 2.0, delTime * LFTri.ar(modFreq).range(1 - modDev, 1 + modDev), decayTime);
	sig = (sig * bal) + (del * (1 - bal));
	sig = sig * amp;
	sig = Limiter.ar(sig);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 6)).add;

SynthDef(\breakComb, {
	arg inBus, outBus, trigFreq=1, decayTime=1.0, start=0.001, step=0.001,
	modFreq=0.1, modLow=0.99, modHigh=1.01, vol=0.8, lagTime=0.01;
	var sig, trig, delTime, del;

	sig = In.ar(inBus, 2);
	trig = Dust.ar(trigFreq);
	delTime = Demand.ar(trig, 0, Lag.ar(Fold.ar(Dseries(start, step, inf), 0.001, 0.99), lagTime));
	del = CombL.ar(sig, 1.0, delTime * SinOsc.ar(modFreq).range(modLow, modHigh), decayTime);
	sig = sig + del;
	sig = sig * vol;
	sig = Limiter.ar(sig);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 9)).add;


SynthDef(\warp, {
	arg outBus, liveBufferCopy, ptrFreq=0.5, freqScale=1, windowSize=0.2, overlaps=8, windowRand=0,
	atk=0.5, sus=0.01, rel=0.5, lowTime=0.01, highTime=0.95, trigFreq=0.5, decayTime=1.0,
	timeScale=1.0;
	var sig, trig, env, del;

	sig = Warp1.ar(1, liveBufferCopy, SinOsc.ar(ptrFreq).range(0.03, 0.97), freqScale, windowSize,
		-1, overlaps, windowRand);
	trig = Impulse.ar(trigFreq);
	env = EnvGen.ar(Env.linen(atk, sus, rel), trig, timeScale: timeScale).range(lowTime, highTime);
	del = CombC.ar(sig, 1.0, env, decayTime);
	sig = sig + del;
	sig = Pan2.ar(sig, 0);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 13)).add;


SynthDef(\warpLive, {
	arg outBus, liveBuffer, ptrFreq=0.5, freqScale=1, windowSize=0.2, overlaps=8, windowRand=0,
	atk=0.5, sus=0.01, rel=0.5, lowTime=0.01, highTime=0.95, trigFreq=0.5, decayTime=1.0,
	timeScale=1.0;
	var sig, trig, env, del;

	sig = Warp1.ar(1, liveBuffer, SinOsc.ar(ptrFreq).range(0.05, 0.95), freqScale, windowSize,
		-1, overlaps, windowRand);
	trig = Impulse.ar(trigFreq);
	env = EnvGen.ar(Env.linen(atk, sus, rel), trig, timeScale: timeScale).range(lowTime, highTime);
	del = CombC.ar(sig, 1.0, env, decayTime);
	sig = sig + del;
	sig = Lag2.ar(sig, 0.001);
	sig = Pan2.ar(sig, 0);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 13)).add;


SynthDef(\dfmNum1, {
	arg inBus, outBus, ffreq=1000, res=0.1, gain=1, type=0, noiseLevel=0.0003, amp=1.0,
	delayTime=0.1, decayTime=0.5, delAmt=0, modFreq=0.1, modDev=0.1, shiftedAmt=0,
	windowSize=0.2, pitchRatio=1, pitchDispersion=0, timeDispersion=0;
	var sig, del;

	sig = In.ar(inBus, 2);
	del = CombC.ar(sig, 2.0, delayTime * SinOsc.ar(modFreq).range(1 - modDev, 1 + modDev), decayTime);
	sig = sig + (del * delAmt);
	sig = DFM1.ar(sig, ffreq, res, gain, type, noiseLevel);
	sig = sig + (PitchShift.ar(sig, windowSize, pitchRatio, pitchDispersion, timeDispersion) * shiftedAmt);
	sig = sig * amp;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 16)).add;

SynthDef(\twoCombs, {
	arg inBus, outBus, delayTimeL=0.1, modFreqL=0.1, modDevL=0.2, decayTimeL=0.5,
	windowSizeL=0.2, pitchRatioL=1, pitchDispersionL=0, timeDispersionL=0,
	delayTimeR=0.1, modFreqR=0.1, modDevR=0.2, decayTimeR=0.5,
	windowSizeR=0.2, pitchRatioR=1, pitchDispersionR=0, timeDispersionR=0,
	inputAmt=0.8, delLAmt=0.2, delRAmt=0.2;
	var input, delL, delR, total;

	input = In.ar(inBus, 2);
	delL = CombC.ar(input, 2.0,
		delayTimeL * LFNoise1.ar(modFreqL).range(1 - modDevL, 1 + modDevL), decayTimeL);
	delL = delL + PitchShift.ar(delL, windowSizeL, pitchRatioL, pitchDispersionL, timeDispersionL);
	delL = Balance2.ar(delL[0], delL[1], -0.9);

	delR = CombC.ar(input, 2.0,
		delayTimeR * LFNoise1.ar(modFreqR).range(1 - modDevR, 1 + modDevR), decayTimeR);
	delR = delR + PitchShift.ar(delR, windowSizeR, pitchRatioR, pitchDispersionR, timeDispersionR);
	delR = Balance2.ar(delR[0], delR[1], 0.9);

	total = (input * inputAmt) + (delL * delLAmt) + (delR * delRAmt);

	Out.ar(outBus, total);
}, (\ir ! 2) ++ (0.1 ! 19)).add;

SynthDef(\fourDelays, {
	arg inBus, outBus, delTime=1.0, decayTime=1.0, offset=0,
	del1Amt=0.4, del2Amt=0.4, del3Amt=0.2, del4Amt=0.2;
	var sig, del1, del2, del3, del4, total;

	sig = In.ar(inBus, 2);

	del1 = CombC.ar(sig, 2.0, delTime, decayTime);
	del1 = Balance2.ar(del1[0], del1[1], -0.9);

	del2 = CombC.ar(sig, 2.0, delTime * 0.75 * (1 + offset), decayTime);
	del2 = Balance2.ar(del2[0], del2[1], 0.9);

	del3 = CombC.ar(sig, 2.0, delTime * 0.25 * (1 + offset), decayTime * 0.6);
	del3 = Balance2.ar(del3[0], del3[1], 0.5);

	del4 = CombC.ar(sig, 2.0, delTime * 0.5, decayTime * 0.6);
	del4 = Balance2.ar(del4[0], del4[1], -0.5);

	total = (del1 * del1Amt) + (del2 * del2Amt) + (del3 * del3Amt) + (del4 * del4Amt);

	Out.ar(outBus, total);
}, (\ir ! 2) ++ (0.1 ! 7)).add;

SynthDef(\pitchShiftComponent, {
	arg inBus, outBus, atk=0.5, sus=0.01, rel=0.5, windowSize=0.2, pitchRatio=1.0,
	pitchDispersion=0, timeDispersion=0, envPitchAmt=0, ffreq=1000, res=0.2,
	noiseLevel=0.0003, amp=1.0, modFreq=0.1, modDev=0.1, pan=0;
	var sig, env;

	sig = In.ar(inBus, 2);
	env = EnvGen.kr(Env.linen(atk, sus, rel), doneAction: 2);
	sig = PitchShift.ar(
		sig, windowSize,
		pitchRatio * (1 + (env * envPitchAmt)),
		pitchDispersion, timeDispersion);
	sig = DFM1.ar(sig,
		ffreq * SinOsc.kr(modFreq).range(1 - modDev, 1 + modDev),
		res, noiselevel: noiseLevel);
	sig = sig * env;
	sig = sig * amp;
	sig = Balance2.ar(sig[0], sig[1], pan);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 15)).add;


SynthDef(\pitchShiftComponentBuffer, {
	arg buffer1, outBus, atk=0.5, sus=0.01, rel=0.5, windowSize=0.2, pitchRatio=1.0,
	pitchDispersion=0, timeDispersion=0, envPitchAmt=0, ffreq=1000, res=0.2,
	noiseLevel=0.0003, amp=1.0, modFreq=0.1, modDev=0.1, pan=0;
	var sig, env;

	sig = PlayBuf.ar(2, buffer1, loop: 1);
	env = EnvGen.kr(Env.linen(atk, sus, rel), doneAction: 2);
	sig = PitchShift.ar(
		sig, windowSize,
		pitchRatio * (1 + (env * envPitchAmt)),
		pitchDispersion, timeDispersion);
	sig = DFM1.ar(sig,
		ffreq * SinOsc.kr(modFreq).range(1 - modDev, 1 + modDev),
		res, noiselevel: noiseLevel);
	sig = sig * env;
	sig = sig * amp;
	sig = Balance2.ar(sig[0], sig[1], pan);

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 15)).add;


SynthDef(\flutter, {
	arg buffer1, outBus, rate=1.0, start=0, len=1.0, fSpeed=35, fAmt=0.2, tSpeed=2.0,
	tThresh=0.7, amp=0.8, pan=0, atk=0.5, sus=1.0, rel=3, lagAmt=0.2;
	var ptr, sig, move, env;

	start = start * s.sampleRate;
	len = len * s.sampleRate;
	move = LFTri.kr(fSpeed, fAmt) * Lag.kr(Schmidt.kr(LFNoise0.kr(tSpeed),tThresh, tThresh), lagAmt);

	ptr = Phasor.ar(
		0,
		BufRateScale.kr(buffer1) * rate + move,
		start,
		start + len,
		start);
	sig = BufRd.ar(2, buffer1, ptr);
	sig = sig * amp;
	sig = Balance2.ar(sig[0], sig[1], pan);

	env = EnvGen.kr(Env.linen(atk, sus, rel), doneAction: 2);
	sig = sig * env;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 13)).add;


SynthDef(\flutterLive, {
	arg liveBuffer, outBus, rate=1.0, start=0, len=1.0, fSpeed=35, fAmt=0.2, tSpeed=2.0,
	tThresh=0.7, amp=0.8, pan=0, atk=0.5, rel=3, lagAmt=0.2;
	var ptr, sig, move, env, lenScaled;

	start = start * s.sampleRate;
	lenScaled = len * s.sampleRate;
	move = LFTri.kr(fSpeed, fAmt) * Lag.kr(Schmidt.kr(LFNoise0.kr(tSpeed),tThresh, tThresh), lagAmt);

	ptr = Phasor.ar(
		0,
		BufRateScale.kr(liveBuffer) * rate + move,
		start,
		start + lenScaled,
		start);
	sig = BufRd.ar(1, liveBuffer, ptr);
	sig = sig * amp;
	sig = Pan2.ar(sig, pan);

	env = EnvGen.kr(Env.linen(atk, (len - atk - rel), rel), doneAction: 2);
	sig = sig * env;

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 13)).add;


SynthDef(\majorScaleSeq, {
	arg outBus, trigFreq=2, numNotes=7, noteOffset=40, amp=0.5, atkRatio=0.5, relRatio=0.5, panFreq=8,
	modulo=7, a=1.4, b=0.3, transArray= #[0,12,24];
	var trig, freq, sig, env, totalLen;

	trig = Impulse.kr(trigFreq);
	freq = Demand.kr(trig, 0, Dseq(Scale.major.degrees, numNotes));
	freq = freq + Demand.kr(trig, 0, Dstutter(modulo, Dseq(transArray, inf)));
	freq = (freq + noteOffset).midicps;

	sig = Saw.ar(freq) * amp;
	totalLen = trigFreq.reciprocal * (numNotes + 1);
	env = EnvGen.kr(Env.perc(totalLen * atkRatio, totalLen * relRatio), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, HenonL.ar(panFreq, a, b).range(-1.0, 1.0));

	Out.ar(outBus, sig);
}, (\ir ! 1) ++ (0.1 ! 11)).add;


SynthDef(\threeNoteSeq, {
	arg outBus, trigFreq=2, numNotes=7, noteOffset=40, amp=0.5, atkRatio=0.5, relRatio=0.5, panFreq=8,
	modulo=7, a=1.4, b=0.3, noteArray = #[0,4,7], transArray= #[0,12,24], filFreqMult=2.0, rq=0.8;
	var trig, freq, sig, env, totalLen;

	trig = Impulse.kr(trigFreq);
	freq = Demand.kr(trig, 0, Dseq(noteArray, numNotes));
	freq = freq + Demand.kr(trig, 0, Dstutter(modulo, Dseq(transArray, inf)));
	freq = (freq + noteOffset).midicps;

	sig = Mix(Saw.ar((freq - 1) + (freq + 2))) * amp;
	sig = RLPF.ar(sig, freq * filFreqMult, rq);
	totalLen = trigFreq.reciprocal * (numNotes + 1);
	env = EnvGen.kr(Env.perc(totalLen * atkRatio, totalLen * relRatio), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, HenonL.ar(panFreq, a, b).range(-1.0, 1.0));

	Out.ar(outBus, sig);
}, (\ir ! 1) ++ (0.1 ! 14)).add;


SynthDef(\fourNoteSeq, {
	arg outBus, trigFreq=2, numNotes=7, noteOffset=40, amp=0.5, atkRatio=0.5, relRatio=0.5, panFreq=8,
	modulo=7, a=1.4, b=0.3, noteArray = #[0,4,7,11], transArray= #[0,12,24];
	var trig, freq, sig, env, totalLen;

	trig = Impulse.kr(trigFreq);
	freq = Demand.kr(trig, 0, Dseq(noteArray, numNotes));
	freq = freq + Demand.kr(trig, 0, Dstutter(modulo, Dseq(transArray, inf)));
	freq = (freq + noteOffset).midicps;

	sig = LFTri.ar(freq) * (amp * 0.25);
	sig = sig + (SinOsc.ar(freq) * (amp * 0.75));
	totalLen = trigFreq.reciprocal * (numNotes + 1);
	env = EnvGen.kr(Env.perc(totalLen * atkRatio, totalLen * relRatio), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, HenonL.ar(panFreq, a, b).range(-1.0, 1.0));

	Out.ar(outBus, sig);
}, [\ir] ++ (0.1 ! 12)).add;


SynthDef(\fiveNoteSeq, {
	arg outBus, trigFreq=2, numNotes=7, noteOffset=40, amp=0.5, atkRatio=0.5, relRatio=0.5, panFreq=8,
	modulo=7, a=1.4, b=0.3, noteArray = #[0,4,7,11,12], transArray= #[0,12,24];
	var trig, freq, sig, env, totalLen;

	trig = Impulse.kr(trigFreq);
	freq = Demand.kr(trig, 0, Dseq(noteArray, numNotes));
	freq = freq + Demand.kr(trig, 0, Dstutter(modulo, Dseq(transArray, inf)));
	freq = (freq + noteOffset).midicps;

	sig = LFTri.ar(freq) * (amp * 0.25);
	sig = sig + (SinOsc.ar(freq) * (amp * 0.75));
	totalLen = trigFreq.reciprocal * (numNotes + 1);
	env = EnvGen.kr(Env.perc(totalLen * atkRatio, totalLen * relRatio), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, HenonL.ar(panFreq, a, b).range(-1.0, 1.0));

	Out.ar(outBus, sig);
}, [\ir] ++ (0.1 ! 12)).add;


SynthDef(\autoTransposeFive, {
	arg outBus, buffer1, trigFreq=2, semitoneArray= #[0,12,0,12,0], numTimes=10, atkRatio=0.5, relRatio=0.5,
	panFreq=4, a=1.4, b=0.3;
	var trig, rate, sig, totalLen, env;

	trig = Impulse.kr(trigFreq);
	rate = Demand.kr(trig, 0, Dseq(semitoneArray, numTimes));
	rate = rate.midiratio;
	sig = PlayBuf.ar(2, buffer1, rate, doneAction: 2);
	totalLen = trigFreq.reciprocal * (numTimes + 1);
	env = EnvGen.kr(Env.perc(totalLen * atkRatio, totalLen * relRatio), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, HenonL.ar(panFreq, a, b).range(-1.0, 1.0));

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 8)).add;


SynthDef(\feedbackNasty, {
	arg outBus, freq=400, amp=0.1, fb=2, fbDelay=0.1, lfoAmt=1, lfoRate=2, envAtk=1, envRel=4, cutoff=400;
	var env, lin, src;

    env= EnvGen.kr(Env.perc(envAtk, envRel), doneAction:2);
	lin= DelayN.ar(HPF.ar(LocalIn.ar(1), cutoff), 1, fbDelay);
    src= SinOsc.ar(freq+SinOsc.ar(lfoRate, 0, lfoAmt), lin*2pi*fb, amp)*env;
    LocalOut.ar(src);

    Out.ar(outBus, Pan2.ar(src));
}).add;


SynthDef(\feedbackPitchShift, {
	arg outBus, freq=100, amp=0.3, windowSize=0.2, pitchRatio=0.1, freqRand=0.01, lpfCutoff=5000;
	var localIn, sig;

	localIn = LocalIn.ar(2);
	localIn = PitchShift.ar(localIn, windowSize, pitchRatio, [0.1, 0.15], 0);
	sig = (Saw.ar([freq, freq * Rand(1 - freqRand, 1 + freqRand)]) * amp) + localIn;
	sig = LPF.ar(sig, lpfCutoff);

	LocalOut.ar(sig * 0.5);
	Out.ar(outBus, sig);
}).add;


SynthDef(\feedbackFreqShift, {
	arg outBus, freq=100, amp=0.3, freqShift=10, freqRand=0.01, lpfCutoff=5000;
	var localIn, sig;

	localIn = LocalIn.ar(2);
	localIn = FreqShift.ar(localIn, freqShift);
	sig = (Saw.ar([freq, freq * Rand(1 - freqRand, 1 + freqRand)]) * amp) + localIn;
	sig = LPF.ar(sig, lpfCutoff);

	LocalOut.ar(sig*0.5);
	Out.ar(outBus, sig);
}).add;


SynthDef(\inputFeedbackPitchShift, {
	arg inBus, outBus, amp=0.3, windowSize=0.2, pitchRatio=0.1, lpfCutoff=5000;
	var localIn, sig;

	localIn = LocalIn.ar(2);
	localIn = PitchShift.ar(localIn, windowSize, pitchRatio, [0.1, 0.15], 0);
	sig = (In.ar(inBus, 2) * amp) + localIn;
	sig = LPF.ar(sig, lpfCutoff);

	LocalOut.ar(sig * 0.5);
	Out.ar(outBus, sig);
}).add;


SynthDef(\inputFeedbackFreqShift, {
	arg inBus, outBus, amp=0.3, freqShift=10, lpfCutoff=5000;
	var localIn, sig;

	localIn = LocalIn.ar(2);
	localIn = FreqShift.ar(localIn, freqShift);
	sig = (In.ar(inBus, 2) * amp) + localIn;
	sig = LPF.ar(sig, lpfCutoff);

	LocalOut.ar(sig*0.5);
	Out.ar(outBus, sig);
}).add;


SynthDef(\sineOscFB, {
	arg outBus, freq=50, fdbk=2, amp=0.3, freqDetune=0.02, panOffset=0.8, lpfCutoff=8000;
	var out, sig1, sig2;

	sig1 = Mix.fill(4, {
		SinOscFB.ar(freq *
			(1+SinOsc.ar(LFNoise1.kr(rrand(0.25,0.5),4,5),
				pi.rand, freqDetune)), fdbk, 0.2)
	});
	sig2 = Mix.fill(4, {
		SinOscFB.ar(freq *
			(1+SinOsc.ar(LFNoise1.kr(rrand(0.25,0.5),4,5),
				pi.rand, freqDetune)), fdbk, 0.2)
	});
	sig1 = sig1 * (amp / 2);
	sig2 = sig2 * (amp / 2);
	sig1 = Pan2.ar(sig1, -1 * panOffset);
	sig2 = Pan2.ar(sig2, panOffset);

	out = Mix([sig1, sig2]);
	out = LPF.ar(out, lpfCutoff);

	Out.ar(outBus, out);
}).add;


SynthDef(\tGrainScrub, {
	arg outBus, buffer1, trate=8, rate=1.0, durAmt=12, posVar=0.01, pos=0, amp=0.5;
	var len, clk, pan, out;

	len = durAmt / trate;
	clk = Impulse.kr(trate);
	pos = pos * BufDur.kr(buffer1);
	pos = pos + TRand.kr(0, posVar, clk);
	pan = WhiteNoise.kr(0.3);
	out = TGrains.ar(
		2,
		clk,
		buffer1,
		rate,
		pos,
		len,
		pan,
		amp);

	Out.ar(outBus, out);
}).add;


SynthDef(\tGrainWalk, {
	arg buffer1, outBus, trigFreq=10, trigBal=0.5, rate=1, readSpeed=1, panFreq=5,
	panWidth=0.2, amp=0.2, dev=0.1, durAmt=10;
	var sig, trig, pos, pan, len;

	trig = (Impulse.ar(trigFreq) * trigBal) + (Dust.ar(trigFreq) * (1 - trigBal));
	len = durAmt / trigFreq;
	pos = Line.ar(0, BufDur.kr(buffer1), BufDur.kr(buffer1) * readSpeed.reciprocal);
	pan = LFNoise0.ar(panFreq).range(panWidth.neg, panWidth);
	sig = TGrains.ar(2, trig, buffer1, rate, pos + TRand.kr(0, dev, trig), len, pan, amp, 4);
	Out.ar(outBus, sig);
}).add;


SynthDef(\sampleJump, {
	arg outBus, buffer1, trigDens=2, lowPos=0, hiPos=1, rate=1, lagTime=0.0006;
	var trig, startPos, sig, rateChoose, numFrames;

	trig = Dust.kr(trigDens);
	numFrames = BufFrames.ir(buffer1);
	startPos = TIRand.kr(numFrames * lowPos, numFrames * hiPos, trig);
	rateChoose = TChoose.kr(trig, [rate, rate * -1]);
	sig = PlayBuf.ar(2, buffer1, rateChoose, trig, startPos, 1);
	sig = Lag.ar(sig, lagTime);

	Out.ar(outBus, sig);
}).add;
