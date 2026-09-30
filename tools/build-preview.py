#!/usr/bin/env python3
"""Generate the standalone, fully offline UI companion (no CDN or network assets)."""
from pathlib import Path
import base64

root = Path(__file__).resolve().parent.parent
fonts = []
for family, file in [('Space Grotesk', 'space_grotesk.ttf'), ('JetBrains Mono', 'jetbrains_mono.ttf')]:
    data = base64.b64encode((root / 'app/src/main/res/font' / file).read_bytes()).decode()
    fonts.append(f"@font-face{{font-family:'{family}';src:url(data:font/ttf;base64,{data}) format('truetype');font-weight:100 900;font-style:normal;font-display:swap;}}")
html = (root / 'web/template.html').read_text()
html = html.replace('/* FONTS */', '\n'.join(fonts))
html = html.replace('/* ENGINE */', (root / 'web/engine.js').read_text())
html = html.replace('/* STUDENT_STYLES */', (root / 'web/student.css').read_text())
html = html.replace('/* STUDENT_ENGINES */', (root / 'web/student-engines.js').read_text())
html = html.replace('/* STUDENT_UI */', (root / 'web/student-ui.js').read_text())
for marker, file in [('LOGIC_STYLES','logic.css'), ('LOGIC_CATALOG','logic-catalog.js'), ('LOGIC_ENGINE','logic-engine.js'), ('LOGIC_DIAGRAMS','logic-diagrams.js'), ('LOGIC_UI','logic-ui.js'), ('VISUAL_HELP','visual-help.js'), ('STUDY_STYLES','study.css'), ('PRACTICE_CATALOG','practice-catalog.js'), ('STUDY_ENGINE','study-engine.js'), ('STUDY_UI','study-ui.js')]:
    html = html.replace('/* '+marker+' */', (root / 'web' / file).read_text())
html = html.replace('/* APP */', (root / 'web/app.js').read_text())
(root / 'web/index.html').write_text(html)
print('Built standalone companion:', root / 'web/index.html')
