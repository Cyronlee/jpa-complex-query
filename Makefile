db:
	docker-compose up -d db

app:
	./gradlew build -x test && docker-compose up -d app

run:
	./gradlew build -x test && docker-compose up -d

stop:
	docker-compose down

test:
	./gradlew test && open ./build/reports/tests/test/index.html

.PHONY: db app run stop test report
