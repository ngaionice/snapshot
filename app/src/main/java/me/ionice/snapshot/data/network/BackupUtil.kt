package me.ionice.snapshot.data.network

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import me.ionice.snapshot.R
import me.ionice.snapshot.data.Constants
import me.ionice.snapshot.data.SnapshotDatabase
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class BackupUtil(private val context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "${context.packageName}_${Constants.backupPrefsName}",
            Context.MODE_PRIVATE
        )

    fun isBackupEnabled(): Boolean =
        preferences != null && preferences.getBoolean(Constants.backupEnabledName, false)

    fun getLoggedInAccountEmail(): String? = GoogleSignIn.getLastSignedInAccount(context)?.email

    fun setBackupEnabled(value: Boolean) {
        with(preferences.edit()) {
            putBoolean(Constants.backupEnabledName, value)
            apply()
        }
    }

    suspend fun getLastBackupTime(): LocalDateTime? {
        val req = getDriveBackupFileRequest() ?: return null
        return withContext(Dispatchers.IO) {
            val file = req.execute()
            val time = file.modifiedTime ?: file.createdTime
            time?.value?.let {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            }
        }
    }

    suspend fun backupDatabase(): Result<Unit> {
        return backupOrRestoreDatabase(isRestoring = false)
    }

    suspend fun restoreDatabase(): Result<Unit> {
        // check that the backup exists
        getExistingBackupId() ?: return Result.failure(Exception("No backup found."))

        return backupOrRestoreDatabase(isRestoring = true)
    }

    /**
     * Provides a synchronized solution to backup and restore functionality to
     * prevent user from backing up and restoring at the same time,
     * which would likely lead to unexpected results.
     */
    private suspend fun backupOrRestoreDatabase(isRestoring: Boolean): Result<Unit> {
        Mutex().withLock {
            SnapshotDatabase.closeAndLockInstance()
            val result =
                if (isRestoring) {
                    // delete the current database and insert the downloaded one
                    val dbFile = context.getDatabasePath(Constants.databaseName)
                    val path = dbFile.absolutePath

                    dbFile.delete()
                    downloadDatabase(path)
                } else {
                    uploadDatabase()
                }
            SnapshotDatabase.unlockInstance()
            return result
        }
    }

    /**
     * Returns a Result indicating whether upload was successful.
     */
    private suspend fun uploadDatabase(): Result<Unit> {
        val dbFile = context.getDatabasePath(Constants.databaseName)
        val service =
            getDriveService() ?: return Result.failure(Exception("User is not logged in."))

        if (!dbFile.exists()) {
            return Result.failure(Exception("Database file does not exist."))
        }

        val metadata = com.google.api.services.drive.model.File()
        metadata.name = dbFile.name
        metadata.mimeType = null
        val backupId = getExistingBackupId()

        return withContext(Dispatchers.IO) {
            try {
                val content = FileContent(null, dbFile)
                if (backupId == null) {
                    service.Files().create(metadata, content).execute()
                } else {
                    service.Files().update(backupId, metadata, content).execute()
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
    private suspend fun downloadDatabase(downloadPath: String): Result<Unit> {
        try {
            val req = getDriveBackupFileRequest()
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
    private suspend fun getDriveBackupFileRequest(): Drive.Files.Get? {
        val backupId = getExistingBackupId() ?: return null
        return withContext(Dispatchers.IO) {
            getDriveService()?.Files()?.get(backupId)?.setFields("name,id,modifiedTime")
        }
    }

    /**
     * Returns the Google Drive file ID of the backup if only 1 copy exists, null otherwise.
     *
     * Note that this means if there is more than 1 copy that exists, null is returned as there should only be 1 copy.
     */
    private suspend fun getExistingBackupId(): String? {
        // if user is not logged in, cannot exist
        val service = getDriveService() ?: return null

        return withContext(Dispatchers.IO) {
            // attempt to fetch ID from GDrive
            val target = service.Files().list()
                .setSpaces("drive")
                .setFields("files(name,id,modifiedTime)")
                .execute().files.filter { it.name == Constants.databaseName }

            // only 1 copy of database should exist
            return@withContext if (target.isEmpty() || target.size > 1) null else target[0].id
        }
    }

    private suspend fun getDriveService(): Drive? {
        return withContext(Dispatchers.IO) {
            GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
                val c = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
                c.selectedAccount = account.account!!
                return@withContext Drive.Builder(NetHttpTransport(), GsonFactory(), c)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build()
            }
            null
        }
    }
}

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
        .build()

    return GoogleSignIn.getClient(context, signInOptions)
}

class AuthResultContract : ActivityResultContract<Int, Task<GoogleSignInAccount>?>() {
    override fun createIntent(context: Context, input: Int): Intent {
        return getGoogleSignInClient(context).signInIntent.putExtra("input", input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
        return when (resultCode) {
            Activity.RESULT_OK -> GoogleSignIn.getSignedInAccountFromIntent(intent)
            else -> null
        }
    }
}