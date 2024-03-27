#!/usr/bin/env sh

bash decode_and_store_files.sh

SCRIPT=$(find . -type f -name platform-status-frontend)
exec $SCRIPT $HMRC_CONFIG -Dconfig.file=conf/platform-status-frontend.conf
