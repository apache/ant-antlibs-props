# Apache Props Antlib™

## Overview

This is a library of supplementary handlers for Apache Ant properties
resolution.

The types provided are instances of
`org.apache.tools.ant.PropertyHelper.Delegate` and can be invoked
using the `<propertyhelper>` task provided in Ant 1.8.0.

For more information see the [docs](docs/index.html).

## Building

Running `ant antlib` will create the antlib in `build/lib`. Any
version of Ant starting with 1.8.0 should work - some parts of the
build used during the release process require Ant 1.10.x.

The code itself should work with Java 1.4, it will compile with the
javac target set to Java 8 but if you invoke Ant with
`-Djavac.-source=1.4 -Djavac.-target=1.4`  (and use a JDK that is
still willing to support 1.4 as target) you should be able to build
for older versions of Java.
