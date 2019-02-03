Mdroid.Barcode = function(opts) {
    var opts = opts || {};

    if (!opts.code) throw 'The code is mandatory';

    this.code = opts.code;
    this.aim  = opts.aim;
    this.type = opts.type;

    // Normalization factor for the bars to be of unit length 
    this.barsNormalization = opts.barsNormalization || 1;
    // The first and last bars are white space
    this.hasSideSpace = opts.hasSideSpace || false;
    this.bars = opts.bars || [];

    this.draw = function(canvas) {
        var ctx = canvas.getContext('2d'),
            head = 0, // printing head position
            bar;

        var barcodeLen=0;
        for (var i = (this.hasSideSpace ? 1:0); i < (this.hasSideSpace ? this.bars.length-1 : this.bars.length); i++) {
            this.bars[i] *= this.barsNormalization;
            barcodeLen += this.bars[i];
        }
        var barUnit = canvas.width / barcodeLen;
        canvas.width = canvas.width;

        // Skip the side padding (overflow)
        for (var i=1; i<this.bars.length-1; i++) {
            bar = this.bars[i] * barUnit;
            
            if (i % 2 == 1) {
                ctx.fillStyle = '#000000';
            } else {
                ctx.fillStyle = '#FFFFFF';
            }
            ctx.fillRect(head,0,bar,canvas.height);
            head += bar;
        }
    };
};
