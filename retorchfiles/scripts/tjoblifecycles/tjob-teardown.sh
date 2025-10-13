#!/bin/bash
set -e

# Execute the script to write timestamp
"$SCRIPTS_FOLDER/writetime.sh" "$2" "$1"

# Store docker logs
DIRECTORY_PATH="$WORKSPACE/target/containerlogs/$1"
EXEC_PATH="$WORKSPACE/target/coverage/execfiles"
COVERAGE_PATH="$WORKSPACE/target/coverage"

if [ ! -d "$DIRECTORY_PATH" ]; then
  echo "Directory for storing logs doesnt exist creating..."
  mkdir -p $DIRECTORY_PATH
fi

if [ ! -d "$EXEC_PATH" ]; then
  echo "Directory for storing coverage execs doesnt exist creating..."
  mkdir -p $EXEC_PATH
fi

for CONTAINER_NAME in $(docker ps -a --format "{{.Names}}" --filter Name=$1); do
  echo "Storing lof con container $CONTAINER_NAME in TJob $1"
  docker logs $CONTAINER_NAME &>"$DIRECTORY_PATH/$CONTAINER_NAME.log"
done

# Change to SUT location
cd "$SUT_LOCATION"
# Generate jacoco.exec for then calculate coverage
docker exec "full-teaching-$1" java -jar org.jacoco.cli-0.8.13-nodeps.jar dump --address localhost --port 6300  --destfile /jacoco.exec

#extract the jacoco.exec and save into the target folder

docker cp "full-teaching-$1:/jacoco.exec" "$EXEC_PATH/jacoco-$1.exec"

# Comprobamos si existe el directorio que usaremos a posteriori para sacar el coverage:

# Comprobar si el JAR existe en el directorio destino
if [ ! -f "$COVERAGE_PATH/app.jar" ]; then
    echo "app.jar no existe en $COVERAGE_PATH. Creando directorio y copiando..."

    # Crear directorio si no existe
    mkdir -p "$COVERAGE_PATH"

    # Copiar el JAR
    docker cp "full-teaching-$1:/app.jar" "$COVERAGE_PATH/app.jar"

    echo "app.jar copiado correctamente."

    echo "Uncompressing the JAR!"

    cd "$COVERAGE_PATH"

    jar xf "$COVERAGE_PATH/app.jar"

    # Obtenemos el jar para calcular la cobertura
    wget "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.13/org.jacoco.cli-0.8.13-nodeps.jar"
    wget "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.13/org.jacoco.cli-0.8.13.jar"
    mkdir classes

    mv "$COVERAGE_PATH/WEB-INF/classes" "$COVERAGE_PATH"


    cd "$SUT_LOCATION"

else
    echo "app.jar ya existe en $COVERAGE_PATH. No se hace nada."
fi

# Tear down Docker containers and volumes
echo "Tearing down Docker containers and volumes for TJOB $1"
docker compose -f docker-compose.yml --env-file "$WORKSPACE/retorchfiles/envfiles/$1.env" --ansi never -p "$1" down --volumes

# Return to the original working directory
cd "$WORKSPACE"


# Execute the script to write timestamp again
"$SCRIPTS_FOLDER/writetime.sh" "$2" "$1"
