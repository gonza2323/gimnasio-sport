#!/bin/bash

autossh -M 0 -N -o "ServerAliveInterval=30" -o "ServerAliveCountMax=3" -R 8081:localhost:8080 server