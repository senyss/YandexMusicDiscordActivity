
const portNative = chrome.runtime.connectNative('com.senyss.ymda');
console.warn("conected");
let isAlive = true;

  chrome.runtime.onConnect.addListener(function (port) {
    console.assert(port.name == 'knockknock')
    port.onMessage.addListener(function (msg) {
      portNative.postMessage(msg);
      console.warn("message send");
      console.log(msg)
    })
  })
  
  function message(msg) {
    console.warn("Received" + msg);
  }
  
  function disconect() {
    console.warn("Disconnected");
    isAlive = false
  }
  
  portNative.onMessage.addListener(message);
  portNative.onDisconnect.addListener(disconect);