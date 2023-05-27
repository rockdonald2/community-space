terraform {
  backend "local" {
    path = "../state/registry.tfstate"
  }
}