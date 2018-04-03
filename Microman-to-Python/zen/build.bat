@echo off
pushd ..\ext\microman
msbuild microman.vcxproj /t:Rebuild /p:Configuration=Release /p:Platform=x64
popd
