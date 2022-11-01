package dev.ionice.snapshot.data.backup

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.ionice.snapshot.R
import dev.ionice.snapshot.core.database.SnapshotDatabase
import dev.ionice.snapshot.work.OneOffBackupSyncWorker
import dev.ionice.snapshot.work.PeriodicBackupSyncWorker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class GDriveBackupModule(private val context: Context) : BackupModule {

    override suspend fun getLastBackupTime(): LocalDateTime? {
        // if user is not logged in, cannot exist
        val req = getDriveService()?.let { getDriveBackupFileRequest(it) } ?: return null
        return withContext(Dispatchers.IO) {
            val file = req.execute()
            val time = file.modifiedTime ?: file.createdTime
            time?.value?.let {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            }
        }
    }

    /**
     * Backs up the database immediately.
     *
     * Use with caution: if the Coroutine scope this function is called in gets cancelled,
     * the behavior is unknown: the backup on Google Drive may be corrupted.
     */
    override suspend fun backupDatabase(selfWorkId: UUID): Result<Unit> {
        // if user is not logged in, cannot exist
        val service =
            getDriveService() ?: return Result.failure(Exception("User is not logged in."))

        return backupOrRestoreDatabase(
            isRestoring = false,
            driveService = service,
            selfWorkId = selfWorkId
        )
    }

    /**
     * Restores the database immediately.
     *
     * Use with caution: if the Coroutine scope this function is called in gets cancelled,
     * the behavior is unknown: the local copy may be corrupted.
     */
    override suspend fun restoreDatabase(selfWorkId: UUID): Result<Unit> {
        // if user is not logged in, cannot exist
        val service =
            getDriveService() ?: return Result.failure(Exception("User is not logged in."))

        // check that the backup exists
        getExistingBackupId(service) ?: return Result.failure(Exception("No backup found."))

        return backupOrRestoreDatabase(
            isRestoring = true,
            driveService = service,
            selfWorkId = selfWorkId
        )
    }

    fun hasRunningJobs(ignoredId: UUID? = null): Boolean {
        val manager = WorkManager.getInstance(context)
        val has = { name: String ->
            // exclude the ignored ID if exists, then checks if any are running
            manager.getWorkInfosForUniqueWork(name).get()
                .filter { work -> ignoredId?.let { work.id != ignoredId } ?: true }
                .any { it.state == State.RUNNING }
        }
        return has(PeriodicBackupSyncWorker.WORK_NAME) || has(OneOffBackupSyncWorker.WORK_NAME)
    }

    /**
     * Provides a synchronized solution to backup and restore functionality to
     * prevent user from backing up and restoring at the same time,
     * which would likely lead to unexpected results.
     */
    private suspend fun backupOrRestoreDatabase(
        isRestoring: Boolean,
        driveService: Drive,
        selfWorkId: UUID
    ): Result<Unit> {
        SnapshotDatabase.runCheckpoint()
        return if (isRestoring) {
            val dbFile = context.getDatabasePath(SnapshotDatabase.DATABASE_NAME)
            val path = dbFile.absolutePath

            // delete the current database and insert the downloaded one
            dbFile.delete()
            // the write-ahead log and shared-mem files also need to be deleted, otherwise
            // Room reads from them first and disregards the updated database file
            File("$path-shm").delete()
            File("$path-wal").delete()
            if (hasRunningJobs(selfWorkId)) return Result.failure(Exception("An backup/restore job is already running."))
            downloadDatabase(downloadPath = path, driveService = driveService)
        } else {
            if (hasRunningJobs(selfWorkId)) return Result.failure(Exception("An backup/restore job is already running."))
            uploadDatabase(driveService)
        }
    }

    /**
     * Returns a Result indicating whether upload was successful.
     */
    private suspend fun uploadDatabase(driveService: Drive): Result<Unit> {
        val dbFile = context.getDatabasePath(SnapshotDatabase.DATABASE_NAME)

        if (!dbFile.exists()) {
            return Result.failure(Exception("Database file does not exist."))
        }

        val metadata = com.google.api.services.drive.model.File()
        metadata.name = dbFile.name
        metadata.mimeType = null
        val backupId = getExistingBackupId(driveService)

        return withContext(Dispatchers.IO) {
            try {
                val content = FileContent(null, dbFile)
                if (backupId == null) {
                    driveService.Files().create(metadata, content).execute()
                } else {
                    driveService.Files().update(backupId, metadata, content).execute()
                }
                System.err.println("Upload complete.")
                Result.success(Unit)
            } catch (e: IOException) {
                Result.failure(e)
            }
        }
    }

    /**
     * Returns a Success if a backup exists and was downloaded to the specified path, or a Failure otherwise.
     */
    private suspend fun downloadDatabase(downloadPath: String, driveService: Drive): Result<Unit> {
        try {
            val req = getDriveBackupFileRequest(driveService)
                ?: return Result.failure(Exception("Backup does not exist."))
            return withContext(Dispatchers.IO) {
                val outputStream = FileOutputStream(downloadPath)
                req.executeMediaAndDownloadTo(outputStream)
                outputStream.close()
                System.err.println("Download complete.")
                Result.success(Unit)
            }
        } catch (e: IOException) {
            return Result.failure(e)
        }
    }

    /**
     * Returns a Drive.Get request if a backup exists on Google Drive of the last logged in account, null otherwise.
     */
    private suspend fun getDriveBackupFileRequest(driveService: Drive): Drive.Files.Get? {
        val backupId = getExistingBackupId(driveService) ?: return null
        return withContext(Dispatchers.IO) {
            driveService.Files().get(backupId)?.setFields("name,id,modifiedTime")
        }
    }

    /**
     * Returns the Google Drive file ID of the backup if only 1 copy exists, null otherwise.
     *
     * Note that this means if there is more than 1 copy that exists, null is returned as there should only be 1 copy.
     */
    private suspend fun getExistingBackupId(driveService: Drive): String? {
        return withContext(Dispatchers.IO) {
            // attempt to fetch ID from GDrive
            val target = driveService.Files().list()
                .setSpaces("drive")
                .setFields("files(name,id,modifiedTime)")
                .execute().files.filter { it.name == SnapshotDatabase.DATABASE_NAME }

            // only 1 copy of database should exist
            if (target.isEmpty() || target.size > 1) null else target[0].id
        }
    }

    private suspend fun getDriveService(): Drive? {
        return withContext(Dispatchers.IO) {
            GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
                val c = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
                c.selectedAccount = account.account!!
                Drive.Builder(NetHttpTransport(), GsonFactory(), c)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build()
            }
        }
    }
}

class GAuthResultContract : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        return getGoogleSignInClient(context).signInIntent.putExtra("input", input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }

    private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
            .build()

        return GoogleSignIn.getClient(context, signInOptions)
    }
}