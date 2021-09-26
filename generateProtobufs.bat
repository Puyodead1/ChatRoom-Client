@echo off
protoc -I=src\main\java\optic_fusion1\client --java_out=src\main\java src\main\java\optic_fusion1\client\ChatRoomProtocol.proto