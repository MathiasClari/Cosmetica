@echo on
git checkout master
call .\gradlew build
@echo on
git checkout 1.19
call .\gradlew build
@echo on
git checkout 1.19.1
call .\gradlew build
@echo on
git checkout 1.19.3
call .\gradlew build
@echo on
git checkout 1.18.1
call .\gradlew build
@echo on
git checkout 1.17
call .\gradlew build
@echo on
git checkout 1.16.5
call .\gradlew build
@echo on
git checkout master