run-dist:
	make -C run-dist

build:
	make -C app build

clean:
	make -C app clean

test:
	make -C app test

report:
	make -C app report

lint:
	make -C app lint

sonar:
	make -C app sonar

start:
	make -C app start

install:
	make -C app install

setup:
	make -C app setup

run:
	make -C app run

.PHONY: build
