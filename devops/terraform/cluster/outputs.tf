output "cluster_name" {
  value = digitalocean_kubernetes_cluster.cs-dev.name
}

output "cluster_subnet" {
  value = digitalocean_kubernetes_cluster.cs-dev.cluster_subnet
}

output "cluster_service_subnet" {
  value = digitalocean_kubernetes_cluster.cs-dev.service_subnet
}

output "cluster_region" {
  value = digitalocean_kubernetes_cluster.cs-dev.region
}

output "cluster_endpoint" {
  value = digitalocean_kubernetes_cluster.cs-dev.endpoint
}

output "cluster_kubeconfig" {
  value     = digitalocean_kubernetes_cluster.cs-dev.kube_config
  sensitive = true
}

output "cluster_urn" {
  value = digitalocean_kubernetes_cluster.cs-dev.urn
}
