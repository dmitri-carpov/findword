# findword

findword scans the given directory recursively and finds all files which contain the searched word.


Environment
-----------

Required
 * Java 1.7 or later
 * sbt 0.13 or later

For Ubuntu simply run:

```bash
$ checkenv
```

Running
-----------

```bash
./findword <word> <path to directory>
```

or

```bash
$ sbt "run <word> <path to directory>"
```

Testing
-----------

```bash
sbt test
```