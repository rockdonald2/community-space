#!/bin/bash

helm repo update
helm upgrade --install community-space . -n community-space --create-namespace -f values.yaml