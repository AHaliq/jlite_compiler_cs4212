SELECTION MAP GENERATOR
=========================

noopt
	selection map per variable a running index (no optimization)

control flow graph
	given a function IR3, construct control flow graph where code block is a line of code
	is an edge list where vertices are a line of code
	- iterate each line
	- if label encounter
		- add it to a map label to line number
		- check map of vertices from and create edge, then delete the entry
	- if goto encounter
		- lookup in map to make edge
		- if doesnt exist add map of label to list of vertices to origin from

def use
	is a edge list
	here (u,v) u is a def of a variable; write access, v is a use of a variable; read access
	def/use includes first 4 arguments
	remaining arguments are given negative index to denote above frame pointer (stack args)
	
web
	each def is initialized to a web
	for each (u,v) where u is the def in def use, union all vertices in path from u to v
	
live range
	sort vertex according web by line number to generate chronological ranges
	union live ranges of the same variable to a single live range as they dont intersect

interference graph
	graph of liveranges / variable name
	an edge exist if they intersect

selection
	chaitin graph colouring on interference graph
	calculate depth(I) = nested level within loops
	calculate cost(v) = sum for all I, v is def and used in I. 10^depth(I)
	let r = 4, r0-r3
	haifa colour spill algorithm
		on spill thus remove the vertex and all edges to it
	generate map between variable name and register/spill index

OBJECT SIZE MAP
===============

for each class add to the map the size to be used when malloc calculated from IR3 definition,
getter will multiply by 4 for variable size assuming ptrs and all variable types are 4

ARM RENDERER
==============

instruction render
	string labels
		- when encounter string literal add to collector and get label and use it

	string concat
		- use strlen(L1) for L1 ascii and L2
		- sum and malloc for concatted variable string

	spill mechanism
		- r = 5, r4-r8
		- register/spill index > r are spilled colours
		- save all colours onto stack relative to frame pointer and register/spill index
		- use r0-r3 to do operations
		- load spill variables to r0-r3 on read
		- store spill variables to memory on write

	function prologue
		- stmfd sp!,{fp,lr,v1,v2,v3,v4,v5}
  		- add fp,sp,#24
		- sub sp,fp,#(24+4*spill)
		- mov arguments r0-r3 to register variable or memory depending on their colour

	function epilogue
    - generate .exit branch
    - for main on vacuous Return, mov a0, #0
    - pop stack
	    - sub sp,fp,#24
	    - ldmfd sp!,{fp,pc,v1,v2,v3,v4,v5}
	
	caller setup
    - load / mov arguments to a0-a3
    - stmfd sp!, {a0-a3}, if to be added to stack
    - mov first 4 arguments to a0-a3
    - make the bl
	
	caller teardown

	memory pointer variables
		- initiated via malloc



global backend program
	generate object size map

	generate selection map (optimize vs nonopt)

	initialize string literal collector

	render functions
		- instruction render
		- carry string literal collector statefully

  render string literals collected

===
TODO

start render instructions
  - figure out malloc when encounter string concat or new
  - figure out string literal container when encounter string literals
  - figure out spilling when encounter read write to spilled variables