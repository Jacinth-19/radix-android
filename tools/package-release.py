#!/usr/bin/env python3
"""Package the archived signed/audited Radix 6 artifact. This script does not build current source 7.0 or create signing keys."""
from pathlib import Path
import hashlib,json,re,shutil,sys,zipfile
root=Path(__file__).resolve().parent.parent
output=Path(sys.argv[1] if len(sys.argv)>1 else root.parent/'deliverables');output.mkdir(parents=True,exist_ok=True)
audit=json.loads((root/'design/apk-static-checks.json').read_text())
assert audit['status']=='pass' and audit['versionCode']==6
apk=root/'Radix-release.apk'
assert hashlib.sha256(apk.read_bytes()).hexdigest()==audit['apkSha256']
for src,name in [(apk,'Radix.apk'),(root/'web/index.html','Radix-preview.html'),(root/'design/DIGITAL-LOGIC-REFERENCE.html','Radix-Logic-Reference.html'),(root/'design/STUDENT-WORKBOOK.md','Radix-Student-Workbook.md'),(root/'design/VERIFICATION.md','Radix-Verification.md')]:shutil.copy2(src,output/name)
exclude={'.git','.gradle','.idea','.cache','.arena','.local','node_modules','build','dist','out','coverage','.venv','__pycache__','.pytest_cache','.next','target'}
files=[]
for p in sorted(root.rglob('*')):
 if not p.is_file():continue
 rel=p.relative_to(root)
 if any(x in exclude for x in rel.parts):continue
 if p.name=='local.properties' or p.suffix in {'.apk','.p12','.pfx','.jks','.keystore','.pass','.password','.iml'}:continue
 if p.parent==root and p.suffix=='.log':continue
 files.append(p)
source=output/'Radix-Android-Source.zip'
with zipfile.ZipFile(source,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for p in files:z.write(p,'radix-android/'+p.relative_to(root).as_posix())
 assert z.testzip() is None
manifestNames=['Radix.apk','Radix-Android-Source.zip','Radix-preview.html','Radix-Logic-Reference.html','Radix-Student-Workbook.md','Radix-Verification.md']
if (output/'Radix-release-signing-key.zip').exists():manifestNames.append('Radix-release-signing-key.zip')
(output/'SHA256SUMS.txt').write_text(''.join(hashlib.sha256((output/n).read_bytes()).hexdigest()+'  '+n+'\n' for n in manifestNames))
# Password is not put in the source archive or public files/manifest. Read it only for a no-disclosure safety scan.
credential=root.parent/'private-signing/Radix-release.password'
secret=credential.read_bytes().strip() if credential.exists() else None
with zipfile.ZipFile(source) as z:
 for info in z.infolist():
  data=z.read(info)
  assert not (secret and secret in data),'Credential safety check failed in source'
  assert not re.search(rb'-----BEGIN (?:ENCRYPTED )?PRIVATE KEY-----\r?\n[A-Za-z0-9+/=]{20,}\r?\n',data),'Private-key safety check failed in source'
for n in manifestNames:
 if n.endswith('signing-key.zip'):continue
 if n==source.name:continue
 data=(output/n).read_bytes();assert not(secret and secret in data),'Credential safety check failed in public deliverables'
print(json.dumps({'status':'pass','sourceFiles':len(files),'manifestArtifacts':len(manifestNames),'apkBytes':apk.stat().st_size,'apkSha256':audit['apkSha256'],'credentialScan':'pass; password neither printed nor included in public files','output':str(output)},indent=2))
