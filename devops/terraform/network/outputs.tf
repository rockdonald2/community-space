output "vpc_id" {
  value = digitalocean_vpc.cs-vpc.id
}

output "vpc_range" {
  value = digitalocean_vpc.cs-vpc.ip_range
}

output "vpc_name" {
  value = digitalocean_vpc.cs-vpc.name
}

output "vpc_urn" {
  value = digitalocean_vpc.cs-vpc.urn
}

output "vpc_region" {
  value = digitalocean_vpc.cs-vpc.region
}
