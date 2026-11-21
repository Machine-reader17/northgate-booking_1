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

Build

Requirements:
- Java 17
- Maven 3.9+

Standard build
```bash
mvn clean verify
```

Development build
```bash
mvn clean verify -Pdev
```

Production build
```bash
mvn clean verify -Pprod
```

All builds compile the application, run unit tests, package the WAR, and generate the JaCoCo coverage report.

Build outputs

| Artifact | Location |
|---|---|
| WAR | `target/booking.war` |
| JaCoCo HTML report | `target/site/jacoco/index.html` |
| JaCoCo execution data | `target/jacoco.exec` |

The WAR filename remains `booking.war`. The project version is embedded in the WAR manifest as `Implementation-Version`.

The development and production Maven profiles currently identify the selected build environment. They do not yet change application runtime behaviour.

Versioning Convention

This project follows Semantic Versioning (`MAJOR.MINOR.PATCH`).
- Development builds use the `-SNAPSHOT` suffix (e.g. `1.0.1-SNAPSHOT`).
- Stable releases use fixed versions without the suffix (e.g. `1.0.0`).
- Release versions are incremented according to the changes and tagged in Git.
- Patch releases contain backward-compatible fixes, minor releases add backward-compatible functionality, and major releases introduce breaking changes.

The Maven WAR filename includes the project version, for example `booking-1.0.0.war`.
