import pathlib,subprocess,tempfile,shutil,sys
root=pathlib.Path(__file__).resolve().parents[1]; java=sys.argv[1]; cp=str(root/'frontend/target/classes')
log=[]
for suite in ['Member1BuilderPrototypeTest','FullRegressionTest','MediatorRegressionTest','StrategyIntegrationTest','StrategyConsoleRegressionTest','WebDataRegressionTest']:
 with tempfile.TemporaryDirectory(dir=root/'tests') as folder:
  data=pathlib.Path(folder)/'data';shutil.copytree(root/'data',data)
  for p in data.glob('*.csv'):
   if p.name not in ['users.csv','venues.csv']: p.write_text(p.read_text(encoding='utf-8-sig').splitlines()[0]+'\n',encoding='utf-8')
  result=subprocess.run([java,'-Dfile.encoding=UTF-8','-cp',cp,suite],cwd=folder,capture_output=True,text=True,encoding='utf-8')
  log.append(suite+'\n'+result.stdout+result.stderr);print(suite,'exit',result.returncode);assert result.returncode==0,result.stdout+result.stderr
 with tempfile.TemporaryDirectory(dir=root/'tests') as folder:
  shutil.copytree(root/'data',pathlib.Path(folder)/'data')
  result=subprocess.run([java,'-Dfile.encoding=UTF-8','-cp',cp,'Main'],cwd=folder,input='1\nrahul\nrahul123\n1\n0\n0\n',capture_output=True,text=True,encoding='utf-8')
  assert result.returncode==0 and 'FACULTY MENU' in result.stdout and 'Application closed' in result.stdout
print('Console login, view, logout, exit PASS');log.append(result.stdout)
(root/'tests/backend-results.txt').write_text('\n'.join(log),encoding='utf-8')
