# Assignment 1

**Name:** Abdul Haliq S/O Abdul Latiff
**No.:** A0125431U

> User Guide is unchanged, please refer to [pa1 readme](abdul_haliq_pa1_readme.md) for more details on running the program

## Distinct Name Checking

How it is implemented is the nonterminal class now has a nullable `name` field and a nullable list of `nc` name checkers.

The name checkers can use `getName` on other nonterminals to perform name checking logic. The name checkers will be executed as a last step of a nonterminal construction. An exception is thrown when the checker fails.

A higher order checker is implemented to repeat the logic for class name checks, variable declaration checks and parameter definition checks.

The higher order checker uses a `HashSet` to store the names retrieved from `getName` and check if duplicates occur.

### Overloaded Method Declaration check

The checker for overloaded method declaration works similar to the higher order checker with the exception that instead of just the name being stored in the hash set, a string is generated from the method's `FmlList`. Thus a method defined as:

```
Void f(Int i, String j) {...}
```

will generate the string `f, Int, String -> Void`.

These strings will be used as keys for the hash set.

Thus duplicates will only be detected on the order of the types of parameters.

Do note functions of the same name and same parameter and return type despite different parameter names will still be detected as a duplicate.

## Type Checking

We implement type checking similarly with a list of lambda functions to plug in to the production rules specified in CUPs.

To perform type checking we used the hashmaps to simulate the class descriptor and local environment in the type introduction rules.

The hash maps are initialized after the AST has been built via the `InitTypeCheckObjects` class.

To define types for methods we made a `MethodSignature` class. However for simplicity sake, type checking lambdas pass around a string which could be
both a value type or a method type. This is required as the production rules in assignment 1 did not localize atoms that are methods into its own production rule. `MethodSignature` then generates a type signature string which will be used as return value, and lambdas expecting a method signature will parse the string to get the types of the method signature. This encoding and decoding via strings can be avoided if we implement an algebraic data type for both kinds of types and use that for the return type of the lambdas. Performance can also be improved by encoding the types to integers and we can do comparison via integers rather than strings.

We also used null in java to indicate null in jlite. When comparing types, if either is null, we declare it as a match.