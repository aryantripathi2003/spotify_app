# spotify app

## App exposes routes to fetch new releases from the spotify api and produce it using kafka and store it in mongodb and index it in elasticsearch to make it possible to search for terms through another route.
## Implemented a manual acknowledgement strategy for kafka using custom consumer rebalance listener and redis to store the offsets and used dead-letter-queues to handle failed messages
## Also implemented custom codecs for json to bson conversions.

## Can run entire project and all services by 
```shell script
docker-compose up
```

Check application.properties for further info on ports and other configs.
(Does not persist data on container restarts.)
### Data persistence is added in kubernetes using statefulsets and persistent volumes

## Can also run the kubernetes cluster directly by
```shell script
kubectl apply -f=./kubernetes/config.yaml
```
(Requires minikube and kubectl)

## Can also run by helm file from helm folder by

### To install helm charts
```shell script
helmfile sync
```

### To uninstall helm charts
```shell script
helmfile sync
```
(Need to change installed to false in helmfile)
