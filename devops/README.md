# CS DevOps

Kitelepítési folyamat:

1. A forráskód (bármely `js, jsx, ts, tsx, java` állomány) módosítása és commit-olása után a projektek automatikusan build-elésre kerülnek a `ci/` mappában található `bash` script-ek használatával `GitLab`-on.
   - Feltölti a létrejött `Docker image`-eket a projekt `GitLab registry`-jébe.
2. A K8S cluster létrehozásához a `terraform/` mappába található két specifikációt a `network`-ot a VPC-ért, és a `cluster`-t a cluster-ért szükséges lefuttatni/build-elni egy DigitalOcean kontóra:

```shell
terraform plan -out apply.tfplan`
terraform apply apply.tfplan
# mindkét specifikációra, szükséges hozzá egy DigitalOcean token
```

3. Végül a projekt a `Helm Chart`-al telepíthető a létrejött clusteren (először a clusterhez szükséges csatlakozni és a `kubeconfig`-t megfelelően beállítani):

```shell
cd helm/
bash deploy.sh # vagy helm install community-space -n community-space --create-namespace -f values.yaml
```
