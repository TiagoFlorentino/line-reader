#!/bin/bash

# Exit on any error
set -e

# Check for Maven
if ! command -v mvn &> /dev/null
then
    echo "❌ Apache Maven is not installed or not in your PATH."
    echo "➡️  Please install Maven: https://maven.apache.org/install.html"
    exit 1
fi

echo "✅ Maven found: $(mvn -v | head -n 1)"

# Build the project
echo "🚧 Building the project with Maven..."
mvn clean package

echo "✅ Build complete."