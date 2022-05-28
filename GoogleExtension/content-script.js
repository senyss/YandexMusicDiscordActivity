/* global addEventListener, CustomEvent, externalAPI, chrome, */

console.log('contentscript')
const port = chrome.runtime.connect({ name: 'knockknock' })

function injectCode (func) {
  const script = document.createElement('script')

  for (var _len = arguments.length, args = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
    args[_key - 1] = arguments[_key]
  }

  script.textContent = 'try {(' + func + ')(' + args + '); } catch(e) {console.error("injected error", e);};';
  (document.head || document.documentElement).appendChild(script)
  script.parentNode.removeChild(script)
}

injectCode(() => {
  externalAPI.on(externalAPI.EVENT_TRACK, function () {
    const t = externalAPI.getCurrentTrack()
    const trackInfo = {
      title: t.title,
      artist: t.artists.map(x => x.title).join(', '),
      album: t.album.title,
      duration: t.duration
    }
    console.log(`${trackInfo}`)
    document.dispatchEvent(new CustomEvent('trackChange', {
      detail: trackInfo
    }))
  })

  externalAPI.on(externalAPI.EVENT_PROGRESS, function () {
    const progress = externalAPI.getProgress().position
    console.log(`${progress}`)
    document.dispatchEvent(new CustomEvent('trackProgress', {
      detail: progress
    }))
  })
})

document.addEventListener('trackChange', (e) => {
    port.postMessage({
      type: 'trackChange',
      data: e.detail
  })
 }
)
let temp = 0
document.addEventListener('trackProgress', (e) => {
let kek = Math.floor(e.detail)
if (kek == temp) {}
else {
  temp = kek
  port.postMessage({
  type: 'trackProgress',
  data: kek
  })
 }
})
