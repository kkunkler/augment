(
SynthDef(\eqFull, {
	arg inBus, outBus, lowShelfFreq, lowShelfRS, lowShelfDB,
	band1Freq, band1RQ, band1DB, band2Freq, band2RQ, band2DB,
	hiShelfFreq, hiShelfRS, hiShelfDB;
	var in, lowShelf, band1, band2, hiShelf, sig;

	in = In.ar(inBus, 2);
	lowShelf = BLowShelf.ar(in, lowShelfFreq, lowShelfRS, lowShelfDB);
	band1 = BPeakEQ.ar(in, band1Freq, band1RQ, band1DB);
	band2 = BPeakEQ.ar(in, band2Freq, band2RQ, band2DB);
	hiShelf = BHiShelf.ar(in, hiShelfFreq, hiShelfRS, hiShelfDB);

	sig = (lowShelf + band1 + band2 + hiShelf) / 4;

	Out.ar(outBus, sig);
}).add;

SynthDef(\hiShelf, {
	arg inBus, outBus, freq, rs, db;
	var sig;

	sig = In.ar(inBus, 2);
	sig = BHiShelf.ar(sig, freq, rs, db);

	Out.ar(outBus, sig);
}).add;

SynthDef(\lowShelf, {
	arg inBus, outBus, freq, rs, db;
	var sig;

	sig = In.ar(inBus, 2);
	sig = BLowShelf.ar(sig, freq, rs, db);

	Out.ar(outBus, sig);
}).add;

SynthDef(\singleBand, {
	arg inBus, outBus, freq, rq, db;
	var sig;

	sig = In.ar(inBus, 2);
	sig = BPeakEQ.ar(sig, freq, rq, db);

	Out.ar(outBus, sig);
}).add;

SynthDef(\doubleBand, {
	arg inBus, outBus, band1Freq, band1RQ, band1DB, band2Freq, band2RQ, band2DB;
	var in, band1, band2, sig;

	in = In.ar(inBus, 2);
	band1 = BPeakEQ.ar(in, band1Freq, band1RQ, band1DB);
	band2 = BPeakEQ.ar(in, band2Freq, band2RQ, band2DB);
	sig = (band1 + band2) / 2;

	Out.ar(outBus, sig);
}).add;

SynthDef(\mantissa, {
	arg inBus, outBus, bits;
	var sig;

	sig = In.ar(inBus, 2);
	sig = MantissaMask.ar(sig, bits);

	Out.ar(outBus, sig);
}).add;

SynthDef(\insideOut, {
	arg inBus, outBus, noiseLevel;
	var sig

	in = In.ar(inBus, 2);
	sig = (in * (1 - noiseLevel)) + PinkNoise.ar(noiseLevel);
	sig = InsideOut.ar(sig);

	Out.ar(outBus, sig);
}).add;
)














