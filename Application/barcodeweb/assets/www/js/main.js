window.onload = function() {
    VERSION = '0.4'

    storageSize = 50;
    scannedBarcodes = [];
    localStorageSupported = false;
    body = document.getElementsByTagName("body")[0];
    
    // Scan button UI
    scanButton = document.getElementById('scan');

    // Barcode UI
    button = document.getElementById('barcode-container');
    barcodeCanvas = document.getElementById('barcode-canvas');
    barsContainer = document.getElementById('bars-container');
    barcode = document.getElementById('barcode');
    barcodeInfo = document.getElementById('barcode-info');
    loading = document.getElementById('loading');
    goodRead = document.getElementById('good-read');
    
    store = document.getElementById('store');
    settingsPane = document.getElementById("settings-pane");

    isBarcodeManagerAvailable = 'BarcodeManager' in window;
    barcode.innerHTML = '';
    firstTimeSettingsInit = true;
    fakeScanTimer = null;
    maskStopEvent = false;
    isScanning = false;
    
    startTime = 0;
    
    audioElement = null;
    audioSupported = false;

    settings = {};

    goodRead.style.top = (barsContainer.offsetHeight - 80) / 2 + 'px';
    goodRead.style.left = (barsContainer.offsetWidth - 80) / 2 + 'px';
    
    // Clean the barcode info
    setBarcodeInfo();

    initModal();

    initSettings();

    initCanvas();

    if (settings.showDialog) {
        modal.parentElement.style.display = 'block';
        setTimeout(function() {
            modal.parentElement.style.display = 'none';
        }, 2000);
    }
};

function initClick() {
    var buttons = document.querySelectorAll('[touchable]'), b;
    
    for (var i=0; i<buttons.length; i++) {
        b = buttons[i];
        b.addEventListener("mousedown", handleTouchStart, false);
        b.addEventListener("mouseup", handleTouchEnd, false);
    }
}

function initTouch() {
    var buttons = document.querySelectorAll('[touchable]'), b;
    
    for (var i=0; i<buttons.length; i++) {
        b = buttons[i];
        b.addEventListener("touchstart", handleTouchStart, true);
        b.addEventListener("touchend", handleTouchEnd, true);
        b.addEventListener("touchcancel", handleTouchEnd, false);
        b.addEventListener("touchleave", handleTouchEnd, false);
        b.addEventListener("touchmove", handleTouchMove, false);
    }
}

function initCanvas() {
    var context = barcodeCanvas.getContext('2d');

    var x = barcodeCanvas.width / 2;
    var y = barcodeCanvas.height / 2;

    context.font = '30pt Monospace';
    context.textAlign = 'center';
    context.fillStyle = '#404040';
    //context.fillText('Start scanning!', x, y);
    context.fillText('Mdroid',x,y);
    context.fillText('Barcode Scanner',x,y+50);
}

function initModal() {
    modal = document.getElementById('modal');
    modal.innerHTML = '';
    var good = 3;
    
    if (!initStorage()) {
        modal.innerHTML += '<p>Bad! LocalStorage is not supported</p>';
        good--;
    } else {
        modal.innerHTML += '<p>Great! LocalStorage is supported</p>';
    }
    
    (audioSupported = initAudio()) ? console.log('Audio supported') : console.log('No audio support');

    if ('ontouchstart' in document.documentElement) {
        initTouch();
    } else {
        initClick();
    }

    if (!audioSupported) {
        modal.innerHTML += '<p>Bad! Audio is not supported!</p>';
        good--;
    } else {
        modal.innerHTML += '<p>Great! Audio is supported!</p>';
    }
    if (!checkCss3Support()) {
        modal.innerHTML += '<p>Bad! CSS3 animations are not supported!</p>';
        good--;
    } else {
        modal.innerHTML += '<p>Great! CSS3 animations are supported!</p>';
    }
    
    if (good == 3) modal.style.background = 'rgba(0, 255, 0, .6)';
    else if (good == 0) modal.style.background = 'rgba(255, 0, 0, .6)';
    else modal.style.background = 'rgba(255, 255, 0, .6)';

}

function initAudio() {
    
    audioElement = document.createElement('audio');
    audioElement.id = 'scan-new';
    
    var type;
    if (audioElement.canPlayType('audio/mpeg') != '') type = 'mp3';
    if (audioElement.canPlayType('audio/ogg') != '') type = 'ogg';
    
    console.log('can play ogg? '+audioElement.canPlayType('audio/ogg'));
    console.log('can play mp3? '+audioElement.canPlayType('audio/mpeg'));
    
    if ('MainSettings' in window) {
        // Running on device, so ask for internal storage
        audioElement.src = "/data/data/com.mdroid.example.scannerhtml5/files/audio/scan-new." + type;
    } else {
        // On desktop should be on the same dir of HTML
        audioElement.src = "audio/scan-new." + type;
    }
    
    document.body.appendChild(audioElement);
    
    return true;
}

function initSettings_old() {
    var ul = settingsPane.querySelector('ul');
    var li = document.createElement('li'), setting;
    li.className = 'setting-item'

    // default
    window.settings = {audioVolume: 100, showDialog: true, fakeScan: false};

    setting = li.cloneNode(false);
    setting.innerHTML = '<a href="http://html5test.com">HTML5 test</a>';
    ul.appendChild(setting);
    
    setting = li.cloneNode(false);
    setting.innerHTML = '<a href="http://css3test.com/">CSS3 test</a>';
    ul.appendChild(setting);
    
    setting = li.cloneNode(false);
    setting.innerHTML = '<b>LocalStorage support: </b>' + localStorageSupported;
    ul.appendChild(setting);
    setting = li.cloneNode(false);
    setting.innerHTML = '<b>Audio support: </b>' + audioSupported;
    ul.appendChild(setting);
    
    setting = li.cloneNode(false);
    setting.innerHTML = '<span>Mute audio</span><input id="audio-enable" type="checkbox">';
    setting.onchange = onSettingsChange;
    if (localStorageSupported) {
        if (isNaN(parseInt(localStorage.audioVolume))) localStorage.audioVolume = settings.audioVolume;
        else settings.audioVolume = parseInt(localStorage.audioVolume);
        setting.querySelector('input').checked = (settings.audioVolume == 0);
    }
    ul.appendChild(setting);
    
    setting = li.cloneNode(false);
    setting.innerHTML = '<span>Show dialog on startup</span><input id="dialog-show" type="checkbox">';
    setting.onchange = onSettingsChange;
    if (localStorageSupported) {
        settings.showDialog = (localStorage.showDialog == 'true');
        setting.querySelector('input').checked = settings.showDialog;
    }
    ul.appendChild(setting);
    
    setting = li.cloneNode(false);
    setting.innerHTML = '<span>Fake scan engine</span><input id="fake-scan" type="checkbox">';
    setting.onchange = onSettingsChange;
    if (localStorageSupported) {
        settings.fakeScan = (localStorage.fakeScan == 'true');
        setting.querySelector('input').checked = settings.fakeScan;
    }
    ul.appendChild(setting);
    
    if (!('MainSettings' in window)) return false;
    setting = li.cloneNode(false);
    setting.innerHTML = '<b>HTML folder:</b> '+MainSettings.getRoot();
    ul.appendChild(setting);

    return true;
}

function initSettings() {
    var tab = $(settingsPane).find('table tbody'),setting;

    // default values
    window.settings = {audioVolume: 100, showDialog: true, fakeScan: isBarcodeManagerAvailable};

    setting = createSetting('storageSupport', 'LocalStorage supported', true);
    setting.querySelector('input').checked = localStorageSupported;
    tab.append(setting);

    setting = createSetting('audioSupport', 'Audio supported', true);
    setting.querySelector('input').checked = audioSupported;
    tab.append(setting);

    setting = createSetting('audioEnable', 'Mute audio');
    if (localStorageSupported) {
        if (isNaN(parseInt(localStorage.audioVolume))) localStorage.audioVolume = settings.audioVolume;
        else settings.audioVolume = parseInt(localStorage.audioVolume);
        setting.querySelector('input').checked = (settings.audioVolume == 0);
    }
    tab.append(setting);
    
    setting = createSetting('showDialog', 'Show dialog on startup');
    if (localStorageSupported) {
        settings.showDialog = (localStorage.showDialog == 'true');
        setting.querySelector('input').checked = settings.showDialog;
    }
    tab.append(setting);

    setting = createSetting('fakeScan', 'Fake scan engine', !isBarcodeManagerAvailable);
    if (localStorageSupported) {
        // Force the fake scan when no barcode manager is available
        settings.fakeScan = (localStorage.fakeScan == 'true') || !isBarcodeManagerAvailable;
        setting.querySelector('input').checked = settings.fakeScan;
    }

    tab.append('<tr class="setting-item"><td class="setting-label">Version</td><td class="version">'+VERSION+'</td><tr>')

    $('.setting-label').click(function() {
        var i = $(this).siblings().find('input[type="checkbox"]')[0];
        if (i && !i.disabled) {
            i.checked = !i.checked;
        }
    });
}

function createSetting(id, text, readOnly) {
    var setting = document.createElement('tr');
    readOnly = readOnly || false;
    setting.className = 'setting-item';

    setting.innerHTML = '<td class="setting-label">'+text+'</td><td class="input"><input id="'+id+'" type="checkbox"></td>';
    setting.onchange = onSettingsChange;
    if (readOnly) {
        setting.querySelector('input').disabled = 'disabled';
    }
    if (localStorageSupported) {
        settings[id] = (localStorage[id] == 'true');
        setting.querySelector('input').checked = settings[id];
    }
    return setting;
}

function onSettingsChange(event) {
    switch(event.target.id) {
    case 'audioEnable':
        if (event.target.checked) {
            if (localStorageSupported) localStorage.audioVolume = '0';
            settings.audioVolume = 0;
        } else {
            if (localStorageSupported) localStorage.audioVolume = '100';
            settings.audioVolume = 100;
        }
        localStorage.audioEnable = event.target.checked;
        settings.audioEnable = event.target.checked;
        break;
    case 'showDialog':
        if (event.target.checked) {
            if (localStorageSupported) localStorage.showDialog = 'true'
            settings.showDialog = true;
        } else {
            if (localStorageSupported) localStorage.showDialog = 'false'
            settings.showDialog = false;
        }
        break;
        
    case 'fakeScan':
        if (event.target.checked) {
            if (localStorageSupported) localStorage.fakeScan = 'true'
            settings.fakeScan = true;
        } else {
            if (localStorageSupported) localStorage.fakeScan = 'false'
            settings.fakeScan = false;
        }
        break;
    }
}

function playSound() {
    if (audioSupported && settings.audioVolume > 0) {
        audioElement.play();
    }
}

function handleTouchStart(event) {
    event.preventDefault();
    switch(this.id) {
        case 'barcode-info':
            var sym = $(this).find('.sym[type="hidden"]');
            if (sym[0]) {            
                alert(sym[0].value);
            }
            break;

        case 'barcode':
            var code = $(this).find('.code[type="hidden"]');
            if (code[0]) {
                alert(code[0].value);
            }
            break;

        case 'bars-container':
            if (!isScanning) startScan();
            break;

        case 'scan':
            if (!isScanning) startScan();
            break;

        case 'clean-store':
            if (localStorageSupported) localStorage.barcodes = [];
            scannedBarcodes = [];
            store.innerHTML = '';
            setBarcodeInfo();
            barcode.innerHTML = 'Cleaned history';
            break;

        case 'settings':
            settingsPane.classList.toggle('open');
            if (firstTimeSettingsInit && settingsPane.classList.contains('open')) {
                firstTimeSettingsInit = false;
                var w;
                $('.setting-item input[type="checkbox"]').each(function() {
                    h = this.offsetHeight;
                    this.style.width = h + 'px';
                });
            }
            if ('MainSettings' in window) MainSettings.setOpen(settingsPane.classList.contains('open'));
            break;

        default: break;
    }
}
function handleTouchEnd(event) {
    event.preventDefault();
    
    switch(this.id) {
        case 'scan':
            if (maskStopEvent) {
                maskStopEvent = false;
            } else {
                stopScan();
            }
            break;
        default: break;
    }
}
function handleTouchMove(event) {
    //event.stopPropagation();
    event.preventDefault();
    //return false;
}

function checkCss3Support() {
    var animation = false,
        animationstring = 'animation',
        keyframeprefix = '',
        domPrefixes = 'Webkit Moz O ms Khtml'.split(' '),
        pfx  = '';

    if( button.style.animationName !== undefined ) { return true; }

    if( animation === false ) {
      for( var i = 0; i < domPrefixes.length; i++ ) {
        if( button.style[ domPrefixes[i] + 'AnimationName' ] !== undefined ) {
          pfx = domPrefixes[ i ];
          animationstring = pfx + 'Animation';
          keyframeprefix = '-' + pfx.toLowerCase() + '-';
          return true;
        }
      }
    }
}

function initStorage() {
    if(localStorage instanceof Storage) {
        localStorageSupported = true;
        scannedBarcodes = [];
        if (localStorage.barcodes) {
            scannedBarcodes = JSON.parse(localStorage.barcodes);
        }

        var li;
        for(var i=scannedBarcodes.length-1; i>Math.max(scannedBarcodes.length-11,-1); i--) {
            li = document.createElement('li');
            li.innerHTML = scannedBarcodes[i];
            store.appendChild(li);
        }
        updateStoreUi();
        return true;
    } else {
        localStorageSupported = false;
        return false;
    }
};

function storeBarcode(bar) {
    if (!scannedBarcodes) {
        scannedBarcodes = JSON.parse(localStorage.barcodes);
    }
    scannedBarcodes.push(bar);
    if (scannedBarcodes.length > storageSize) { 
        scannedBarcodes.shift();
        store.removeChild(store.lastChild);
    }
    var li = document.createElement('li');
    li.className = "slidein";
    li.innerHTML = bar;
    store.insertBefore(li, store.firstChild);
    if (store.childElementCount > 10) store.removeChild(store.lastChild);
    
    localStorage.barcodes = JSON.stringify(scannedBarcodes);
    updateStoreUi();
}

function updateStoreUi() {
    for(var i=1; i<Math.min(store.children.length,10); i++) {
        store.children[i].style.display = 'block';
    }
}

function startScan(timeout) {
    if (!timeout || timeout > 4500 || timeout < 0) timeout = 4500;
    
    if ('BarcodeManager' in window && !settings.fakeScan) {
        BarcodeManager.startDecode(timeout);
    } else {
        onStart();
        fakeScanTimer = setTimeout(function() {
            var scanned = Mdroid.BarcodeGenerator.randomCode(10);
            onRead(scanned, 'EAN13', ']E4');
        }, 3000);
    }
}

function stopScan() {
    if ('BarcodeManager' in window && !settings.fakeScan) {
        BarcodeManager.stopDecode();
    } else {
        onStop();
    }
}

function onStart() {
    isScanning = true;
    barsContainer.className = "scanning";
	barcode.innerHTML = 'Scanning...';
	setBarcodeInfo();
	goodRead.style.display = 'none';
	maskStopEvent = false;
	startTime = new Date().getTime();
}

function onStop() {
    isScanning = false;
    if (maskStopEvent) return;
    if (fakeScanTimer) clearTimeout(fakeScanTimer);
    barsContainer.className = "";
	barcode.innerHTML = 'Stopped!';
	setBarcodeInfo();
	//goodRead.style.display = 'none';
	maskStopEvent = false;
}

function onTimeout() {
    isScanning = false;
    barsContainer.className = "";
	barcode.innerHTML = 'Timeout!';
    setBarcodeInfo();
	goodRead.style.display = 'none';
	maskStopEvent = true;
}

function onRead(code, symbology, aim) {
    isScanning = false;
    var endTime = new Date().getTime();
    barsContainer.className = "";
    setBarcodeData(code);
  	setBarcodeInfo(symbology, aim, endTime-startTime);
    maskStopEvent = true;
    
    // Draw barcode
    var b = Mdroid.BarcodeGenerator.random(code, symbology, aim);
    b.draw(barcodeCanvas);
    
    // Show good read
    goodRead.style.display = 'block';
    setTimeout(function() {goodRead.style.display = 'none';}, 500);

    // Beep
    playSound();

    if(localStorageSupported) storeBarcode(code);
    //getLocation();
}

function setBarcodeData(code) {
    if (!code) barcode.innerHTML = '';
    var toShow;
    
    if (code.length > 19) {
        toShow = code.slice(0, 16);
        toShow += '<span class="more">...</span><input class="code" type="hidden" value="'+code+'">';
    } else {
        toShow = code;
    }
    barcode.innerHTML = toShow;
}

function setBarcodeInfo(sym, aim, time) {
    if (!sym || !aim) {
        barcodeInfo.children[0].innerHTML = '';
        barcodeInfo.children[1].style.display = 'none';
        barcodeInfo.children[2].innerHTML = '';
        barcodeInfo.children[3].style.display = 'none';
        barcodeInfo.children[4].style.display = 'none';
        barcodeInfo.children[5].innerHTML = '';
    } else {
        if (!time) time = '--';
        if (sym.length > 7) {
            sym = sym.slice(0, 4) +
                '<span class="more">...</span><input class="sym" type="hidden" value="'+sym+'">';
        }
        barcodeInfo.children[0].innerHTML = sym;
        barcodeInfo.children[1].style.display = 'inline';
        barcodeInfo.children[2].innerHTML = aim;
        barcodeInfo.children[3].style.display = 'inline';
        barcodeInfo.children[4].style.display = 'inline';
        barcodeInfo.children[5].innerHTML = time + 'ms';
    }
}

// Geolocation APIs, not used for the moment
function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition, showError, {timeout: 60000});
    }
}

function showPosition(position) {
    var latlon=position.coords.latitude+","+position.coords.longitude;
    var x = document.documentElement.clientWidth;
    var y = document.documentElement.clientHeight;
    var scale = Math.round(Math.max(x/640, y/640));
    
    // Google Maps APIs for static map image with a marker
    var img_url="http://maps.googleapis.com/maps/api/staticmap?center="
        +latlon+"&markers=color:red|"+latlon+"&zoom=12&size="+x+"x"+y+"&scale="+scale+"&sensor=false";
    document.getElementById("map").innerHTML="<img src='"+img_url+"'>";
}

function showError(error) {
    switch(error.code) {

    case error.PERMISSION_DENIED:
        console.log("User denied the request for Geolocation.");
        break;
    case error.POSITION_UNAVAILABLE:
        console.log("Location information is unavailable.");
        break;
    case error.TIMEOUT:
        console.log("The request to get user location timed out.");
        break;
    case error.UNKNOWN_ERROR:
        console.log("An unknown error occurred.");
        break;
    }
}
