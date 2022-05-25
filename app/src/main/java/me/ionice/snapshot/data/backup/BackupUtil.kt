package me.ionice.snapshot.data.backup

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
import java.io.File
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

    fun getLastBackupTime(): LocalDateTime? {
        val backupId = getExistingBackupId()
        if (preferences == null || !isBackupEnabled() || backupId == null) return null

        return getDriveService()?.let { service ->
            service.Files().get(backupId).execute().modifiedTime.value.let {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            }
        }
    }

    fun setBackupEnabled(value: Boolean) {
        with(preferences.edit()) {
            putBoolean(Constants.backupEnabledName, value)
            apply()
        }
    }

    suspend fun backupDatabase(): Result<Unit> {
        if (getLoggedInAccountEmail() == null) {
            return Result.failure(Exception("User is not logged in."))
        }
        val mutex = Mutex()

        mutex.withLock {
            SnapshotDatabase.closeAndLockInstance()
            val uploadResult = uploadDatabase()
            SnapshotDatabase.unlockInstance()
            return uploadResult
        }
    }

    private fun getDriveService(): Drive? {
        GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
            val credential =
                GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = account.account!!
            return Drive.Builder(NetHttpTransport(), GsonFactory(), credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build()
        }
        return null
    }

    /**
     * Returns a Result indicating whether upload was successful.
     */
    private suspend fun uploadDatabase(): Result<Unit> {
        println("Checking backup prerequisites")
        val dbFile = context.getDatabasePath(Constants.databaseName)
        val service =
            getDriveService() ?: return Result.failure(Exception("User is not logged in."))

        println("Found Google Drive service.")
        if (!dbFile.exists()) {
            return Result.failure(Exception("One or more database files are missing."))
        }

        val parentFolder = listOf(Constants.gDriveBackupFolder)

        val model = getGDriveFileModel(dbFile, parentFolder)
        val backupId = getExistingBackupId()

        println("Starting backup")
        return withContext(Dispatchers.IO) {
            try {
                val uploadedFiles = if (backupId == null) {
                    service.Files().create(model).execute()
                } else {
                    service.Files().update(backupId, model).execute()
                }
                upsertBackupId(uploadedFiles)
            } catch (e: IOException) {
                Result.failure<Unit>(e)
            }
            println("Backup complete")
            Result.success(Unit)
        }
    }

    private fun downloadDatabase(): Result<Unit> {
        return Result.success(Unit)
    }

    private fun getGDriveFileModel(
        file: File,
        parents: List<String>
    ): com.google.api.services.drive.model.File {
        val model = com.google.api.services.drive.model.File()
        model.name = file.name
        model.parents = parents
        return model
    }

    private fun getExistingBackupId(): String? {
        if (preferences == null) return null
        return preferences.getString(Constants.dbId, null)
    }

    private fun upsertBackupId(file: com.google.api.services.drive.model.File) {
        with(preferences.edit()) {
            putString(Constants.dbId, file.id)
            apply()
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