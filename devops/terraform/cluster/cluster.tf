resource "digitalocean_kubernetes_cluster" "cs-dev" {
  name   = "cs-dev"
  region = local.region

  version       = "1.25.8-do.0"
  auto_upgrade  = false
  surge_upgrade = false
  vpc_uuid      = data.digitalocean_vpc.cs-vpc.id
  ha            = false

  node_pool {
    name       = "worker-pool"
    size       = "s-2vcpu-4gb"
    auto_scale = true
    min_nodes = 1
    max_nodes = 3
  }

  maintenance_policy {
    start_time = "02:00"
    day        = "sunday"
  }

  depends_on = [data.digitalocean_vpc.cs-vpc]
}
