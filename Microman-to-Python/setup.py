#!/usr/bin/env python
# -*- coding: utf-8 -*-

from setuptools import setup
from setuptools.extension import Extension

with open('README.md') as readme_file:
    readme = readme_file.read()

with open('HISTORY.rst') as history_file:
    history = history_file.read()


requirements = [

]


test_requirements = [
    'pytest',
    'pytest-cov',
    'coverage'
]


extensions = [Extension(
    "microman.ext",
    [
        "py/pyext.c",
        "ext/microman/stats.cpp",
        "ext/microman/curve_fit.cpp"
    ],
    include_dirs=['ext/microman']
)]


setup(
    name='microman',
    version='0.1.0',
    description="Automate aquisition and statistics gathering for a microscope.",
    long_description=readme + '\n\n' + history,
    author="Michael Droettboom",
    author_email='michael.droettboom@nih.gov',
    packages=[
        'microman',
    ],
    package_dir={
        'microman': 'py'
    },
    include_package_data=False,
    install_requires=requirements,
    zip_safe=False,
    keywords='microman',
    classifiers=[
        'Development Status :: 2 - Pre-Alpha',
        'Intended Audience :: Developers',
        'Natural Language :: English',
        'Programming Language :: Python :: 3.5',
    ],
    test_suite='tests',
    tests_require=test_requirements,
    ext_modules=extensions
)
