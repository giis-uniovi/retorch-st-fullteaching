#!/bin/bash

# ==============================
#  Instalador automático de herramientas
#  npm, Java, Maven y Angular CLI
# ==============================

echo "🚀 Iniciando comprobación de entorno de desarrollo..."

# Función para verificar si un comando existe
check_command() {
    command -v "$1" >/dev/null 2>&1
}

# ==============================
# NPM
# ==============================
echo "🔍 Comprobando npm..."
if check_command npm; then
    echo "✅ npm ya está instalado (versión $(npm -v))"
else
    echo "⚙️ npm no está instalado. Instalando Node.js + npm..."
    # Dependiendo del sistema operativo:
    if check_command apt; then
        sudo apt update
        sudo apt install -y nodejs npm
    elif check_command dnf; then
        sudo dnf install -y nodejs npm
    elif check_command brew; then
        brew install node
    else
        echo "❌ No se pudo determinar el gestor de paquetes. Instala Node.js manualmente desde https://nodejs.org/"
        exit 1
    fi
    echo "✅ npm instalado correctamente (versión $(npm -v))"
fi

# ==============================
# Java (JDK)
# ==============================
echo "🔍 Comprobando Java..."
if check_command java; then
    echo "✅ Java ya está instalado (versión $(java -version 2>&1 | head -n 1))"
else
    echo "⚙️ Java no está instalado. Instalando..."
    if check_command apt; then
        sudo apt update
        sudo apt install -y default-jdk
    elif check_command dnf; then
        sudo dnf install -y java-17-openjdk-devel
    elif check_command brew; then
        brew install openjdk
    else
        echo "❌ No se pudo determinar el gestor de paquetes. Instala Java manualmente desde https://adoptium.net/"
        exit 1
    fi
    echo "✅ Java instalado correctamente"
fi

# ==============================
# Maven
# ==============================
echo "🔍 Comprobando Maven..."
if check_command mvn; then
    echo "✅ Maven ya está instalado (versión $(mvn -v | head -n 1))"
else
    echo "⚙️ Maven no está instalado. Instalando..."
    if check_command apt; then
        sudo apt update
        sudo apt install -y maven
    elif check_command dnf; then
        sudo dnf install -y maven
    elif check_command brew; then
        brew install maven
    else
        echo "❌ No se pudo determinar el gestor de paquetes. Instala Maven manualmente desde https://maven.apache.org/"
        exit 1
    fi
    echo "✅ Maven instalado correctamente"
fi

# ==============================
# Angular CLI
# ==============================
echo "🔍 Comprobando Angular CLI..."
if check_command ng; then
    echo "✅ Angular CLI ya está instalado (versión $(ng version | grep 'Angular CLI:' | awk '{print $3}'))"
else
    echo "⚙️ Angular CLI no está instalado. Instalando globalmente..."
    sudo npm install -g @angular/cli@19.2.17
    echo "✅ Angular CLI instalado correctamente (versión $(ng version | grep 'Angular CLI:' | awk '{print $3}'))"
fi




echo "🔍 Comprobando Python..."
if check_command python3; then
    echo "✅ Python ya está instalado (versión $(python3 --version))"
else
    echo "⚙️ Python no está instalado. Instalando..."
    if check_command apt; then
        sudo apt update
        sudo apt install -y python3 python3-pip
    elif check_command dnf; then
        sudo dnf install -y python3 python3-pip
    elif check_command brew; then
        brew install python
    else
        echo "❌ No se pudo determinar el gestor de paquetes. Instala Python manualmente desde https://www.python.org/downloads/"
        exit 1
    fi
    echo "✅ Python instalado correctamente (versión $(python3 --version))"
fi

echo "🎉 Configuración completada correctamente."