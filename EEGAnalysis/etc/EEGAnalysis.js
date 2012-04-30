var _imports = {
	fft: JavaImporter(Packages.filters.utils.FFT),
	sinewave: JavaImporter(Packages.filters.SineWaveGenerator),
	msgbox: JavaImporter(Packages.utils.MessageBox)
};

var fft = function (data) {
	with(_imports.fft) {
	}
};

var inversefft = function(data) {
	with(_imports.fft) {
	}
};

var alert = function(str) {
	with(_imports.msgbox) {
		new MessageBox(str);
	}
};
