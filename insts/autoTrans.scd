(
SynthDef(\autoTransposeFive, {
	arg outBus, buffer, trigFreq=2, semitoneArray= #[0,12,0,12,0], numTimes=10, atkRatio=0.5, relRatio=0.5,
	panFreq=4, a=1.4, b=0.3;
	var trig, rate, sig, totalLen, env;

	trig = Impulse.kr(trigFreq);
	rate = Demand.kr(trig, 0, Dseq(semitoneArray, numTimes));
	rate = rate.midiratio;
	sig = PlayBuf.ar(2, buffer, rate, doneAction: 2);
	totalLen = trigFreq.reciprocal * (numTimes + 1);
	env = EnvGen.kr(Env.perc(totalLen * atkRatio, totalLen * relRatio), doneAction: 2);
	sig = sig * env;
	sig = Pan2.ar(sig, HenonL.ar(panFreq, a, b).range(-1.0, 1.0));

	Out.ar(outBus, sig);
}, (\ir ! 2) ++ (0.1 ! 8)).add;
)

x = Synth(\autoTransposeFive, [\outBus, 0, \buffer, ~prelude, \numTimes, 100, \atkRatio, 0.1, \trigFreq, 5, \semitoneArray, #[0,7,0,12,0]]);