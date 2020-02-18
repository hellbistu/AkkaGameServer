
::先删除protogen文件夹
del protogen

::生成
bin\protoc-3.11.3-win64\bin\protoc.exe --java_out=. protocols/*.proto

del gate\src\protogen\*
copy protogen\* gate\src\protogen\