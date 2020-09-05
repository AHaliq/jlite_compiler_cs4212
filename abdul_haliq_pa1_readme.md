# Assignment 1

**Name:** Abdul Haliq S/O Abdul Latiff
**No.:** A0125431U

## User Guide

Use the makefile to run the project

```
make
```
default make is build

### build
```
make build
```

generates java file from `jlite.cup` and `jlite.flex` and compiles the `App.java` with them.

### clean
```
make clean
```

deletes all generated files

### run
```
make run FILE=<full_path_to_input_file>
```

run the parser with an input file

*ensure the project has been built with `make build` prior to running this*

### test

```
make test
```

runs the test suite in the test directory

it iterates through all files in the `in` directory and looks for matching file prefix but with `.out` extension to `diff` with.

no output `diff` is a successful test.

*ensure the project has been built with `make build` prior to running this*

## Developer Guide

This project first generates the java file from the `.cup` file to create symbols used in `.flex` for tokenization

The runner class `App.java` creates an instance of the `parser.java` from `.cup` and pairs it with the scanner `Lexer.java` from `.flex`

The grammar in `.cup` has actions to build an AST.

The AST is then rendered in the runner class before terminating.

### Project Structure

* `bin` is where the compiled java is stored
* `lib` is where cup dependency jars are stored
* `src` is where the code is stored
  * `src/javasrc` is where generated java files from `.cup` and `.flex` are stored
  * `src/ast` is the package for AST classes
  * `App.java` is the main class
* `tests` is where the test cases are stored

### Environment

```
$ lsb_release -a
No LSB modules are available.
Distributor ID: Ubuntu
Description:    Ubuntu 16.04.6 LTS
Release:        16.04
Codename:       xenial
$ jflex --version
This is JFlex 1.8.2
$ java --version
openjdk 14.0.2 2020-07-14
OpenJDK Runtime Environment (build 14.0.2+12-46)
OpenJDK 64-Bit Server VM (build 14.0.2+12-46, mixed mode, sharing)

```