
# Assignment 3

# User Guide

In addition to user guide from previous assignments, the `make run` command now also has two new parameters
- `OUTPUT` if specified will output ARM code as a file with the argument as path
- `OUT` must be specified with `OUTPUT` will compile the outputted file to an ARM executable at the argument path

Note that `make test` is not used to run ARM code due to the nature of my development environment. (I have gem5 running on a separate box as my dev environment.. dont ask why)

Example use
```sh
make clean && make
make run FILE=./tests/asg3_tests/asg3_merry_xmas.j OUTPUT=./xmas.s OUT=./xmas
echo 'run executable xmas on gem5'
```

# Developer Guide

## Changelog
1. removed label reset between functions
2. fix not rendering implicit this in IR3
3. fix CUP was capturing string concat as an AExp and was typechecked as Int, thus string concats always fail
4. null values originally were given `null` type which is invalid, now parent expression propagates their type e.g. a string assigned to null will pass its type string to the null node.

## Noteworthy features
1. Implicit this, IR3 now generates code for this when it is omitted in jlite, this is done by storing each local variable when rendering the IR3 for local variable declaration into the state as `s.localVars`. In a higher order function `genNonLocal` it will detect if the identifier is non local and generate the IR3 for this using the type for this in `s.thisType`. This is done in identifier renderer when the non local is a term to be read and when the identifier is a field that is to be written.
2. String concatenation is done by overloading the arithmetic add IR3 format as the IR3 statement is ambiguous without typing data. Given that the IR3 at the start declares the type for all variables, we can derive the type of the operand without typing data in assignment 2. We pass this to the renderer and in the add statement ARM render it will detect if it is a string or an int. If a string it will use `strlen` to calculate the string length of both operand strings and sum them, then malloc memory the amount of the sum and use `strcat` to create a concatenation of the two strings.
3. String literals when encountered during rendering generates a label via the state. Thus we can populate all the string data as we render them without having to make a second pass.
4. Argument spilling is done in the function call ARM render when there are more than arguments including this then there are argument registers. One pain point during development is that the left most register in the `stmfd` instruction is the top of the stack, not the intuitive right most register.
5. When null is encountered in rendering ARM, it is treated as an empty string, even for the case of object variables. This is ok as we do not print object and thus it could be garbage anyways.

## Design

### Optimization

Note, i didn't have time to implement register allocation optimization even though i have planned my architecture around easily integrating it with or without optimization. My intention was to use a variant of Chaitin colouring called Haifa best of three algorithm.

At the end of the process will be a `HashMap` called selection map. This will map a variable to a register or stack memory address via an index. Default without optimization uses `noopt` to generate an index per variable.

If optimization were implemented there will be variables sharing the same register without interference. Thus no changes is needed to the ARM rendering code, the optimizer will only have to give an optimized selection map rather than the one produced by `noopt`.

Below are the classes / components to facilitate it

**noopt**
	selection map per variable a running index (no optimization)

**control flow graph**
	given a function IR3, construct control flow graph where code block is a line of code
	is an edge list where vertices are a line of code
	- iterate each line
	- if label encounter
		- add it to a map label to line number
		- check map of vertices from and create edge, then delete the entry
	- if goto encounter
		- lookup in map to make edge
		- if doesnt exist add map of label to list of vertices to origin from

**def use**
	is a edge list
	here (u,v) u is a def of a variable; write access, v is a use of a variable; read access
	def/use includes first 4 arguments
	remaining arguments are given negative index to denote above frame pointer (stack args)
	
**web**
	each def is initialized to a web
	for each (u,v) where u is the def in def use, union all vertices in path from u to v
	
**live range**
	sort vertex according web by line number to generate chronological ranges
	union live ranges of the same variable to a single live range as they dont intersect

**interference graph**
	graph of liveranges / variable name
	an edge exist if they intersect

**selection**
	chaitin graph colouring on interference graph
	calculate depth(I) = nested level within loops
	calculate cost(v) = sum for all I, v is def and used in I. 10^depth(I)
	let r = 4, r0-r3
	haifa colour spill algorithm
		on spill thus remove the vertex and all edges to it
	generate map between variable name and register/spill index

### Rendering

**object size map**

for each class add to the map the size to be used when malloc calculated from IR3 definition

getter will multiply by 4 for variable size assuming ptrs and all variable types are 4

this is used during malloc operations when constructing objects

objects with no field or zero size will elide its malloc code using garbage for the value of this

**arm state**

mainly used to collate string literals to render `.data` section


**instruction render**
- string labels
	- when encounter string literal add to collector and get label and use it
  - when encounter println for int, generate label for ints
  - when encounter println for bool, generate label for bools
- string concat
	- use strlen(L1) for L1 ascii and L2
	- sum and malloc for concatted variable string
- spill mechanism
	- r = 5, r4-r8
	- register/spill index > r are spilled colours
	- save all colours onto stack relative to frame pointer and register/spill index
	- use r0-r3 to do operations
	- load spill variables to r0-r3 on read
	- store spill variables to memory on write
- function prologue
	- stmfd sp!,{fp,lr,v1,v2,v3,v4,v5}
  	- add fp,sp,#24
	- sub sp,fp,#(24+4*spill)
	- mov arguments r0-r3 to register variable or memory depending on their colour
- function epilogue
  - generate .exit branch
  - for main on vacuous Return, mov a0, #0
  - pop stack
	  - sub sp,fp,#24
	  - ldmfd sp!,{fp,pc,v1,v2,v3,v4,v5}
- caller setup
  - load / mov arguments to a0-a3
  - stmfd sp!, {a0-a3}, if to be added to stack
  - mov first 4 arguments to a0-a3
  - make the bl
- memory pointer variables
	- initiated via malloc

**global backend program**
- generate object size map
- generate selection map (optimize vs nonopt)
- initialize string literal collector
- render functions
	- instruction render
	- carry string literal collector statefully
- render string literals collected

### Architecture

We have `Backend.java` that starts the backend process splitting the IR3 code data and mthds. Each method then has its own rendering function which calls `IR3Parser.java` to parse into `IR3StmtParse.java` which is then used in `ArmRender.java` to render each IR3 statement to its corresponding ARM code.

The data structures used in rendering are created in `Backend.java` constructing objects via `RegisterAllocation.java`, `ObjectMaps.java` and `ArmState.java`.

#### IR3StmtParse

statement parsing is done in `IR3Parser.java`, it goes through a sequence of regexp to match what type of IR3 code it is. The capture groups then store the relevant data needed to render ARM code.

#### ArmRender

statement rendering is via a giant switch statement of the enum specified in `IR3StmtParse.java`. Higher order renderers are there to reduce code repitition and increase code reuse e.g. `functionCall(...), binOp(...)`, in the same style as assignment 1 and 2.

The overall architecture can be seen as striving for purely functional with effectful state objects passed around.

## room for improvement

If given foresight in the assignments to come, I would had explictly create rich data structures for each intermediate representation and group of data rather than immediately rendering output strings. This is evident when we need to reparse IR3 strings to render ARM.

I would also avoid hacking `NonTerminal` class with easy to access variables and put them into the state carrying object for the algorithm it concerns.

Beyond optimization we could also implement coelescing to reduce transitive register assignments.