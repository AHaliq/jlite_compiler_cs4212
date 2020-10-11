# Assignment 1

**Name:** Abdul Haliq S/O Abdul Latiff
**No.:** A0125431U

> User Guide is unchanged, please refer to [pa1 readme](abdul_haliq_pa1_readme.md) for more details on running the program

## Changes in previous features

There is an error in my assignment 1 implementation where statements of function calls with no arguments are not handled. This is now fixed.

## Distinct Name Checking

How it is implemented is the nonterminal class now has a nullable `name` field and a nullable list of `nc` name checkers.

The name checkers can use `getName` on other nonterminals to perform name checking logic. The name checkers will be executed as a last step of a nonterminal construction. An exception is thrown when the checker fails.

A higher order checker is implemented to repeat the logic for class name checks, variable declaration checks and parameter definition checks.

The higher order checker uses a `HashSet` to store the names retrieved from `getName` and check if duplicates occur.

### Overloaded Method Declaration check

The checker for overloaded method declaration works similar to the higher order checker with the exception that instead of just the name being stored in the hash set, ~~a string is generated from the method's `FmlList`~~ a `MethodSignature` instance is constructed and it generates the type signature string. This along with the method name. Thus a method defined as:

```
Void f(Int i, String j) {...}
```

will generate the string ~~`f, Int, String -> Void`~~ `f :: Int -> String -> Void`.

These strings will be used as keys for the hash set.

Thus duplicates will only be detected on the order of the types of parameters.

Do note functions of the same name and same parameter and return type despite different parameter names will still be detected as a duplicate.

## Type Checking

We implement type checking similarly with a list of lambda functions to plug in to the AST in the action for the production rules specified in CUP.

To perform type checking we used hashmaps to simulate the class descriptor and local environment in the type introduction rules.

The hash maps are initialized after the AST has been built via the `InitTypeCheckObjects` class in the type check lambda for program. Thus it is possible to manually create a class descriptor and local environment and probe type check different parts of the tree rather than from the root program node.

To define types for methods we made a `MethodSignature` class. However for simplicity sake, type checking lambdas pass around a string which could be
both a value type or a method type. This is required as the production rules in assignment 1 did not localize atoms that are methods into its own production rule.

`MethodSignature` then generates a type signature string which will be used as return value, and lambdas expecting a method signature will parse the string to get the types of the method signature. This encoding and decoding via strings can be avoided if we implement an algebraic data type for both kinds of types and use that for the return type of the lambdas. Performance can also be improved by encoding the types to integers and we can do comparison via integers rather than strings.

Lastly we could also improve modularity by storing keywords such as `this` and `main` into an enum rather than typing them literally in `TypeCheck.java`

We also used null in java to indicate null in jlite. When comparing types, if either is null, we declare it as a match. This is done via the `teq` function.

## IR3 Generation

We implement IR3 generation with the same methodology using lambdas and composing them in the CUP definition.

A state object is used and passed between lambdas to keep track of render information such as temporary and label ids.

The program lambda is being used to generate the layout of the code, thus it is the most bloated lambda.

Everything else however complies to the composable small reusable functions paradigm.

### Getting `this` for function calls

To get `this` we need to IR3 render the atom before the parantheses of the calling node.

However doing this will lead to an IR3 chain of the last temporary variable be the function itself.

To stop rendering one step short and to get the reference to the object we use for this, a hack was implemented in the form of a boolean flag `stopIdentifierRender` which in the atom dot lambda will capture the value and reset the flag, and if the value is true after evaluating the sub atoms it will refrain from continuing the chain of assigning the temp variable to the id. Thus we are able to prevent the last evaluation node and capture the value for `this`.