terraform {
  backend "local" {
    path = "../state/cluster.tfstate"
  }
}