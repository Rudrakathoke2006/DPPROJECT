import pathlib,subprocess,sys,time,urllib.request,hashlib,shutil
root=pathlib.Path(__file__).resolve().parents[1];java=sys.argv[1];runtime=root/'qa-final';data=runtime/'data'
# Client definitions only; never execute the sample-dependent legacy suite.
original_args=sys.argv;sys.argv=['test','http://127.0.0.1:8092/app']
exec((root/'frontend/verify_http.py').read_text(encoding='utf-8').split("faculty = Client")[0]);sys.argv=original_args
cp=str(root/'frontend/target/classes')+';'+str(root/'frontend/lib/*')
def hashes():return {p.name:hashlib.sha256(p.read_bytes()).hexdigest() for p in data.glob('*.csv')}
def start():
 log=open(root/'tests/server-results.txt','a',encoding='utf-8')
 process=subprocess.Popen([java,'-Dfile.encoding=UTF-8','-cp',cp,'campus.web.Launcher',str(root/'frontend/src/main/webapp'),'8092'],cwd=runtime,stdout=log,stderr=log)
 for _ in range(60):
  try:urllib.request.urlopen(BASE,timeout=1);return process,log
  except Exception:time.sleep(.25)
 raise AssertionError('Startup timeout')
def stop(p,log):p.terminate();p.wait(timeout=20);log.close()
before=hashes();p,log=start()
try:
 faculty=Client('rahul','rahul123');faculty.get('page=event&id=FINALQA');check('Conference Room' in faculty.text,'Restart preserves event and allocation')
 student=Client('aarav','aarav123');student.get('page=notifications');check('Final workflow notice' in student.text,'Restart preserves notification')
 check(hashes()==before,'All CSV bytes persist across restart')
finally:stop(p,log)
subprocess.run(['powershell','-NoProfile','-ExecutionPolicy','Bypass','-File',str(root/'reset.ps1'),'-RuntimeDirectory',str(runtime)],check=True)
after=hashes()
check(all(after[n]==before[n] for n in ['users.csv','venues.csv']),'Reset preserves account/password/role and venue bytes')
check(all(len(p.read_text(encoding='utf-8-sig').splitlines())==1 for p in data.glob('*.csv') if p.name not in ['users.csv','venues.csv']),'Reset clears every event CSV and preserves headers')
backup=max((runtime/'backups').iterdir(),key=lambda p:p.name)/'data'
check(all(hashlib.sha256((backup/n).read_bytes()).hexdigest()==v for n,v in before.items()),'Backup contains exact pre-reset data')
p,log=start()
try:
 faculty=Client('rahul','rahul123');faculty.get('page=events');check('FINALQA' not in faculty.text and 'EVT101' not in faculty.text,'Restart does not resurrect deleted or sample events')
finally:stop(p,log)
# Exercise full workflow again on the reset runtime through real JSP/HTTP.
p,log=start()
try:
 result=subprocess.run([sys.executable,str(root/'tests/workflow.py'),BASE],capture_output=True,text=True,encoding='utf-8');print(result.stdout);assert result.returncode==0,result.stderr
 (root/'tests/workflow-results.txt').write_text(result.stdout,encoding='utf-8')
finally:stop(p,log)
print('Lifecycle checks PASSED')
