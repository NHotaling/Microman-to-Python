#include <stdio.h>
#include <Python.h>

#define NPY_NO_DEPRECATED_API NPY_1_7_API_VERSION

#include <numpy/ndarrayobject.h>

#include "stats.h"

static PyObject* run_stats(PyObject *self, PyObject *obj) {
    int depth;

    depth = 8;
    PyArrayObject *array = (PyArrayObject *)PyArray_ContiguousFromAny(
        obj, NPY_UBYTE, 5, 5);
    if (array == NULL) {
        depth = 16;
        array = (PyArrayObject *)PyArray_ContiguousFromAny(
            obj, NPY_USHORT, 2, 2);
        if (array == NULL) {
            return NULL;
        }
    }

    int result = common_run_stats((unsigned char *)PyArray_BYTES(array),
                                  PyArray_DIM(array, 4),
                                  PyArray_DIM(array, 3),
                                  depth,
                                  PyArray_DIM(array, 2),
                                  PyArray_DIM(array, 1),
                                  PyArray_DIM(array, 0));

    Py_XDECREF(array);

    return PyLong_FromLong(result);
}

static struct PyMethodDef microman_methods[] = {
    {
        "run_stats", run_stats, METH_O,
        "Run the statistics on the image."
    },
    {NULL, NULL, 0, NULL}
};

// Module definition
static struct PyModuleDef microman_definition = {
    PyModuleDef_HEAD_INIT,
    "microman",
    "Collect statistics for an image.",
    -1,
    microman_methods
};

// Module initialization
PyMODINIT_FUNC PyInit_ext(void) {
    Py_Initialize();
    _import_array();
    return PyModule_Create(&microman_definition);
}
