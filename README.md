# northgate-booking_1

## Backup and Restore

### Backup location
Automated backups are stored on the EC2 instance at:
`/home/deploy/backups/northgate/`

Archives use the following naming convention:
`northgate-backup-YYYY-MM-DD_HHMMSS.tar.gz`

The backup script retains the seven most recent archives and runs daily at 01:30 through the `deploy` user's crontab.

### Verifying a backup
List available archives:
```bash
ls -lh /home/deploy/backups/northgate/
```

Check an archive's integrity:
```bash
gzip -t /home/deploy/backups/northgate/northgate-backup-YYYY-MM-DD_HHMMSS.tar.gz
```

Inspect its contents:
```bash
tar -tzf /home/deploy/backups/northgate/northgate-backup-YYYY-MM-DD_HHMMSS.tar.gz
```

### Restore procedure
For a recovery test, create a separate directory:
```bash
mkdir -p /home/deploy/restore-test
```

Extract the selected archive:
```bash
tar -xzf /home/deploy/backups/northgate/northgate-backup-YYYY-MM-DD_HHMMSS.tar.gz -C /home/deploy/restore-test
```

Inspect the extracted files and verify that the expected project files are present.

**Important:** Test restoration in a separate directory before replacing any live application files. A production restoration should account for application downtime, existing files, configuration, ownership and permissions.
