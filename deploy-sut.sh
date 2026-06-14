#!/usr/bin/env bash
# deploy-sut.sh  -  Deploy full-teaching SUT stack (Linux/Mac)
# Usage:   ./deploy-sut.sh          (uses local.env, TJOB_NAME=local)
#          ./deploy-sut.sh down     (stop)
# Requires: Docker with Compose plugin

ACTION="${1:-up}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/local.env"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"
LOCAL_OVERRIDE="$SCRIPT_DIR/docker-compose.local.yml"
NETWORK="jenkins_network"
# Read TJOB_NAME from local.env
TJOB_NAME="$(grep '^TJOB_NAME=' "$ENV_FILE" | cut -d= -f2 | tr -d '[:space:]')"

step() { printf "\n\033[36m==> %s\033[0m\n" "$*"; }
ok()   { printf "    \033[32mOK: %s\033[0m\n" "$*"; }
fail() { printf "    \033[31mFAIL: %s\033[0m\n" "$*"; exit 1; }

if [ "$ACTION" = "down" ] || [ "${1:-}" = "down" ]; then
    step "Stopping SUT stack ($TJOB_NAME)"
    docker compose -f "$COMPOSE_FILE" -f "$LOCAL_OVERRIDE" --env-file "$ENV_FILE" -p "$TJOB_NAME" down > /dev/null 2>&1
    ok "Stack stopped"
    exit 0
fi

# 1. Ensure network exists
step "Checking Docker network '$NETWORK'"
if ! docker network ls --format '{{.Name}}' | grep -q "^${NETWORK}$"; then
    docker network create "$NETWORK" > /dev/null; ok "Created"
else
    ok "Already exists"
fi

# 2. Build full-teaching image from sut/
step "Building full-teaching image from sut/"
docker compose -f "$COMPOSE_FILE" -f "$LOCAL_OVERRIDE" --env-file "$ENV_FILE" build full-teaching 2>&1 | tail -2
ok "Done"

# 3. Start stack
step "Starting SUT containers (project: $TJOB_NAME)"
docker compose -f "$COMPOSE_FILE" -f "$LOCAL_OVERRIDE" --env-file "$ENV_FILE" -p "$TJOB_NAME" up -d 2>&1 | tail -5
ok "Containers started"

# 4. Wait for app ready
step "Waiting for application (up to 90s)"
for i in $(seq 1 18); do
    sleep 5
    if docker logs "full-teaching-$TJOB_NAME" 2>&1 | grep -q "Started Application"; then
        ok "App ready at https://localhost:5000"
        printf "\n    Run test: mvn test -Dtest=FullTeachingEndToEndEChatTests -DTJOB_NAME=%s -DSUT_URL=https://localhost:5000\n" "$TJOB_NAME"
        printf "    To stop:  ./deploy-sut.sh down\n\n"
        exit 0
    fi
    printf "    %ds...\n" "$((i*5))"
done
fail "App did not start within 90 seconds — check: docker logs full-teaching-$TJOB_NAME"
