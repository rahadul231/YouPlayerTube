"""Real background-process kill and force-stop checks on isolated CI emulator only."""
import subprocess, time, re, xml.etree.ElementTree as ET
PACKAGE = 'com.youplayertube.app'
def adb(*args):
    return subprocess.check_output(['adb', *args], text=True).strip()
def tree():
    adb('shell', 'rm', '-f', '/sdcard/ypt-window.xml')
    adb('shell', 'uiautomator', 'dump', '/sdcard/ypt-window.xml')
    return ET.fromstring(adb('shell', 'cat', '/sdcard/ypt-window.xml'))
def clickable(root, text):
    parents = {child: parent for parent in root.iter() for child in parent}
    for node in root.iter():
        if node.attrib.get('text') == text:
            while node is not None and node.attrib.get('clickable') != 'true':
                node = parents.get(node)
            if node is not None:
                return node
    raise AssertionError(f'Clickable {text} missing')
def wait_selected(label):
    for _ in range(10):
        try:
            if clickable(tree(), label).attrib.get('selected') == 'true':
                return
        except (AssertionError, subprocess.CalledProcessError, ET.ParseError):
            pass
        time.sleep(1)
    raise AssertionError(f'{label} not selected')
def launch():
    adb('shell', 'am', 'start', '-a', 'android.intent.action.MAIN', '-c', 'android.intent.category.LAUNCHER', '-f', '0x10200000', '-n', PACKAGE+'/.MainActivity')
adb('shell', 'am', 'force-stop', PACKAGE)
launch(); wait_selected('Home')
node = clickable(tree(), 'Library')
x1,y1,x2,y2 = map(int,re.findall(r'\d+',node.attrib['bounds']))
adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))
wait_selected('Library')
adb('shell','input','keyevent','KEYCODE_HOME')
time.sleep(2)
adb('shell','am','kill',PACKAGE)
for _ in range(10):
    result=subprocess.run(['adb','shell','pidof',PACKAGE],capture_output=True,text=True)
    if not result.stdout.strip(): break
    time.sleep(1)
else: raise AssertionError('Process did not terminate; process-death evidence not established')
launch(); wait_selected('Library')
print('PASS: saved task restored Library after real background process termination')
adb('shell','am','force-stop',PACKAGE)
launch(); wait_selected('Home')
print('PASS: fresh launch after force-stop starts at Home')
