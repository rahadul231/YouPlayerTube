"""Convert actual AndroidJUnitRunner status events to JUnit XML; reject incomplete runs."""
import pathlib, re, sys, xml.etree.ElementTree as E
source=pathlib.Path(sys.argv[1]); output=pathlib.Path(sys.argv[2]); text=source.read_text()
fields={}; cases=[]
for line in text.splitlines():
    if line.startswith('INSTRUMENTATION_STATUS: '):
        key,_,value=line[len('INSTRUMENTATION_STATUS: '):].partition('=')
        fields[key]=value
    elif line.startswith('INSTRUMENTATION_STATUS_CODE: '):
        code=int(line.split(':',1)[1].strip())
        if code != 1 and 'class' in fields and 'test' in fields:
            cases.append((fields.copy(),code))
        fields={}
root=E.Element('testsuite',name='AndroidJUnitRunner',tests=str(len(cases)),
    failures=str(sum(code == -2 for _,code in cases)),
    errors=str(sum(code not in (0,-2,-3,-4) for _,code in cases)),
    skipped=str(sum(code in (-3,-4) for _,code in cases)))
for fields,code in cases:
    case=E.SubElement(root,'testcase',classname=fields['class'],name=fields['test'])
    if code in (-3,-4): E.SubElement(case,'skipped')
    elif code != 0:
        E.SubElement(case,'failure' if code == -2 else 'error',message=fields.get('stack',fields.get('stream','Instrumentation failure'))).text=text
output.parent.mkdir(parents=True,exist_ok=True)
E.ElementTree(root).write(output,encoding='utf-8',xml_declaration=True)
match=re.search(r'OK \((\d+) tests?\)',text)
assert match and int(match[1]) == len(cases) and len(cases)>0, 'Incomplete/non-executed instrumentation run'
assert all(code==0 for _,code in cases), 'Device tests failed or skipped; inspect raw transcript and XML'
print(f'Executed {len(cases)} Android tests; all passed')
