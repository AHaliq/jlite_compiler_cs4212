# Assignment 1

**Name:** Abdul Haliq S/O Abdul Latiff
**No.:** A0125431U

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