#!/usr/bin/env python3
"""Static validation of the bundled offline reminder sound and its CC0 notice."""
from hashlib import sha256
from pathlib import Path
import xml.etree.ElementTree as ET
import wave

root = Path(__file__).resolve().parents[1]
asset = root / 'app/src/main/res/raw/radix_reminder.wav'
expected_hash = 'f9d0ef5a5c740c2ac250ac3876db9e4340669044a5bf78ae09192918db3e2ebc'
assert asset.is_file(), 'Bundled reminder asset is missing.'
data = asset.read_bytes()
assert sha256(data).hexdigest() == expected_hash, 'Bundled sound differs from the reviewed CC0 asset.'
with wave.open(str(asset), 'rb') as audio:
    assert (audio.getnchannels(), audio.getsampwidth(), audio.getframerate()) == (1, 2, 44100)
    assert audio.getnframes() == 12992
    assert 0.29 < audio.getnframes() / audio.getframerate() < 0.30

license_file = root / 'licenses/KENNEY-INTERFACE-SOUNDS-CC0.txt'
license_text = license_file.read_text(errors='replace')
asset_notice = (root / 'design/ASSET-LICENSES.md').read_text()
assert 'Creative Commons Zero' in license_text and 'creativecommons.org/publicdomain/zero/1.0' in license_text
assert 'CC0 1.0' in asset_notice and expected_hash in asset_notice
assert '4596a49eaf5a533948d49a47467f606bcdea70ff' in asset_notice

manifest = ET.parse(root / 'app/src/main/AndroidManifest.xml').getroot()
android = '{http://schemas.android.com/apk/res/android}'
permissions = {node.attrib.get(android + 'name') for node in manifest.findall('uses-permission')}
assert 'android.permission.INTERNET' not in permissions, 'Offline app must not request internet access.'
source = (root / 'app/src/main/java/com/radix/app/Notifications.kt').read_text()
ui = (root / 'app/src/main/java/com/radix/app/OnboardingScreens.kt').read_text()
assert 'radix_study_v2' in source and 'R.raw.radix_reminder' in source and 'setSound(sound,audio)' in source
assert 'getNotificationChannel(LEGACY_CHANNEL)' in source and 'fresh.setImportance(old.importance)' in source and 'fresh.setSound(oldSound,audio)' in source
assert 'Settings.System.DEFAULT_NOTIFICATION_URI' in source, 'Default-upgrade chime must not overwrite an explicit old channel mute/custom sound.'
assert 'fun preview(ctx:Context)' in source and 'StudyReminder.preview(context)' in ui
assert 'ACTION_CHANNEL_NOTIFICATION_SETTINGS' in ui and 'EXTRA_CHANNEL_ID' in ui
assert 'android.intent.action.MY_PACKAGE_REPLACED' in (root / 'app/src/main/AndroidManifest.xml').read_text()
strings = (root / 'app/src/main/java/com/radix/app/AppStrings.kt').read_text()
assert all(text in strings for text in ('Preview reminder', 'Écouter un aperçu', 'Probar recordatorio', 'landscapeKmapHint', 'landscapeCircuitHint'))
assert 'tipsEs' in source and 'Recordatorios de estudio' in source and 'Recordatorio diario' in strings
assert 'app/src/main/res/raw/radix_reminder.wav' in asset_notice

print('PASS: bundled 0.295-second mono PCM chime, pinned CC0 source/hash, legacy mute/custom-sound preservation, user-controlled channel and no INTERNET permission.')
