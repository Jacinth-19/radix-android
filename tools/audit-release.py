#!/usr/bin/env python3
"""Static APK/signature/ELF/JVM evidence audit. This does not install/run Android UI."""
from pathlib import Path
import hashlib,json,os,re,shutil,struct,subprocess,sys,xml.etree.ElementTree as E,zipfile
root=Path(__file__).resolve().parent.parent;design=root/'design'
apk=Path(sys.argv[1] if len(sys.argv)>1 else root/'Radix-release.apk').resolve()
old=Path(sys.argv[2]).resolve() if len(sys.argv)>2 else None
sdk=Path(os.environ.get('ANDROID_HOME',os.environ.get('ANDROID_SDK_ROOT','')));bt=sdk/'build-tools/36.0.0'
def cmd(name,*args):return subprocess.check_output([str(bt/name),*map(str,args)],stderr=subprocess.STDOUT,text=True)
def save(name,text): (design/name).write_text(text)
badging=cmd('aapt2','dump','badging',apk);save('apk-badging.txt',badging)
permissions=cmd('aapt2','dump','permissions',apk);save('apk-permissions.txt',permissions)
manifest=cmd('aapt2','dump','xmltree','--file','AndroidManifest.xml',apk)
assert "name='com.radix.app'" in badging and "versionCode='6'" in badging and "versionName='6.0.0'" in badging
assert re.search(r"(?:minSdkVersion|sdkVersion):'26'",badging) and "targetSdkVersion:'36'" in badging
assert 'android.permission.INTERNET' not in permissions
assert 'application-debuggable' not in badging and not re.search(r'android:debuggable[^\n]*0xffffffff',manifest)
found=set(re.findall(r"uses-permission: name='([^']+)'",permissions));assert found<={'android.permission.POST_NOTIFICATIONS','android.permission.RECEIVE_BOOT_COMPLETED','com.radix.app.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION'},found
normal=cmd('apksigner','verify','--verbose','--print-certs',apk)
legacy=cmd('apksigner','verify','--verbose','--print-certs','--min-sdk-version','21',apk)
save('apk-signature-verification.txt','## Normal verification (actual minSdk 26)\n'+normal+'\n## Verification-only legacy API-21 override (does not modify APK)\n'+legacy)
assert 'Verified using v2 scheme (APK Signature Scheme v2): true' in normal
assert 'Verified using v3 scheme (APK Signature Scheme v3): true' in normal
assert 'Verified using v1 scheme (JAR signing): true' in legacy
assert 'Signer #1 key size (bits): 4096' in normal
cert=re.search(r'Signer #1 certificate SHA-256 digest: (\w+)',normal).group(1)
assert cert=='785b9e3334078ed72a2a106e5dad849d80597d1dac929ce40f840671ca639020'
cmd('zipalign','-P','16','-c','4',apk)
libs=[]
with zipfile.ZipFile(apk) as z,apk.open('rb') as f:
 for info in z.infolist():
  if not(info.filename.startswith('lib/') and info.filename.endswith('.so')):continue
  f.seek(info.header_offset);head=f.read(30);n,e=struct.unpack_from('<HH',head,26);off=info.header_offset+30+n+e
  assert info.compress_type==zipfile.ZIP_STORED and off%16384==0
  data=z.read(info);assert data[:4]==b'\x7fELF' and data[5]==1
  if data[4]==2:phoff=struct.unpack_from('<Q',data,32)[0];ents,num=struct.unpack_from('<HH',data,54);align_at=48;fmt='<Q'
  else:assert data[4]==1;phoff=struct.unpack_from('<I',data,28)[0];ents,num=struct.unpack_from('<HH',data,42);align_at=28;fmt='<I'
  aligns=[struct.unpack_from(fmt,data,phoff+i*ents+align_at)[0] for i in range(num) if struct.unpack_from('<I',data,phoff+i*ents)[0]==1]
  assert aligns and all(v>=16384 for v in aligns)
  libs.append({'entry':info.filename,'uncompressed':True,'zipDataOffset':off,'zipAligned16KiB':True,'elfLoadAlignments':aligns})
assert {x['entry'].split('/')[1] for x in libs}=={'arm64-v8a','armeabi-v7a','x86','x86_64'}
native={};combined=E.Element('testsuites',{'name':'Radix 6 native JVM unit tests; debug and release variants','tests':'168'})
for variant in ['debug','release']:
 dest=design/'native-unit-tests'/variant
 if dest.exists():shutil.rmtree(dest)
 dest.mkdir(parents=True)
 suites=[E.parse(p).getroot() for p in sorted((root/f'app/build/test-results/test{variant.title()}UnitTest').glob('TEST*.xml'))]
 results={key:sum(int(r.attrib[key]) for r in suites) for key in ['tests','failures','errors','skipped']}
 assert results=={'tests':93,'failures':0,'errors':0,'skipped':0},results
 native[variant]=results
 for src,r in zip(sorted((root/f'app/build/test-results/test{variant.title()}UnitTest').glob('TEST*.xml')),suites):shutil.copy2(src,dest/src.name);combined.append(r)
 shutil.copy2(root/f'app/build/reports/lint-results-{variant}.xml',design/f'native-lint-{variant}.xml')
 issues=E.parse(design/f'native-lint-{variant}.xml').getroot().findall('issue')
 assert not [i for i in issues if i.attrib['severity'] in ['Error','Fatal']]
E.indent(combined);E.ElementTree(combined).write(design/'native-unit-tests.xml',encoding='utf-8',xml_declaration=True)
shutil.copy2(design/'native-lint-release.xml',design/'native-lint.xml')
if old:
 sig=cmd('apksigner','verify','--print-certs',old)
 oldcert=re.search(r'Signer #1 certificate SHA-256 digest: (\w+)',sig).group(1)
 assert oldcert==cert
 previous=cmd('aapt2','dump','badging',old);assert "versionCode='5'" in previous
 save('upgrade-certificate-comparison.json',json.dumps({'status':'pass','previousCode':5,'newCode':6,'samePackage':'com.radix.app','sameCertificate':True,'certificateSha256':cert,'note':'Static update prerequisites verified; installed Android upgrade/data-retention runtime test not performed.'},indent=2)+'\n')
report={'status':'pass','applicationId':'com.radix.app','versionName':'6.0.0','versionCode':6,'minSdk':26,'targetSdk':36,'debuggable':False,'internetPermission':False,'dangerousPermissions':False,'nativeUnitTests':native,'apkBytes':apk.stat().st_size,'apkSha256':hashlib.sha256(apk.read_bytes()).hexdigest(),'certificateSha256':cert,'signatureV2':True,'signatureV3':True,'legacyV1VerifiedWithApi21Override':True,'nativeLibraries':libs,'installedAndroidRuntimeQA':False}
save('apk-static-checks.json',json.dumps(report,indent=2)+'\n')
print(json.dumps({k:v for k,v in report.items() if k not in ['nativeLibraries']},indent=2))
