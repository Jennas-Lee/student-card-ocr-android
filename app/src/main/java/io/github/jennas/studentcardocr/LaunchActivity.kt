package io.github.jennas.studentcardocr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMI)
            }
        }
    }

    const val PERMISSIONS_REQUEST_CODE = 1000;
    val PERMISSIONS = {
        Manifest.permission.CAMERA
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        var result: Int

        for (perms in permissions) {
            result = ContextCompat.checkSelfPermission(this, perms)

            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }

        return true
    }

    override public override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if(grantResults)
            }
        }
    }


}