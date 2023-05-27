resource "helm_release" "nginx-ingress" {
  name       = "nginx-ingress"
  repository = "https://kubernetes.github.io/ingress-nginx"
  chart      = "ingress-nginx"
  version    = "4.5.2"
  namespace  = "kube-system"
  timeout    = 600

  set {
    name  = "controller.publishService.enabled"
    value = "true"
  }

  depends_on = [digitalocean_kubernetes_cluster.cs-dev]
}

resource "helm_release" "cert-manager" {
  repository = "https://charts.jetstack.io"
  name       = "cert-manager"
  namespace  = "kube-system"
  chart      = "cert-manager"
  version    = "1.11.0"
  timeout    = 600

  set {
    name  = "installCRDs"
    value = "true"
  }

  depends_on = [digitalocean_kubernetes_cluster.cs-dev]
}

resource "helm_release" "sealed-secrets" {
  repository = "https://bitnami-labs.github.io/sealed-secrets"
  name       = "sealed-secrets"
  namespace  = "kube-system"
  chart      = "sealed-secrets"
  version    = "2.7.6"
  timeout    = 600

  set {
    name  = "ingress.enabled"
    value = "false"
  }

  depends_on = [digitalocean_kubernetes_cluster.cs-dev]
}

resource "helm_release" "metrics-server" {
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  name       = "metrics-server"
  namespace  = "kube-system"
  chart      = "metrics-server"
  version    = "3.9.0"
  timeout    = 600

  depends_on = [digitalocean_kubernetes_cluster.cs-dev]
}
