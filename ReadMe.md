# Build docker images
```
sbt assembly
docker build -t senz/sdbl-bal .
```

# Run with docker
```
docker run -it \
-e SWITCH_HOST=dev.localhost \
-e SWITCH_PORT=9090 \
-e EPIC_HOST=220.247.245.88 \
-e EPIC_PORT=8200 \
-e CASSANDRA_HOST=10.2.2.23 \
-e CASSANDRA_PORT=9042 \
-v /home/eranga/sdbl/logs:/app/logs:rw \
-v /home/eranga/sdbl/keys:/app/.keys:rw \
senz/sdbl-bal
```