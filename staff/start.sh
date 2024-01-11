#!/usr/bin/env bash

kubectl apply -f mysql-deployment.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f mysql-pvc.yaml
kubectl apply -f mysql-secret.yaml

