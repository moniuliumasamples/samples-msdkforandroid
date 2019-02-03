if (!('Mdroid' in window)) window.Mdroid = {};
Mdroid.BarcodeGenerator = {};

Mdroid.BarcodeGenerator.generate = function(text, sym) {
    if (!('BarGen' in window)) throw 'Cannot find a Barcode Generation library';
    
    var barcode = new Mdroid.Barcode({
        code: text,
        type: sym,
        aim: ''
    });
    barcode.bars = BarGen.generate(text, sym).trim().split(' ');
    return barcode;
};

var ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
    ALPHABET_LEN = 37;
Mdroid.BarcodeGenerator.randomCode = function(len) {
    len = len || 8;
    var text = '';
    
    for (var i=0; i < len; i++) {
        text += ALPHABET.charAt(Math.floor(Math.random() * ALPHABET_LEN));
    }

    return text;
}

Mdroid.BarcodeGenerator.random = function(code, sym, aim) {
    var barsCount = 50;
    var bars = [];

    for (var i=0; i < barsCount; i++) {
        bars.push( Math.round(Math.random() * 4) + 1 );
    }

    return new Mdroid.Barcode({
        code: code,
        type: sym,
        aim: aim,
        bars: bars
    });
}
