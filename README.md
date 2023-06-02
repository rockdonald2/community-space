# Community Space

[![pipeline status](https://gitlab.com/rockdonald2/community-space/badges/main/pipeline.svg)](https://gitlab.com/rockdonald2/community-space/-/commits/main)

Ez a projekt tartalmazza a `Community Space` nevű projekt forráskódját minden platformra nézve.

## Kódszerkezet

A kód 3 részben van szétbontva:

1. `backend` mappa tartalmazza a projekt micro-service-einek kódját, jelenleg 5 ilyen projekt van ezen belül;
2. `frontend` mappa tartalmazza a `NextJS` front-end kódot;
3. `devops` mappa tartalmazza a `Terraform`, `Helm`, és `CI/CD` kódrészleteket, amelyek megvalósítják a folyamatos fejlesztést (minden commit után automatikus build és Docker deploy folyamatok), IaC a K8S cluster létrehozásához és Helm a könnyű K8S kitelepítésért.
