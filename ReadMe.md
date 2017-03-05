# Build docker images
```
sbt assembly
docker build -t senz/sdblinq .
```

# Run with docker
```
docker run -it \
-e SWITCH_HOST=dev.localhost \
-e SWITCH_PORT=7070 \
-e EPIC_HOST=124.43.16.185 \
-e EPIC_PORT=8200 \
-v /home/docker/sdbl/inq/logs:/app/logs:rw \
-v /home/docker/sdbl/inq/keys:/app/.keys:rw \
senz/sdblinq:0.1
```

# hosts

## epic
```
# at sdbl
10.100.31.240   8200

# public  
124.43.16.185   8200
```

## switch
```
# sdbl
10.100.31.44    7070

# local
dev.localhost   7070
172.17.0.1      7070
```

