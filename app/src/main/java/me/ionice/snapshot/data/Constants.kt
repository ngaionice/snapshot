package me.ionice.snapshot.data

object Constants {

    const val databaseName = "snapshot_database"
    const val gDriveBackupFolder = "db_backup_main"

    const val backupPrefsName = "backup_preferences"
    const val backupEnabledName = "is_enabled"

    // the order of the values in these 2 variables should always be the same
    val dbIds = listOf("database_id", "database_shm_id", "database_wal_id")
    val dbNames = listOf("", "-shm", "-wal")
}