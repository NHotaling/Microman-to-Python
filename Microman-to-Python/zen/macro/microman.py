# -*- mode: python; -*-

# Ugh...  no os.path...
libpath = __file__[:__file__.rfind('\\') + 1] + 'microman'

# Load the C# extension
import clr
clr.AddReferenceToFileAndPath(libpath + '\\microman.dll')

# Add the python modules to the load path
import sys
sys.path.insert(0, libpath)

from microman import Stats

image = ZenImage("test")
image.Load(libpath + "\\test.jpg")
array = image.CopyPixelsToArray(ZenPixelType.Gray16)

raise ValueError(Stats.run_stats(
    array, image.Bounds.SizeX, image.Bounds.SizeY, 16,
    image.Bounds.SizeZ, image.Bounds.SizeC, image.Bounds.SizeT
))
