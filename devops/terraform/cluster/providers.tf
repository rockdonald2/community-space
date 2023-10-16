terraform {
  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
    kubernetes = {
      version = "~> 2.0"
    }
    helm = {
      version = "~> 2.0"
    }
    kubectl = {
      source  = "gavinbunney/kubectl"
      version = ">= 1.7.0"
    }
  }
}

provider "digitalocean" {
  token = var.do_token
}

variable "do_token" {
  type      = string
  nullable  = false
  sensitive = true
}

provider "kubernetes" {
  host                   = digitalocean_kubernetes_cluster.cs-dev.endpoint
  client_certificate     = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].client_certificate)
  client_key             = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].client_key)
  cluster_ca_certificate = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].cluster_ca_certificate)
  token                  = digitalocean_kubernetes_cluster.cs-dev.kube_config[0].token

  exec {
    api_version = "client.authentication.k8s.io/v1beta1"
    command     = "doctl"
    args = ["kubernetes", "cluster", "kubeconfig", "exec-credential",
    "--version=v1beta1", digitalocean_kubernetes_cluster.cs-dev.id]
  }
}

provider "kubectl" {
  load_config_file       = true
  host                   = digitalocean_kubernetes_cluster.cs-dev.endpoint
  token                  = digitalocean_kubernetes_cluster.cs-dev.kube_config[0].token
  cluster_ca_certificate = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].cluster_ca_certificate)
  config_path            = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    host                   = digitalocean_kubernetes_cluster.cs-dev.endpoint
    client_certificate     = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].client_certificate)
    client_key             = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].client_key)
    cluster_ca_certificate = base64decode(digitalocean_kubernetes_cluster.cs-dev.kube_config[0].cluster_ca_certificate)
    token                  = digitalocean_kubernetes_cluster.cs-dev.kube_config[0].token
    config_path            = "~/.kube/config"
  }
}
