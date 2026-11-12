
#!/usr/bin/env bash

# Northgate booking system backup script.
# Usage: ./scripts/backup.sh [source_directory] [destination_directory]

set -Eeuo pipefail

# Resolve the project root relative to this script.
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_SOURCE="$(cd -- "$SCRIPT_DIR/.." && pwd)"

# Allow callers to override the source and destination.
SOURCE_INPUT="${1:-$DEFAULT_SOURCE}"
# Use an absolute path so the backup destination is predictable under cron,
# where HOME may differ from the interactive shell environment.
DEST_INPUT="${2:-/home/deploy/backups/northgate}"

# Reject unexpected arguments.
if (( $# > 2 )); then
    echo "Usage: $0 [source_directory] [destination_directory]" >&2
    exit 2
fi

# Ensure the source exists and is a directory.
if [[ ! -d "$SOURCE_INPUT" ]]; then
    echo "ERROR: Source directory does not exist: $SOURCE_INPUT" >&2
    exit 1
fi

SOURCE_DIR="$(cd -- "$SOURCE_INPUT" && pwd)"

# Create the backup destination if necessary.
if ! mkdir -p -- "$DEST_INPUT"; then
    echo "ERROR: Cannot create backup destination: $DEST_INPUT" >&2
    exit 1
fi

DEST_DIR="$(cd -- "$DEST_INPUT" && pwd)"
LOG_FILE="$DEST_DIR/backup.log"

# Write timestamped messages to the backup log.
log() {
    printf '[%s] %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$*" \
        | tee -a "$LOG_FILE"
}

# Prevent multiple backup processes from running simultaneously.
exec 9>>"$DEST_DIR/.backup.lock"

if ! flock -n 9; then
    log "ERROR: Another backup process is already running. Exiting."
    exit 1
fi

on_error() {
    local exit_code=$?
    local failed_line=${BASH_LINENO[0]}

    trap - ERR
    log "ERROR: Backup failed near line ${failed_line} (exit code: ${exit_code})"
    exit "$exit_code"
}
trap on_error ERR

TIMESTAMP="$(date '+%Y-%m-%d_%H%M%S')"
ARCHIVE_NAME="northgate-backup-${TIMESTAMP}.tar.gz"
ARCHIVE_PATH="$DEST_DIR/$ARCHIVE_NAME"
TEMP_ARCHIVE="$DEST_DIR/.${ARCHIVE_NAME}.tmp"

# Remove an incomplete temporary archive if the script exits.
cleanup() {
    rm -f -- "$TEMP_ARCHIVE"
}
trap cleanup EXIT

log "Starting backup"
log "Source: $SOURCE_DIR"
log "Destination: $DEST_DIR"

# Create the compressed archive.
tar -czf "$TEMP_ARCHIVE" -C "$SOURCE_DIR" .

# Validate the compressed archive before keeping it.
gzip -t "$TEMP_ARCHIVE"
tar -tzf "$TEMP_ARCHIVE" > /dev/null

# Rename the completed archive into its final name.
mv -- "$TEMP_ARCHIVE" "$ARCHIVE_PATH"
log "Archive created: $ARCHIVE_PATH"

# Collect timestamped archives, newest first.
BACKUPS=()

while IFS= read -r BACKUP; do
    BACKUPS+=("$BACKUP")
done < <(
    find "$DEST_DIR" -maxdepth 1 -type f \
        -name 'northgate-backup-*.tar.gz' \
        -printf '%f\n' | sort -r
)

# Delete older archives, keeping only the latest seven.
if (( ${#BACKUPS[@]} > 7 )); then
    for (( i = 7; i < ${#BACKUPS[@]}; i++ )); do
        OLD_ARCHIVE="$DEST_DIR/${BACKUPS[$i]}"
        rm -- "$OLD_ARCHIVE"
        log "Deleted old archive: $OLD_ARCHIVE"
    done
fi

log "Backup finished successfully"
