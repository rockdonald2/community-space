# Alkalmazás specifikáció

## Alapötlet

Tudásmegosztásra kihelyezett közösségi munkaplatform a vállalati/egyéb közösségen belüli gyors információmegosztásra és megőrzésre, ahol bárki könnyedén megoszthatja aktuális gondolatait/felhívásait/közléseit egy adott tematikáról, amelyekre mások reagálhatnak és visszajelzést adhatnak.

Kiindulási alap a következő már piacon levő open-source kezdeményezés, amely `Go`-ban került implementálásra [GitHub/usememos](https://github.com/usememos/memos). De akár hasonló valamelyest a Slack és a Teams is.

## Funkcionalitások

Az alkalmazás elvárt funkcionalitásai:

- Felhasználók képesek memo-kat megosztani különböző láthatósági szintekkel: publikus vagy privát (csak a publikáló felhasználó által megtekinthető),
- Ezek a memo-k tartalmazhatnak tetszőleges dolgokat akár képeket, emojikat, akár `Markdown`-t, stb (jelenleg scope-n kívül esik, hogy akár fájlfeltöltést is támogassunk).
- Aznapi vagy bármikori memo-k tetszőleges megtekintése **valós időben**,
- Keresés memo-k között: cím, szöveg alapján és tag-elési funkcionalitás, prioritási szintek,
- Online státusz az aktív felhasználókhoz,
- Aktivitási hőtérkép,
- Felhasználók reagálhatnak a memo-kra reakciókkal,
- Értesítési mechanizmus a platformon történő eseményekre, pl. új memo, reakciók saját memo-kra, 

Értelemszerűen autentikációs funkcionalitások is beépítésre kerülnek.

Kiegészítő funkcionalitások:

- Alkalmazás cluster automatizált létrehozása (`Terraform`),
- Szolgáltatás-skálázhatóság (`K8S és Helm`),
- Cloud platform kitelepítés (`DigitalOcean`),
- `MongoDB és Redis` skálázható (és redundáns) kitelepítései,
- *Opcionális*: biztonsági réteg.

### Technológia összefoglaló

Front-end összefoglaló:

- Nyelv: `tsx`,
- Keretrendszer: `Next.js`,
- Komponenskönyvtár: `MaterialUI`.

Back-end összefoglaló:

- NoSQL adatbázis: `MongoDB`,
- Cache és PubSub megoldás: `Redis`
- Általános back-end keretrendszer: `Spring Boot` (micro-service-k implementálására, pl. Spring JPA, Spring Repository, Spring WebFlux, háromrétegű architektúra),
- Üzenet broker (in-cluster és nem külsős): `Kafka` vagy `RabbitMQ` (nekem több tapasztalatom van az utóbbival),
- Infrastruktúra és DevOps: `Terraform` (K8S cluster létrehozása), `K8S és Helm`.

Kiegészítő technológiák: `WebSocket`, `PubSub`, `Reactive Gateway`.

ℹ️ **Megjegyzés**: opcionális a `Redis`-t, amennyiben nincs szükség nagy mennyiségű cachelési kapacitásra kiválthatja a `Kafka` is funkcionalitását tekintve valamilyen üzenetsoros megoldással kiváltva, azonban egyszerűbb lenne a `Redis`.

## Komponensdiagram

![diagram](diagram.png)

### Diagram-magyarázat

1. Gateway-service

> Az szolgáltatási (API) réteg egyetlen belépési pontja, minden platformbéli interakció ezen keresztül érhető el, általános `routing` szerepkör;  
> Kiegészítő a valósidejű megvalósításhoz, hogy a különböző csatorna-routing-ot is ez a service fogja végezni (jön egy kérés egy adott `WebSocket` csatorna eléréséhez, akkor ez fogja továbbítani a releváns service-hez). Pl. egy ilyen funkcionalitás az online státusz: Redis-ben lehet tárolni az aktív felhasználókat (`Account Mgmt.` által kezelve), amint változik ez a lista (aminek minden bemenetének van egy adott aktivitási lejártja) az üzenet publikálásra kerül a releváns `WebSocket`-re ahhoz, hogy a felhasználó megjelenített aktív felhasználó listája frissüljön.

2. Account-Mgmt. service

> Feladata felhasználók kezelése, beleértve az autentikációt és az aktív felhasználói lista kezelését.
> Karbantartja a listát az aktív felhasználókkal, folyamatosan publish-olva üzeneteket a megfelelő csatornákra.

3. Memo-Mgmt. service

> Feladata a memo-k kezelése, beleértve azok létrehozását, keresését, reakciók menedzsmentjét, stb.

4. Activity-Mgmt. service

> Az aktivitási hőtérkép létrehozásához feladata, hogy "elfogyassza" a platformon történő interakciókat.
> Akár feladata összevonható az `Account-Mgmt.`-el.

5. Notifications service

> Értesítések kezelése.

### Üzenetsor és valósidejűség

Több platformbéli folyamat is van amikor előtérbe kerül a valósidejűség és az üzenetsorok alkalmazása, de elmondhatom azt, hogy egyaránt jelen lesznek szinkron és aszinkron kommunikációs flow-k.

Tipikus szinkron folyamatok:

- autentikáció, felhasználói bejelentkezések, stb., hiszen ekkor szükségünk a válaszra;
- memo-k létrehozása, reakciók, keresés, stb.

Aszinkron flow-k:

- aktív felhasználók: `WebSocket` alapú megvalósítása, hogy minden aktív és bejelentkezett felhasználó folyamatos képet kapjon az éppen jelenlevő felhasználókról; elképzelése, hogy mivel minden platformbéli művelet keresztül megy a `Gateway`-n, ezért az akár publikált egy gyors üzenetet az üzenetsorra, amit kiolvas az `Account Mgmt.` és karbantartja az éppen aktív felhasználók listáját (értsd. tovább publish-olja a megfelelő `WebSocket`-re).
- értesítések: minden platformbéli jelentős tevékenység értesítés formájában közlésre kerül; elképzelés, hogy az ilyen események a releváns service által közlésre kerülnek egy üzenetsorban, és mivel ezek közlése egy aszinkron folyamat ezért kiolvassa a `Notifications svc.` és ugyancsak közli a megfelelő `WebSocket`-re a felhasználóknak. Minden értesítés minden felhasználóhoz megérkezik, aki éppen nem aktív következő bejelentkezéskor kapja azt meg.
- aktivitási hőtérkép: minden platformbéli tevékenység (memo-kkal kapcsolatos) rögzítésre kerül, hogy egy általános hőtérképet jelenítsünk meg az aktivitásról (értsd. GitLab vagy GitHub-szerűen); amikor ilyen tevékenység merül fel a releváns service közli azt az üzenetsorban, amit kiolvas az `Activity Mgmt.` és karbantartja ezt a hőtérképet (értelemszerűen ennek is meglesz a megfelelő csatornája a felhasználóknak).