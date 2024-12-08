echo "Cleaning the project..."
./mvnw clean

echo "Compiling the project..."
./mvnw compile

echo "Packaging the project..."
./mvnw clean package

echo "Rebuilding Docker images..."
docker-compose build

echo "Starting the application..."
docker-compose up -d