~context.registerInstrument(SequencerInstrument.new(\testSeq, \heldArp, \sawFull, [0,3,2], 2,
	{
		arg controls, synth;

	Pbindef(\heldArp,
		\type, \set,
		\id, synth,
		\instrument, \sawFull,
		\args, #[],
		\point, Pseries(0, 1, inf),
		\index, Pfunc({
			|event|
			var pointer, arraySize, val;

			pointer = event[\point];
			arraySize = controls[\noteArray].size;
			val = controls[\indexArray][pointer % arraySize];
			val % arraySize;
		}),
		\freq, Pfunc({
			|event|
			var index;

			if(controls[\noteArray].notEmpty, {
				index = event[\index];
				controls[\noteArray][index];
			}, {
				40
			});
		}).midicps,
		\ffreq, Pkey(\freq) * 2,
		\dur, 0.125,
		\amp, Pn(Pseg(Pseq([0, 1]), Pwhite(2.0, 4.0, 1), \lin, 1)),
		\onOff, Pfunc({
			if(controls[\noteArray].notEmpty, { 1.0 }, { 0.0})
		}),
		\atk, 0.001,
		\rel, 0.9,
		\pan, 0
	);
	}, \inst, \sequencer
));

