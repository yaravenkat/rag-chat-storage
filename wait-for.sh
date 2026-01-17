#!/usr/bin/env bash
set -e

if [ -z "$1" ]; then
  echo "usage: $0 host:port [-- command args]"
  exit 1
fi

HOSTPORT="$1"
shift

TIMEOUT=${WAIT_FOR_TIMEOUT:-60}
SLEEP_INTERVAL=${WAIT_FOR_INTERVAL:-1}

host=$(echo "$HOSTPORT" | cut -d: -f1)
port=$(echo "$HOSTPORT" | cut -d: -f2)

echo "Waiting for $host:$port (timeout ${TIMEOUT}s)..."

start_ts=$(date +%s)
while :
do
  if (echo > /dev/tcp/"$host"/"$port") >/dev/null 2>&1; then
    echo "$host:$port is available"
    break
  fi
  now=$(date +%s)
  elapsed=$((now - start_ts))
  if [ "$elapsed" -ge "$TIMEOUT" ]; then
    echo "Timed out after ${TIMEOUT}s waiting for $host:$port"
    exit 1
  fi
  sleep "$SLEEP_INTERVAL"
done

if [ "$1" = "--" ]; then
  shift
fi

if [ $# -gt 0 ]; then
  exec "$@"
fi

exit 0