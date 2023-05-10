# Alkalmazás specifikáció

## Alapötlet

Tudásmegosztásra kihelyezett közösségi munkaplatform a vállalati/egyéb közösségen belüli gyors információmegosztásra és megőrzésre rövid memo-k formájában, ahol bárki könnyedén megoszthatja aktuális gondolatait/felhívásait/közléseit egy adott tematikáról vagy gondolatról, amelyekre mások reagálhatnak és visszajelzést adhatnak.

Kiindulási alap a következő már "piacon" levő open-source kezdeményezés, amely `Go`-ban került implementálásra [GitHub/usememos](https://github.com/usememos/memos). De akár hasonló valamelyest a Slack és a Teams is.

## Funkcionalitások

### Kollaboratív és stratégiai megfontolások

Az alkalmazás elvárt alapvető funkcionalitásai:

- Felhasználók képesek saját contókat létrehozni a platformhoz való csatlakozáshoz,
- Felhasználók képesek közösségekhez, ún. hub-okhoz csatlakozási kéréseket leadni és adminisztrátor felhasználók ezeket elfogadni,
- Felhasználók képesek saját hub-okat létrehozni,
- Felhasználók képesek memo-kat megosztani különböző láthatósági szintekkel a hub-on belül: publikus vagy privát (csak a publikáló felhasználó által megtekinthető memo),
  - Ezek a memo-k tartalmazhatnak tetszőleges dolgokat akár képeket (külső forrásból), emojikat, akár `Markdown`-t, stb. (jelenleg scope-n kívül esik, hogy akár fájlfeltöltést is támogassunk).
- Aznapi vagy bármikori memo-k tetszőleges megtekintése **valós időben**, alapesetben a felhasználónak kilistázza az éppen aktuális memo-kat prioritásuk és publikálási dátumuk szerint rendezve,
- Keresési funkcionalitás memo-k között: cím és tag-elési funkcionalitás, prioritási szintek,
- Folyamatosan real-time karbantartott "online" státusz az aktív felhasználókhoz (hub-on belül és kívül (értelemszerűen a főoldalon)),
- Aktivitási hőtérkép az aktuális hónapra vetítve informatív jelleggel,
- Felhasználók reagálhatnak, visszajelzést adhatnak a memo-kra reakciókkal és teljesítési kitűzőkkel,
- Értesítési mechanizmus a platformon történő eseményekre, pl. új memo, reakciók saját memo-kra, teljesítés, stb.

### Kiegészítő funkcionalitások

Kiegészítő, technológiai (az alap use-case-n kívül eső) funkcionalitások:

- Kubernetes kitelepítés (`K8S és Helm`),
- Alkalmazás cluster (K8S cluster) automatizált létrehozása (`Terraform`),
- Cloud platform kitelepítés (`DigitalOcean`),
- `MongoDB és Apache Kafka (és/vagy RabbitMQ)` skálázható (és redundáns) kitelepítései,
- `Redis`, mint cachelési megoldás skálázható kitelepítése.

## Komponensdiagram

![diagram](diagram.png)

### Diagram-magyarázat

1. Gateway-service

> A szolgáltatási réteg egyetlen belépési pontja, minden platformbéli interakció ezen keresztül érhető el, általános `routing` szerepkör; közvetíti a bejövő kérést a megfelelő belső micro-service-hez, miközben a felhasználónak egyetlen külső pontot definiálunk;
> Kiegészítő a valós idejű megvalósításhoz, hogy a különböző csatorna-routing-ot is ez a service fogja végezni (jön egy kérés egy adott `WebSocket` csatorna eléréséhez, akkor ez fogja továbbítani a releváns service-hez). Pl. egy ilyen funkcionalitás az online státusz: Redis-ben lehet tárolni az aktív felhasználókat (`Account Mgmt.` által kezelve), amint változik ez a lista (aminek minden bemenetének van egy adott aktivitási lejártja) az üzenet publikálásra kerül a releváns `WebSocket`-re ahhoz, hogy a felhasználó megjelenített aktív felhasználó listája frissüljön valós időben;  
> További funkcionalitás a biztonsági réteg megvalósítása, hiszen ez a micro-service nem fogja a védett útvonalakra beengedni a kérést, amennyiben nem rendelkezik a megfelelő authorizációs token-nel.

2. Account-Mgmt. service

> Feladata felhasználók kezelése és azok létrehozása, valamint kiállítja a megfelelő authorizációs tokeneket,  
> Karbantartja a listát az aktív felhasználókkal, folyamatosan publish-olva üzeneteket a megfelelő csatornákra,  
> Felhasználói jogkörök kezelése.

3. Memo-Mgmt. service

> Feladata a memo-k kezelése, beleértve azok létrehozását, köztük való keresés megvalósítása, kilistázása, stb.

4. Activity-Mgmt. service

> Az aktivitási hőtérkép létrehozásához feladata, hogy "elfogyassza" a platformon történő interakciókat.
> Akár feladata összevonható az `Notifications-Mgmt.`-el.

5. Notifications service

> Értesítések kezelése, a platformon történő érdekes események közlése az éppen aktív felhasználóknak,  
> Események tárolása, amennyiben a felhasználók épp nem aktívak.  

6. Hub-Mgmt. service

> Feladata a hub-ok kezelése, beleértve azok létrehozását, a csatlakozási kérelmek menedzsmentje, kilistázása, stb.

Megtörténhet, hogy az `Activity-Mgmt.` és `Notifications` szolgáltatásait összevonom egyetlen micro-service-ben `Activity-Notifications-Mgmt.` alatt, másképp önmagukban feltételezhetően túl egyszerűek lennének.

### Rövid technológia összefoglaló

Front-end összefoglaló:

- Nyelv: `Typescript`,
- Keretrendszer: `Next.js`,
- Komponenskönyvtár: `MaterialUI`.

Back-end összefoglaló:

- NoSQL adatbázis: `MongoDB`,
- Cache megoldás: `Redis`
- Általános back-end keretrendszer: `Spring Boot` (micro-service-k implementálására, pl. Spring JPA, Spring Repository, Spring WebFlux, háromrétegű architektúra),
- Üzenet/STOMP broker (in-cluster): `Kafka (és/vagy RabbitMQ)`,
- Infrastruktúra és DevOps: `Terraform` (K8S cluster létrehozása), `K8S és Helm`.

Kiegészítő egyéb technológiák: `WebSocket és STOMP`, `Spring Gateway`.

Bizonyos tantárgybéli követelmények miatt máshonnan kötelező a `Kafka` használata és valójában a `RabbitMQ`-ra a `WebSocket` stateful-problémájának megoldására van szükség, ezért lehet eliminálni az összefüggésből a `RabbitMQ`-t és csak `Kafka`-t használni, valamint szerverek között `WebSocket` kapcsolatokat fenntartani, így megoldva a problémát.

### Üzenetsor és valósidejűség

Több platformbéli folyamat is van amikor előtérbe kerül a valósidejűség és az üzenetsorok alkalmazása az aszinkron kommunikáció alkalmazására, de elmondhatom azt, hogy egyaránt jelen lesznek szinkron és aszinkron kommunikációs flow-k is.

Tipikus szinkron folyamatok:

- autentikáció, felhasználói bejelentkezések, stb., hiszen ekkor szükségünk a válaszra;
- memo-k létrehozása, reakciók közlése, listázás, stb.;
- hub-k létrehozása, csatlakozási kérelmek, azok elfogadása;

Aszinkron flow-k:

- Aktív felhasználók: `WebSocket` alapú megvalósítása, hogy minden aktív és bejelentkezett felhasználó folyamatos képet kapjon az éppen jelenlevő felhasználókról (hub-on belül és kívül).
- Értesítések: minden platformbéli érdekes esemény értesítés formájában közlésre kerül az éppen aktív és érintett felhasználóknak; elképzelés, hogy az ilyen események a releváns service által közlésre kerülnek egy üzenetsorban, és mivel ezek közlése egy aszinkron folyamat ezért kiolvassa a `Notifications svc.` és ugyancsak közli a megfelelő `WebSocket`-re a felhasználóknak. Minden értesítés minden érintett aktív felhasználóhoz megérkezik, aki éppen nem aktív következő bejelentkezéskor kapja azt meg.
- Aktivitási hőtérkép: minden platformbéli tevékenység (memo-kkal kapcsolatos) rögzítésre kerül, hogy egy általános hőtérképet jelenítsünk meg az aktivitásról (értsd. GitLab vagy GitHub-szerűen); amikor ilyen tevékenység merül fel a releváns service közli azt az üzenetsorban, amit kiolvas az `Activity Mgmt.` és karbantartja ezt a hőtérképet (értelemszerűen ennek is meglesz a megfelelő csatornája a felhasználóknak).
