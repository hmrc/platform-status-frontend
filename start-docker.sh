#!/usr/bin/env bash

log_error_and_exit() {
  echo "$1" >&2
  exit 1
}

PREFIX="BASE64FILES_"

for var in $(compgen -e | grep "^$PREFIX"); do
  IFS='_' read -r _ index type <<< "$var"

  if [[ "$type" == "LOCATION" ]]; then
    location[$index]=${!var}
  elif [[ "$type" == "CONTENT" ]]; then
    content[$index]=${!var}
  fi
done

for index in "${!location[@]}"; do
  if [[ -n "${content[$index]}" && -n "${location[$index]}" ]]; then
    echo "${content[$index]}" | base64 --decode > "${location[$index]}" 2>/dev/null || \
    log_error_and_exit "Error processing ${location[$index]}: Invalid base64 or insufficient permissions."
  else
    log_error_and_exit "Error: Matching location or content missing for index $index."
  fi
done

SCRIPT=$(find . -type f -name platform-status-frontend)
exec $SCRIPT $HMRC_CONFIG -Dconfig.file=conf/platform-status-frontend.conf
