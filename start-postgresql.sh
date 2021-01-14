#!/bin/bash
docker run -d \
    -p 5432:5432 \
    --name local-postgres \
    -e POSTGRES_USER=testdb \
    -e POSTGRES_PASSWORD=123 \
    -e POSTGRES_DB=testdb \
    postgres:9.6.20-alpine