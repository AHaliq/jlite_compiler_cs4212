DIR:=$(strip $(dir $(realpath $(lastword $(MAKEFILE_LIST)))))
JFLEX:=$(DIR)/lib/jflex-full-1.8.2.jar
CUP:=$(DIR)/lib/java-cup-11b.jar
build:
	@java -jar $(CUP) -destdir $(DIR)/src/javasrc/cup -parser Parser -symbols Sym -nonterms $(DIR)/src/jlite.cup
	@echo "successful cup"
	@echo "1/3\n"
	@java -cp $(DIR)/src:$(JFLEX) jflex.Main -d $(DIR)/src/javasrc/jflex $(DIR)/src/jlite.flex
	@echo "successful flex"
	@echo "2/3\n"
	@javac -cp $(CUP):$(JFLEX) -sourcepath $(DIR)/src -d $(DIR)/bin $(DIR)/src/App.java
	@echo "successful app"
	@echo "3/3\n"

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