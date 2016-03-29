#!/bin/bash
# Idea from https://docs.docker.com/compose/startup-order/

set -e

host="$1"
shift
cmd="$@"

until mysql -h "$host" -u "asqatasun" -c '\l'; do
  >&2 echo "MariaDB is unavailable - sleeping"
  sleep 1
done

>&2 echo "MariaDB is up - executing command"
exec $cmd
