.PHONY : build run armcompile clean test cuphelp

DIR:=$(strip $(dir $(realpath $(lastword $(MAKEFILE_LIST)))))
JFLEX:=$(DIR)/lib/jflex-full-1.8.2.jar
CUP:=$(DIR)/lib/java-cup-11b.jar
COM:=$(DIR)/lib/commons-text-1.9.jar
COML:=$(DIR)/lib/commons-lang3-3.11.jar
build:
	@java -jar $(CUP) -expect 3 -destdir $(DIR)/src/javasrc/cup -parser Parser -symbols Sym -nonterms $(DIR)/src/jlite.cup
	@echo "successful cup"
	@echo "1/3\n"
	@java -cp $(DIR)/src:$(JFLEX):$(COM):$(COML) jflex.Main -d $(DIR)/src/javasrc/jflex $(DIR)/src/jlite.flex
	@echo "successful flex"
	@echo "2/3\n"
	@javac -cp $(CUP):$(JFLEX):$(COM):$(COML) -sourcepath $(DIR)/src -d $(DIR)/bin $(DIR)/src/App.java
	@echo "successful app"
	@echo "3/3\n"

run:
	@java --class-path $(DIR)/bin:$(CUP):$(COM):$(COML) App $(FILE) $(if $(DEBUG),$(DEBUG),false) $(if $(RENDER),$(RENDER),0) $(if $(OUTPUT),$(OUTPUT),null)
	$(if $(OUT),make armcompile OUT=$(OUT) IN=$(OUTPUT),)

armcompile:
	arm-linux-gnueabi-gcc-5 -o $(OUT) $(IN) --static

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
		java --class-path $(DIR)/bin:$(CUP):$(COM):$(COML) App $${file} $(if $(DEBUG),$(DEBUG),false) $(if $(RENDER),$(RENDER),0) > out; \
		diff out $(DIR)/tests/out/$$(echo $${file##*/} | cut -f 1 -d '.').out ; \
	done
	@rm out

cuphelp:
	@java -jar $(CUP) --help