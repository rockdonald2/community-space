resource "digitalocean_container_registry" "dev-registry" {
  name                   = "cs-dev-registry"
  subscription_tier_slug = "basic"
  region                 = local.region
}
