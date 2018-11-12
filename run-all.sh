#!/bin/bash

if ant dist ; then
    RECV_OUT=`mktemp`
    SEND_OUT=`mktemp`

    java -jar dist/receiver.jar > "$RECV_OUT" &
    RECV_PID=$!

    java -jar dist/sender.jar > "$SEND_OUT" &
    SEND_PID=$!

    wait "$RECV_PID"
    wait "$SEND_PID"

    echo "Receiver:"
    cat "$RECV_OUT"
    echo "Sender:"
    cat "$SEND_OUT"
fi
