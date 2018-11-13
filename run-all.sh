#!/bin/bash

if ant dist ; then
    java -jar dist/receiver.jar &> "recv.txt" &
    RECV_PID=$!

    sleep 1

    java -jar dist/sender.jar > "send.txt" &
    SEND_PID=$!

    function finish {
        kill "$RECV_PID"
        kill "$SEND_PID"
    }

    trap finish INT

    wait "$RECV_PID"
    wait "$SEND_PID"
fi
