DIR:=$(strip $(dir $(realpath $(lastword $(MAKEFILE_LIST)))))

build:
	@java -jar $(DIR)/lib/java-cup-11b.jar -destdir $(DIR)/src/javasrc/cup $(DIR)/src/jlite.cup
	@jflex -d $(DIR)/src/javasrc/jflex $(DIR)/src/jlite.flex
	@javac -cp $(DIR)/lib/java-cup-11b.jar -sourcepath $(DIR)/src -d $(DIR)/bin $(DIR)/src/App.java

run:
	@java --class-path $(DIR)/bin:$(DIR)/lib/java-cup-11b.jar App $(FILE)

clean:
	@rm -rf $(DIR)/bin
	@rm -rf $(DIR)/src/javasrc/cup
	@rm -rf $(DIR)/src/javasrc/jflex
	@mkdir $(DIR)/src/javasrc/cup
	@mkdir $(DIR)/src/javasrc/jflex
	@mkdir $(DIR)/bin

test: $(DIR)/tests/in/*
	@for file in $^ ; do \
		echo "\n"test case: $${file##*/} ; \
		java --class-path $(DIR)/bin:$(DIR)/lib/java-cup-11b.jar App $${file} > out; \
		diff out $(DIR)/tests/out/$$(echo $${file##*/} | cut -f 1 -d '.').out ; \
	done
	@rm out