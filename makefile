DIR:=$(strip $(dir $(realpath $(lastword $(MAKEFILE_LIST)))))

build:
	java -jar $(DIR)/lib/java-cup-11b.jar -destdir $(DIR)/src/javasrc/cup $(DIR)/src/jlite.cup
	javac -d ${DIR}/bin $(DIR)/src/App.java

run:
	java --class-path $(DIR)/bin App

clean:
	rm -rf $(DIR)/bin
	rm -rf $(DIR)/src/javasrc/cup
	mkdir $(DIR)/src/javasrc/cup
	mkdir $(DIR)/bin