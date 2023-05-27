resource "digitalocean_vpc" "cs-vpc" {
  name     = "cs-vpc-dev"
  region   = local.region
  ip_range = "172.31.0.0/26"
}