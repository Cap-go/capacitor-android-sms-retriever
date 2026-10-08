import { CapacitorUpdater } from '@capgo/capacitor-updater';
import { Capacitor } from '@capacitor/core';
import './style.css';
import { AndroidSmsRetriever } from '@capgo/capacitor-android-sms-retriever';

const watchChip = document.getElementById('watch-chip');
const hashValue = document.getElementById('hash-value');
const watchStatus = document.getElementById('watch-status');
const smsPane = document.getElementById('sms-pane');
const phoneDisplay = document.getElementById('phone-display');
const versionLine = document.getElementById('version-line');
const activityLog = document.getElementById('activity-log');

const btnHash = document.getElementById('btn-hash');
const btnStart = document.getElementById('btn-start');
const btnStop = document.getElementById('btn-stop');
const btnPhone = document.getElementById('btn-phone');
const btnVersion = document.getElementById('btn-version');

let listenersRegistered = false;

const timestamp = () => {
  const d = new Date();
  return d.toISOString().slice(11, 19);
};

const appendLog = (message) => {
  const line = `[${timestamp()}] ${message}`;
  const existing = activityLog.textContent.trim();
  activityLog.textContent = existing ? `${existing}\n${line}` : line;
  activityLog.scrollTop = activityLog.scrollHeight;
};

const setChip = (label, className) => {
  watchChip.textContent = label;
  watchChip.className = `chip ${className}`;
};

const formatError = (error) => {
  if (error?.message) {
    return error.message;
  }
  return String(error);
};

const ensureListeners = async () => {
  if (listenersRegistered) {
    return;
  }
  await AndroidSmsRetriever.addListener('smsReceived', (event) => {
    setChip('Received', 'chip-listening');
    smsPane.textContent = event.message;
    appendLog(`smsReceived: ${event.message}`);
    watchStatus.textContent = 'Last action: SMS received';
  });
  await AndroidSmsRetriever.addListener('smsRetrieverTimeout', () => {
    setChip('Timeout', 'chip-timeout');
    appendLog('smsRetrieverTimeout: 5 minute window ended');
    watchStatus.textContent = 'Last action: retriever timeout';
  });
  await AndroidSmsRetriever.addListener('smsRetrieverError', (event) => {
    setChip('Error', 'chip-error');
    appendLog(`smsRetrieverError: ${event.message}`);
    watchStatus.textContent = `Last action: error (${event.message})`;
  });
  listenersRegistered = true;
  appendLog('Event listeners registered');
};

btnHash.addEventListener('click', async () => {
  try {
    const result = await AndroidSmsRetriever.getHashString();
    hashValue.textContent = result.hash;
    appendLog(`getHashString: ${result.hash}`);
  } catch (error) {
    hashValue.textContent = `Error: ${formatError(error)}`;
    appendLog(`getHashString failed: ${formatError(error)}`);
  }
});

btnStart.addEventListener('click', async () => {
  try {
    await ensureListeners();
    const result = await AndroidSmsRetriever.startWatch();
    setChip('Listening', 'chip-listening');
    watchStatus.textContent = `Last action: startWatch (${result.status})`;
    appendLog(`startWatch: ${result.status}`);
  } catch (error) {
    setChip('Error', 'chip-error');
    watchStatus.textContent = `Last action: startWatch failed`;
    appendLog(`startWatch failed: ${formatError(error)}`);
  }
});

btnStop.addEventListener('click', async () => {
  try {
    const result = await AndroidSmsRetriever.stopWatch();
    setChip('Idle', 'chip-idle');
    watchStatus.textContent = `Last action: stopWatch (${result.status})`;
    appendLog(`stopWatch: ${result.status}`);
  } catch (error) {
    setChip('Error', 'chip-error');
    appendLog(`stopWatch failed: ${formatError(error)}`);
  }
});

btnPhone.addEventListener('click', async () => {
  try {
    const result = await AndroidSmsRetriever.getPhoneNumber();
    phoneDisplay.value = result.phoneNumber;
    appendLog(`getPhoneNumber: ${result.phoneNumber}`);
  } catch (error) {
    phoneDisplay.value = `Error: ${formatError(error)}`;
    appendLog(`getPhoneNumber failed: ${formatError(error)}`);
  }
});

btnVersion.addEventListener('click', async () => {
  try {
    const result = await AndroidSmsRetriever.getPluginVersion();
    versionLine.textContent = `Version: ${result.version}`;
    appendLog(`getPluginVersion: ${result.version}`);
  } catch (error) {
    versionLine.textContent = `Version: error (${formatError(error)})`;
    appendLog(`getPluginVersion failed: ${formatError(error)}`);
  }
});

appendLog(`Platform: ${Capacitor.getPlatform()} (native: ${Capacitor.isNativePlatform()})`);

if (Capacitor.isNativePlatform()) {
  CapacitorUpdater.notifyAppReady().catch((error) => {
    appendLog(`notifyAppReady failed: ${formatError(error)}`);
  });
}

AndroidSmsRetriever.getPluginVersion()
  .then((result) => {
    versionLine.textContent = `Version: ${result.version}`;
  })
  .catch(() => {
    versionLine.textContent = 'Version: unavailable on this platform';
  });
