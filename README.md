# microman #

microman automates the process of controlling the microscope,
performing statistics on the result, and then capturing more data from
the microscope.

It is designed to run inside the macro environment of Zeiss Zen, but
also is architected so it could run as a standalone Python application
in the future to support other microscopes.

## Building and Installing the ZEN macro ##

The supported way to install this project is at the commandline, not
from inside Microsoft Visual Studio.

### Prequisites ###

Install Microsoft Visual Studio Professional 2017.  Make sure the
".NET Development Environment" package is installed.

The following commands should be run from the "x64 Native Tools for
Visual Studio 2017" command prompt.

Change to the `{project_root}\zen` directory.

### Building ###

Build the extension by running `build.bat`.

**NOTE:** There is no way to reload an assembly in a running ZEN
instance, so you will need to shutdown ZEN and restart it every time
you rebuild and install the assembly.

### Installing ###

Install the extension by running `install.bat`.  It will install it
for the current user.

## Building and installing the standard Python library ##

This is useful mainly for testing and development so we don't have to
shutdown/restart Zeiss Zen every time we need to test a change.

### Prequisites ###

You'll need a working Python 3.x installation and the development
tools to build C/C++ extensions.

Change to the `{project_root}` directory.

### Installing ###

Install the library by running `pip3 install .`.
