#!/usr/bin/env bash
# deploy.sh  -  Deploy (or tear down) the FullTeaching SUT locally (Linux/Mac). Run from the project root directory.
# Usage:   ./deploy.sh               (uses local.env, TJOB_NAME=local; builds the image and deploys)
#          ./deploy.sh --no-build    (skip the image build)
#          ./deploy.sh --down        (tear down the local deployment)
# Requires: Docker with Compose plugin

DOWN=false
SKIP_BUILD=false
for arg in "$@"; do
    case "$arg" in
        --down) DOWN=true ;;
        --no-build) SKIP_BUILD=true ;;
        *) echo "Unknown argument: $arg. Valid options: --no-build, --down"; exit 1 ;;
    esac
done

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

if [ "$DOWN" = true ]; then
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
if [ "$SKIP_BUILD" = true ]; then
    step "Skipping the image build (--no-build)"
else
    step "Building full-teaching image from sut/"
    docker compose -f "$COMPOSE_FILE" -f "$LOCAL_OVERRIDE" --env-file "$ENV_FILE" build full-teaching 2>&1 | tail -2
    ok "Done"
fi

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
        printf "    To stop:  ./deploy.sh --down\n\n"
        exit 0
    fi
    printf "    %ds...\n" "$((i*5))"
done
fail "App did not start within 90 seconds — check: docker logs full-teaching-$TJOB_NAME"
