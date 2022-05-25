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

    suspend fun getLastBackupTime(): LocalDateTime? {
        val backupIds = getExistingBackupIds()
        if (preferences == null || !isBackupEnabled() || backupIds == null) return null

        return getDriveService()?.let { service ->
            withContext(Dispatchers.IO) {
                val minTime = backupIds.map { service.Files().get(it).execute().modifiedTime.value }
                    .minOrNull()
                minTime?.let {
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(minTime),
                        ZoneId.systemDefault()
                    )
                }
            }
        }
    }

    fun setBackupEnabled(value: Boolean) {
        with(preferences.edit()) {
            putBoolean(Constants.backupEnabledName, value)
            apply()
        }
    }

    fun backupDatabase(): Result<Unit> {
        if (getLoggedInAccountEmail() == null) {
            return Result.failure(Exception("User is not logged in."))
        }

        synchronized(this) {
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
    private fun uploadDatabase(): Result<Unit> {
        val basePath = context.getDatabasePath(Constants.databaseName).absolutePath

        println("Checking backup prerequisites")
        val dbFiles = Constants.dbNames.map { File("$basePath$it") }
        val service =
            getDriveService() ?: return Result.failure(Exception("User is not logged in."))

        println("Found Google Drive service.")
        if (dbFiles.map { it.exists() }.contains(false)) {
            return Result.failure(Exception("One or more database files are missing."))
        }

        val parentFolder = listOf(Constants.gDriveBackupFolder)

        val models = dbFiles.map { getGDriveFileModel(it, parentFolder) }
        val backupIds = getExistingBackupIds()

        println("Starting backup")
        try {
            val uploadedFiles = if (backupIds == null) {
                models.map { service.Files().create(it).execute() }
            } else {
                models.mapIndexed { index, it ->
                    service.Files().update(backupIds[index], it).execute()
                }
            }
            upsertBackupIds(uploadedFiles)
        } catch (e: IOException) {
            return Result.failure(e)
        }
        println("Backup complete")
        return Result.success(Unit)
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

    private fun getExistingBackupIds(): List<String>? {
        if (preferences == null ||
            Constants.dbIds.map { preferences.contains(it) }.contains(false)
        ) {
            return null
        }
        return Constants.dbIds.map { preferences.getString(it, null)!! }
    }

    private fun upsertBackupIds(files: List<com.google.api.services.drive.model.File>) {
        with(preferences.edit()) {
            Constants.dbIds.forEachIndexed { index, idName -> putString(idName, files[index].id) }
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