# deploy-sut.ps1  -  Deploy full-teaching SUT stack (Windows)
# Usage:  .\deploy-sut.ps1            (uses local.env, TJOB_NAME=local)
#         .\deploy-sut.ps1 -Down      (stop)
# Requires: Docker Desktop running

param(
    [switch]$Down
)

$ScriptDir     = Split-Path -Parent $MyInvocation.MyCommand.Path
$EnvFile       = "$ScriptDir\local.env"
$ComposeFile   = "$ScriptDir\docker-compose.yml"
$LocalOverride = "$ScriptDir\docker-compose.local.yml"
$Network       = "jenkins_network"
# Read TJOB_NAME from local.env
$TJobName      = (Get-Content $EnvFile | Select-String "^TJOB_NAME=" | ForEach-Object { $_ -replace "^TJOB_NAME=","" }).Trim()

function Step($msg) { Write-Host "`n==> $msg" -ForegroundColor Cyan }
function OK($msg)   { Write-Host "    OK: $msg" -ForegroundColor Green }
function Fail($msg) { Write-Host "    FAIL: $msg" -ForegroundColor Red; exit 1 }

if ($Down) {
    Step "Stopping SUT stack ($TJobName)"
    docker compose -f $ComposeFile -f $LocalOverride --env-file $EnvFile -p $TJobName down 2>&1 | Out-Null
    OK "Stack stopped"
    exit 0
}

# 1. Ensure network exists
Step "Checking Docker network '$Network'"
$nets = docker network ls --filter "name=$Network" --format "{{.Name}}" 2>$null
if ($nets -notcontains $Network) {
    docker network create $Network | Out-Null; OK "Created"
} else { OK "Already exists" }

# 2. Build full-teaching image from sut/
Step "Building full-teaching image from sut/"
docker compose -f $ComposeFile -f $LocalOverride --env-file $EnvFile build full-teaching 2>&1 | Out-Null
OK "Done"

# 3. Start stack
Step "Starting SUT containers (project: $TJobName)"
docker compose -f $ComposeFile -f $LocalOverride --env-file $EnvFile -p $TJobName up -d 2>&1 | Out-Null
OK "Containers started"

# 4. Wait for app ready
Step "Waiting for application (up to 90s)"
for ($i = 1; $i -le 18; $i++) {
    Start-Sleep 5
    if (docker logs "full-teaching-$TJobName" 2>&1 | Select-String "Started Application") {
        OK "App ready at https://localhost:5000"
        Write-Host "`n    Run test: mvn test -Dtest=FullTeachingEndToEndEChatTests -DTJOB_NAME=$TJobName -DSUT_URL=https://localhost:5000" -ForegroundColor Yellow
        Write-Host "    To stop:  .\deploy-sut.ps1 -Down`n" -ForegroundColor Yellow
        exit 0
    }
    Write-Host "    $([int]($i*5))s..."
}
Fail "App did not start within 90 seconds. Check: docker logs full-teaching-$TJobName"
