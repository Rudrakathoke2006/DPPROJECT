"""Integration checks. Run ONLY against a disposable -RuntimeDirectory qa-* instance."""
import html
import http.cookiejar
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid

BASE = sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8081/app'
checks = 0

def check(condition, message):
    global checks
    assert condition, message
    checks += 1
    print('PASS', message)

class Client:
    def __init__(self, username, password):
        self.open = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
        self.csrf = ''
        self.get('')
        self.post(action='login', username=username, password=password)
    def request(self, url, data=None):
        try:
            with self.open.open(url, data, timeout=30) as response:
                self.status = response.status
                text = response.read().decode('utf-8')
        except urllib.error.HTTPError as ex:
            self.status = ex.code
            text = ex.read().decode('utf-8')
        token = re.search(r'name="csrf" value="([^"]+)"', text)
        if token:
            self.csrf = html.unescape(token.group(1))
        self.text = text
        return text
    def get(self, query):
        return self.request(BASE + ('?' + query if query else ''))
    def post(self, **data):
        data.setdefault('csrf', self.csrf)
        return self.request(BASE, urllib.parse.urlencode(data).encode())

faculty = Client('rahul', 'rahul123')
check('Good things are happening' in faculty.text, 'Faculty authentication renders real JSP dashboard')
for page in ['dashboard', 'events', 'calendar', 'approvals', 'registrations', 'resources', 'notifications', 'activity', 'create', 'event&id=EVT101', 'edit&id=EVT103', 'clone&id=EVT101']:
    faculty.get('page=' + page)
    check(faculty.status == 200 and 'HTTP Status 500' not in faculty.text, 'JSP page ' + page)
faculty.post(action='create', csrf='wrong')
check(faculty.status == 403, 'Invalid CSRF token blocked')
faculty.get('')
event_id = 'WEBQA' + uuid.uuid4().hex[:8]
fields = dict(id=event_id, name='QA <campus> & event', type='1', level='UNIVERSITY', date='2026-12-22', venue='Innovation Lab', description='Integration check', budget='5000', capacity='50', duration='3', equipment='Projector;WiFi', instructions='Bring ID')
faculty.post(action='create', **fields)
check('Your event is created' in faculty.text, 'Create event through existing Builder and persistence')
check('QA &lt;campus&gt; &amp; event' in faculty.text and 'QA <campus> & event' not in faculty.text, 'Output HTML is escaped')
faculty.post(action='create', **fields)
check('Could not save the event' in faculty.text, 'Duplicate ID rejected')
faculty.post(action='edit', **dict(fields, name='QA revised event'))
check('Changes saved.' in faculty.text, 'Edit through existing Memento service')
faculty.post(action='undo', id=event_id)
check('last edit was restored' in faculty.text, 'Undo through existing Memento service')
faculty.post(action='clone', id=event_id, newId=event_id+'C', name='QA cloned event', date='2026-12-23', venue='Seminar Hall')
check('Event duplicated' in faculty.text, 'Clone through existing Prototype service')
# Process exactly the first queue item per request, as required by the original console API.
for i in range(20):
    faculty.get('page=approvals')
    match = re.search(r'name="action" value="approve"><input type="hidden" name="id" value="([^"]+)"', faculty.text)
    if not match:
        break
    first = html.unescape(match.group(1))
    faculty.post(action='approve', id=first, decision='yes')
    check('Decision saved.' in faculty.text, 'Faculty approval for ' + first)
    if first == event_id:
        break
for username, password in [('priya','priya123'),('anjali','anjali123')]:
    approver=Client(username,password)
    approver.post(action='approve',id=event_id,decision='yes')
    check('Decision saved.' in approver.text, 'Approval chain advances through ' + username)
faculty.post(action='allocate',id=event_id)
check('Venue, equipment and support staff allocated.' in faculty.text, 'Abstract Factory resources allocated')
faculty.post(action='prepare',id=event_id)
check('Preparation report' in faculty.text, 'Template preparation report rendered')
student=Client('aarav','aarav123')
student.get('page=events')
check('QA cloned event' not in student.text, 'Students cannot see pending events')
student.get('page=create')
check(student.status == 403, 'Student create page denied')
student.get('')
student.post(action='create', **dict(fields,id=event_id+'NO'))
check(student.status == 403, 'Student forged create POST denied')
student.get('')
student.post(action='register',id=event_id,channel='INAPP')
check('registration is confirmed' in student.text, 'Student registration reaches existing backend')
student.post(action='register',id=event_id,channel='INAPP')
check('already registered' in student.text, 'Duplicate registration blocked')
faculty.post(action='publish',id=event_id,message='QA schedule update')
check('Update published to 1 registered student' in faculty.text, 'Observer and Bridge publish to registered student')
student.get('page=notifications')
check('QA schedule update' in student.text, 'Student notification visible')
student.get('page=event&id='+event_id+'C')
check(student.status == 404, 'Pending event detail hidden from student')
admin=Client('admin','admin123')
check('Administrator' in admin.text, 'Admin login works')
admin.post(action='logout')
check('Welcome back.' in admin.text, 'Logout clears session')
invalid=Client('rahul','incorrect')
check('password is incorrect' in invalid.text, 'Invalid credentials rejected')
print(f'\n{checks} checks passed. Original project data was not used for test writes.')
